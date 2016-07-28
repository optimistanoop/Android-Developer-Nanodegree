
/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.sunshine.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.wearable.DataApi.DataListener;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Digital watch face with seconds. In ambient mode, the seconds aren't displayed. On devices with
 * low-bit ambient mode, the text is drawn without anti-aliasing in ambient mode.
 */
public class SunshineWatchFace extends CanvasWatchFaceService {
    private static final Typeface NORMAL_TYPEFACE =
            Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);

    /**
     * Update rate in milliseconds for interactive mode. We update once a second since seconds are
     * displayed in interactive mode.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<SunshineWatchFace.Engine> mWeakReference;

        public EngineHandler(SunshineWatchFace.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            SunshineWatchFace.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine implements
            ConnectionCallbacks, OnConnectionFailedListener, DataListener {

        // Constants for receiving data at Watch Face
        private static final String PATH_WEATHER = "/weather";
        private static final String KEY_HIGH = "high_temp";
        private static final String KEY_LOW = "low_temp";
        private static final String KEY_ID = "weather_id";

        // Variables for Weather Conditions
        private String mHighTemp;
        private String mLowTemp;
        private int mWeatherId;

        // Google API Client
        private GoogleApiClient mGoogleApiClient;

        final Handler mUpdateTimeHandler = new EngineHandler(this);
        boolean mRegisteredTimeZoneReceiver = false;
        boolean mAmbient;
        Calendar mCalendar;
        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mCalendar.setTimeZone(TimeZone.getDefault());
                mCalendar.setTimeInMillis(System.currentTimeMillis());
            }
        };

        // Paint objects
        Paint mBackgroundPaint;
        Paint mTextTimePaint;
        Paint mTextDatePaint;
        Paint mTextTempLowPaint;
        Paint mTextTempHighPaint;

        // Offsets date, time and weather
        float mXOffsetTime;
        float mXOffsetDate;
        float mYOffsetTime;
        float mYOffsetDate;
        float mYOffsetDivider;
        float mYOffsetWeather;

        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(SunshineWatchFace.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_VARIABLE)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());
            Resources resources = SunshineWatchFace.this.getResources();

            mGoogleApiClient = new GoogleApiClient.Builder(SunshineWatchFace.this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Wearable.API)
                    .build();

            // Initialize Paints
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(resources.getColor(R.color.primary));
            mTextTimePaint = new Paint();
            mTextTimePaint = createTextTimePaint(resources.getColor(R.color.digital_text));
            mTextDatePaint = new Paint();
            mTextDatePaint = createTextDatePaint();
            mTextTempLowPaint = new Paint();
            mTextTempLowPaint = createTextTempPaint(resources.getColor(R.color.primary_light));
            mTextTempHighPaint = new Paint();
            mTextTempHighPaint = createTextTempPaint(resources.getColor(R.color.digital_text));

            // Initialize Offsets
            mXOffsetTime = mTextTimePaint.measureText("12:00") / 2;
            mXOffsetDate = mTextDatePaint.measureText("WED, JUN 13, 2016") / 2;
            mYOffsetTime = resources.getDimension(R.dimen.y_time);
            mYOffsetDate = resources.getDimension(R.dimen.y_date);
            mYOffsetDivider = resources.getDimension(R.dimen.y_divider);
            mYOffsetWeather = resources.getDimension(R.dimen.y_weather);

            // Calendar for current date/time
            mCalendar = Calendar.getInstance();
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        // Helper methods to initialize paint objects
        private Paint createTextTimePaint(int textColor) {
            Paint paint = new Paint();
            paint.setColor(textColor);
            paint.setTypeface(NORMAL_TYPEFACE);
            paint.setAntiAlias(true);
            paint.setTextSize(getResources().getDimension(R.dimen.time_text));
            return paint;
        }
        private Paint createTextDatePaint() {
            Paint paint = new Paint();
            paint.setTypeface(NORMAL_TYPEFACE);
            paint.setAntiAlias(true);
            paint.setTextSize(getResources().getDimension(R.dimen.date_text));
            return paint;
        }
        private Paint createTextTempPaint(int textColor) {
            Paint paint = new Paint();
            paint.setColor(textColor);
            paint.setTypeface(NORMAL_TYPEFACE);
            paint.setAntiAlias(true);
            paint.setTextSize(getResources().getDimension(R.dimen.temp_text));
            return paint;
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                mGoogleApiClient.connect();
                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                mCalendar.setTimeZone(TimeZone.getDefault());
                mCalendar.setTimeInMillis(System.currentTimeMillis());
            } else {
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    Wearable.DataApi.removeListener(mGoogleApiClient, this);
                    mGoogleApiClient.disconnect();
                }
                unregisterReceiver();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            SunshineWatchFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            SunshineWatchFace.this.unregisterReceiver(mTimeZoneReceiver);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (mAmbient != inAmbientMode) {
                mAmbient = inAmbientMode;
                invalidate();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            // Draw background color
            if (isInAmbientMode()) {
                canvas.drawColor(Color.BLACK);
            } else {
                canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);
            }

            // Set current time in calendar
            mCalendar.setTimeInMillis(System.currentTimeMillis());

            // Draw time text
            int hourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = mCalendar.get(Calendar.MINUTE);
            String text = String.format("%02d:%02d", hourOfDay, minute);
            canvas.drawText(text, bounds.centerX() - mXOffsetTime, mYOffsetTime, mTextTimePaint);

            // Draw date text
            String dayName = mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
            String monthName = mCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
            int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
            int year = mCalendar.get(Calendar.YEAR);
            String dateText = String.format("%s, %s %d, %d", dayName.toUpperCase(), monthName.toUpperCase(), dayOfMonth, year);
            if (isInAmbientMode()) {
                mTextDatePaint.setColor(getResources().getColor(R.color.digital_text));
            } else {
                mTextDatePaint.setColor(getResources().getColor(R.color.primary_light));
            }
            canvas.drawText(dateText, bounds.centerX() - mXOffsetDate, mYOffsetDate, mTextDatePaint);

            // Draw weather info if high and low temp are available
            if (mHighTemp != null && mLowTemp != null) {
                // Draw divider
                canvas.drawLine(bounds.centerX() - 25, mYOffsetDivider, bounds.centerX() + 25, mYOffsetDivider, mTextDatePaint);

                // Draw high and low temperature
                float highTextSize = mTextTempHighPaint.measureText(mHighTemp);
                if (mAmbient) {
                    mTextTempLowPaint.setColor(getResources().getColor(R.color.digital_text));
                    float lowTextSize = mTextTempLowPaint.measureText(mLowTemp);
                    float xOffset = bounds.centerX() - ((highTextSize + lowTextSize + 20) / 2);
                    canvas.drawText(mHighTemp, xOffset, mYOffsetWeather, mTextTempHighPaint);
                    canvas.drawText(mLowTemp, xOffset + highTextSize + 20, mYOffsetWeather, mTextTempLowPaint);
                } else {
                    mTextTempLowPaint.setColor(getResources().getColor(R.color.primary_light));
                    float xOffset = bounds.centerX() - (highTextSize / 2);
                    canvas.drawText(mHighTemp, xOffset, mYOffsetWeather, mTextTempHighPaint);
                    canvas.drawText(mLowTemp, bounds.centerX() + (highTextSize / 2) + 20, mYOffsetWeather, mTextTempLowPaint);

                    // Draw icon (if not in ambient mode)
                    Drawable b = getResources().getDrawable(Utility.getIconResourceForWeatherCondition(mWeatherId));
                    Bitmap icon = ((BitmapDrawable) b).getBitmap();
                    float scaledWidth = (mTextTempHighPaint.getTextSize() / icon.getHeight()) * icon.getWidth();
                    Bitmap weatherIcon = Bitmap.createScaledBitmap(icon, (int) scaledWidth, (int) mTextTempHighPaint.getTextSize(), true);
                    float iconXOffset = bounds.centerX() - ((highTextSize / 2) + weatherIcon.getWidth() + 30);
                    canvas.drawBitmap(weatherIcon, iconXOffset, mYOffsetWeather - weatherIcon.getHeight(), null);
                }
            }
        }

        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }

        // Google API Client Listeners
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Wearable.DataApi.addListener(mGoogleApiClient, this);
            Log.d("GOOGLE_API_CLIENT", "Connected");
        }
        @Override
        public void onConnectionSuspended(int i) {
            Log.d("GOOGLE_API_CLIENT", "Connection Suspended");
        }
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.d("GOOGLE_API_CLIENT", "Connection Failed: " + connectionResult.getErrorMessage());
        }

        // Wearable Data Change Listener
        @Override
        public void onDataChanged(DataEventBuffer dataEventBuffer) {
            for (DataEvent dataEvent : dataEventBuffer) {
                if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                    DataItem dataItem = dataEvent.getDataItem();
                    if (dataItem.getUri().getPath().compareTo(PATH_WEATHER) == 0) {
                        DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                        mHighTemp = dataMap.getString(KEY_HIGH);
                        mLowTemp = dataMap.getString(KEY_LOW);
                        mWeatherId = dataMap.getInt(KEY_ID);
                        Log.d("WATCH_DATA", "\nHigh: " + mHighTemp + "\nLow: " + mLowTemp + "\nID: " + mWeatherId);
                        invalidate();
                    }
                }
            }
        }

    }
}
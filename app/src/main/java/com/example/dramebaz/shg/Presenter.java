package com.example.dramebaz.shg;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;

import com.example.dramebaz.shg.client.SplitwiseRestClient;
import com.example.dramebaz.shg.splitwise.Balance;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class Presenter {
    public static String getCostString(String cost, String currencyCode) {
        String currencySymbol = currencyCode;
        if(currencyCode.equals("USD")) {
            currencySymbol = "$";
        } else if(currencyCode.equals("INR")) {
            currencySymbol = "Rs";
        }
        return String.format("%s %s", currencySymbol, cost);
    }

    public static String shortDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        DateFormat targetFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());

        Date convertedDate = null;
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(convertedDate != null) {
            return targetFormat.format(convertedDate);
        } else {
            return "";
        }
    }

    public static Spanned getBalanceText(String amount){
        Spanned[] responses = new Spanned[]{
                Html.fromHtml("<font color='#BBBBBB'>settled up</font>"),
                Html.fromHtml("<font color='red'>you owe</font>"),
                Html.fromHtml("<font color='#5BC5A7'>owes you</font>")
        };

        if (amount == null) {
            return responses[0];
        }

        try {
            Double amountVal = Double.valueOf(amount);
            if( amountVal > 0 ){
                return responses[2];
            }
            if (amountVal < 0 ){
                return responses[1];
            }
        } catch (Exception e){
            Log.e("ERROR", "Error in Balance");
        }
        return responses[0];
    }

    public static Spanned getBalanceString(Balance balance) {
        if (balance.amount == null){
            return null;
        }
        String amountText = getCostString(balance.amount, balance.currencyCode).replaceAll("-", "");

        try {
            Double amountVal = Double.valueOf(balance.amount);
            if (amountVal > 0){
                return Html.fromHtml("<font color='#5BC5A7'>"+ amountText + "</font>");
            } if (amountVal < 0) {
                return Html.fromHtml("<font color='red'>"+ amountText + "</font>");
            }
            return null;
        } catch (Exception e){
            return null;
        }
    }
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static Map<String,Integer> getUsersShareMap(Context context, final Integer cost, final String type, final Integer id){
        final Map<String, Integer> userShareMap = new LinkedHashMap<>();
        // get current user id
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(context);
        final int currentUserId =  pref.getInt("currentUserId", 0);
        // check type
        if(type.equals("group")){
            // get all members id in case of group
            SplitwiseRestClient client = RestApplication.getSplitwiseRestClient();
            client.getGroup(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                    try {
                        // divide cost between all members
                        // paid share will be of current user id
                        JSONObject group = json.getJSONObject("group");
                        JSONArray members = group.getJSONArray("members");
                        int length = members.length();
                        for(int i=0;i<length;i++){
                            JSONObject member = members.getJSONObject(i);
                            if(member.getInt("id")!= currentUserId){
                                userShareMap.put("users__"+i+"__user_id", member.getInt("id"));
                                userShareMap.put("users__"+i+"__paid_share", 0);
                                userShareMap.put("users__"+i+"__owed_share", cost%length);
                            }else {
                                userShareMap.put("users__"+i+"__user_id", member.getInt("id"));
                                userShareMap.put("users__"+i+"__paid_share", cost);
                                userShareMap.put("users__"+i+"__owed_share", cost%length);
                            }
                        }
                        Log.i("delete_group", json.toString());

                    } catch (Exception e) {
                        Log.e("FAILED delete_group", "json_parsing", e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
            }, id);

        }else{
            userShareMap.put("users__0__user_id", currentUserId);
            userShareMap.put("users__0__paid_share", cost);
            userShareMap.put("users__0__owed_share", cost%2);
            userShareMap.put("users__1__user_id", id);
            userShareMap.put("users__1__paid_share", 0);
            userShareMap.put("users__1__owed_share", cost%2);
        }

        return userShareMap;
    }
}

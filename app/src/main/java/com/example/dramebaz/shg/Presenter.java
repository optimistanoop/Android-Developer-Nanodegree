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

}

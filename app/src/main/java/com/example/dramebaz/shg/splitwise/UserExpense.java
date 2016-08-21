package com.example.dramebaz.shg.splitwise;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserExpense {
    public User user;
    public String paidShare;
    public String owedShare;
    public String netBalance;

    public static UserExpense fromJSONObject(JSONObject jsonObject) throws JSONException {
        UserExpense u = new UserExpense();
        u.user = User.fromJSONObject(jsonObject.getJSONObject("user"));
        u.paidShare = jsonObject.getString("paid_share");
        if(u.paidShare.equals("null")) {
            u.paidShare = null;
        }

        u.owedShare = jsonObject.getString("owed_share");
        if(u.owedShare.equals("null")) {
            u.owedShare = null;
        }

        u.netBalance = jsonObject.getString("net_balance");
        if(u.netBalance.equals("null")) {
            u.netBalance = null;
        }

        return u;
    }

    public Double getNetBalance() {
        if(netBalance == null) {
            return null;
        }
        return Double.parseDouble(netBalance);
    }

    public boolean isPayer() {
        if(paidShare != null && Double.parseDouble(paidShare) > 0) {
            return true;
        }
        return false;
    }

    public static List<UserExpense> fromJSONArray(JSONArray jsonArray) throws  JSONException {
        List<UserExpense> userExpenses = new ArrayList<>();
        for (int i=0; i < jsonArray.length(); i++) {
            userExpenses.add(fromJSONObject(jsonArray.getJSONObject(i)));
        }
        return userExpenses;
    }
}

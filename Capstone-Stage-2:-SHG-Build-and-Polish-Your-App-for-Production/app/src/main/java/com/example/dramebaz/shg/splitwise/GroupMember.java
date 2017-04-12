package com.example.dramebaz.shg.splitwise;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GroupMember {
    public User user;
    public Balance balance;

    public static GroupMember fromJSONObject(JSONObject jsonObject) throws JSONException {
        GroupMember gm = new GroupMember();
        gm.user = User.fromJSONObject(jsonObject);
        if(!jsonObject.isNull("balance") && jsonObject.getJSONArray("balance").length() > 0) {
            gm.balance = Balance.fromJSONObject(jsonObject.getJSONArray("balance").getJSONObject(0));
        }
        else {
            gm.balance = new Balance();
            gm.balance.amount = "0";
            gm.balance.currencyCode = "";
        }
        return gm;
    }

    public static List<GroupMember> fromJSONArray(JSONArray jsonArray) throws  JSONException {
        List<GroupMember> gms = new ArrayList<>();
        for (int i=0; i < jsonArray.length(); i++) {
            gms.add(fromJSONObject(jsonArray.getJSONObject(i)));
        }
        return gms;
    }
}

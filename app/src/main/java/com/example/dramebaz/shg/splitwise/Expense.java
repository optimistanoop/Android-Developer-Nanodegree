package com.example.dramebaz.shg.splitwise;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Expense {
    public Integer id;
    public String groupId;
    public String friendshipId;
    public String deleted_at;
    public String description;
    public String cost;
    public String currencyCode;
    public String date;
    public String createdAt;
    public List<UserExpense> userExpenses;

    public static Expense fromJSONObject(JSONObject jsonObject) throws JSONException {
        Expense e = new Expense();
        e.id = jsonObject.getInt("id");
        e.groupId = jsonObject.getString("group_id");
        e.friendshipId = jsonObject.getString("friendship_id");
        e.deleted_at = jsonObject.getString("deleted_at");
        e.description = jsonObject.getString("description");
        e.cost = jsonObject.getString("cost");
        e.currencyCode = jsonObject.getString("currency_code");
        e.date = jsonObject.getString("date");
        e.createdAt = jsonObject.getString("created_at");
        e.userExpenses = UserExpense.fromJSONArray(jsonObject.getJSONArray("users"));
        return e;
    }

    public static List<Expense> fromJSONArray(JSONArray jsonArray) throws  JSONException {
        List<Expense> expenses = new ArrayList<>();
        for (int i=0; i < jsonArray.length(); i++) {
            expenses.add(fromJSONObject(jsonArray.getJSONObject(i)));
        }
        return expenses;
    }


}

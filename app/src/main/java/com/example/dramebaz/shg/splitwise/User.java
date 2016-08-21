package com.example.dramebaz.shg.splitwise;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    public Integer id;
    public String firstName;
    public String lastName;
    public String pictureUrl;

    public static User fromJSONObject(JSONObject jsonObject) throws JSONException {
        User u = new User();
        u.id = jsonObject.getInt("id");
        u.firstName = jsonObject.getString("first_name");
        u.lastName = jsonObject.getString("last_name");
        u.pictureUrl = jsonObject.getJSONObject("picture").getString("medium");
        return u;
    }
}

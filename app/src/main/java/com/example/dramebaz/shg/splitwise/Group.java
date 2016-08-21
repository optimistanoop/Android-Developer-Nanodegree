package com.example.dramebaz.shg.splitwise;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Group {
    public Integer id;
    public String name;
    public String type;
    public String countryCode;
    public List<GroupMember> members;

    public static Group fromJSONObject(JSONObject jsonObject) throws JSONException {
        Group g = new Group();
        g.id = jsonObject.getInt("id");
        g.name = jsonObject.getString("name");
        //g.type = jsonObject.getString("group_type");
        //g.countryCode = jsonObject.getString("country_code");
        g.members = GroupMember.fromJSONArray(jsonObject.getJSONArray("members"));
        return g;
    }

    public static List<Group> fromJSONArray(JSONArray jsonArray) throws  JSONException {
        List<Group> groups = new ArrayList<>();
        for (int i=0; i < jsonArray.length(); i++) {
            Group group = fromJSONObject(jsonArray.getJSONObject(i));
            if(group.id > 0) {
                groups.add(group);
            }
        }
        return groups;
    }
}

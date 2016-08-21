package com.example.dramebaz.shg.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dramebaz.shg.R;
import com.example.dramebaz.shg.splitwise.Group;

import java.util.List;

public class GroupsSideBarAdapter extends ArrayAdapter<Group> {
    List<Group> groups;

    public GroupsSideBarAdapter(Context context, List<Group> groups) {
        super(context, 0, groups);
    }

    public void addAll(List<Group> groups) {
        super.addAll(groups);
        this.groups = groups;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // Return an integer here representing the type of View.
        // Note: Integers must be in the range 0 to getViewTypeCount() - 1
        if(position > 1) {
            return 0;
        }
        else {
            return 1;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // View should be created based on the type returned from `getItemViewType(int position)`
        // convertView is guaranteed to be the "correct" recycled type
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.sidebar_list_item, null);
        }

        TextView tvGroupName = (TextView)convertView.findViewById(R.id.text1);
        tvGroupName.setText(groups.get(position).name);

        return convertView;
    }
}

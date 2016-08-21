package com.example.dramebaz.shg;

/**
 * Created by dramebaz on 20/8/16.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dramebaz.shg.splitwise.Group;
import com.example.dramebaz.shg.splitwise.GroupMember;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GroupAdapter extends ArrayAdapter<Group> {
    private static class ViewHolder {
        ImageView profileImage;
        TextView username;
        TextView balanceText;
        TextView totalBalance;
    }

    public GroupAdapter(Context context, List<Group> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Group group = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.balance_per_contact_item, parent, false);

            viewHolder.profileImage = (ImageView) convertView.findViewById(R.id.ivContactProfilePic);
            viewHolder.username = (TextView) convertView.findViewById(R.id.tvContactName);
            viewHolder.balanceText = (TextView) convertView.findViewById(R.id.tvBalanceText);
            viewHolder.totalBalance = (TextView) convertView.findViewById(R.id.tvTotalBalance);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Picasso.with(getContext()).load(R.drawable.ic_group_work_black_24dp).into(viewHolder.profileImage);
        viewHolder.username.setText(String.format("%s %s", group.name, ""));
        SharedPreferences settings = getContext().getSharedPreferences("currentUser", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        String id = settings.getString("id", "");
        for(GroupMember gm :group.members){
            if(gm.user.id.toString().equals(id)){
                viewHolder.totalBalance.setText(Presenter.getBalanceString(gm.balance));
                viewHolder.balanceText.setText(Presenter.getBalanceText(gm.balance.amount));

            }
        }

        return convertView;
    }
}
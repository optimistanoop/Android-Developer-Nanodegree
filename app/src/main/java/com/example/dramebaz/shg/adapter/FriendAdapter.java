package com.example.dramebaz.shg.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dramebaz.shg.Presenter;
import com.example.dramebaz.shg.R;
import com.example.dramebaz.shg.splitwise.GroupMember;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendAdapter extends ArrayAdapter<GroupMember>{
    private static class ViewHolder {
        ImageView profileImage;
        TextView username;
        TextView balanceText;
        TextView totalBalance;
    }

    public FriendAdapter(Context context, List<GroupMember> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GroupMember member = getItem(position);
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
        String lastName = member.user.lastName;
        if(lastName.equals("null")){
            lastName = "";
        }
        Picasso.with(getContext()).load(member.user.pictureUrl).into(viewHolder.profileImage);
        viewHolder.username.setText(String.format("%s %s", member.user.firstName, lastName));
        viewHolder.balanceText.setText(Presenter.getBalanceText(member.balance.amount));
        viewHolder.totalBalance.setText(Presenter.getBalanceString(member.balance));

        return convertView;
    }
}
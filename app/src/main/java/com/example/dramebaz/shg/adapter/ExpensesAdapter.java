package com.example.dramebaz.shg.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dramebaz.shg.Presenter;
import com.example.dramebaz.shg.R;
import com.example.dramebaz.shg.splitwise.Expense;
import com.example.dramebaz.shg.splitwise.UserExpense;

import java.util.List;

public class ExpensesAdapter extends ArrayAdapter<Expense> {

    private Integer currentUserId;
    private Context context;

    public ExpensesAdapter(Context context, List<Expense> expenses, Integer currentUserId) {
        super(context, R.layout.expense, expenses);
        this.currentUserId = currentUserId;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Expense e = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.expense, parent, false);
        }

        TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
        tvDate.setText(Presenter.shortDate(e.date));

        TextView tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
        tvDescription.setText(e.description);

        TextView tvPayer = (TextView) convertView.findViewById(R.id.tvPayer);
        TextView tvBalanceSummary = (TextView) convertView.findViewById(R.id.tvBalanceSummary);
        TextView tvBalanceCost = (TextView) convertView.findViewById(R.id.tvBalanceCost);

        for(UserExpense ue : e.userExpenses) {
            if(ue.isPayer()) {
                if(ue.user.id.equals(currentUserId))
                    tvPayer.setText(context.getResources().getString(R.string.you_paid));
                else
                    tvPayer.setText(ue.user.firstName + " paid");
            }
            if(ue.user.id.equals(currentUserId)) {
                if(ue.getNetBalance() != null && ue.getNetBalance() > 0) {
                    tvBalanceSummary.setText(context.getResources().getString(R.string.you_lent));
                    tvBalanceSummary.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                    tvBalanceCost.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                }
                else {
                    tvBalanceSummary.setText(context.getResources().getString(R.string.you_borrowed));
                    tvBalanceSummary.setTextColor(Color.RED);
                    tvBalanceCost.setTextColor(Color.RED);
                }
                tvBalanceCost.setText(Presenter.getCostString(String.valueOf(Math.abs(ue.getNetBalance())), e.currencyCode));
            }
        }

        tvDescription.setText(e.description);

        TextView tvCost = (TextView) convertView.findViewById(R.id.tvCost);
        tvCost.setText(Presenter.getCostString(e.cost, e.currencyCode));

        return convertView;
    }
}

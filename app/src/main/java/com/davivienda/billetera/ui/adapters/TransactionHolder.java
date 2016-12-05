package com.davivienda.billetera.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.davivienda.billetera.R;

public class TransactionHolder extends RecyclerView.ViewHolder {

    protected View container;
    protected TextView name;
    //protected TextView productAmount;
    protected TextView price;
    //protected TextView daviPoints;

    public TransactionHolder(View itemView) {
        super(itemView);
        container = itemView.findViewById(R.id.container);
        name = (TextView) itemView.findViewById(R.id.name);
        //productAmount = (TextView) itemView.findViewById(R.id.product_amount);
        price = (TextView) itemView.findViewById(R.id.price);
        //daviPoints = (TextView) itemView.findViewById(R.id.davipoints);
    }
}

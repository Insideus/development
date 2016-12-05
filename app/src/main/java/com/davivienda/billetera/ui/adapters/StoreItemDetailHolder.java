package com.davivienda.billetera.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.davivienda.billetera.R;

public class StoreItemDetailHolder extends RecyclerView.ViewHolder {

    protected TextView title;
    protected TextView quantity;
    protected ViewGroup container;

    public StoreItemDetailHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title);
        quantity = (TextView) itemView.findViewById(R.id.item_quantity);
        container = (ViewGroup) itemView.findViewById(R.id.container);
    }
}

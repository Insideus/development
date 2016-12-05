package com.davivienda.billetera.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.davivienda.billetera.R;

public class StoreItemBuyButtonHolder extends RecyclerView.ViewHolder {
    public View button;
    public StoreItemBuyButtonHolder(View itemView) {
        super(itemView);
        button = itemView.findViewById(R.id.buy_button);
    }
}

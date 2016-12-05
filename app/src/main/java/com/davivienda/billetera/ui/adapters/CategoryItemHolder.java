package com.davivienda.billetera.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.davivienda.billetera.R;

public class CategoryItemHolder extends RecyclerView.ViewHolder{

    protected View container;
    protected ImageView image;
    protected TextView name;
    protected TextView price;

    public CategoryItemHolder(View itemView) {
        super(itemView);
        container = itemView.findViewById(R.id.container);
        image = (ImageView) itemView.findViewById(R.id.image);
        name = (TextView) itemView.findViewById(R.id.title);
        price = (TextView) itemView.findViewById(R.id.price);
    }
}

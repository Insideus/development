package com.davivienda.billetera.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.davivienda.billetera.R;

public class OtpStoreHolder extends RecyclerView.ViewHolder {
    protected View container;
    protected ImageView brandLogo;
    protected TextView name;
    protected TextView address;
    protected TextView distance;

    public OtpStoreHolder(View itemView) {
        super(itemView);
        container = itemView.findViewById(R.id.container);
        brandLogo = (ImageView) itemView.findViewById(R.id.brand_logo);
        name = (TextView) itemView.findViewById(R.id.name);
        address = (TextView) itemView.findViewById(R.id.address);
        distance = (TextView) itemView.findViewById(R.id.distance);
    }
}

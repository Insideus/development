package com.davivienda.billetera.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.davivienda.billetera.R;

/**
 * Created by fennoma_dev on 14/09/2016.
 */
public class DateTitleHolder extends RecyclerView.ViewHolder {

    protected TextView title;

    public DateTitleHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title);
    }
}

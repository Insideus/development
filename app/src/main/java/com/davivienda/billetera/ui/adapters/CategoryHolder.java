package com.davivienda.billetera.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.davivienda.billetera.R;

public class CategoryHolder extends RecyclerView.ViewHolder {

    protected TextView title;
    protected TextView categoryTitle;
    protected RecyclerView recyclerView;

    public CategoryHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title);
        categoryTitle = (TextView) itemView.findViewById(R.id.category_title);
        recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler);
    }
}

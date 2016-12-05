package com.davivienda.billetera.ui.adapters;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import com.davivienda.billetera.R;
import com.davivienda.billetera.model.Store;
import com.davivienda.billetera.model.StoreCategory;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryHolder> {

    private final Activity activity;
    private List<StoreCategory> categories;
    private Store store;

    public CategoryAdapter(Activity activity, Store store){
        this.activity = activity;
        categories = new ArrayList<>();
        this.store = store;
    }

    @Override
    public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryHolder(activity.getLayoutInflater().inflate(R.layout.store_category_items_container, parent, false));
    }

    @Override
    public void onBindViewHolder(CategoryHolder holder, int position) {
        StoreCategory category = categories.get(position);
        holder.title.setVisibility(View.GONE);
        holder.categoryTitle.setText(category.getName());
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerView.setAdapter(new CategoryItemAdapter(activity, category.getProducts(), true, store));
        holder.recyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setCategories(List<StoreCategory> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

}

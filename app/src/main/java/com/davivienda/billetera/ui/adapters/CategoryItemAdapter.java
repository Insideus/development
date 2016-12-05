package com.davivienda.billetera.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.davivienda.billetera.R;
import com.davivienda.billetera.activities.StoreDetailActivity;
import com.davivienda.billetera.activities.StoreItemDetailActivity;
import com.davivienda.billetera.model.Store;
import com.davivienda.billetera.model.StoreProduct;
import com.davivienda.billetera.utils.CurrencyUtils;
import com.davivienda.billetera.utils.ImageUtils;

public class CategoryItemAdapter extends RecyclerView.Adapter<CategoryItemHolder> {

    private Activity activity;
    private List<StoreProduct> products;
    private Store store;
    private boolean fromStoreDetail;

    public CategoryItemAdapter(Activity activity, List<StoreProduct> products, boolean fromStoreDetail, Store store) {
        this.activity = activity;
        this.products = products;
        this.fromStoreDetail = fromStoreDetail;
        this.store = store;
    }

    @Override
    public CategoryItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryItemHolder(activity.getLayoutInflater().inflate(R.layout.store_category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CategoryItemHolder holder, int position) {
        final StoreProduct product = products.get(position);
        holder.name.setText(product.getAppDisplayName());
        ImageUtils.loadImageFullURL(holder.image, product.getImage());
        if(fromStoreDetail) {
            holder.price.setText(CurrencyUtils.getCurrencyForString(product.getListPrice()));
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(StoreItemDetailActivity.PRODUCT_KEY, product);
                    bundle.putBoolean(StoreItemDetailActivity.ACCEPT_DAVIPOINTS_KEY, store.getAcceptDavipoints());
                    activity.startActivityForResult(new Intent(activity, StoreItemDetailActivity.class).putExtras(bundle), StoreDetailActivity.ADD_ITEM_TO_CART);
                }
            });
        } else {
            holder.price.setText(CurrencyUtils.getCurrencyForString(product.getSelectedProductPrice()));
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

}

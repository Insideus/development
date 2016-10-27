package ar.com.fennoma.davipocket.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.activities.StoreItemDetailActivity;
import ar.com.fennoma.davipocket.model.StoreProduct;
import ar.com.fennoma.davipocket.utils.CurrencyUtils;
import ar.com.fennoma.davipocket.utils.ImageUtils;

public class CategoryItemAdapter extends RecyclerView.Adapter<CategoryItemHolder> {

    private Activity activity;
    private List<StoreProduct> products;
    private boolean fromStoreDetail;

    public CategoryItemAdapter(Activity activity, List<StoreProduct> products, boolean fromStoreDetail) {
        this.activity = activity;
        this.products = products;
        this.fromStoreDetail = fromStoreDetail;
    }

    @Override
    public CategoryItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryItemHolder(activity.getLayoutInflater().inflate(R.layout.store_category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CategoryItemHolder holder, int position) {
        final StoreProduct product = products.get(position);
        holder.name.setText(product.getName());
        holder.price.setText(CurrencyUtils.getCurrencyForString(String.valueOf(product.getListPrice())));
        ImageUtils.loadImageFullURL(holder.image, product.getImage());
        if(fromStoreDetail) {
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(StoreItemDetailActivity.PRODUCT_KEY, product);
                    activity.startActivity(new Intent(activity, StoreItemDetailActivity.class).putExtras(bundle));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}

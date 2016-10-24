package ar.com.fennoma.davipocket.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.activities.StoreItemDetailActivity;
import ar.com.fennoma.davipocket.model.StoreProduct;
import ar.com.fennoma.davipocket.utils.ImageUtils;

public class CategoryItemAdapter extends RecyclerView.Adapter<CategoryItemHolder> {

    private Activity activity;
    private List<StoreProduct> products;

    public CategoryItemAdapter(Activity activity, List<StoreProduct> products) {
        this.activity = activity;
        this.products = products;
    }

    @Override
    public CategoryItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryItemHolder(activity.getLayoutInflater().inflate(R.layout.store_category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CategoryItemHolder holder, int position) {
        StoreProduct product = products.get(position);
        holder.name.setText(product.getName());
        holder.price.setText(product.getListPrice());
        ImageUtils.loadImageFullURL(holder.image, product.getImage());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, StoreItemDetailActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
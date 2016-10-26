package ar.com.fennoma.davipocket.ui.adapters;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.StoreCategory;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryHolder>{

    private final Activity activity;
    private List<StoreCategory> categories;

    public CategoryAdapter(Activity activity){
        this.activity = activity;
        categories = new ArrayList<>();
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
        holder.recyclerView.setAdapter(new CategoryItemAdapter(activity, category.getProducts(), true));
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

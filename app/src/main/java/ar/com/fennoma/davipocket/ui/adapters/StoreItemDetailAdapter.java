package ar.com.fennoma.davipocket.ui.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.model.StoreConfiguration;
import ar.com.fennoma.davipocket.model.StoreItemDetailConfiguration;
import ar.com.fennoma.davipocket.model.StoreProduct;

public class StoreItemDetailAdapter extends RecyclerView.Adapter {

    private static final String ADDITION_KEY = "ADDITION";

    public interface IShowableItemContainer {
        int getKindOfItems();
        List<StoreConfiguration> getConfigurations();
    }

    private Activity activity;
    private List<IShowableItemContainer> itemsToShow;

    public StoreItemDetailAdapter(Activity activity, StoreProduct product) {
        this.activity = activity;
        discriminateConfigurations(product);
    }

    private void discriminateConfigurations(StoreProduct product) {
        itemsToShow = new ArrayList<>();
        StoreItemDetailConfiguration additional = new StoreItemDetailConfiguration();
        if(product == null || product.getConfigurations() == null || product.getConfigurations().isEmpty()){
            return;
        }
        for(StoreConfiguration configuration : product.getConfigurations()){
            if(configuration.getType().equals(ADDITION_KEY)){
                additional.addConfiguration(configuration);
            }else{
                itemsToShow.add(new StoreItemDetailConfiguration(configuration));
            }
        }
        itemsToShow.add(additional);
    }

    @Override
    public int getItemViewType(int position) {
        return itemsToShow.get(position).getKindOfItems();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return itemsToShow.size();
    }
}

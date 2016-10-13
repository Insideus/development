package ar.com.fennoma.davipocket.ui.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.Store;

public class WithoutDeliveryStoreAdapter extends RecyclerView.Adapter<WithoutDeliveryStoreHolder>{

    private Activity activity;
    private List<Store> stores;

    public WithoutDeliveryStoreAdapter(Activity activity){
        this.activity = activity;
        stores = new ArrayList<>();
    }

    public void setStores(List<Store> stores){
        this.stores = stores;
        notifyDataSetChanged();
    }


    @Override
    public WithoutDeliveryStoreHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WithoutDeliveryStoreHolder(LayoutInflater.from(activity).inflate(R.layout.store_item, parent, false));
    }

    @Override
    public void onBindViewHolder(WithoutDeliveryStoreHolder holder, int position) {
        Store store = stores.get(position);
        holder.name.setText(store.getName());
        if(position % 2 == 0) {
            holder.imageView.setImageResource(R.drawable.without_delivery_mock_1);
        }else{
            holder.imageView.setImageResource(R.drawable.without_delivery_mock_2);
        }
        if(position % 3 == 0){
            holder.brandLogo.setImageResource(R.drawable.brand_mocked_1);
        } else if(position % 3 == 1){
            holder.brandLogo.setImageResource(R.drawable.brand_mocked_2);
        } else {
            holder.brandLogo.setImageResource(R.drawable.brand_mocked_3);
        }
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }
}

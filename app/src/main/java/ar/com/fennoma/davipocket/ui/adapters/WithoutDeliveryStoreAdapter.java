package ar.com.fennoma.davipocket.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.activities.StoreDetailActivity;
import ar.com.fennoma.davipocket.model.Store;
import ar.com.fennoma.davipocket.utils.ImageUtils;

public class WithoutDeliveryStoreAdapter extends RecyclerView.Adapter<WithoutDeliveryStoreHolder>{

    private Activity activity;
    private List<Store> stores;
    private LatLng latLng;

    public WithoutDeliveryStoreAdapter(Activity activity){
        this.activity = activity;
        stores = new ArrayList<>();
    }

    public void setLatLng(LatLng latLng){
        this.latLng = latLng;
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
        final Store store = stores.get(position);
        holder.name.setText(store.getName());
        if(store.getImage() != null && store.getImage().length() > 0) {
            ImageUtils.loadImageFullURL(holder.imageView, store.getImage());
        } else {
            holder.imageView.setImageResource(R.drawable.placeholder);
        }
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(StoreDetailActivity.STORE_KEY, store);
                activity.startActivity(new Intent(activity, StoreDetailActivity.class).putExtras(bundle));
            }
        });
        if(store.getLogo() != null && store.getLogo().length() > 0) {
            ImageUtils.loadImageFullURL(holder.brandLogo, store.getLogo());
        } else {
            holder.brandLogo.setImageResource(R.drawable.placeholder_small);
        }
        if(TextUtils.isEmpty(store.getAddress())) {
            holder.address.setVisibility(View.GONE);
        } else {
            holder.address.setVisibility(View.VISIBLE);
            holder.address.setText(store.getAddress());
        }
        if(store.getLatitude() == null || store.getLongitude() == null || latLng == null) {
            holder.distance.setText(activity.getString(R.string.delivery_adapter_not_available_distance));
        } else {
            holder.distance.setText(calculateDistance(store.getLatitude(), store.getLongitude()));
        }
    }

    private String calculateDistance(Double latitude, Double longitude) {
        Location locationA = new Location("Location A");
        locationA.setLatitude(latitude);
        locationA.setLatitude(longitude);
        Location locationB = new Location("Location B");
        locationB.setLatitude(latLng.latitude);
        locationB.setLongitude(latLng.longitude);
        float distance = locationA.distanceTo(locationB);
        double rounded = Math.round(distance * 10 ) / 10f;
        String finalResult = String.valueOf(rounded).replace(".", ",");
        return finalResult.concat(" ").concat(activity.getString(R.string.delivery_adapter_km_indicator));
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }
}

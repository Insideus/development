package com.davivienda.billetera.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import com.davivienda.billetera.R;
import com.davivienda.billetera.activities.StoreDetailActivity;
import com.davivienda.billetera.model.Store;
import com.davivienda.billetera.utils.ImageUtils;
import com.davivienda.billetera.utils.LocationUtils;

public class WithoutDeliveryStoreAdapter extends RecyclerView.Adapter<WithoutDeliveryStoreHolder>{

    private Activity activity;
    private List<Store> allStores;
    private ArrayList<Store> stores;
    private LatLng latLng;

    public WithoutDeliveryStoreAdapter(Activity activity){
        this.activity = activity;
        allStores = new ArrayList<>();
        stores = new ArrayList<>();
    }

    public void setLatLng(LatLng latLng){
        this.latLng = latLng;
    }

    public void setStores(List<Store> stores){
        this.allStores = stores;
        this.stores.clear();
        this.stores.addAll(allStores);
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
        holder.distance.setText(LocationUtils.calculateDistance(activity, latLng, store.getLatitude(), store.getLongitude()));
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }

    public void doQuery(String newText) {
        List<Store> toShow = new ArrayList<>();
        for(Store store : allStores){
            if(store != null && store.getName().toLowerCase().contains(newText.toLowerCase())){
                toShow.add(store);
            }
        }
        stores.clear();
        stores.addAll(toShow);
        notifyDataSetChanged();
    }

    public void showAllResults() {
        stores.clear();
        stores.addAll(allStores);
        notifyDataSetChanged();
    }
}

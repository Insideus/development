package com.davivienda.billetera.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davivienda.billetera.R;
import com.davivienda.billetera.activities.OtpPaymentActivity;
import com.davivienda.billetera.model.Store;
import com.davivienda.billetera.utils.LocationUtils;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class OtpStoreListAdapter extends RecyclerView.Adapter<OtpStoreHolder>{

    private final LatLng latLng;
    private List<Store> stores;
    private Activity activity;

    public OtpStoreListAdapter(Activity activity, LatLng latLng) {
        this.activity = activity;
        this.latLng = latLng;
        this.stores = new ArrayList<>();
    }

    @Override
    public OtpStoreHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OtpStoreHolder(LayoutInflater.from(activity).inflate(R.layout.otp_store_item, parent, false));
    }

    @Override
    public void onBindViewHolder(OtpStoreHolder holder, int position) {
        final Store store = stores.get(position);
        holder.name.setText(store.getName());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, OtpPaymentActivity.class));
            }
        });
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

    public void setStores(List<Store> stores) {
        this.stores = stores;
        notifyDataSetChanged();
    }
}

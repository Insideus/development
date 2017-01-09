package com.davivienda.billetera.ui.controls;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.davivienda.billetera.R;
import com.davivienda.billetera.activities.OtpPaymentActivity;
import com.davivienda.billetera.activities.StoreDetailActivity;
import com.davivienda.billetera.model.Store;
import com.davivienda.billetera.utils.ImageUtils;
import com.davivienda.billetera.utils.LocationUtils;
import com.google.android.gms.maps.model.LatLng;

import static com.davivienda.billetera.activities.BaseActivity.STORE_KEY;

public class MapMarkerInfoShower {

    private final View viewToShow;
    private Activity activity;
    private boolean fromHome;
    private Store store;
    private LatLng latLng;

    public MapMarkerInfoShower(Activity activity, LatLng latLng, View viewToShow, boolean fromHome){
        this.activity = activity;
        this.fromHome = fromHome;
        this.viewToShow = viewToShow;
        this.latLng = latLng;
        viewToShow.setOnClickListener(new OnMarkerInfoClicked());
    }

    public void setStore(Store store){
        this.store = store;
        ImageView brandLogo = (ImageView) viewToShow.findViewById(R.id.brand_logo);
        if(fromHome){
            if(store.getLogo() != null && store.getLogo().length() > 0) {
                ImageUtils.loadImageFullURL(brandLogo, store.getLogo());
            } else {
                brandLogo.setImageResource(R.drawable.placeholder_small);
            }
        }else{
            brandLogo.setImageResource(R.drawable.otp_store_item_holder);
        }
        ((TextView) viewToShow.findViewById(R.id.name)).setText(store.getName());
        TextView address = (TextView) viewToShow.findViewById(R.id.address);
        if(TextUtils.isEmpty(store.getAddress())) {
            address.setVisibility(View.GONE);
        } else {
            address.setVisibility(View.VISIBLE);
            address.setText(store.getAddress());
        }
        ((TextView) viewToShow.findViewById(R.id.distance)).setText(LocationUtils.calculateDistance(activity,
                latLng, store.getLatitude(), store.getLongitude()));
    }

    private class OnMarkerInfoClicked implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if(store == null){
                return;
            }
            Intent in;
            if(fromHome) {
                in = new Intent(activity, StoreDetailActivity.class);
            } else {
                in = new Intent(activity, OtpPaymentActivity.class);
            }
            in.putExtra(STORE_KEY, store);
            activity.startActivity(in);
        }
    }
}

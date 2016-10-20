package ar.com.fennoma.davipocket.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.android.gms.maps.model.LatLng;

import ar.com.fennoma.davipocket.fragments.DeliveryStoreFragment;
import ar.com.fennoma.davipocket.fragments.WithoutDeliveryStoreFragment;

public class HomePagerAdapter extends FragmentStatePagerAdapter {

    private int tabCount;
    private WithoutDeliveryStoreFragment withoutDeliveryStoreFragment;
    private DeliveryStoreFragment deliveryStoreFragment;

    public HomePagerAdapter(FragmentManager supportFragmentManager, int tabCount) {
        super(supportFragmentManager);
        this.tabCount = tabCount;
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:{
                withoutDeliveryStoreFragment = new WithoutDeliveryStoreFragment();
                return withoutDeliveryStoreFragment;
            }
            case 1:{
                deliveryStoreFragment = new DeliveryStoreFragment();
                return deliveryStoreFragment;
            }
        }
        return null;
    }

    public void setLocation(LatLng latLng){
        if(withoutDeliveryStoreFragment != null){
            withoutDeliveryStoreFragment.setLocation(latLng);
        }
        if(deliveryStoreFragment != null){
            deliveryStoreFragment.setLocation(latLng);
        }
    }
}

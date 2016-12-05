package com.davivienda.billetera.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import com.davivienda.billetera.R;
import com.davivienda.billetera.activities.BaseActivity;
import com.davivienda.billetera.model.ServiceException;
import com.davivienda.billetera.model.Store;
import com.davivienda.billetera.service.Service;
import com.davivienda.billetera.session.Session;
import com.davivienda.billetera.tasks.DaviPayTask;
import com.davivienda.billetera.ui.adapters.WithoutDeliveryStoreAdapter;

public class DeliveryStoreFragment extends Fragment {

    private LatLng latLng;
    private WithoutDeliveryStoreAdapter adapter;

    public void setLocation(LatLng latLng) {
        this.latLng = latLng;
        adapter.setLatLng(latLng);
        new GetStoresTask((BaseActivity) getActivity()).execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_delivery_store, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new WithoutDeliveryStoreAdapter(getActivity());
        recyclerView.setAdapter(adapter);
    }

    private class GetStoresTask extends DaviPayTask<List<Store>> {

        public GetStoresTask(BaseActivity activity) {
            super(activity);
        }

        @Override
        protected List<Store> doInBackground(Void... params) {
            List<Store> stores = null;
            String sid = Session.getCurrentSession(getActivity().getApplicationContext()).getSid();
            try {
                stores = Service.getStoresWithoutDelivery(sid,
                        latLng != null ? String.valueOf(latLng.latitude) : "",
                        latLng != null ? String.valueOf(latLng.longitude) : "");
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            return stores;
        }

        @Override
        protected void onPostExecute(List<Store> stores) {
            super.onPostExecute(stores);
            if(!processedError) {
                adapter.setStores(stores);
            }
        }

    }

}

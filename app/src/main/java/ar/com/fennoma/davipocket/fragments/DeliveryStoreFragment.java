package ar.com.fennoma.davipocket.fragments;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.Store;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.ui.adapters.WithoutDeliveryStoreAdapter;

public class DeliveryStoreFragment extends Fragment {

    private Location location;
    private WithoutDeliveryStoreAdapter adapter;

    public void setLocation(Location location) {
        this.location = location;
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
        new GetStoresTask().execute();
    }

    private class GetStoresTask extends AsyncTask<Void, Void, Void> {

        private List<Store> stores;

        @Override
        protected Void doInBackground(Void... params) {
            stores = Store.fromJsonArray(Service.getMOCKEDStoresWithoutDelivery());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(stores == null){
                return;
            }
            adapter.setStores(stores);
        }
    }
}

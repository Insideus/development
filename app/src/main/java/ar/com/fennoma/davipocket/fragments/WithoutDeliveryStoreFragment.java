package ar.com.fennoma.davipocket.fragments;

import android.os.AsyncTask;
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

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.model.Store;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.ui.adapters.WithoutDeliveryStoreAdapter;

public class WithoutDeliveryStoreFragment extends Fragment {

    private LatLng latLng;
    private WithoutDeliveryStoreAdapter adapter;

    public void setLocation(LatLng latLng) {
        this.latLng = latLng;
        adapter.setLatLng(latLng);
        new GetStoresTask().execute();
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

    private class GetStoresTask extends AsyncTask<Void, Void, Void>{

        private List<Store> stores;

        @Override
        protected Void doInBackground(Void... params) {
            String sid = Session.getCurrentSession(getActivity().getApplicationContext()).getSid();
            try {
                stores = Service.getStoresWithoutDelivery(sid,
                        latLng != null ? String.valueOf(latLng.latitude) : "",
                        latLng != null ? String.valueOf(latLng.longitude) : "");
            } catch (ServiceException e) {
                e.printStackTrace();
            }
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

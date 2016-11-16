package ar.com.fennoma.davipocket.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.Cart;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.ui.adapters.MyOrdersAdapter;

public class MyOrdersActivity extends BaseActivity {

    private MyOrdersAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_shops_activity);
        setToolbar(R.id.toolbar, false, getString(R.string.my_shops_title));
        setRecycler();
        new GetOrderTask().execute();
    }

    private void setRecycler() {
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        if(recycler == null){
            return;
        }
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyOrdersAdapter(this);
        recycler.setAdapter(adapter);
    }

    private class GetOrderTask extends AsyncTask<Void, Void, ArrayList<Cart>> {

        String errorCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected ArrayList<Cart> doInBackground(Void... voids) {
            ArrayList<Cart> ordersList = null;
            try {
                ordersList = Service.getOrders(Session.getCurrentSession(getApplicationContext()).getSid());
            } catch (ServiceException e) {
                e.printStackTrace();
                errorCode = e.getErrorCode();
            }
            return ordersList;
        }

        @Override
        protected void onPostExecute(ArrayList<Cart> orders) {
            super.onPostExecute(orders);
            hideLoading();
            if (orders == null) {
                //Hancdle invalid session error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                if (error != null && error == ErrorMessages.INVALID_SESSION) {
                    handleInvalidSessionError();
                } else {
                    showServiceGenericError();
                }
            } else {
                adapter.setOrdersList(orders);
            }
        }

    }

}
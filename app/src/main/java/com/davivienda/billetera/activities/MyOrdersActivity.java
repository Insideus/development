package com.davivienda.billetera.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import com.davivienda.billetera.R;
import com.davivienda.billetera.model.Cart;
import com.davivienda.billetera.model.ServiceException;
import com.davivienda.billetera.service.Service;
import com.davivienda.billetera.session.Session;
import com.davivienda.billetera.tasks.DaviPayTask;
import com.davivienda.billetera.ui.adapters.MyOrdersAdapter;

public class MyOrdersActivity extends BaseActivity {

    private MyOrdersAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_shops_activity);
        setToolbar(R.id.toolbar, false, getString(R.string.my_shops_title));
        setRecycler();
        new GetOrderTask(this).execute();
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

    private class GetOrderTask extends DaviPayTask<ArrayList<Cart>> {

        public GetOrderTask(BaseActivity activity) {
            super(activity);
        }

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
            if(!processedError) {
                adapter.setOrdersList(orders);
            }
        }

    }

}

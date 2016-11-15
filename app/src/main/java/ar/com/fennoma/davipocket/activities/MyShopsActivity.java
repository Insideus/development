package ar.com.fennoma.davipocket.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.Cart;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.ui.adapters.MyShopsAdapter;

public class MyShopsActivity extends BaseActivity {

    private MyShopsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_shops_activity);
        setToolbar(R.id.toolbar, false, getString(R.string.my_shops_title));
        setRecycler();
        showLoading();
        new GetOrderTask().execute();
    }

    private void setRecycler() {
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        if(recycler == null){
            return;
        }
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyShopsAdapter(this);
        recycler.setAdapter(adapter);
    }

    private class GetOrderTask extends AsyncTask<Object, Object, Void> {
        @Override
        protected Void doInBackground(Object... voids) {
            try {
                final ArrayList<Cart> ordersList = Service.getOrders(Session.getCurrentSession(getApplicationContext()).getSid());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setOrdersList(ordersList);
                    }
                });
            } catch (ServiceException e) {
                e.printStackTrace();
            }

            hideLoading();
            return null;
        }
    }
}

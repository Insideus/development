package com.davivienda.billetera.activities;

import android.app.SearchManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

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

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.order_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getString(R.string.my_orders_search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    adapter.doQuery(newText);
                }else{
                    adapter.showAllResults();
                }
                return false;
            }
        });
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                setItemsVisibility(menu, searchItem, false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                setItemsVisibility(menu, searchItem, true);
                return true;
            }
        });

        return true;
    }

    private void setItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i=0; i<menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception) item.setVisible(visible);
        }
    }

}

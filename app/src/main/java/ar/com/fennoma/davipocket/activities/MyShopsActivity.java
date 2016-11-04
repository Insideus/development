package ar.com.fennoma.davipocket.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.Card;
import ar.com.fennoma.davipocket.model.CardBin;
import ar.com.fennoma.davipocket.model.Cart;
import ar.com.fennoma.davipocket.model.Store;
import ar.com.fennoma.davipocket.model.StoreProduct;
import ar.com.fennoma.davipocket.ui.adapters.MyShopsAdapter;

public class MyShopsActivity extends BaseActivity {

    private MyShopsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_shops_activity);
        setToolbar(R.id.toolbar, false, getString(R.string.my_shops_title));
        setRecycler();
        hardcodedShops();
    }

    private void hardcodedShops() {
        Store store = new Store();
        store.setName("Juan Valdez Prueba");
        store.setLogo("http://i.imgur.com/W0hA7DP.png");
        Card card = new Card();
        CardBin cardBin = new CardBin();
        cardBin.setImage("/uploads/images/master_clasica.png");
        card.setBin(cardBin);
        card.setLastDigits("3787");
        List<Cart> shops = new ArrayList<>();
        Cart cart = new Cart();
        cart.setStore(store);
        cart.setSelectedCard(card);
        ArrayList<StoreProduct> products = new ArrayList<>();
        StoreProduct product = new StoreProduct();
        product.setAppDisplayName("Flat White");
        product.setListPrice(6800d);
        product.setCurrencyPrice(6800d);
        products.add(product);
        product = new StoreProduct();
        product.setAppDisplayName("Espresso");
        product.setListPrice(7200d);
        product.setCurrencyPrice(7200d);
        products.add(product);
        cart.setCartPrice(12000d);
        cart.setCartDavipoints(80);
        cart.setProducts(products);
        cart.setDeliveredTo("APTO MAMÁ");
        cart.setStarsGiven(5);
        cart.setComment("Llegó a destino a tiempo :D");
        shops.add(cart);
        cart = new Cart();
        cart.setStore(store);
        cart.setSelectedCard(card);
        products = new ArrayList<>();
        product = new StoreProduct();
        product.setAppDisplayName("Flat White");
        product.setListPrice(6800d);
        product.setCurrencyPrice(6800d);
        products.add(product);
        product = new StoreProduct();
        product.setAppDisplayName("Espresso");
        product.setListPrice(7200d);
        product.setCurrencyPrice(7200d);
        products.add(product);
        cart.setProducts(products);
        cart.setCartPrice(12000d);
        cart.setCartDavipoints(80);
        cart.setStarsGiven(3);
        shops.add(cart);
        adapter.setShops(shops);
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



}

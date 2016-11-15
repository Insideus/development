package ar.com.fennoma.davipocket.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.activities.OrderReceiptActivity;
import ar.com.fennoma.davipocket.model.Cart;
import ar.com.fennoma.davipocket.model.MyShopHolder;
import ar.com.fennoma.davipocket.utils.CurrencyUtils;
import ar.com.fennoma.davipocket.utils.DavipointUtils;
import ar.com.fennoma.davipocket.utils.ImageUtils;

public class MyShopsAdapter extends RecyclerView.Adapter<MyShopHolder>{

    private Activity activity;
    private List<Cart> shops;

    public MyShopsAdapter(Activity activity){
        this.activity = activity;
        this.shops = new ArrayList<>();
    }

    @Override
    public MyShopHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyShopHolder(activity.getLayoutInflater().inflate(R.layout.my_shops_shop_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyShopHolder holder, int position) {
        final Cart cart = shops.get(position);
        if(cart.getStore().getLogo() != null && cart.getStore().getLogo().length() > 0) {
            ImageUtils.loadImageFullURL(holder.brandLogo, cart.getStore().getLogo());
        } else {
            holder.brandLogo.setImageResource(R.drawable.placeholder_small);
        }

//        Hidden by definition
//        if(TextUtils.isEmpty(cart.getDeliveredTo())){
//            holder.deliveryContainer.setVisibility(View.GONE);
//        }else{
//            holder.deliveryContainer.setVisibility(View.VISIBLE);
//            holder.deliveredTo.setText(cart.getDeliveredTo());
//        }

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(OrderReceiptActivity.CART_KEY, cart);
                bundle.putBoolean(OrderReceiptActivity.FROM_MADE_SHOP, true);
                activity.startActivity(new Intent(activity, OrderReceiptActivity.class).putExtras(bundle));
            }
        });
        holder.daviPrice.setText(String.valueOf(cart.getCartDavipoints()));
        Double cashAmount = DavipointUtils.cashDifference(cart.getCartPrice(), cart.getCartDavipoints());
        holder.cashPrice.setText("$".concat(CurrencyUtils.getCurrencyForString(cashAmount)));
        holder.brandName.setText(cart.getStore().getName());
        holder.totalPrice.setText("$".concat(CurrencyUtils.getCurrencyForString(cart.getCartPrice())));
    }

    @Override
    public int getItemCount() {
        return shops.size();
    }

    public void setShops(List<Cart> shops) {
        this.shops = shops;
        notifyDataSetChanged();
    }
}

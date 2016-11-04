package ar.com.fennoma.davipocket.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import ar.com.fennoma.davipocket.activities.StoreReceiptActivity;
import ar.com.fennoma.davipocket.model.Cart;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.MyShopHolder;

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
        if(TextUtils.isEmpty(cart.getDeliveredTo())){
            holder.deliveryContainer.setVisibility(View.GONE);
        }else{
            holder.deliveryContainer.setVisibility(View.VISIBLE);
            holder.deliveredTo.setText(cart.getDeliveredTo());
        }
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(StoreReceiptActivity.CART_KEY, cart);
                bundle.putBoolean(StoreReceiptActivity.FROM_MADE_SHOP, true);
                activity.startActivity(new Intent(activity, StoreReceiptActivity.class).putExtras(bundle));
            }
        });
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

package ar.com.fennoma.davipocket.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.activities.OrderReceiptActivity;
import ar.com.fennoma.davipocket.model.Cart;
import ar.com.fennoma.davipocket.model.MyShopHolder;
import ar.com.fennoma.davipocket.utils.CurrencyUtils;
import ar.com.fennoma.davipocket.utils.DateUtils;
import ar.com.fennoma.davipocket.utils.DavipointUtils;
import ar.com.fennoma.davipocket.utils.ImageUtils;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyShopHolder>{

    private Context context;
    private List<Cart> ordersList;

    public MyOrdersAdapter(Context context){
        this.context = context;
        this.ordersList = new ArrayList<>();
    }

    @Override
    public MyShopHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyShopHolder(LayoutInflater.from(context).inflate(R.layout.my_shops_shop_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyShopHolder holder, int position) {
        final Cart curCart = ordersList.get(position);
        if(curCart.getStore().getLogo() != null && curCart.getStore().getLogo().length() > 0) {
            ImageUtils.loadImageFullURL(holder.brandLogo, curCart.getStore().getLogo());
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
                bundle.putParcelable(OrderReceiptActivity.CART_KEY, curCart);
                bundle.putBoolean(OrderReceiptActivity.FROM_MADE_SHOP, true);
                context.startActivity(new Intent(context, OrderReceiptActivity.class).putExtras(bundle));
            }
        });

        holder.daviPrice.setText(String.valueOf(curCart.getCartDavipoints()));
        Double cashAmount = DavipointUtils.cashDifference(curCart.getCartPrice(), curCart.getCartDavipoints());
        holder.cashPrice.setText("$".concat(CurrencyUtils.getCurrencyForString(cashAmount)));
        holder.brandName.setText(curCart.getStore().getName());
        holder.totalPrice.setText("$".concat(CurrencyUtils.getCurrencyForString(curCart.getCartPrice())));
        holder.buyDate.setText(DateUtils.formatDate(DateUtils.DEFAULT_FORMAT, DateUtils.DOTTED_DDMMMMYYHHMM_FORMAT, curCart.getDate()));
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public void setOrdersList(List<Cart> ordersList) {
        this.ordersList = ordersList;
        notifyDataSetChanged();
    }
}

package ar.com.fennoma.davipocket.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ar.com.fennoma.davipocket.R;

public class MyShopHolder extends RecyclerView.ViewHolder {

    public View container;
    public ImageView brandLogo;
    public TextView daviPrice;
    public TextView cashPrice;
    public TextView totalPrice;
    public TextView brandName;
    public TextView buyDate;
    public View deliveryContainer;
    public TextView deliveredTo;


    public MyShopHolder(View itemView) {
        super(itemView);
        container = itemView.findViewById(R.id.container);
        brandLogo = (ImageView) itemView.findViewById(R.id.brand_logo);
        daviPrice = (TextView) itemView.findViewById(R.id.davi_price);
        cashPrice = (TextView) itemView.findViewById(R.id.cash_price);
        totalPrice = (TextView) itemView.findViewById(R.id.total_price);
        brandName = (TextView) itemView.findViewById(R.id.brand_name);
        buyDate = (TextView) itemView.findViewById(R.id.buy_date);
        deliveryContainer = itemView.findViewById(R.id.delivery_container);
        deliveredTo = (TextView) itemView.findViewById(R.id.delivered_to);
    }
}

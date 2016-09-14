package ar.com.fennoma.davipocket.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ar.com.fennoma.davipocket.R;

/**
 * Created by fennoma_dev on 14/09/2016.
 */
public class TransactionHolder extends RecyclerView.ViewHolder {

    protected View container;
    protected TextView name;
    //protected TextView productAmount;
    protected TextView price;
    //protected TextView daviPoints;

    public TransactionHolder(View itemView) {
        super(itemView);
        container = itemView.findViewById(R.id.container);
        name = (TextView) itemView.findViewById(R.id.name);
        //productAmount = (TextView) itemView.findViewById(R.id.product_amount);
        price = (TextView) itemView.findViewById(R.id.price);
        //daviPoints = (TextView) itemView.findViewById(R.id.davipoints);
    }
}

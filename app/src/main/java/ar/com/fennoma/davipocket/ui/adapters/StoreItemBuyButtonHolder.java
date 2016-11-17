package ar.com.fennoma.davipocket.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import ar.com.fennoma.davipocket.R;

public class StoreItemBuyButtonHolder extends RecyclerView.ViewHolder {
    public View button;
    public StoreItemBuyButtonHolder(View itemView) {
        super(itemView);
        button = itemView.findViewById(R.id.buy_button);
    }
}

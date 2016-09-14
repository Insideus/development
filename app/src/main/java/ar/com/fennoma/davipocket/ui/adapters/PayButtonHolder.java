package ar.com.fennoma.davipocket.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import ar.com.fennoma.davipocket.R;

/**
 * Created by fennoma_dev on 14/09/2016.
 */
public class PayButtonHolder extends RecyclerView.ViewHolder {

    protected View loadMoreButton;
    protected View payButton;

    public PayButtonHolder(View itemView) {
        super(itemView);
        payButton = itemView.findViewById(R.id.pay_button);
        loadMoreButton = itemView.findViewById(R.id.load_more_button);
    }
}

package ar.com.fennoma.davipocket.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ar.com.fennoma.davipocket.R;

public class StoreItemDetailHolder extends RecyclerView.ViewHolder {

    protected TextView title;
    protected ViewGroup container;

    public StoreItemDetailHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title);
        container = (ViewGroup) itemView.findViewById(R.id.container);
    }
}

package ar.com.fennoma.davipocket.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ar.com.fennoma.davipocket.R;

public class WithoutDeliveryStoreHolder extends RecyclerView.ViewHolder {
    protected ImageView imageView;
    protected ImageView brandLogo;
    protected TextView name;
    protected TextView address;
    protected TextView distance;

    public WithoutDeliveryStoreHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.image);
        brandLogo = (ImageView) itemView.findViewById(R.id.brand_logo);
        name = (TextView) itemView.findViewById(R.id.name);
        address = (TextView) itemView.findViewById(R.id.address);
        distance = (TextView) itemView.findViewById(R.id.distance);
    }
}

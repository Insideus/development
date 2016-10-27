package ar.com.fennoma.davipocket.ui.adapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.StoreConfiguration;
import ar.com.fennoma.davipocket.model.StoreConfigurationItem;
import ar.com.fennoma.davipocket.model.StoreProduct;
import ar.com.fennoma.davipocket.utils.CurrencyUtils;

public class StoreItemDetailAdapter extends RecyclerView.Adapter<StoreItemDetailHolder> {

    private Activity activity;
    private List<StoreConfiguration> itemsToShow;

    public StoreItemDetailAdapter(Activity activity, StoreProduct product) {
        this.activity = activity;
        this.itemsToShow = product.getConfigurations();
    }

    @Override
    public StoreItemDetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StoreItemDetailHolder(activity.getLayoutInflater().inflate(R.layout.store_item_detail_with_title, parent, false));
    }

    @Override
    public void onBindViewHolder(StoreItemDetailHolder holder, int position) {
        final StoreConfiguration configuration = itemsToShow.get(position);
        holder.title.setText(configuration.getSubType());
        for (StoreConfigurationItem configurationItem : configuration.getConfigurations()) {
            View selectableRow = activity.getLayoutInflater().inflate(R.layout.store_item_detail_selectable_item, null);
            final View container = selectableRow.findViewById(R.id.container);
            TextView title = (TextView) selectableRow.findViewById(R.id.title);
            TextView price = (TextView) selectableRow.findViewById(R.id.price);
            View bottomSeparator = selectableRow.findViewById(R.id.bottom_separator);
            if(configuration.getConfigurations().indexOf(configurationItem) + 1 == configuration.getConfigurations().size()) {
                container.setBackground(ContextCompat.getDrawable(activity, R.drawable.white_shape_rounded_bottom_corners));
                bottomSeparator.setVisibility(View.GONE);
            } else {
                container.setBackground(ContextCompat.getDrawable(activity, R.drawable.white_background));
                bottomSeparator.setVisibility(View.VISIBLE);
            }
            title.setText(configurationItem.getName());
            if (configurationItem.getExtraPrice() == null || configurationItem.getExtraPrice() == 0) {
                price.setVisibility(View.GONE);
            } else {
                price.setVisibility(View.VISIBLE);
                price.setText(CurrencyUtils.getCurrencyForString(String.valueOf(configurationItem.getExtraPrice())));
            }
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    container.setSelected(!container.isSelected());
                }
            });
            holder.container.addView(selectableRow);
        }
    }

    @Override
    public int getItemCount() {
        return itemsToShow.size();
    }
}

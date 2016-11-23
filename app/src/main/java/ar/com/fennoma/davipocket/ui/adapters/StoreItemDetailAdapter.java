package ar.com.fennoma.davipocket.ui.adapters;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.activities.StoreItemDetailActivity;
import ar.com.fennoma.davipocket.model.IShowableItem;
import ar.com.fennoma.davipocket.model.StoreConfiguration;
import ar.com.fennoma.davipocket.model.StoreConfigurationItem;
import ar.com.fennoma.davipocket.model.StoreProduct;
import ar.com.fennoma.davipocket.model.TransactionPayButton;
import ar.com.fennoma.davipocket.utils.CurrencyUtils;

public class StoreItemDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private StoreItemDetailActivity activity;
    private List<IShowableItem> itemsToShow;

    public StoreItemDetailAdapter(StoreItemDetailActivity activity, StoreProduct product) {
        this.activity = activity;
        this.itemsToShow = new ArrayList<>();
        this.itemsToShow.addAll(product.getConfigurations());
        this.itemsToShow.add(new TransactionPayButton());
    }

    @Override
    public int getItemViewType(int position) {
        return itemsToShow.get(position).getKindOfItem();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == IShowableItem.STORE_CONFIGURATION) {
            return new StoreItemDetailHolder(activity.getLayoutInflater().inflate(R.layout.store_item_detail_with_title, parent, false));
        }else if(viewType == IShowableItem.BUTTON){
            return new StoreItemBuyButtonHolder(activity.getLayoutInflater().inflate(R.layout.store_item_buy_button, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder genericHolder, int position) {
        switch (getItemViewType(position)){
            case IShowableItem.STORE_CONFIGURATION:{
                final StoreConfiguration configuration = (StoreConfiguration) itemsToShow.get(position);
                StoreItemDetailHolder holder = (StoreItemDetailHolder) genericHolder;
                holder.title.setText(configuration.getSubType());
                Resources res = activity.getResources();
                String text = "";
                if(configuration.getMinConfiguration() == configuration.getMaxConfiguration()) {
                    text = res.getQuantityString(R.plurals.configuration_quantity_required_text, configuration.getMaxConfiguration(), configuration.getMaxConfiguration());
                    holder.quantity.setText(text);
                } else {
                    text = res.getString(R.string.configuration_quantity_text, configuration.getMinConfiguration(), configuration.getMaxConfiguration());
                    holder.quantity.setText(text);
                }
                if(holder.container.getChildCount() > 1) {
                    holder.container.removeViews(1, holder.container.getChildCount() - 1);
                }
                for (StoreConfigurationItem configurationItem : configuration.getConfigurations()) {
                    View selectableRow = activity.getLayoutInflater().inflate(R.layout.store_item_detail_selectable_item, null);
                    final View container = selectableRow.findViewById(R.id.container);
                    TextView title = (TextView) selectableRow.findViewById(R.id.title);
                    TextView price = (TextView) selectableRow.findViewById(R.id.price);
                    View bottomSeparator = selectableRow.findViewById(R.id.bottom_separator);
                    if (configuration.getConfigurations().indexOf(configurationItem) + 1 == configuration.getConfigurations().size()) {
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
                        String itemPrice = ("$ ").concat(CurrencyUtils.getCurrencyForString(configurationItem.getExtraPrice()));
                        price.setText(itemPrice);
                    }
                    if (activity.isConfigurationAdded(configuration, configurationItem)) {
                        container.setSelected(true);
                    } else {
                        container.setSelected(false);
                    }
                    container.setOnClickListener(getSelectedItemListener(container, configuration, configurationItem, position));
                    holder.container.addView(selectableRow);
                }
                break;
            }
            case IShowableItem.BUTTON:{
                StoreItemBuyButtonHolder holder = (StoreItemBuyButtonHolder) genericHolder;
                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.validateProductAndAddToCart();
                    }
                });
            }
        }
    }

    @NonNull
    private View.OnClickListener getSelectedItemListener(final View container, final StoreConfiguration configuration, final StoreConfigurationItem configurationItem, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Remove item
                Log.d("POSITION", String.valueOf(position) + " " + configurationItem.getName());
                if(container.isSelected()) {
                    if(activity.removeConfiguration(configuration, configurationItem)) {
                        container.setSelected(false);
                        activity.setProductAmount();
                        notifyItemChanged(position);
                    } else {
                        //Show error?
                    }
                //Add item.
                } else {
                    if(activity.addConfiguration(configuration, configurationItem)) {
                        container.setSelected(true);
                        activity.setProductAmount();
                        notifyItemChanged(position);
                    } else {
                        //Show error?
                        activity.showConfigurationNotAdded(configuration);
                    }
                }

            }
        };
    }

    @Override
    public int getItemCount() {
        return itemsToShow.size();
    }

}

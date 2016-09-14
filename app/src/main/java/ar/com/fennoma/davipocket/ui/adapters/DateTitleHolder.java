package ar.com.fennoma.davipocket.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ar.com.fennoma.davipocket.R;

/**
 * Created by fennoma_dev on 14/09/2016.
 */
public class DateTitleHolder extends RecyclerView.ViewHolder {

    protected TextView title;

    public DateTitleHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title);
    }
}

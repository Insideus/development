package ar.com.fennoma.davipocket.ui.adapters;

import android.app.MediaRouteButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ar.com.fennoma.davipocket.R;

public class ByDayBarHolder extends RecyclerView.ViewHolder {
    protected TextView dateFromDay;
    protected TextView dateFromMonth;
    protected TextView dateFromYear;
    protected TextView dateToDay;
    protected TextView dateToMonth;
    protected TextView dateToYear;
    protected View dateToFilter;
    protected View dateFromFilter;
    protected View dateFromContainer;
    protected View dateToContainer;
    protected View filterButton;

    public ByDayBarHolder(View inflate) {
        super(inflate);
        dateFromDay = (TextView) inflate.findViewById(R.id.date_from_day);
        dateFromMonth = (TextView) inflate.findViewById(R.id.date_from_month);
        dateFromYear = (TextView) inflate.findViewById(R.id.date_from_year);
        dateToDay = (TextView) inflate.findViewById(R.id.date_to_day);
        dateToMonth = (TextView) inflate.findViewById(R.id.date_to_month);
        dateToYear = (TextView) inflate.findViewById(R.id.date_to_year);
        dateToFilter = inflate.findViewById(R.id.date_to_filter);
        dateFromFilter = inflate.findViewById(R.id.date_from_filter);
        filterButton = inflate.findViewById(R.id.filter_button);
        dateFromContainer = inflate.findViewById(R.id.date_from);
        dateToContainer = inflate.findViewById(R.id.date_to);
    }
}

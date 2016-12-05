package com.davivienda.billetera.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.viewpagerindicator.CirclePageIndicator;

import com.davivienda.billetera.R;

public class TourActivity extends BaseActivity {

    private static final int TOUR_IMAGES_AMOUNT = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tour_activity_layout);
        setActionBar(getString(R.string.tour_title), false);
        setPager();
    }

    private void setPager() {
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        if (pager == null || indicator == null) {
            return;
        }
        pager.setAdapter(new TourAdapter());
        indicator.setViewPager(pager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.close_ex, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ex_button) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class TourAdapter extends FragmentStatePagerAdapter {

        public TourAdapter() {
            super(getSupportFragmentManager());
        }

        @Override
        public int getCount() {
            return TOUR_IMAGES_AMOUNT;
        }

        @Override
        public Fragment getItem(int position) {
            PagerFragment pagerFragment = new PagerFragment();
            pagerFragment.setNumberOfPage(position);
            return pagerFragment;
        }

    }

    public static class PagerFragment extends Fragment {

        private int numberOfPage = 0;

        public void setNumberOfPage(int numberOfPage) {
            this.numberOfPage = numberOfPage;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.tour_fragment_layout, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            if (getView() == null) {
                return;
            }
            ImageView image = (ImageView) getView().findViewById(R.id.image);
            if(image == null){
                return;
            }
            image.setImageResource(getImage());
        }

        private int getImage() {
            switch (numberOfPage){
                case 0:
                    return R.drawable.tour_1;
                case 1:
                    return R.drawable.tour_2;
                case 2:
                    return R.drawable.tour_3;
            }
            return R.drawable.tour_1;
        }
    }
}

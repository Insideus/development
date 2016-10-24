package ar.com.fennoma.davipocket.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.ui.adapters.HomePagerAdapter;

public class MainActivity extends BaseActivity {

    public static final String OPEN_TOUR = "tour open";
    private HomePagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_old);
        setToolbar();
        //setPager();
        //checkForTour();
    }

    private void setToolbar(){
        setToolbar(R.id.toolbar, false, getString(R.string.main_activity_title));
        /*View toolbar = findViewById(R.id.toolbar);
        setSupportActionBar((Toolbar) toolbar);
        hideTitle();
        setNavigationDrawer(R.id.drawer_layout, R.id.toolbar, true);*/
        /*ImageView logo = (ImageView) toolbar.findViewById(R.id.toolbar_logo);
        if(logo == null){
            return;
        }
        logo.setImageResource(R.drawable.home_toolbar_logo);*/
    }

    /*private void setPager() {
        TypoTabLayout tabLayout = (TypoTabLayout) findViewById(R.id.tab_layout);
        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        if(tabLayout == null || pager == null){
            return;
        }
        tabLayout.setPager(pager)
                .setTypo("MyriadPro-Regular.otf")
                .addTitle(getString(R.string.home_store_tab_title))
                .addTitle(getString(R.string.home_domicile_tab_title))
                .build();
        adapter = new HomePagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        pager.setAdapter(adapter);
    }

    private void checkForTour() {
        if (getIntent() != null && getIntent().getBooleanExtra(OPEN_TOUR, false)) {
            startTour();
            return;
        }
        if (TextUtils.isEmpty(SharedPreferencesUtils.getString(OPEN_TOUR))) {
            startTour();
            SharedPreferencesUtils.setString(OPEN_TOUR, SharedPreferencesUtils.FALSE);
        }
    }

    private void startTour() {
        startActivity(new Intent(this, TourActivity.class));
    }

    @Override
    protected void goToHome() {
        startTour();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.maps){
            startActivity(new Intent(this, MapActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}

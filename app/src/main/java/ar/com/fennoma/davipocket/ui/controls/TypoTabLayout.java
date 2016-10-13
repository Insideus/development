package ar.com.fennoma.davipocket.ui.controls;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.DavipocketApplication;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

public class TypoTabLayout extends TabLayout {

    private ViewPager pager;
    private String typo;
    private List<String> titles;

    public TypoTabLayout(Context context) {
        super(context);
    }

    public TypoTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TypoTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TypoTabLayout setPager(ViewPager pager){
        this.pager = pager;
        return this;
    }

    public TypoTabLayout addTitle(String title){
        if(titles == null){
            titles = new ArrayList<>();
        }
        titles.add(title);
        return this;
    }

    public TypoTabLayout setTypo(String typo){
        this.typo = typo;
        return this;
    }

    public void build() {
        if(pager == null || TextUtils.isEmpty(typo) || titles == null || titles.isEmpty()){
            return;
        }
        for(String title : titles){
            addTab(newTab().setText(title));
        }
        setTabGravity(TabLayout.GRAVITY_FILL);

        ViewGroup vg = (ViewGroup) getChildAt(0);
        for(int i = 0; i < vg.getChildCount(); i++){
            changeTypographyToTabTextView((ViewGroup) vg.getChildAt(i), "fonts/".concat(typo));
        }

        setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
                ViewGroup viewGroup = (ViewGroup) TypoTabLayout.this.getChildAt(0);
                for(int i = 0; i < viewGroup.getChildCount(); i++){
                    changeTypographyToTabTextView((ViewGroup) viewGroup.getChildAt(i), "fonts/".concat(typo));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(this));
    }

    private void changeTypographyToTabTextView(ViewGroup tab, String typo) {
        for (int i = 0; i < tab.getChildCount(); i++) {
            View textView = tab.getChildAt(i);
            if (textView instanceof TextView) {
                CalligraphyUtils.applyFontToTextView(((TextView) textView),
                        TypefaceUtils.load(DavipocketApplication.getInstance().getAssets(), typo));
            }
        }
    }
}

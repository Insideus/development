package com.davivienda.billetera.ui.controls;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import com.davivienda.billetera.R;

public class RatingBar extends LinearLayout {

    private List<View> stars;
    private int amountOfStars;
    private Activity activity;

    public RatingBar(Context context) {
        super(context);
    }

    public RatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RatingBar setSelectedTill(int amount){
        if(!isLayoutReady()){
            return null;
        }
        for(int i = 0; i < amountOfStars; i++){
            stars.get(i).setSelected(i <= amount - 1);
        }
        return this;
    }

    public RatingBar setAsClickable(){
        if(!isLayoutReady()){
            return null;
        }
        for(View star : stars){
            star.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int limit = stars.indexOf(v);
                    for(int i = 0; i < amountOfStars; i++){
                        stars.get(i).setSelected(i <= limit);
                    }
                }
            });
        }
        return this;
    }

    public RatingBar setActivity(Activity activity){
        this.activity = activity;
        return this;
    }

    public RatingBar setAmountOfStars(int amountOfStars) {
        this.amountOfStars = amountOfStars;
        stars = new ArrayList<>();
        if(activity == null){
            return this;
        }
        for(int i = 1; i <= amountOfStars; i++){
            View star = createStar();
            stars.add(star);
            this.addView(star);
        }
        return this;
    }

    private View createStar() {
        View inflate = activity.getLayoutInflater().inflate(R.layout.rating_star_layout, this, false);
        inflate.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1 / amountOfStars));
        return inflate;
    }


    private boolean isLayoutReady(){
        return stars != null && !stars.isEmpty() && stars.size() == amountOfStars;
    }

    public int getSelectedRate(){
        if(!isLayoutReady()){
            return 0;
        }
        int result = 0;
        for(View star : stars){
            if(star.isSelected()){
                result++;
            }
        }
        return result;
    }
}

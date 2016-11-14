package ar.com.fennoma.davipocket.ui.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.makeramen.roundedimageview.RoundedImageView;

import ar.com.fennoma.davipocket.R;

public class ResizableImageView extends RoundedImageView {

    private static final int PROPORTION_4__3 = -1;

    private float scale;

    public ResizableImageView(Context context) {
        super(context);
    }

    public ResizableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ResizableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ResizableImageView);
        try {
            scale = a.getFloat(R.styleable.ResizableImageView_scale, -1);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        float scaledHeight = getScaledHeight(width);
        int height = (int) Math.ceil(scaledHeight);
        setMeasuredDimension(width, height);
    }

    private float getScaledHeight(int width) {
        if(scale == 0){
            return 0;
        }
        if (scale == PROPORTION_4__3) {
            return width * 3 / 4;
        }
        return width / scale;
    }

}

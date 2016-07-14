package ar.com.fennoma.davipocket.ui.controls;

import android.content.Context;
import android.content.res.Resources;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.EditText;

public class BoxEditText extends EditText {

    private int size;

    public BoxEditText(Context context, int size) {
        super(context);
        this.size = size;
        setDefaultValues();
    }

    private void setDefaultValues() {
        setInputType();
        setMaxLenght(1);
        setGravity(Gravity.CENTER);
        setTextSize(calculateTextSize());
    }

    private float calculateTextSize() {
        float div = 5 / 8f;
        float v = size / Resources.getSystem().getDisplayMetrics().density;
        return div * v;
        //return 20;
    }

    private void setMaxLenght(int maxLenght) {
        setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLenght)});
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(size, size);
    }

    public void setBackground(int box_background_drawable) {
        if (box_background_drawable == 0) {
            return;
        }
        setBackgroundResource(box_background_drawable);
    }

    public void setInputType() {
        setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    public void setBoxTextColor(int box_textColor) {
        setTextColor(box_textColor);
    }
}

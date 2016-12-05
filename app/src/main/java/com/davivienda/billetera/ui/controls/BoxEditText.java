package com.davivienda.billetera.ui.controls;

import android.content.Context;
import android.content.res.Resources;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

import uk.co.chrisjenx.calligraphy.CalligraphyUtils;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

public class BoxEditText extends EditText {

    private int size;
    private CodeBoxContainer.IInputConnector listener;

    public BoxEditText(Context context, int size) {
        super(context);
        this.size = size;
        setDefaultValues();
    }

    private void setDefaultValues() {
        setInputType();
        setMaxLenght(1);
        setTextSize(calculateTextSize());
        setGravity(Gravity.CENTER);
        setPadding(0, 0, 0, 0);
        CalligraphyUtils.applyFontToTextView(this, TypefaceUtils.load(getContext().getAssets(), "fonts/MyriadPro_Regular.otf"));
    }

    private float calculateTextSize() {
        float proportion = 5 / 8f;
        float dpSize = size / Resources.getSystem().getDisplayMetrics().density;
        return proportion * dpSize;
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

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new BackspaceInputConnection(super.onCreateInputConnection(outAttrs),
                true);
    }

    public void setListener(CodeBoxContainer.IInputConnector listener) {
        this.listener = listener;
    }

    private class BackspaceInputConnection extends InputConnectionWrapper {

        public BackspaceInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if(listener == null){
                return super.sendKeyEvent(event);
            }
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                if(getText().length() > 0){
                    return super.sendKeyEvent(event);
                }
                listener.onBackSpacePressed();
                return true;
            }
            if(getText().length() == 1) {
                listener.onKeyPressed(event.getNumber());
                return true;
            }
            return super.sendKeyEvent(event);
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            // magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
            if (beforeLength == 1 && afterLength == 0) {
                // backspace
                return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                        && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
            }

            return super.deleteSurroundingText(beforeLength, afterLength);
        }
    }
}

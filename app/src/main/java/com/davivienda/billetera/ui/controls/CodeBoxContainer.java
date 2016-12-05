package com.davivienda.billetera.ui.controls;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Display;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.davivienda.billetera.R;

public class CodeBoxContainer extends LinearLayout {

    private List<EditText> editTexts;
    private int amount;
    private int windowsWidth;

    //Margins
    private int box_margin;
    private int box_margin_left;
    private int box_margin_top;
    private int box_margin_right;
    private int box_margin_bottom;
    private int margin_between_boxes;

    private Drawable box_background_drawable;
    private int box_textColor;
    private Runnable runnable;

    private boolean handledNumber = false;

    public CodeBoxContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CodeBoxContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CodeBoxContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if(isInEditMode()){
            return;
        }
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CodeBoxContainer);
        try {
            amount = a.getInteger(R.styleable.CodeBoxContainer_amount, 1);
            box_margin = (int) a.getDimension(R.styleable.CodeBoxContainer_box_margin, 0);
            box_margin_left = (int) a.getDimension(R.styleable.CodeBoxContainer_box_margin_left, 0);
            box_margin_top = (int) a.getDimension(R.styleable.CodeBoxContainer_box_margin_top, 0);
            box_margin_right = (int) a.getDimension(R.styleable.CodeBoxContainer_box_margin_right, 0);
            box_margin_bottom = (int) a.getDimension(R.styleable.CodeBoxContainer_box_margin_bottom, 0);
            margin_between_boxes = (int) a.getDimension(R.styleable.CodeBoxContainer_margin_between_boxes, 0);
            box_background_drawable = a.getDrawable(R.styleable.CodeBoxContainer_box_background_drawable);
            box_textColor = a.getColor(R.styleable.CodeBoxContainer_box_textColor, -1);
        } finally {
            a.recycle();
        }
        createBoxes();
        setListenersToEditTexts();
    }

    private void createBoxes() {
        editTexts = new ArrayList<>();
        calculateWindowsWidth();
        int boxSize = getBoxSize();
        for (int i = 0; i < amount; i++) {
            EditText singleBox = createSingleBox(boxSize, i);
            addView(singleBox);
            editTexts.add(singleBox);
        }
    }

    private void calculateWindowsWidth() {
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        windowsWidth = size.x;
    }

    private int getBoxSize() {
        int margins;
        if (box_margin == 0) {
            if (margin_between_boxes == 0) {
                margins = box_margin_left + box_margin_right;
            } else {
                margins = margin_between_boxes;
            }
        } else {
            margins = box_margin;
        }
        return windowsWidth / 4 - margins;
    }

    public interface IInputConnector{
        void onBackSpacePressed();
        void onKeyPressed(char key);
    }

    private EditText createSingleBox(int size, final int position) {
        BoxEditText result = new BoxEditText(getContext(), size);
        result.setBackground(box_background_drawable);
        result.setBoxTextColor(box_textColor);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        if(margin_between_boxes != 0){
            if(position != 0) {
                params.setMargins(margin_between_boxes, 0, 0, 0);
            }
        }else {
            if (box_margin == 0) {
                params.setMargins(box_margin_left, box_margin_top, box_margin_right, box_margin_bottom);
            } else {
                params.setMargins(box_margin, box_margin, box_margin, box_margin);
            }
        }
        result.setLayoutParams(params);
        result.setListener(new IInputConnector() {
            @Override
            public void onBackSpacePressed() {
                if(position == 0){
                    return;
                }
                editTexts.get(position - 1).requestFocus();
            }

            @Override
            public void onKeyPressed(char key) {
                if(position >= amount - 1){
                    return;
                }
                handledNumber = true;
                EditText editText = editTexts.get(position + 1);
                editText.setText(String.valueOf(key));
                editText.requestFocus();
            }
        });
        return result;
    }

    public void setRunnable(Runnable runnable){
        this.runnable = runnable;
    }

    private void setListenersToEditTexts() {
        for(EditText editText : editTexts){
            if(editTexts.indexOf(editText) < editTexts.size() - 1){
                editText.addTextChangedListener(new OnTextChangedGoToNextSpot(editTexts.indexOf(editText)));
            }else{
                editText.addTextChangedListener(new CodeValidator());
                editText.setOnEditorActionListener(new OnSoftKeyboardDoneButtonPressed());
            }
        }
    }

    public int size() {
        return amount;
    }

    private class OnSoftKeyboardDoneButtonPressed implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                if(runnable != null){
                    runnable.run();
                }
            }
            return false;
        }
    }

    private class OnTextChangedGoToNextSpot implements TextWatcher {
        private int position;

        OnTextChangedGoToNextSpot(int position){
            this.position = position;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(count == 1){
                editTexts.get(position + 1).requestFocus();
                return;
            }
            if(position == 0){
                return;
            }
            editTexts.get(position - 1).requestFocus();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
    }

    public String getCode(){
        String result = "";
        for(EditText codePart : editTexts){
            result = result.concat(String.valueOf(codePart.getText()));
        }
        return result;
    }

    private class CodeValidator implements TextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(count == 0){
                editTexts.get(editTexts.size() - 2).requestFocus();
                return;
            }
            if(before == 1){
                editTexts.get(editTexts.size() - 1).setSelection(1);
                return;
            }
            if(runnable != null){
                runnable.run();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
    }
}

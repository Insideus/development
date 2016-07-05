package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public class AccountActivationActivity extends BaseActivity{

    private List<EditText> codeEditTextList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activation_activity_layout);
        setActionBar(getString(R.string.account_activation_activity_title), false);
        codeEditTextList = new ArrayList<>();
        findCodeEditTexts();
        setListenersToEditTexts();
        setRequestCodeButton();
    }

    private void setRequestCodeButton() {
        View requestCode = findViewById(R.id.request_code);
        if(requestCode == null){
            return;
        }
        requestCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.toast(AccountActivationActivity.this, getString(R.string.account_activation_code_resend_text));
            }
        });
    }

    private void setListenersToEditTexts() {
        for(EditText editText : codeEditTextList){
            if(codeEditTextList.indexOf(editText) < codeEditTextList.size() - 1){
                editText.addTextChangedListener(new OnTextChangedGoToNextSpot(codeEditTextList.indexOf(editText)));
            }else{
                editText.addTextChangedListener(new CodeValidator());
                editText.setOnEditorActionListener(new OnSoftKeyboardDoneButtonPressed());
            }
        }
    }

    private void findCodeEditTexts() {
        codeEditTextList.add((EditText) findViewById(R.id.code_number_1));
        codeEditTextList.add((EditText) findViewById(R.id.code_number_2));
        codeEditTextList.add((EditText) findViewById(R.id.code_number_3));
        codeEditTextList.add((EditText) findViewById(R.id.code_number_4));
    }

    private String getCode(){
        String result = "";
        for(EditText codePart : codeEditTextList){
            result = result.concat(String.valueOf(codePart.getText()));
        }
        return result;
    }


    private class OnTextChangedGoToNextSpot implements TextWatcher {
        private int position;

        OnTextChangedGoToNextSpot(int position){
            this.position = position;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(count == 1){
                codeEditTextList.get(position + 1).requestFocus();
                return;
            }
            if(position == 0){
                return;
            }
            codeEditTextList.get(position - 1).requestFocus();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
    }

    private class CodeValidator implements TextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(count == 0){
                codeEditTextList.get(codeEditTextList.size() - 2).requestFocus();
                return;
            }
            validateCode();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
    }

    private void validateCode() {
        if(getCode().length() != codeEditTextList.size()){
            DialogUtil.toast(this, getString(R.string.account_activation_incomplete_code_error));
            return;
        }
        startActivity(new Intent(this, PolicyPickerActivity.class));
    }

    private class OnSoftKeyboardDoneButtonPressed implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                validateCode();
            }
            return false;
        }
    }
}

package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.PersonIdType;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.tasks.DaviPayTask;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public class ChangePasswordStep1Activity extends LoginBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_step_1_layout);
        setActionBar(getString(R.string.change_password_title), true);
        findFields();
        setContinueButton();
    }

    private void findFields() {
        selectedIdTypeText = (TextView) findViewById(R.id.login_id_type_text);
        personIdNumber = (EditText) findViewById(R.id.id_number);
        ArrayList<PersonIdType> personIdTypes = Session.getCurrentSession(this).getPersonIdTypes();
        if(personIdTypes != null && personIdTypes.size() > 0) {
            selectedIdType = personIdTypes.get(0);
            setSelectedIdTypeName();
        }
        selectedIdTypeText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                DialogUtil.hideKeyboard(ChangePasswordStep1Activity.this);
                showCombo();
                return false;
            }
        });
    }

    private void setContinueButton() {
        View continueButton = findViewById(R.id.send_button);
        if(continueButton == null){
            return;
        }
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(personIdNumber.getText())){
                    DialogUtil.toast(ChangePasswordStep1Activity.this,
                            getString(R.string.input_data_error_generic_title),
                            getString(R.string.input_data_error_generic_subtitle),
                            getString(R.string.change_password_step_1_empty_id_number_error));
                    return;
                }
                new ForgotPasswordTask(ChangePasswordStep1Activity.this, personIdNumber.getText().toString(),
                        String.valueOf(selectedIdType.getId())).execute();
            }
        });
    }

    public class ForgotPasswordTask extends DaviPayTask<String> {

        String personId;
        String selectedIdType;

        public ForgotPasswordTask(BaseActivity activity, String personId, String selectedIdType) {
            super(activity);
            this.personId = personId;
            this.selectedIdType = selectedIdType;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            String response = null;
            try {
                response = Service.forgotPassword(personId, selectedIdType, getTodo1Data());
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if(!processedError) {
                goToNextStep(response);
            }
        }

    }

    private void goToNextStep(String validationProduct) {
        Intent setVirtualPasswordIntent = new Intent(this, ChangePasswordStep2Activity.class);
        setVirtualPasswordIntent.putExtra(ChangePasswordStep2Activity.ID_TYPE_KEY, String.valueOf(selectedIdType.getId()));
        setVirtualPasswordIntent.putExtra(ChangePasswordStep2Activity.ID_NUMBER_KEY, personIdNumber.getText().toString());
        setVirtualPasswordIntent.putExtra(ChangePasswordStep2Activity.PRODUCT_CODE_KEY, validationProduct);
        startActivity(setVirtualPasswordIntent);
    }

}

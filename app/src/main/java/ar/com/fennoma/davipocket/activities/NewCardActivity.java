package ar.com.fennoma.davipocket.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public class NewCardActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_card_detail);
        setToolbar(R.id.toolbar_layout, false, getString(R.string.new_card_activity_title));
        setLayouts();
    }

    private void setLayouts() {
        View saveButton = findViewById(R.id.save_button);
        View discardCard = findViewById(R.id.discard_button);
        View favouriteButton = findViewById(R.id.favourite_card_indicator);
        if(saveButton == null || discardCard == null || favouriteButton == null){
            return;
        }
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInputs()) {
                    DialogUtil.toast(NewCardActivity.this, "Tarjeta Creada");
                    finish();
                }
            }
        });
        discardCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean validateInputs() {
        EditText number = (EditText) findViewById(R.id.credit_card_number);
        EditText month = (EditText)findViewById(R.id.expiration_month);
        EditText year = (EditText) findViewById(R.id.expiration_year);
        EditText cvv = (EditText) findViewById(R.id.cvv);
        EditText name = (EditText) findViewById(R.id.credit_card_name);
        if(number == null || month == null || year == null || cvv == null || name == null){
            DialogUtil.toast(this, "Hubo un error");
            return false;
        }
        List<String> errorList = new ArrayList<>();
        if(TextUtils.isEmpty(number.getText())){
            errorList.add("Debe ingresar un número de tarjeta");
        }
        if(TextUtils.isEmpty(month.getText())){
            errorList.add("Debe ingresar el mes de expiración de la tarjeta");
        } else if(month.getText().length() > 2 || Integer.valueOf(month.getText().toString()) > 12 || Integer.valueOf(month.getText().toString()) < 1) {
            errorList.add("El mes de expiración ingresado no es válido");
        }
        if(TextUtils.isEmpty(year.getText())){
            errorList.add("Debe ingresar el año de expiración de la tarjeta");
        } else if(year.getText().length() < 2 || year.getText().length() > 2){
            errorList.add("El año de expiración ingresado no es válido");
        }
        if(TextUtils.isEmpty(cvv.getText())){
            errorList.add("Debe ingresar el CVV ingresado de la tarjeta");
        } else if (cvv.getText().length() > 3 || cvv.getText().length() < 3){
            errorList.add("El CVV ingresado no es válido");
        }
        if(TextUtils.isEmpty(name.getText())){
            errorList.add("Debe ingresar el nombre del propietario de la tarjeta");
        }
        if(!errorList.isEmpty()){
            DialogUtil.toast(this, DialogUtil.concatMessages(errorList));
            return false;
        }

        return true;
    }
}

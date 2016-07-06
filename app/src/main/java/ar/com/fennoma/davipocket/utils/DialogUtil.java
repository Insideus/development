package ar.com.fennoma.davipocket.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

public class DialogUtil {

    public static void showDatePicker(Activity activity, Calendar calendar, DatePickerDialog.OnDateSetListener listener) {
        hideKeyboard(activity);
        new DatePickerDialog(activity, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public static void hideKeyboard(Activity activity){
        View currentFocus = activity.getCurrentFocus();
        if(currentFocus == null){
            return;
        }
        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
    }

    public static void toast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void toast(Context context, List<String> messages){
        Toast.makeText(context, concatMessages(messages), Toast.LENGTH_LONG).show();
    }

    public static String concatMessages(List<String> messages){
        String result = "";
        for(String message : messages){
            result = result.concat(message);
            if(messages.indexOf(message) < messages.size() - 1) {
                result = result.concat("\n");
            }
        }
        return result;
    }

}

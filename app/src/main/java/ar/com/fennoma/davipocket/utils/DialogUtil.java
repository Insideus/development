package ar.com.fennoma.davipocket.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.activities.ToastDialogActivity;

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

    public static void toast(Activity activity, String text){
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }

    public static void toast(Activity activity, String title, String subtitle, String text){
        Intent intent = new Intent(activity, ToastDialogActivity.class);
        intent.putExtra(ToastDialogActivity.TITLE_KEY, title);
        intent.putExtra(ToastDialogActivity.SUBTITLE_KEY, subtitle);
        intent.putExtra(ToastDialogActivity.TEXT_KEY, text);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
    }

    public static void toast(Activity activity, String title, String subtitle, List<String> messages){
        Intent intent = new Intent(activity, ToastDialogActivity.class);
        intent.putExtra(ToastDialogActivity.TITLE_KEY, title);
        intent.putExtra(ToastDialogActivity.SUBTITLE_KEY, subtitle);
        intent.putExtra(ToastDialogActivity.TEXT_KEY, concatMessages(messages));
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
    }

    public static void invalidSessionToast(Activity activity){
        Intent intent = new Intent(activity, ToastDialogActivity.class);
        intent.putExtra(ToastDialogActivity.TITLE_KEY, activity.getString(R.string.invalid_session_title));
        intent.putExtra(ToastDialogActivity.SUBTITLE_KEY, activity.getString(R.string.invalid_session_subtitle));
        intent.putExtra(ToastDialogActivity.TEXT_KEY, activity.getString(R.string.invalid_session_text));
        intent.putExtra(ToastDialogActivity.INVALID_SESSION_KEY, true);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
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

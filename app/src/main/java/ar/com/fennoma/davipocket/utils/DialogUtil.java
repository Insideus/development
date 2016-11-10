package ar.com.fennoma.davipocket.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.activities.LoginConfirmationActivity;
import ar.com.fennoma.davipocket.activities.ToastDialogActivity;

public class DialogUtil {

    public static final String SIX_MONTHS_AGO = "six months ago";
    public static final String FOURTEEN_YEARS_AGO = "14 years ago";
    public static final String TODAY = "today";

    public static void showDatePicker(Activity activity, Calendar calendar, DatePickerDialog.OnDateSetListener listener, String fromDate, String toDate) {
        hideKeyboard(activity);
        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, R.style.DialogTheme, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        if(!TextUtils.isEmpty(fromDate)) {
            datePickerDialog.getDatePicker().setMinDate(getLongFromDate(fromDate));
        }
        if(!TextUtils.isEmpty(toDate)){
            datePickerDialog.getDatePicker().setMaxDate(getLongFromDate(toDate));
        }
        datePickerDialog.setTitle("");
        datePickerDialog.show();
    }

    private static long getLongFromDate(String date) {
        Calendar calendar = Calendar.getInstance();
        if(date.equals(SIX_MONTHS_AGO)){
            calendar.add(Calendar.MONTH, -6);
            return calendar.getTimeInMillis();
        } else if(date.equals(FOURTEEN_YEARS_AGO)){
            calendar.add(Calendar.YEAR, -14);
            return calendar.getTimeInMillis();
        } else if(date.equals(TODAY)){
            return calendar.getTimeInMillis();
        }
        return 0;
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

    public static void toastWithResult(Activity activity, int request, String title, String subtitle, String text){
        Intent intent = new Intent(activity, ToastDialogActivity.class);
        intent.putExtra(ToastDialogActivity.TITLE_KEY, title);
        intent.putExtra(ToastDialogActivity.SUBTITLE_KEY, subtitle);
        intent.putExtra(ToastDialogActivity.TEXT_KEY, text);
        activity.startActivityForResult(intent, request);
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

    public static void showErrorAndGoToLoginActivityToast(Activity activity){
        Intent intent = new Intent(activity, ToastDialogActivity.class);
        intent.putExtra(ToastDialogActivity.TITLE_KEY, activity.getString(R.string.login_error_message_title));
        intent.putExtra(ToastDialogActivity.SUBTITLE_KEY, "");
        intent.putExtra(ToastDialogActivity.TEXT_KEY, activity.getString(R.string.login_error_message_text));
        intent.putExtra(ToastDialogActivity.INVALID_SESSION_KEY, true);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
    }

    public static void toastWithCallButton(Activity activity, String title, String subtitle, String text, String phone){
        Intent intent = new Intent(activity, ToastDialogActivity.class);
        intent.putExtra(ToastDialogActivity.TITLE_KEY, title);
        intent.putExtra(ToastDialogActivity.SUBTITLE_KEY, subtitle);
        intent.putExtra(ToastDialogActivity.TEXT_KEY, text);
        intent.putExtra(ToastDialogActivity.SHOW_CALL_BUTTON_KEY, true);
        intent.putExtra(ToastDialogActivity.CALL_BUTTON_NUMBER_KEY, phone);
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

    public static void toast(Activity activity, String title, String subtitle, String text, int request) {
        Intent intent = new Intent(activity, ToastDialogActivity.class);
        intent.putExtra(ToastDialogActivity.TITLE_KEY, title);
        intent.putExtra(ToastDialogActivity.SUBTITLE_KEY, subtitle);
        intent.putExtra(ToastDialogActivity.TEXT_KEY, text);
        activity.startActivityForResult(intent, request);
        activity.overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
    }

    public static void noConnectionDialog(Activity activity, int request) {
        Intent intent = new Intent(activity, ToastDialogActivity.class);
        intent.putExtra(ToastDialogActivity.TITLE_KEY, activity.getString(R.string.no_connection_dialog_title));
        intent.putExtra(ToastDialogActivity.SUBTITLE_KEY, activity.getString(R.string.no_connection_dialog_subtitle));
        intent.putExtra(ToastDialogActivity.TEXT_KEY, activity.getString(R.string.no_connection_dialog_text));
        intent.putExtra(ToastDialogActivity.SHOW_RETRY_CONNECTION_BUTTON, true);
        activity.startActivityForResult(intent, request);
        activity.overridePendingTransition(0, 0);
    }

    public static void showKeyBoard(Activity activity) {
        View currentFocus = activity.getCurrentFocus();
        if(currentFocus == null){
            return;
        }
        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(currentFocus, 0);
    }
}

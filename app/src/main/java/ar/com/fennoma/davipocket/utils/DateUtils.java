package ar.com.fennoma.davipocket.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static final String DDMMYY_FORMAT = "dd/MM/yy";
    public static final String DDMMYYYY_FORMAT = "dd/MM/yyyy";
    public static final String DOTTED_DDMMMYY_FORMAT = "dd · MMM · yy";
    public static final String DOTTED_DDMMMMYY_FORMAT = "dd · MMMM · yy";

    public static int getYearsFromDate(Date birthday){
        return getDiffYears(birthday, Calendar.getInstance(new Locale("es", "ES")).getTime());
    }

    public static int getDiffYears(Date first, Date last) {
        if(first == null || last == null){
            return 0;
        }
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(new Locale("es", "ES"));
        cal.setTime(date);
        return cal;
    }

    public static Date getDate(String date, String dateFormat) {
        try {
            return new SimpleDateFormat(dateFormat, new Locale("es", "ES")).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatDate(String originalFormat, String outputFormat, String originalDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(originalFormat, new Locale("es", "ES"));
        SimpleDateFormat resultFormatter = new SimpleDateFormat(outputFormat, new Locale("es", "ES"));
        String result = "";
        try{
            Date date = formatter.parse(originalDate);
            result = resultFormatter.format(date);
        }
        catch (ParseException e){
            e.printStackTrace();
            return result;
        }
        return result;
    }

    public static String formatPickerDate(int day, int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        SimpleDateFormat formatter = new SimpleDateFormat(DDMMYYYY_FORMAT);
        return formatter.format(cal.getTime());
    }

    public static String getMonthFromExpirationDate(String expirationDate) {
        if(TextUtils.isEmpty(expirationDate) || expirationDate.length() < 7){
            return "●●";
        }
        return expirationDate.substring(0, 2);
    }

    public static String getYearFromExpirationDate(String expirationDate) {
        if(TextUtils.isEmpty(expirationDate) || expirationDate.length() < 7){
            return "●●";
        }
        return expirationDate.substring(expirationDate.length() - 2, expirationDate.length());
    }
}

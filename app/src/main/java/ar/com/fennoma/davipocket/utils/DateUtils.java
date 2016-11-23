package ar.com.fennoma.davipocket.utils;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static final String DDMMYY_FORMAT = "dd/MM/yy";
    public static final String SPACED_DDMMMMYY_FORMAT = "dd - MMMM - yy";
    public static final String DDMMYYYY_FORMAT = "dd/MM/yyyy";
    public static final String DOTTED_DDMMMYY_FORMAT = "dd · MMM · yy";
    public static final String DOTTED_DDMMMMYY_FORMAT = "dd · MMMM · yy";
    public static final String DOTTED_DDMMMMYYHHMM_FORMAT = "dd · MMMM · yy / HH:mm ";
    public static final String DEFAULT_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZZZZZ";
    public static final String DOTTED_DDMMYY_FORMAT = "dd·MM·yy";

    public static int getYearsFromDate(Date birthday){
        return getDiffYears(birthday, Calendar.getInstance(Locale.getDefault()).getTime());
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
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTime(date);
        return cal;
    }

    public static Date getDate(String date, String dateFormat) {
        try {
            return new SimpleDateFormat(dateFormat, Locale.getDefault()).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatDate(String originalFormat, String outputFormat, String originalDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(originalFormat, Locale.getDefault());
        SimpleDateFormat resultFormatter = new SimpleDateFormat(outputFormat, Locale.getDefault());
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

    public static String getBuyDateFormated(Date date) {
        SimpleDateFormat resultFormatter = new SimpleDateFormat(DOTTED_DDMMMMYYHHMM_FORMAT, Locale.getDefault());
        return  resultFormatter.format(date);
    }

    public static String toCamelCase(String string) {
        String result = "";
        if(TextUtils.isEmpty(string)) {
            return result;
        }
        char toAdd;
        boolean foundLetter = false;
        for(int i = 0; i < string.length(); i++) {
            toAdd = string.charAt(i);
            if(Character.isLetter(toAdd)) {
                if(foundLetter){
                    result = result.concat(String.valueOf(toAdd).toLowerCase());
                } else {
                    foundLetter = true;
                    result = result.concat(String.valueOf(toAdd).toUpperCase());
                }
            } else {
                result = result.concat(String.valueOf(toAdd));
            }
        }
        return result;
    }

    public static String getUserLastLogin(String lastLogin){
        DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.getDefault());
        fromFormat.setLenient(false);
        DateFormat toFormat = new SimpleDateFormat("dd MMM yy / hh:mm", Locale.getDefault());
        toFormat.setLenient(false);
        try {
            Date fromDate = fromFormat.parse(lastLogin);
            String firstPart = toCamelCase(toFormat.format(fromDate));
            String lastPart;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fromDate);
            if(calendar.get(Calendar.AM_PM) == Calendar.AM) {
                lastPart = "a.m.";
            } else {
                lastPart = "p.m.";
            }
            return firstPart.concat(" ").concat(lastPart);
        } catch (Exception e) {
            e.printStackTrace();
            return "N/A";
        }
    }
}

package ar.com.fennoma.davipocket.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static final String DDMMYY_FORMAT = "dd/MM/yy";
    public static final String DOTTED_DDMMMYY_FORMAT = "dd · MM · yy";

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

}

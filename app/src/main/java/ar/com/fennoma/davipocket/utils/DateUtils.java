package ar.com.fennoma.davipocket.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static final String DDMMYY_FORMAT = "dd/MM/yy";
    public static final String DOTTED_DDMMMYY_FORMAT = "dd · MM · yy";

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

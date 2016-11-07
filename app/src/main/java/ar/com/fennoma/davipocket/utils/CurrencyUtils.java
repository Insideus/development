package ar.com.fennoma.davipocket.utils;

import java.text.DecimalFormat;

public class CurrencyUtils {

    public static String getCurrencyForString(Double number) {
        if (number == null) {
            return "";
        }
        DecimalFormat df = new DecimalFormat("#,###,###,###,###,###,##0.00");
        return df.format(number);
    }

    public static String getCurrencyForStringWithDecimal(Double number){
        if (number == null) {
            return "";
        }
        DecimalFormat df = new DecimalFormat("##################0.00");
        String format = df.format(number);
        return format.replace(",", ".");
    }

    public static String getCurrencyForStringWithOutDecimal(Double number) {
        if (number == null) {
            return "";
        }
        DecimalFormat df = new DecimalFormat("#,###,###,###,###,###,###,###");
        return df.format(number);
    }

}

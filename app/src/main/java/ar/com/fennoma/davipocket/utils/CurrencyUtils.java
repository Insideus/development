package ar.com.fennoma.davipocket.utils;

import java.text.DecimalFormat;

public class CurrencyUtils {
    public static String getCurrencyForString(String originalString) {
        DecimalFormat df = new DecimalFormat("#,###,###,##0.00");
        return df.format(Float.valueOf(originalString));
    }
}

package com.davivienda.billetera.utils;

import android.text.TextUtils;

/**
 * Created by Julian Vega on 08/09/2016.
 */
public class CardsUtils {

    private static String NUMBER_MASK = "●●●●    ●●●●    ●●●●    ";
    private static String DATE_MASK = "●● / ●●";

    public static String getMaskedCardNumber(String number) {
        return NUMBER_MASK.concat(number);
    }

    public static String formatFullNumber(String fullNumber){
        return fullNumber.replace(" ", "    ");
    }

    public static String getMaskedExpirationDate() {
        return DATE_MASK;
    }

    public static String parseFullCardNumber(String fullNumber){
        if(TextUtils.isEmpty(fullNumber) || fullNumber.length() < 16){
            return NUMBER_MASK + "●●●●";
        }
        String firstQuarter = fullNumber.substring(0, 4);
        String secondQuarter = fullNumber.substring(4, 8);
        String thirdQuarter = fullNumber.substring(8, 12);
        String fourthQuarter = fullNumber.substring(12, 16);
        return String.format("%s%s%s%s%s%s%s", firstQuarter, " ", secondQuarter, " ", thirdQuarter, " ", fourthQuarter);
    }
}

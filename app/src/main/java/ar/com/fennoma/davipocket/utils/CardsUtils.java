package ar.com.fennoma.davipocket.utils;

/**
 * Created by Julian Vega on 08/09/2016.
 */
public class CardsUtils {

    private static String NUMBER_MASK = "○○○○    ○○○○    ○○○○    ";
    private static String DATE_MASK = "●● / ●●";

    public static String getMaskedCardNumber(String number) {
        return NUMBER_MASK.concat(number);
    }

    public static String getMaskedExpirationDate() {
        return DATE_MASK;
    }

}

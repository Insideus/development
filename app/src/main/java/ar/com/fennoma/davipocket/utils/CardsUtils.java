package ar.com.fennoma.davipocket.utils;

/**
 * Created by Julian Vega on 08/09/2016.
 */
public class CardsUtils {

    private static String MASK = "○○○○    ○○○○    ○○○○    ";

    public static String getMaskedCardNumber(String number) {
        return MASK.concat(number);
    }

}

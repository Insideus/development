package ar.com.fennoma.davipocket.utils;

/**
 * Created by Julian Vega on 27/10/2016.
 */
public class DavipointUtils {

    public static int getDavipointsEquivalence() {
        return 25;
    }

    public static int toDavipoints(double amount){
        return (int) amount / getDavipointsEquivalence();
    }

    public static double cashDifference(double cashAmount, int davipoints){
        return cashAmount - davipoints * getDavipointsEquivalence();
    }
}

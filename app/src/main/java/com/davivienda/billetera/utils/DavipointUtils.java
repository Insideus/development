package com.davivienda.billetera.utils;

/**
 * Created by Julian Vega on 27/10/2016.
 */
public class DavipointUtils {

    public static int toDavipoints(double amount, int pointsEquivalence) {
        int equivalence = (int) amount / pointsEquivalence;
        if(amount % pointsEquivalence > 0) {
            equivalence += 1;
        }
        return equivalence;
    }

    public static double cashDifference(double cashAmount, int davipoints, int pointsEquivalence){
        return cashAmount - davipoints * pointsEquivalence;
    }
}

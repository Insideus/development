package com.davivienda.billetera.utils;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.cert.Certificate;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

/**
 * Created by Julian Vega on 25/07/2016.
 */
public class EncryptionUtils {

    private static Boolean encrypted = Boolean.TRUE;

    @SuppressLint("GetInstance")
    public static String encryptPassword(Activity act, String password) {
        if(!encrypted) {
            return password;
        }
        if(password == null || password.length() < 1) {
            return "";
        }
        try {
            Cipher c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            Certificate cert = getCertificate(act, "certs/CERCCV.cer");
            c.init(Cipher.ENCRYPT_MODE, cert.getPublicKey());
            return Base64.encodeToString(c.doFinal(password.getBytes("UTF-8")), Base64.DEFAULT);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException
                | InvalidKeyException | IOException | javax.security.cert.CertificateException e) {
            e.printStackTrace();
        }
        return "";
    }

    @SuppressLint("GetInstance")
    public static String encryptPin(Activity act, String password) {
        if(!encrypted) {
            return password;
        }
        if(password == null || password.length() < 1) {
            return "";
        }
        try {
            Cipher c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            Certificate cert = getCertificate(act, "certs/RQPINCANALSCN.cer");
            c.init(Cipher.ENCRYPT_MODE, cert.getPublicKey());
            return Base64.encodeToString(c.doFinal(password.getBytes("UTF-8")), Base64.DEFAULT);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException
                | InvalidKeyException | IOException | javax.security.cert.CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Certificate getCertificate(Activity act, String certName) throws IOException, CertificateException {
        InputStream inStream = act.getAssets().open(certName);
        X509Certificate cert = X509Certificate.getInstance(inStream);
        inStream.close();
        return cert;
    }

}
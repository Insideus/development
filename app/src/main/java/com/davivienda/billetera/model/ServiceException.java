package com.davivienda.billetera.model;

/**
 * Created by Julian Vega on 05/07/2016.
 */
public class ServiceException extends Exception {

    String errorCode;
    String additionalData;

    public ServiceException(String errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public ServiceException(String errorCode, String additionalData) {
        super();
        this.errorCode = errorCode;
        this.additionalData = additionalData;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getAdditionalData() {
        return additionalData;
    }
}

package ar.com.fennoma.davipocket.model;

/**
 * Created by Julian Vega on 05/07/2016.
 */
public class ServiceException extends Exception {

    String errorCode;

    public ServiceException(String errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

}

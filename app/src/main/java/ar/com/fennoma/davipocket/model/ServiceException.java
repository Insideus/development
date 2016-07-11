package ar.com.fennoma.davipocket.model;

/**
 * Created by Julian Vega on 05/07/2016.
 */
public class ServiceException extends Exception {

    String errorCode;
    String nextTokenSession;

    public ServiceException(String errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public ServiceException(String errorCode, String nextTokenSession) {
        super();
        this.errorCode = errorCode;
        this.nextTokenSession = nextTokenSession;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getNextTokenSession() {
        return nextTokenSession;
    }
}

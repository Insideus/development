package ar.com.fennoma.davipocket.model;

/**
 * Created by Julian Vega on 06/07/2016.
 */
public enum  ErrorMessages {

    LOGIN_ERROR("error.login_error"),
    TOKEN_NEEDED_ERROR("error.login_error"),
    INVALID_TOKEN("error.login_error");


    ErrorMessages(String errorCode) {
        this.errorCode = errorCode;
    }

    private String errorCode;

    public String getErrorCode() {
        return errorCode;
    }

    public static ErrorMessages getError(String errorCode) {
        if (errorCode == null) {
            return null;
        }
        for (ErrorMessages key : values()) {
            if (errorCode.equalsIgnoreCase(key.getErrorCode())) {
                return key;
            }
        }
        return null;
    }

}

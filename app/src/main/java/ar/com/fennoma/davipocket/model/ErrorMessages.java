package ar.com.fennoma.davipocket.model;

/**
 * Created by Julian Vega on 06/07/2016.
 */
public enum  ErrorMessages {

    INVALID_SESSION("error.invalid_session"),
    LOGIN_ERROR("error.login_error"),
    TOKEN_REQUIRED_ERROR("error.login_error.token_required"),
    NEXT_TOKEN_ERROR("error.login_error.next_token"),
    INVALID_TOKEN("error.login_error"),
    CONFIMATION_ERROR("error.user_confirmation_error");


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

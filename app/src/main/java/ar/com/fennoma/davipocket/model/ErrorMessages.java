package ar.com.fennoma.davipocket.model;

/**
 * Created by Julian Vega on 06/07/2016.
 */
public enum  ErrorMessages {

    INVALID_SESSION("error.invalid_session"),
    LOGIN_ERROR("error.login_error"),
    TOKEN_REQUIRED_ERROR("error.login_error.token_required"),
    NEXT_TOKEN_ERROR("error.login_error.next_token"),
    WEB_PASSWORD_ERROR("error.login_error.web_password"),
    INVALID_TOKEN("error.login_error"),
    CONFIRMATION_ERROR("error.user_confirmation_error"),
    SET_VIRTUAL_PASSWORD("error.login_error.set_virtual_password"),
    VALIDATE_PRODUCT_ERROR("error.validate_product_error"),
    PASSWORD_EXPIRED("error.login_error.virtual_password_expired"),
    PASSWORD_EXPIRED_OTP_VALIDATION_NEEDED("error.login_error.virtual_password_expired.otp_validation_needed"),
    VIRTUAL_PASSWORD_VALIDATION_ERROR("error.virtual_password_validation"),
    CARD_BLOCKED_24("error.add_card.blocked_24"),
    NEW_DEVICE("error.new_device"),
    NEW_DEVICE_EXISTING_DEVICE("error.new_device_existing_device"),
    NEW_DEVICE_OTP_VALIDATION_NEEDED("error.new_device.otp_validation_needed"),
    NEW_DEVICE_EXISTING_DEVICE_OTP_VALIDATION_NEEDED("error.new_device_existing_device.otp_validation_needed"),
    OTP_VALIDATION_NEEDED("error.otp_validation_needed");

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

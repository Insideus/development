package ar.com.fennoma.davipocket.model;

/**
 * Created by Julian Vega on 06/07/2016.
 */
public enum LoginSteps {

    FACEBOOK("registration_incomplete.facebook_connect", 1),
    ADDITIONAL_INFO("registration_incomplete.user_additional_info", 2),
    ACCOUNT_VERIFICATION("registration_incomplete.user_account_verification", 3),
    COMMUNICATION_PERMISSIONS("registration_incomplete.user_communication_permissions", 4),
    REGISTRATION_COMPLETED("registration_incomplete.registration_completed", 5);


    LoginSteps(String step, int stepNumber) {
        this.step = step;
        this.stepNumber = stepNumber;
    }

    private String step;
    private int stepNumber;

    public String getStep() {
        return step;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public static LoginSteps getStep(String stepName) {
        if (stepName == null) {
            return null;
        }
        for (LoginSteps key : values()) {
            if (stepName.equalsIgnoreCase(key.getStep())) {
                return key;
            }
        }
        return null;
    }

}

package ar.com.fennoma.davipocket.model;

/**
 * Created by Julian Vega on 06/07/2016.
 */
public enum LoginSteps {


    /*
    - facebook
    - confirmacion de datos (email, cel, fecha nac)
    - aceptar t&c y politicas de privacidad ****
    - paso de ecard (opcional) ****
     */
    FACEBOOK("registration_incomplete.facebook_connect", 1),
    ADDITIONAL_INFO("registration_incomplete.user_info", 2),
    ACCOUNT_VERIFICATION("registration_incomplete.phone_validation", 3),
    COMMUNICATION_PERMISSIONS("registration_incomplete.accept_terms", 4),
    CATEGORIES_OF_INTEREST("registration_incomplete.categories_of_interest", 5),
    GET_E_CARD("registration_incomplete.ecard", 6),
    REGISTRATION_COMPLETED("registration_incomplete.registration_completed", 7);


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

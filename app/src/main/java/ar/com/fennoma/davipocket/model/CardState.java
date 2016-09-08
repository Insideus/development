package ar.com.fennoma.davipocket.model;

/**
 * Created by Julian Vega on 06/07/2016.
 */
public enum CardState {

    ACTIVATED_ENROLED,
    ACTIVE_NOT_ENROLED,
    BLOCKED_ACTIVABLE,
    BLOCKED;

    public static CardState getCardState(Card card) {
        if(card.getEnrolled() && !card.getActivate()) {
            return ACTIVATED_ENROLED;
        }
        if(!card.getEnrolled() && card.getEnrolling()) {
            return ACTIVE_NOT_ENROLED;
        }
        if(card.getActivate() && !card.getEnrolling()) {
            return BLOCKED_ACTIVABLE;
        }
        if(!card.getActivate() && !card.getEnrolling()) {
            return BLOCKED;
        }
        return BLOCKED;
    }

    public static Boolean blackAndWhiteImage(CardState state) {
        switch (state) {
            case BLOCKED_ACTIVABLE:
                return true;
            case BLOCKED:
                return true;
            default:
                return false;
        }
    }

    public static Boolean blurredImage(CardState state) {
        switch (state) {
            case ACTIVE_NOT_ENROLED:
                return true;
            case BLOCKED:
                return true;
            default:
                return false;
        }
    }

    public static CardButtonsState getButtonsState(CardState state) {
        CardButtonsState buttonsStates = new CardButtonsState();
        switch (state) {
            case ACTIVATED_ENROLED:
                buttonsStates.activateButton = false;
                buttonsStates.blockButton = true;
                buttonsStates.favouritetButton = true;
                return buttonsStates;
            case ACTIVE_NOT_ENROLED:
                buttonsStates.activateButton = true;
                buttonsStates.blockButton = true;
                buttonsStates.favouritetButton = null;
                return buttonsStates;
            case BLOCKED_ACTIVABLE:
                buttonsStates.activateButton = true;
                buttonsStates.blockButton = false;
                buttonsStates.favouritetButton = null;
                return buttonsStates;
            case BLOCKED:
                buttonsStates.activateButton = true;
                buttonsStates.blockButton = false;
                buttonsStates.favouritetButton = null;
                return buttonsStates;
            default:
                return buttonsStates;
        }
    }

    public static class CardButtonsState {
        //TRUE: boton habilitado
        //FALSE: boton deshabilitado
        //NULL: boton oculto
        public Boolean favouritetButton;
        public Boolean activateButton;
        public Boolean blockButton;

    }

}

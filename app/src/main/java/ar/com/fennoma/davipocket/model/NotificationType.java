package ar.com.fennoma.davipocket.model;

/**
 * Created by Julian Vega on 06/07/2016.
 */
public enum NotificationType {

    ORDER_READY("order_ready"),
    OTT_PAYMENT("ott_pay"),
    OTT_CONFIRMED_PAYMENT("ott_pay_approval"),
    OTT_PAY_REJECTED("ott_pay_rejected");

    NotificationType(String type) {
        this.type = type;
    }

    private String type;

    public String getType() {
        return type;
    }

    public static NotificationType getType(String type) {
        if (type == null) {
            return null;
        }
        for (NotificationType key : values()) {
            if (type.equalsIgnoreCase(key.getType())) {
                return key;
            }
        }
        return null;
    }

}

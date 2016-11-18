package ar.com.fennoma.davipocket.model;

/**
 * Created by Julian Vega on 06/07/2016.
 */
public enum CartType {

    ORDER("ORDER"),
    OTT("OTT");

    CartType(String type) {
        this.type = type;
    }

    private String type;

    public String getType() {
        return type;
    }

    public static CartType getType(String type) {
        if (type == null) {
            return CartType.ORDER;
        }
        for (CartType key : values()) {
            if (type.equalsIgnoreCase(key.getType())) {
                return key;
            }
        }
        return CartType.ORDER;
    }

}

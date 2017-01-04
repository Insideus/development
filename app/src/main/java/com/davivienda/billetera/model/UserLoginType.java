package com.davivienda.billetera.model;

/**
 * Created by Julian Vega on 06/07/2016.
 */
public enum UserLoginType {

    NORMAL("NORMAL"),
    TOKEN("TOKEN");

    UserLoginType(String type) {
        this.type = type;
    }

    private String type;

    public String getType() {
        return type;
    }

    public static UserLoginType getType(String type) {
        if (type == null) {
            return UserLoginType.NORMAL;
        }
        for (UserLoginType key : values()) {
            if (type.equalsIgnoreCase(key.getType())) {
                return key;
            }
        }
        return UserLoginType.NORMAL;
    }

}
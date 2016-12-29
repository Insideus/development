package com.davivienda.billetera.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.davivienda.billetera.DaviPayApplication;
import com.davivienda.billetera.utils.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;

public class User implements Parcelable {

    private final static String JSON_LAST_LOGIN = "last_login";
    private final static String JSON_NAME = "name";
    private final static String JSON_POINTS = "points";
    private final static String JSON_CAN_USE_POINTS = "points";
    private final static String JSON_POINTS_EQUIVALENCE = "points";

    private String lastLogin;
    private String name;
    private Boolean canUseDavipoints;
    private int points = -1;
    private int pointsEquivalence = -1;

    public User() {

    }

    public User(int points, String lastLogin, String name, Boolean canUseDavipoints, int pointsEquivalence) {
        this.points = points;
        this.lastLogin = lastLogin;
        this.name = name;
        this.canUseDavipoints = canUseDavipoints;
        this.pointsEquivalence = pointsEquivalence;
    }

    public static User fromJson(JSONObject object) {
        User user = new User();

        try {
            user.setCanUseDavipoints(object.getBoolean("cant_pay_davipoints"));
        } catch (JSONException e) {
            e.printStackTrace();
            user.setCanUseDavipoints(false);
        }

        try {
            user.setPointsEquivalence(object.getInt("point_equivalence"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            object = object.getJSONObject("user");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            user.setLastLogin(object.getString(JSON_LAST_LOGIN));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            user.setName(object.getString(JSON_NAME));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            user.setPoints(object.getInt(JSON_POINTS));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = DateUtils.getUserLastLogin(lastLogin);
    }

    public String getPoints() {
        if (points == -1) {
            return "N/A";
        } else {
            return NumberFormat.getNumberInstance(DaviPayApplication.getInstance().getResources().getConfiguration().locale).format(points);
        }
    }

    public int getPointsInt() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Boolean getCanUseDavipoints() {
        return canUseDavipoints;
    }

    public void setCanUseDavipoints(Boolean canUseDavipoints) {
        this.canUseDavipoints = canUseDavipoints;
    }

    public int getPointsEquivalence() {
        return pointsEquivalence;
    }

    public void setPointsEquivalence(int pointsEquivalence) {
        this.pointsEquivalence = pointsEquivalence;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.lastLogin);
        dest.writeString(this.name);
        dest.writeValue(this.canUseDavipoints);
        dest.writeInt(this.points);
        dest.writeInt(this.pointsEquivalence);
    }

    protected User(Parcel in) {
        this.lastLogin = in.readString();
        this.name = in.readString();
        this.canUseDavipoints = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.points = in.readInt();
        this.pointsEquivalence = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

}

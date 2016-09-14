package ar.com.fennoma.davipocket.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ar.com.fennoma.davipocket.DavipocketApplication;

public class User implements Parcelable {
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private final static String JSON_LAST_LOGIN = "last_login";
    private final static String JSON_NAME = "name";
    private final static String JSON_POINTS = "points";
    private String lastLogin;
    private String name;
    private int points = -1;

    protected User(Parcel in) {
        lastLogin = in.readString();
        name = in.readString();
        points = in.readInt();
    }

    public User() {}

    public User(int points, String lastLogin, String name) {
        this.points = points;
        this.lastLogin = lastLogin;
        this.name = name;
    }

    public static User fromJson(JSONObject object) {
        User user = new User();

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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(lastLogin);
        dest.writeString(name);
        dest.writeInt(points);
    }

    @Override
    public int describeContents() {
        return 0;
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

    @SuppressLint("SimpleDateFormat")
    public void setLastLogin(String lastLogin) {
        //TODO: mover esto a DateUtils
        DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
        fromFormat.setLenient(false);
        DateFormat toFormat = new SimpleDateFormat("dd · MMM · yyyy / hh:mm a");
        toFormat.setLenient(false);
        try {
            final Date fromDate = fromFormat.parse(lastLogin);
            final String toDate = toFormat.format(fromDate).toUpperCase();
            this.lastLogin = toDate;
        } catch (Exception e) {
            e.printStackTrace();
            this.lastLogin = "N/A";
        }
    }

    public String getPoints() {
        if (points == -1) {
            return "N/A";
        } else {
            return NumberFormat.getNumberInstance(DavipocketApplication.getInstance().getResources().getConfiguration().locale).format(points);
        }
    }

    public int getPointsInt() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}

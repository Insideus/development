package ar.com.fennoma.davipocket.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

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

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}

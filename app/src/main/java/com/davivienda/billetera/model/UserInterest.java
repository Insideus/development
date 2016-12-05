package com.davivienda.billetera.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Julian Vega on 02/09/2016.
 */
public class UserInterest implements Parcelable {

    private String code;
    private String name;
    private Boolean selected;

    public UserInterest() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public static ArrayList<UserInterest> fromJsonArray(JSONObject json) {
        ArrayList<UserInterest> userInterests = new ArrayList<>();
        if(json.has("categoriesOfInterest")) {
            JSONArray jsonArray = json.optJSONArray("categoriesOfInterest");
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject obj = jsonArray.optJSONObject(i);
                if (obj != null) {
                    UserInterest type = fromJson(obj);
                    if(type != null) {
                        userInterests.add(type);
                    }
                }
            }
        }
        return userInterests;
    }

    public static UserInterest fromJson(JSONObject json) {
        UserInterest interest = new UserInterest();
        try {
            interest.setCode(json.getString("code"));
            interest.setName(json.getString("name"));
            interest.setSelected(false);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return interest;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.code);
        dest.writeString(this.name);
        dest.writeValue(this.selected);
    }

    protected UserInterest(Parcel in) {
        this.code = in.readString();
        this.name = in.readString();
        this.selected = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<UserInterest> CREATOR = new Creator<UserInterest>() {
        @Override
        public UserInterest createFromParcel(Parcel source) {
            return new UserInterest(source);
        }

        @Override
        public UserInterest[] newArray(int size) {
            return new UserInterest[size];
        }
    };

}

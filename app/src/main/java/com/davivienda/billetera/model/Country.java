package com.davivienda.billetera.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Julian Vega on 04/07/2016.
 */
public class Country implements Parcelable {

    private long id;
    private String name;
    private String prefix;

    public Country() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.prefix);
    }

    protected Country(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.prefix = in.readString();
    }

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel source) {
            return new Country(source);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };

    public static ArrayList<Country> fromJsonArray(JSONObject json) {
        ArrayList<Country> countries = new ArrayList<>();
        if(json.has("countries")) {
            JSONArray jsonArray = json.optJSONArray("countries");
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject obj = jsonArray.optJSONObject(i);
                if (obj != null) {
                    Country type = fromJson(obj);
                    if(type != null) {
                        countries.add(type);
                    }
                }
            }
        }
        return countries;
    }

    public static Country fromJson(JSONObject json) {
        Country country = new Country();
        try {
            if(json.has("id")) {
                country.setId(json.getLong("id"));
            }
            if(json.has("name")) {
                country.setName(json.getString("name"));
            }
            if(json.has("prefix")) {
                country.setPrefix(json.getString("prefix"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return country;
    }

    @Override
    public String toString() {
        return this.getName() + " (" + this.getPrefix() + ")";
    }

}
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
public class BankProduct implements Parcelable {

    private long id;
    private String name;
    private String code;

    public BankProduct() {
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.code);
    }

    protected BankProduct(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.code = in.readString();
    }

    public static final Creator<BankProduct> CREATOR = new Creator<BankProduct>() {
        @Override
        public BankProduct createFromParcel(Parcel source) {
            return new BankProduct(source);
        }

        @Override
        public BankProduct[] newArray(int size) {
            return new BankProduct[size];
        }
    };

    public static ArrayList<BankProduct> fromJsonArray(JSONObject json) {
        ArrayList<BankProduct> products = new ArrayList<>();
        if(json.has("products")) {
            JSONArray jsonArray = json.optJSONArray("products");
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject obj = jsonArray.optJSONObject(i);
                if (obj != null) {
                    BankProduct type = fromJson(obj);
                    if(type != null) {
                        products.add(type);
                    }
                }
            }
        }
        return products;
    }

    public static BankProduct fromJson(JSONObject json) {
        BankProduct country = new BankProduct();
        try {
            if(json.has("id")) {
                country.setId(json.getLong("id"));
            }
            if(json.has("name")) {
                country.setName(json.getString("name"));
            }
            if(json.has("code")) {
                country.setCode(json.getString("code"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return country;
    }

    @Override
    public String toString() {
        return this.getName();
    }

}
package ar.com.fennoma.davipocket.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Transaction implements Parcelable, IShowableItem {

    private String date;
    private String name;
    private Double price;

    public Transaction() {

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public static ArrayList<Transaction> fromJsonArray(JSONObject json) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        if(json.has("movements")) {
            JSONArray jsonArray = json.optJSONArray("movements");
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject obj = jsonArray.optJSONObject(i);
                if (obj != null) {
                    Transaction transaction = fromJson(obj);
                    if(transaction != null) {
                        transactions.add(transaction);
                    }
                }
            }
        }
        return transactions;
    }

    public static Transaction fromJson(JSONObject json) {
        Transaction transaction = new Transaction();
        try {
            transaction.setDate(json.getString("date"));
            transaction.setName(json.getString("name"));
            transaction.setPrice(json.getDouble("amount"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return transaction;
    }

    @Override
    public int getKindOfItem() {
        return IShowableItem.TRANSACTION;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeString(this.name);
        dest.writeValue(this.price);
    }

    protected Transaction(Parcel in) {
        this.date = in.readString();
        this.name = in.readString();
        this.price = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel source) {
            return new Transaction(source);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };
}

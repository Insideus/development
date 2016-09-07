package ar.com.fennoma.davipocket.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Julian Vega on 07/09/2016.
 */
public class CardBin implements Parcelable {

    private String image;
    private String name;
    private String franchise;

    public CardBin() {

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFranchise() {
        return franchise;
    }

    public void setFranchise(String franchise) {
        this.franchise = franchise;
    }

    public static CardBin fromJson(JSONObject json) {
        CardBin cardBin = new CardBin();
        try {
            cardBin.setImage(json.getString("image"));
            cardBin.setName(json.getString("name"));
            cardBin.setFranchise(json.getString("franchise"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return cardBin;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.image);
        dest.writeString(this.name);
        dest.writeString(this.franchise);
    }

    protected CardBin(Parcel in) {
        this.image = in.readString();
        this.name = in.readString();
        this.franchise = in.readString();
    }

    public static final Parcelable.Creator<CardBin> CREATOR = new Parcelable.Creator<CardBin>() {
        @Override
        public CardBin createFromParcel(Parcel source) {
            return new CardBin(source);
        }

        @Override
        public CardBin[] newArray(int size) {
            return new CardBin[size];
        }
    };

}

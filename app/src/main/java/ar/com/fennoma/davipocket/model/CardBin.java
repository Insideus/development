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
    private String logo;

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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public static CardBin fromJson(JSONObject json) {
        CardBin cardBin = new CardBin();
        try {
            if(json.has("image")) {
                cardBin.setImage(json.getString("image"));
            }
            if(json.has("name")) {
                cardBin.setName(json.getString("name"));
            }
            if(json.has("franchise")) {
                cardBin.setFranchise(json.getString("franchise"));
            }
            if(json.has("logo")) {
                cardBin.setLogo(json.getString("logo"));
            }
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
        dest.writeString(this.logo);
    }

    protected CardBin(Parcel in) {
        this.image = in.readString();
        this.name = in.readString();
        this.franchise = in.readString();
        this.logo = in.readString();
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

package ar.com.fennoma.davipocket.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Card implements Parcelable, CardToShowOnList {

    private String lastDigits;
    private String ownerName;
    private Boolean enrolled;
    private Boolean defaultCard;
    private Boolean enrolling;
    private Boolean pay;
    private String message;
    private Boolean activate;
    private CardBin bin;

    public Card() {

    }

    public String getLastDigits() {
        return lastDigits;
    }

    public void setLastDigits(String lastDigits) {
        this.lastDigits = lastDigits;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Boolean getEnrolled() {
        return enrolled;
    }

    public void setEnrolled(Boolean enrolled) {
        this.enrolled = enrolled;
    }

    public Boolean getDefaultCard() {
        return defaultCard;
    }

    public void setDefaultCard(Boolean defaultCard) {
        this.defaultCard = defaultCard;
    }

    public Boolean getEnrolling() {
        return enrolling;
    }

    public void setEnrolling(Boolean enrolling) {
        this.enrolling = enrolling;
    }

    public Boolean getPay() {
        return pay;
    }

    public void setPay(Boolean pay) {
        this.pay = pay;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getActivate() {
        return activate;
    }

    public void setActivate(Boolean activate) {
        this.activate = activate;
    }

    public CardBin getBin() {
        return bin;
    }

    public void setBin(CardBin bin) {
        this.bin = bin;
    }

    public static ArrayList<Card> fromJsonArray(JSONObject json) {
        ArrayList<Card> cards = new ArrayList<>();
        if(json.has("cards")) {
            JSONArray jsonArray = json.optJSONArray("cards");
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject obj = jsonArray.optJSONObject(i);
                if (obj != null) {
                    Card card = fromJson(obj);
                    if(card != null) {
                        cards.add(card);
                    }
                }
            }
        }
        return cards;
    }

    public static Card fromJson(JSONObject json) {
        Card card = new Card();
        try {
            card.setLastDigits(json.getString("last_digits"));
            card.setOwnerName(json.getString("owner"));
            card.setEnrolled(json.getBoolean("enrolled"));
            card.setDefaultCard(json.getBoolean("default"));
            card.setEnrolling(json.getBoolean("enrolling"));
            card.setPay(json.getBoolean("pay"));
            card.setActivate(json.getBoolean("activate"));
            card.setMessage(json.getString("message"));
            JSONObject jsonBin = json.getJSONObject("bin");
            CardBin bin = CardBin.fromJson(jsonBin);
            card.setBin(bin);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return card;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.lastDigits);
        dest.writeString(this.ownerName);
        dest.writeValue(this.enrolled);
        dest.writeValue(this.defaultCard);
        dest.writeValue(this.enrolling);
        dest.writeValue(this.pay);
        dest.writeString(this.message);
        dest.writeValue(this.activate);
        dest.writeParcelable(this.bin, flags);
    }

    protected Card(Parcel in) {
        this.lastDigits = in.readString();
        this.ownerName = in.readString();
        this.enrolled = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.defaultCard = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.enrolling = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.pay = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.message = in.readString();
        this.activate = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.bin = in.readParcelable(CardBin.class.getClassLoader());
    }

    public static final Parcelable.Creator<Card> CREATOR = new Parcelable.Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel source) {
            return new Card(source);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

}

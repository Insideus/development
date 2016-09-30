package ar.com.fennoma.davipocket.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ar.com.fennoma.davipocket.utils.CardsUtils;
import ar.com.fennoma.davipocket.utils.DateUtils;

public class Card implements Parcelable, CardToShowOnList {

    private Boolean eCard;
    private String lastDigits;
    private String ownerName;
    private Boolean enrolled;
    private Boolean defaultCard;
    private Boolean enrolling;
    private Boolean pay;
    private String message;
    private Boolean activate;
    private CardBin bin;
    private String fullNumber;
    private String expirationMonth;
    private String expirationYear;

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
            if(json.has("card")){
                json = json.getJSONObject("card");
            }
            if(json.has("last_digits")) {
                card.setLastDigits(json.getString("last_digits"));
            }
            if(json.has("owner")){
                card.setOwnerName(json.getString("owner"));
            }
            if(json.has("enrolled")) {
                card.setEnrolled(json.getBoolean("enrolled"));
            }
            if(json.has("default")) {
                card.setDefaultCard(json.getBoolean("default"));
            }
            if(json.has("enrolling")) {
                card.setEnrolling(json.getBoolean("enrolling"));
            }
            if(json.has("pay")) {
                card.setPay(json.getBoolean("pay"));
            }
            if(json.has("activate")) {
                card.setActivate(json.getBoolean("activate"));
            }
            if(json.has("message")) {
                card.setMessage(json.getString("message"));
            }
            if(json.has("ecard")){
                card.setECard(json.getBoolean("ecard"));
            } else {
                card.setECard(false);
            }
            if(json.has("digits")){
                card.setFullNumber(CardsUtils.parseFullCardNumber(json.getString("digits")));
            }
            if(json.has("expiration_date")){
                String expirationDate = json.getString("expiration_date");
                card.setExpirationMonth(DateUtils.getMonthFromExpirationDate(expirationDate));
                card.setExpirationYear(DateUtils.getYearFromExpirationDate(expirationDate));
            }
            if(json.has("bin")) {
                JSONObject jsonBin = json.getJSONObject("bin");
                CardBin bin = CardBin.fromJson(jsonBin);
                card.setBin(bin);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return card;
    }

    @Override
    public int getTypeOfCard() {
        return CardToShowOnList.CARD;
    }

    public Boolean getECard() {
        return eCard;
    }

    public void setECard(Boolean eCard) {
        this.eCard = eCard;
    }

    public String getFullNumber() {
        return fullNumber;
    }

    public void setFullNumber(String fullNumber) {
        this.fullNumber = fullNumber;
    }

    public String getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(String expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public String getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(String expirationYear) {
        this.expirationYear = expirationYear;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.eCard);
        dest.writeString(this.lastDigits);
        dest.writeString(this.ownerName);
        dest.writeValue(this.enrolled);
        dest.writeValue(this.defaultCard);
        dest.writeValue(this.enrolling);
        dest.writeValue(this.pay);
        dest.writeString(this.message);
        dest.writeValue(this.activate);
        dest.writeParcelable(this.bin, flags);
        dest.writeString(this.fullNumber);
        dest.writeString(this.expirationMonth);
        dest.writeString(this.expirationYear);
    }

    protected Card(Parcel in) {
        this.eCard = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.lastDigits = in.readString();
        this.ownerName = in.readString();
        this.enrolled = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.defaultCard = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.enrolling = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.pay = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.message = in.readString();
        this.activate = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.bin = in.readParcelable(CardBin.class.getClassLoader());
        this.fullNumber = in.readString();
        this.expirationMonth = in.readString();
        this.expirationYear = in.readString();
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
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

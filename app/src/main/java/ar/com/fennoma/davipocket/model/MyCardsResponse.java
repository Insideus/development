package ar.com.fennoma.davipocket.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Julian Vega on 07/10/2016.
 */
public class MyCardsResponse implements Parcelable {

    private Boolean canGetEcard;
    private ArrayList<Card> cards;

    public MyCardsResponse() {
    }

    public Boolean getCanGetEcard() {
        return canGetEcard;
    }

    public void setCanGetEcard(Boolean canGetEcard) {
        this.canGetEcard = canGetEcard;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public static MyCardsResponse fromJson(JSONObject json) {
        MyCardsResponse response = new MyCardsResponse();
        if (json == null) {
            response.setCanGetEcard(false);
            response.setCards(new ArrayList<Card>());
            return response;
        }
        try {
            if(json.has("ecard") && !json.isNull("ecard")){
                response.setCanGetEcard(json.getBoolean("ecard"));
            }
            response.setCards(Card.fromJsonArray(json));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.canGetEcard);
        dest.writeTypedList(this.cards);
    }

    protected MyCardsResponse(Parcel in) {
        this.canGetEcard = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.cards = in.createTypedArrayList(Card.CREATOR);
    }

    public static final Parcelable.Creator<MyCardsResponse> CREATOR = new Parcelable.Creator<MyCardsResponse>() {
        @Override
        public MyCardsResponse createFromParcel(Parcel source) {
            return new MyCardsResponse(source);
        }

        @Override
        public MyCardsResponse[] newArray(int size) {
            return new MyCardsResponse[size];
        }
    };

}

package ar.com.fennoma.davipocket.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Julian Vega on 31/10/2016.
 */

public class PreCheckout implements Parcelable {

    private ArrayList<Card> cards = new ArrayList<>();
    private ArrayList<Integer> installments = new ArrayList<>();

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public ArrayList<Integer> getInstallments() {
        return installments;
    }

    public void setInstallments(ArrayList<Integer> installments) {
        this.installments = installments;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.cards);
        dest.writeList(this.installments);
    }

    public PreCheckout() {
    }

    protected PreCheckout(Parcel in) {
        this.cards = in.createTypedArrayList(Card.CREATOR);
        this.installments = new ArrayList<Integer>();
        in.readList(this.installments, Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<PreCheckout> CREATOR = new Parcelable.Creator<PreCheckout>() {
        @Override
        public PreCheckout createFromParcel(Parcel source) {
            return new PreCheckout(source);
        }

        @Override
        public PreCheckout[] newArray(int size) {
            return new PreCheckout[size];
        }
    };

    public static PreCheckout fromJson(JSONObject json) {
        PreCheckout response = new PreCheckout();
        try {
            response.setCards(Card.fromJsonArray(json));
            if(json.has("instalments")) {
                JSONArray jsonArray = json.optJSONArray("instalments");
                for (int i = 0; i < jsonArray.length(); ++i) {
                    Integer installment = jsonArray.getInt(i);
                    response.getInstallments().add(installment);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

}

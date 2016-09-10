package ar.com.fennoma.davipocket.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Julian Vega on 09/09/2016.
 */
public class TransactionDetails implements Parcelable {

    private String availableAmount;
    private String paymentDate;
    private ArrayList<Transaction> transactions = new ArrayList<>();

    public TransactionDetails() {

    }

    public String getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(String availableAmount) {
        this.availableAmount = availableAmount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public static TransactionDetails fromJson(JSONObject json) {
        TransactionDetails transactionDetails = new TransactionDetails();
        try {
            json = json.getJSONObject("movements");
            transactionDetails.setPaymentDate(json.getString("payment_date"));
            transactionDetails.setAvailableAmount(json.getString("available_amount"));
            transactionDetails.setTransactions(Transaction.fromJsonArray(json));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return transactionDetails;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.availableAmount);
        dest.writeString(this.paymentDate);
        dest.writeTypedList(this.transactions);
    }

    protected TransactionDetails(Parcel in) {
        this.availableAmount = in.readString();
        this.paymentDate = in.readString();
        this.transactions = in.createTypedArrayList(Transaction.CREATOR);
    }

    public static final Parcelable.Creator<TransactionDetails> CREATOR = new Parcelable.Creator<TransactionDetails>() {
        @Override
        public TransactionDetails createFromParcel(Parcel source) {
            return new TransactionDetails(source);
        }

        @Override
        public TransactionDetails[] newArray(int size) {
            return new TransactionDetails[size];
        }
    };
}

package ar.com.fennoma.davipocket.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TransactionDetails implements Parcelable {

    private Double availableAmount;
    private String paymentDate;
    private boolean loadMore;
    private boolean showPayButton;
    private ArrayList<Transaction> transactions = new ArrayList<>();

    public TransactionDetails() {}

    public Double getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(Double availableAmount) {
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

    public static TransactionDetails fromJson(boolean fromECard, JSONObject json) {
        TransactionDetails transactionDetails = new TransactionDetails();
        try {
            transactionDetails.setLoadMore(json.getBoolean("next_page"));
            transactionDetails.showPayButton = true;
            json = json.getJSONObject("movements");
            if(!fromECard) {
                transactionDetails.setShowPayButton(shouldShowPayButton(json));
            }
            if (json.has("payment_date")) {
                transactionDetails.setPaymentDate(json.getString("payment_date"));
            } else {
                transactionDetails.setPaymentDate("No disponible");
            }
            transactionDetails.setAvailableAmount(json.getDouble("available_amount"));
            transactionDetails.setTransactions(Transaction.fromJsonArray(json));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return transactionDetails;
    }

    private static Boolean shouldShowPayButton(JSONObject json) throws JSONException {
        Double totalPesos;
        Double totalDollars;
        if(json.has("total")) {
            totalPesos = json.getDouble("total");
        }else {
            totalPesos = 0d;
        }
        if(json.has("total_usd")) {
            totalDollars = json.getDouble("total_usd");
        }else {
            totalDollars = 0d;
        }
        if(totalPesos != null && totalPesos == 0 && totalDollars != null && totalDollars == 0){
            return false;
        }
        return true;
    }

    public boolean isLoadMore() {
        return loadMore;
    }

    public void setLoadMore(boolean loadMore) {
        this.loadMore = loadMore;
    }

    public boolean isShowPayButton() {
        return showPayButton;
    }

    public void setShowPayButton(boolean showPayButton) {
        this.showPayButton = showPayButton;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.availableAmount);
        dest.writeString(this.paymentDate);
        dest.writeByte(this.loadMore ? (byte) 1 : (byte) 0);
        dest.writeByte(this.showPayButton ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.transactions);
    }

    protected TransactionDetails(Parcel in) {
        this.availableAmount = (Double) in.readValue(Double.class.getClassLoader());
        this.paymentDate = in.readString();
        this.loadMore = in.readByte() != 0;
        this.showPayButton = in.readByte() != 0;
        this.transactions = in.createTypedArrayList(Transaction.CREATOR);
    }

    public static final Creator<TransactionDetails> CREATOR = new Creator<TransactionDetails>() {
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
package ar.com.fennoma.davipocket.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PaymentDetail {

    private String minimumPayment;
    private String total;
    private Date paymentDate;
    private String cardLastDigits;
    private List<Account> accounts;

    public static PaymentDetail fromJsonObject(JSONObject json) {
        PaymentDetail detail = new PaymentDetail();

        if (json == null) {
            return detail;
        }
        try {
            if(json.has("detail")){
                json = json.getJSONObject("detail");
            }
            if (json.has("minimum_payment")) {
                detail.setMinimumPayment(json.getString("minimum_payment"));
            }
            if (json.has("total")) {
                detail.setTotal(json.getString("total"));
            }
            if (json.has("payment_date")) {
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("DD/mm/yyyy", Locale.getDefault());
                    detail.setPaymentDate(simpleDateFormat.parse(json.getString("payment_date")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if(json.has("last_digits")){
                detail.setCardLastDigits(json.getString("last_digits"));
            }
            if(json.has("accounts")){
                detail.setAccounts(Account.fromJSONArray(json.getJSONArray("accounts")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return detail;
    }

    public String getMinimumPayment() {
        return minimumPayment;
    }

    public void setMinimumPayment(String minimumPayment) {
        this.minimumPayment = minimumPayment;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getCardLastDigits() {
        return cardLastDigits;
    }

    public void setCardLastDigits(String cardLastDigits) {
        this.cardLastDigits = cardLastDigits;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}

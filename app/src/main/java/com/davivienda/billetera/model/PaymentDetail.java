package com.davivienda.billetera.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PaymentDetail {

    private Double minimumPayment;
    private Double total;
    private String paymentDate;
    private Double availableAmount;
    private String cardLastDigits;
    private List<Account> accounts;
    private Double minimumPaymentUsd;
    private Double totalUsd;
    private Double transactionCost;

    public Double getMinimumPayment() {
        return minimumPayment;
    }

    public void setMinimumPayment(Double minimumPayment) {
        this.minimumPayment = minimumPayment;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
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

    public Double getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(Double availableAmount) {
        this.availableAmount = availableAmount;
    }

    public Double getMinimumPaymentUsd() {
        return minimumPaymentUsd;
    }

    public void setMinimumPaymentUsd(Double minimumPaymentUsd) {
        this.minimumPaymentUsd = minimumPaymentUsd;
    }

    public Double getTotalUsd() {
        return totalUsd;
    }

    public void setTotalUsd(Double totalUsd) {
        this.totalUsd = totalUsd;
    }

    public Double getTransactionCost() {
        return transactionCost;
    }

    public void setTransactionCost(Double transactionCost) {
        this.transactionCost = transactionCost;
    }

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
                detail.setMinimumPayment(json.getDouble("minimum_payment"));
            }
            if(json.has("available_amount")){
                detail.setAvailableAmount(json.getDouble("available_amount"));
            }
            if (json.has("total")) {
                detail.setTotal(json.getDouble("total"));
            }
            if (json.has("payment_date")) {
                detail.setPaymentDate(json.getString("payment_date"));
            }
            if(json.has("last_digits")){
                detail.setCardLastDigits(json.getString("last_digits"));
            }
            if (json.has("minimum_payment_usd")) {
                detail.setMinimumPaymentUsd(json.getDouble("minimum_payment_usd"));
            }
            if (json.has("total_usd")) {
                detail.setTotalUsd(json.getDouble("total_usd"));
            }
            if(json.has("accounts")){
                detail.setAccounts(Account.fromJSONArray(json.getJSONArray("accounts")));
            }
            if(json.has("transaction_cost")){
                detail.setTransactionCost(json.getDouble("transaction_cost"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return detail;
    }

}

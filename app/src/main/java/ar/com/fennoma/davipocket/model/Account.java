package ar.com.fennoma.davipocket.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Account {

    private String name;
    private String lastDigits;
    private String balance;

    public static List<Account> fromJSONArray(JSONArray jsonArray) {
        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                Account account = fromJSONObject(jsonArray.getJSONObject(i));
                if (account != null) {
                    accounts.add(account);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return accounts;
    }

    public static Account fromJSONObject(JSONObject json) {
        if (json == null) {
            return null;
        }
        Account account = new Account();
        try {
            if (json.has("name")) {
                account.setName(json.getString("name"));
            }
            if(json.has("last_digits")){
                account.setLastDigits(json.getString("last_digits"));
            }
            if(json.has("balance")){
                account.setBalance(json.getString("balance"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastDigits() {
        return lastDigits;
    }

    public void setLastDigits(String lastDigits) {
        this.lastDigits = lastDigits;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}

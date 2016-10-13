package ar.com.fennoma.davipocket.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Store {
    private long id;
    private String name;
    private String address;
    private String country;
    private String city;
    private double latitude;
    private double longitude;
    private String website;
    private String companyName;
    private String zipCode;
    private String phone;
    private String logo;
    private String image;

    public static Store fromJson(JSONObject json){
        Store store = new Store();
        if(json == null){
            return store;
        }
        try{
            if(json.has("id")) {
                store.setId(json.getLong("id"));
            }
            if(json.has("name")){
                store.setName(json.getString("name"));
            }
            if(json.has("address")){
                store.setAddress(json.getString("address"));
            }
            if(json.has("country")){
                store.setCountry(json.getString("country"));
            }
            if(json.has("city")){
                store.setCity(json.getString("city"));
            }
            if(json.has("latitude")){
                store.setLatitude(json.getDouble("latitude"));
            }
            if(json.has("longitude")){
                store.setLongitude(json.getDouble("longitude"));
            }
            if(json.has("website")){
                store.setWebsite(json.getString("website"));
            }
            if(json.has("company_name")){
                store.setCompanyName(json.getString("company_name"));
            }
            if(json.has("zip_code")){
                store.setZipCode(json.getString("zip_code"));
            }
            if(json.has("phone")){
                store.setPhone(json.getString("phone"));
            }
            if(json.has("logo")){
                store.setLogo(json.getString("logo"));
            }
            if(json.has("image")){
                store.setImage(json.getString("image"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return store;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static List<Store> fromJsonArray(JSONArray jsonArray) {
        List<Store> stores = new ArrayList<>();
        if(jsonArray == null){
            return null;
        }
        for(int i = 0; i < jsonArray.length(); i++){
            try {
                stores.add(fromJson(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return stores;
    }
}

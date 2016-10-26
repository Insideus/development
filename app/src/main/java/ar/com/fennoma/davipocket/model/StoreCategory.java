package ar.com.fennoma.davipocket.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StoreCategory implements Parcelable {
    private String id;
    private String name;
    private List<StoreProduct> products;

    public static List<StoreCategory> fromJSONArray(JSONArray jsonArray){
        List<StoreCategory> categories = new ArrayList<>();
        if(jsonArray == null){
            return categories;
        }
        for(int i = 0; i < jsonArray.length(); i++){
            try {
                categories.add(fromJSON(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return categories;
    }

    public static StoreCategory fromJSON(JSONObject json){
        StoreCategory category = new StoreCategory();
        if(json == null){
            return category;
        }
        try {
            if(json.has("id")){
                category.setId(json.getString("id"));
            }
            if(json.has("name")){
                category.setName(json.getString("name"));
            }
            if(json.has("products")){
                category.setProducts(StoreProduct.fromJSONArray(json.getJSONArray("products")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StoreProduct> getProducts() {
        return products;
    }

    public void setProducts(List<StoreProduct> products) {
        this.products = products;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeList(this.products);
    }

    public StoreCategory() {
    }

    protected StoreCategory(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.products = new ArrayList<>();
        in.readList(this.products, StoreProduct.class.getClassLoader());
    }

    public static final Creator<StoreCategory> CREATOR = new Creator<StoreCategory>() {
        @Override
        public StoreCategory createFromParcel(Parcel source) {
            return new StoreCategory(source);
        }

        @Override
        public StoreCategory[] newArray(int size) {
            return new StoreCategory[size];
        }
    };
}

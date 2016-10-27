package ar.com.fennoma.davipocket.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StoreProduct implements Parcelable{

    private String id;
    private String name;
    private String description;
    private String image;
    private Double listPrice;
    private String appDisplayName;
    private String currencyPrice;
    private List<StoreConfiguration> configurations;

    public static List<StoreProduct> fromJSONArray(JSONArray jsonArray) {
        List<StoreProduct> products = new ArrayList<>();
        if(jsonArray == null){
            return products;
        }
        for(int i = 0; i < jsonArray.length(); i++){
            try {
                products.add(fromJSON(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return products;
    }

    public static StoreProduct fromJSON(JSONObject json) {
        StoreProduct product = new StoreProduct();
        if (json == null) {
            return product;
        }
        try {
            if (json.has("id")) {
                product.setId(json.getString("id"));
            }
            if(json.has("name")){
                product.setName(json.getString("name"));
            }
            if(json.has("description")){
                product.setDescription(json.getString("description"));
            }
            if(json.has("image")){
                product.setImage(json.getString("image"));
            }
            if(json.has("list_price")){
                product.setListPrice(json.getDouble("list_price"));
            }
            if(json.has("app_display_name")){
                product.setAppDisplayName(json.getString("app_display_name"));
            }
            if(json.has("configuration")){
                product.setConfigurations(StoreConfiguration.fromJSONArray(json.getJSONArray("configuration")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return product;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Double getListPrice() {
        return listPrice;
    }

    public void setListPrice(Double listPrice) {
        this.listPrice = listPrice;
    }

    public String getAppDisplayName() {
        return appDisplayName;
    }

    public void setAppDisplayName(String appDisplayName) {
        this.appDisplayName = appDisplayName;
    }

    public String getCurrencyPrice() {
        return currencyPrice;
    }

    public void setCurrencyPrice(String currencyPrice) {
        this.currencyPrice = currencyPrice;
    }

    public List<StoreConfiguration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(List<StoreConfiguration> configurations) {
        this.configurations = configurations;
    }

    public StoreProduct createEmptyProduct() {
        StoreProduct emptyProduct = new StoreProduct();
        emptyProduct.setId(this.getId());
        emptyProduct.setName(this.getName());
        emptyProduct.setDescription(this.getDescription());
        emptyProduct.setImage(this.getImage());
        emptyProduct.setListPrice(this.getListPrice());
        emptyProduct.setAppDisplayName(this.getAppDisplayName());
        emptyProduct.setCurrencyPrice(this.getCurrencyPrice());
        emptyProduct.setConfigurations(new ArrayList<StoreConfiguration>());
        return emptyProduct;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.image);
        dest.writeValue(this.listPrice);
        dest.writeString(this.appDisplayName);
        dest.writeString(this.currencyPrice);
        dest.writeTypedList(this.configurations);
    }

    public StoreProduct() {
    }

    protected StoreProduct(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.image = in.readString();
        this.listPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.appDisplayName = in.readString();
        this.currencyPrice = in.readString();
        this.configurations = in.createTypedArrayList(StoreConfiguration.CREATOR);
    }

    public static final Creator<StoreProduct> CREATOR = new Creator<StoreProduct>() {
        @Override
        public StoreProduct createFromParcel(Parcel source) {
            return new StoreProduct(source);
        }

        @Override
        public StoreProduct[] newArray(int size) {
            return new StoreProduct[size];
        }
    };

}

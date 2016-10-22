package ar.com.fennoma.davipocket.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StoreConfigurationItem implements Parcelable {

    private String id;
    private String name;
    private String extraPrice;

    public static List<StoreConfigurationItem> fromJSONArray(JSONArray jsonArray) {
        List<StoreConfigurationItem> configurationItems = new ArrayList<>();
        if(jsonArray == null){
            return configurationItems;
        }
        for(int i = 0; i < jsonArray.length(); i++){
            try {
                configurationItems.add(fromJSON(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return configurationItems;
    }

    public static StoreConfigurationItem fromJSON(JSONObject json) {
        StoreConfigurationItem configurationItem = new StoreConfigurationItem();
        if (json == null) {
            return configurationItem;
        }
        try {
            if (json.has("id")) {
                configurationItem.setId(json.getString("id"));
            }
            if(json.has("name")){
                configurationItem.setName(json.getString("name"));
            }
            if(json.has("extra_price")){
                configurationItem.setExtraPrice(json.getString("extra_price"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return configurationItem;
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

    public String getExtraPrice() {
        return extraPrice;
    }

    public void setExtraPrice(String extraPrice) {
        this.extraPrice = extraPrice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.extraPrice);
    }

    public StoreConfigurationItem() {
    }

    protected StoreConfigurationItem(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.extraPrice = in.readString();
    }

    public static final Creator<StoreConfigurationItem> CREATOR = new Creator<StoreConfigurationItem>() {
        @Override
        public StoreConfigurationItem createFromParcel(Parcel source) {
            return new StoreConfigurationItem(source);
        }

        @Override
        public StoreConfigurationItem[] newArray(int size) {
            return new StoreConfigurationItem[size];
        }
    };
}

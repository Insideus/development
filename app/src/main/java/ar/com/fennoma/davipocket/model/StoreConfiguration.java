package ar.com.fennoma.davipocket.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StoreConfiguration implements Parcelable {

    private String type;
    private String subType;
    private int minConfiguration;
    private int maxConfiguration;
    private List<StoreConfigurationItem> configurations;

    public static List<StoreConfiguration> fromJSONArray(JSONArray jsonArray) {
        List<StoreConfiguration> configurations = new ArrayList<>();
        if(jsonArray == null){
            return configurations;
        }
        for(int i = 0; i < jsonArray.length(); i++){
            try {
                configurations.add(fromJSON(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return configurations;
    }

    public static StoreConfiguration fromJSON(JSONObject json) {
        StoreConfiguration configuration = new StoreConfiguration();
        if (json == null) {
            return configuration;
        }
        try {
            if (json.has("type")) {
                configuration.setType(json.getString("type"));
            }
            if(json.has("sub_type")){
                configuration.setSubType(json.getString("sub_type"));
            }
            if(json.has("min_configuration")){
                configuration.setMinConfiguration(json.getInt("min_configuration"));
            }
            if(json.has("max_configuration")){
                configuration.setMaxConfiguration(json.getInt("max_configuration"));
            }
            if(json.has("configuration_item")){
                configuration.setConfigurations(StoreConfigurationItem.fromJSONArray(json.getJSONArray("configuration_item")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return configuration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public int getMinConfiguration() {
        return minConfiguration;
    }

    public void setMinConfiguration(int minConfiguration) {
        this.minConfiguration = minConfiguration;
    }

    public int getMaxConfiguration() {
        return maxConfiguration;
    }

    public void setMaxConfiguration(int maxConfiguration) {
        this.maxConfiguration = maxConfiguration;
    }

    public List<StoreConfigurationItem> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(List<StoreConfigurationItem> configurations) {
        this.configurations = configurations;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.subType);
        dest.writeInt(this.minConfiguration);
        dest.writeInt(this.maxConfiguration);
        dest.writeList(this.configurations);
    }

    public StoreConfiguration() {
    }

    protected StoreConfiguration(Parcel in) {
        this.type = in.readString();
        this.subType = in.readString();
        this.minConfiguration = in.readInt();
        this.maxConfiguration = in.readInt();
        this.configurations = new ArrayList<StoreConfigurationItem>();
        in.readList(this.configurations, StoreConfigurationItem.class.getClassLoader());
    }

    public static final Creator<StoreConfiguration> CREATOR = new Creator<StoreConfiguration>() {
        @Override
        public StoreConfiguration createFromParcel(Parcel source) {
            return new StoreConfiguration(source);
        }

        @Override
        public StoreConfiguration[] newArray(int size) {
            return new StoreConfiguration[size];
        }
    };
}

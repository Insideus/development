package ar.com.fennoma.davipocket.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Julian Vega on 04/07/2016.
 */
public class PersonIdType implements Parcelable {

    private long id;
    private String name;
    private String nameKey;

    public PersonIdType() {
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

    public String getNameKey() {
        return nameKey;
    }

    public void setNameKey(String nameKey) {
        this.nameKey = nameKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.nameKey);
    }

    protected PersonIdType(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.nameKey = in.readString();
    }

    public static final Parcelable.Creator<PersonIdType> CREATOR = new Parcelable.Creator<PersonIdType>() {
        @Override
        public PersonIdType createFromParcel(Parcel source) {
            return new PersonIdType(source);
        }

        @Override
        public PersonIdType[] newArray(int size) {
            return new PersonIdType[size];
        }
    };

    public static ArrayList<PersonIdType> fromJsonArray(JSONObject json) {
        ArrayList<PersonIdType> types = new ArrayList<>();
        if(json.has("personIdTypes")) {
            JSONArray jsonArray = json.optJSONArray("personIdTypes");
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject obj = jsonArray.optJSONObject(i);
                if (obj != null) {
                    PersonIdType type = fromJson(obj);
                    if(type != null) {
                        types.add(type);
                    }
                }
            }
        }
        return types;
    }

    public static PersonIdType fromJson(JSONObject json) {
        PersonIdType type = new PersonIdType();
        try {
            if(json.has("id")) {
                type.setId(json.getLong("id"));
            }
            if(json.has("name")) {
                type.setName(json.getString("name"));
            }
            if(json.has("nameKey")) {
                type.setNameKey(json.getString("nameKey"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return type;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        return this.id == ((PersonIdType)o).id;
    }
}
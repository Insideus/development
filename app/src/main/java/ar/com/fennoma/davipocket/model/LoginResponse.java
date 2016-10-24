package ar.com.fennoma.davipocket.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Julian Vega on 06/07/2016.
 */
public class LoginResponse implements Parcelable {

    private String sid;
    private String accountStatus;

    public LoginResponse() {
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public static LoginResponse fromJson(JSONObject json) {
        LoginResponse response = new LoginResponse();
        try {
            if(json.has("sid")) {
                response.setSid(json.getString("sid"));
            }
            if(json.has("account_status")) {
                response.setAccountStatus(json.getString("account_status"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sid);
        dest.writeString(this.accountStatus);
    }

    protected LoginResponse(Parcel in) {
        this.sid = in.readString();
        this.accountStatus = in.readString();
    }

    public static final Parcelable.Creator<LoginResponse> CREATOR = new Parcelable.Creator<LoginResponse>() {
        @Override
        public LoginResponse createFromParcel(Parcel source) {
            return new LoginResponse(source);
        }

        @Override
        public LoginResponse[] newArray(int size) {
            return new LoginResponse[size];
        }
    };
}

package ar.com.fennoma.davipocket.model;

import android.os.Parcel;

public class TransactionTitle implements IShowableItem {
    @Override
    public int getKindOfItem() {
        return IShowableItem.TITLE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public TransactionTitle() {
    }

    protected TransactionTitle(Parcel in) {
    }

    public static final Creator<TransactionTitle> CREATOR = new Creator<TransactionTitle>() {
        @Override
        public TransactionTitle createFromParcel(Parcel source) {
            return new TransactionTitle(source);
        }

        @Override
        public TransactionTitle[] newArray(int size) {
            return new TransactionTitle[size];
        }
    };
}

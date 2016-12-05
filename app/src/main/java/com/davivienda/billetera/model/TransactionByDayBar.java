package com.davivienda.billetera.model;

import android.os.Parcel;


public class TransactionByDayBar implements IShowableItem {
    @Override
    public int getKindOfItem() {
        return IShowableItem.BY_DAY_BAR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public TransactionByDayBar() {
    }

    protected TransactionByDayBar(Parcel in) {
    }

    public static final Creator<TransactionByDayBar> CREATOR = new Creator<TransactionByDayBar>() {
        @Override
        public TransactionByDayBar createFromParcel(Parcel source) {
            return new TransactionByDayBar(source);
        }

        @Override
        public TransactionByDayBar[] newArray(int size) {
            return new TransactionByDayBar[size];
        }
    };
}

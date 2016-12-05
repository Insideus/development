package com.davivienda.billetera.model;

import android.os.Parcel;

public class TransactionPayButton implements IShowableItem {
    @Override
    public int getKindOfItem() {
        return IShowableItem.BUTTON;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public TransactionPayButton() {
    }

    protected TransactionPayButton(Parcel in) {
    }

    public static final Creator<TransactionPayButton> CREATOR = new Creator<TransactionPayButton>() {
        @Override
        public TransactionPayButton createFromParcel(Parcel source) {
            return new TransactionPayButton(source);
        }

        @Override
        public TransactionPayButton[] newArray(int size) {
            return new TransactionPayButton[size];
        }
    };
}

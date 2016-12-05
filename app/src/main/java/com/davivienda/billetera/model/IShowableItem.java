package com.davivienda.billetera.model;

import android.os.Parcelable;

public interface IShowableItem extends Parcelable {
    int TRANSACTION = 0;
    int TITLE = 1;
    int BUTTON = 2;
    int BY_DAY_BAR = 3;
    int STORE_CONFIGURATION = 4;

    int getKindOfItem();
}

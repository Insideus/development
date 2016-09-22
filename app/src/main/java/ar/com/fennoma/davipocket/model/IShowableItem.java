package ar.com.fennoma.davipocket.model;

import android.os.Parcelable;

public interface IShowableItem extends Parcelable {
    int TRANSACTION = 0;
    int TITLE = 1;
    int BUTTON = 2;

    int getKindOfItem();
}

package ar.com.fennoma.davipocket.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Julian Vega on 31/10/2016.
 */

public class Cart implements Parcelable {

    private Store store;
    private ArrayList<StoreProduct> products = new ArrayList<>();
    private Double cartPrice;
    private int cartDavipoints;
    private int selectedInstallment;
    private Card selectedCard;

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public ArrayList<StoreProduct> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<StoreProduct> products) {
        this.products = products;
    }

    public Double getCartPrice() {
        return cartPrice;
    }

    public void setCartPrice(Double cartPrice) {
        this.cartPrice = cartPrice;
    }

    public int getCartDavipoints() {
        return cartDavipoints;
    }

    public void setCartDavipoints(int cartDavipoints) {
        this.cartDavipoints = cartDavipoints;
    }

    public int getSelectedInstallment() {
        return selectedInstallment;
    }

    public void setSelectedInstallment(int selectedInstallment) {
        this.selectedInstallment = selectedInstallment;
    }

    public Card getSelectedCard() {
        return selectedCard;
    }

    public void setSelectedCard(Card selectedCard) {
        this.selectedCard = selectedCard;
    }

    public void calculateCartPrice() {
        Double price = 0d;
        for(StoreProduct product : getProducts()) {
            price += product.getSelectedProductPrice();
        }
        this.cartPrice = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.store, flags);
        dest.writeTypedList(this.products);
        dest.writeValue(this.cartPrice);
        dest.writeInt(this.cartDavipoints);
        dest.writeInt(this.selectedInstallment);
        dest.writeParcelable(this.selectedCard, flags);
    }

    public Cart() {
    }

    protected Cart(Parcel in) {
        this.store = in.readParcelable(Store.class.getClassLoader());
        this.products = in.createTypedArrayList(StoreProduct.CREATOR);
        this.cartPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.cartDavipoints = in.readInt();
        this.selectedInstallment = in.readInt();
        this.selectedCard = in.readParcelable(Card.class.getClassLoader());
    }

    public static final Creator<Cart> CREATOR = new Creator<Cart>() {
        @Override
        public Cart createFromParcel(Parcel source) {
            return new Cart(source);
        }

        @Override
        public Cart[] newArray(int size) {
            return new Cart[size];
        }
    };

}

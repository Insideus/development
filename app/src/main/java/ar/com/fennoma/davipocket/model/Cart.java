package ar.com.fennoma.davipocket.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.utils.CurrencyUtils;

public class Cart implements Parcelable {
    private Store store;
    private ArrayList<StoreProduct> products = new ArrayList<>();
    private Double cartPrice;
    private int cartDavipoints;
    private int selectedInstallment;
    private Card selectedCard;

    private Integer starsGiven;
    private String date;
    private String deliveredTo;
    private String comment;
    private String receiptNumber;

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

    public String getDeliveredTo() {
        return deliveredTo;
    }

    public void setDeliveredTo(String deliveredTo) {
        this.deliveredTo = deliveredTo;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public Integer getStarsGiven() {
        return starsGiven;
    }

    public void setStarsGiven(Integer starsGiven) {
        this.starsGiven = starsGiven;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void calculateCartPrice() {
        Double price = 0d;
        for(StoreProduct product : getProducts()) {
            price += product.getSelectedProductPrice();
        }
        this.cartPrice = price;
    }

    public Cart() {
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
        dest.writeValue(this.starsGiven);
        dest.writeString(this.deliveredTo);
        dest.writeString(this.comment);
        dest.writeString(this.receiptNumber);
    }

    protected Cart(Parcel in) {
        this.store = in.readParcelable(Store.class.getClassLoader());
        this.products = in.createTypedArrayList(StoreProduct.CREATOR);
        this.cartPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.cartDavipoints = in.readInt();
        this.selectedInstallment = in.readInt();
        this.selectedCard = in.readParcelable(Card.class.getClassLoader());
        this.starsGiven = (Integer) in.readValue(Integer.class.getClassLoader());
        this.deliveredTo = in.readString();
        this.comment = in.readString();
        this.receiptNumber = in.readString();
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

    public List<Pair<String, String>> toServiceParams() {
        List<Pair<String, String>> params = new ArrayList<>();
        Pair<String, String> storeIdParam = new Pair("store_id", String.valueOf(store.getId()));
        params.add(storeIdParam);
        Pair<String, String> amountParam = new Pair("amount", CurrencyUtils.getCurrencyForStringWithDecimal(getCartPrice()));
        params.add(amountParam);
        Pair<String, String> davipointsParam = new Pair("davipoints", String.valueOf(cartDavipoints));
        params.add(davipointsParam);
        Pair<String, String> installmentsParam = new Pair("installments", String.valueOf(selectedInstallment));
        params.add(installmentsParam);
        Pair<String, String> cardNumberParam = new Pair("last_digits", selectedCard.getLastDigits());
        params.add(cardNumberParam);
        JSONArray productsJson = new JSONArray();
        for(StoreProduct p : products) {
            JSONObject productJson = p.toCartJson();
            productsJson.put(productJson);
        }
        Pair<String, String> productsParam = new Pair("products", productsJson.toString());
        params.add(productsParam);
        return params;
    }

    public static ArrayList<Cart> fromJson(JSONArray jsonArray) throws JSONException {
        ArrayList<Cart> cartList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Cart cart = Cart.fromJson(jsonArray.getJSONObject(i));
            cartList.add(cart);
        }

        return cartList;
    }

    private static Cart fromJson(JSONObject jsonObject) throws JSONException {
        Cart cart = new Cart();
        cart.setReceiptNumber(jsonObject.getString("id"));
        cart.setCartPrice(jsonObject.getDouble("total"));
        cart.setDate(jsonObject.getString("date"));

        // Objects and arrays
        cart.setStore(Store.fromJson(jsonObject.getJSONObject("store")));
        cart.setProducts(StoreProduct.fromJSONArray(jsonObject.getJSONArray("products")));
        cart.setSelectedCard(Card.fromJson(jsonObject.getJSONObject("card")));
        return cart;
    }
}

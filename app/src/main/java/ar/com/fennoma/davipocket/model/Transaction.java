package ar.com.fennoma.davipocket.model;

public class Transaction {
    private String date;
    private String name;
    private String price;
    private long davipoints = 0;
    private int productAmount = 0;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProductAmount() {
        return productAmount;
    }

    public void setProductAmount(int productAmount) {
        this.productAmount = productAmount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public long getDavipoints() {
        return davipoints;
    }

    public void setDavipoints(long davipoints) {
        this.davipoints = davipoints;
    }
}

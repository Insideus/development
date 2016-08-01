package ar.com.fennoma.davipocket.model;

public class Card extends CardToShowOnList {

    private boolean favouriteCard;
    private boolean enabled = true;
    private boolean inactive = false;
    private String ownerName;
    private String cardNumber;
    private String month;
    private String year;
    private String cvv;

    public Card(int imageResource) {
        super(imageResource, CardToShowOnList.ACTUAL_CARD);
    }

    public boolean isFavouriteCard() {
        return favouriteCard;
    }

    public void setFavouriteCard(boolean favouriteCard) {
        this.favouriteCard = favouriteCard;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}

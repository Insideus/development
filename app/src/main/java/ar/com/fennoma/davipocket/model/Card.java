package ar.com.fennoma.davipocket.model;

public class Card extends CardToShowOnList {

    private boolean favouriteCard;
    private boolean enabled = true;
    private boolean inactive = false;

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
}

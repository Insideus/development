package ar.com.fennoma.davipocket.model.mocked;

public class Card extends CardToShowOnList {

    private boolean firstCard;
    private boolean enabled;

    public Card(int imageResource) {
        super(imageResource, CardToShowOnList.ACTUAL_CARD);
    }

    public boolean isFirstCard() {
        return firstCard;
    }

    public void setFirstCard(boolean firstCard) {
        this.firstCard = firstCard;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

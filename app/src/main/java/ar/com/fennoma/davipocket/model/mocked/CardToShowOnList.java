package ar.com.fennoma.davipocket.model.mocked;

public class CardToShowOnList {
    public final static int BUTTON_CARD = 0;
    public final static int ACTUAL_CARD = 1;

    private int imageResource;
    private int kindOfCard;

    public CardToShowOnList(int imageResource, int kindOfCard){
        this.imageResource = imageResource;
        this.kindOfCard = kindOfCard;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public int getKindOfCard() {
        return kindOfCard;
    }

    public void setKindOfCard(int kindOfCard) {
        this.kindOfCard = kindOfCard;
    }
}

package ar.com.fennoma.davipocket.model;

public class ButtonCard implements CardToShowOnList {

    public static final int eCard = 0;
    public static final int portfolioCard = 1;
    private int imageResource;

    private int type;

    public ButtonCard(int imageResource) {
        this.imageResource = imageResource;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    @Override
    public int getTypeOfCard() {
        return CardToShowOnList.BUTTON_CARD;
    }
}

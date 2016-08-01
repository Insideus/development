package ar.com.fennoma.davipocket.model;

public class ButtonCard extends CardToShowOnList {

    public static final int eCard = 0;
    public static final int portfolioCard = 1;

    private int type;

    public ButtonCard(int imageResource) {
        super(imageResource, CardToShowOnList.BUTTON_CARD);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

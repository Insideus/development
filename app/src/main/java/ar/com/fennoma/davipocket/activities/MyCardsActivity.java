package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.ButtonCard;
import ar.com.fennoma.davipocket.model.Card;
import ar.com.fennoma.davipocket.model.CardState;
import ar.com.fennoma.davipocket.model.CardToShowOnList;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.utils.CardsUtils;
import ar.com.fennoma.davipocket.utils.DialogUtil;
import ar.com.fennoma.davipocket.utils.ImageUtils;

public class MyCardsActivity extends BaseActivity {

    private CardsAdapter cardsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cards);
        setToolbar(R.id.toolbar_layout, false, getString(R.string.my_cards_activity_title));
        setRecycler();
        //cardsAdapter.setList(addButtons(Service.getMockedCardList()));
        new GetUserCardsTask().execute();
    }

    private List<CardToShowOnList> addButtons(List<CardToShowOnList> cards){
        ButtonCard buttonCard = new ButtonCard(R.drawable.add_e_card_button);
        buttonCard.setType(ButtonCard.eCard);
        cards.add(buttonCard);

        buttonCard = new ButtonCard(R.drawable.add_portfolio_card_button);
        buttonCard.setType(ButtonCard.portfolioCard);
        cards.add(buttonCard);
        return cards;
    }

    private void setRecycler() {
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        if(recycler == null){
            return;
        }
        recycler.setLayoutManager(new LinearLayoutManager(this));
        cardsAdapter = new CardsAdapter();
        recycler.setAdapter(cardsAdapter);
    }

    private class CardsAdapter extends RecyclerView.Adapter {

        private List<CardToShowOnList> cards;

        public CardsAdapter(){
            cards = new ArrayList<>();
        }

        private class ButtonCardHolder extends RecyclerView.ViewHolder{

            private ImageView cardButton;
            public ButtonCardHolder(View itemView) {
                super(itemView);
                cardButton = (ImageView) itemView.findViewById(R.id.card_image);
            }
        }

        private class ActualCardHolder extends RecyclerView.ViewHolder{

            private ImageView card;
            private View translucentCovering;
            private ImageView favouriteCard;
            private ImageView checkCardData;
            private ImageView disableCard;
            private TextView name;
            private TextView number;
            private TextView date;

            public ActualCardHolder(View itemView) {
                super(itemView);
                card = (ImageView) itemView.findViewById(R.id.card);
                translucentCovering = itemView.findViewById(R.id.translucent_covering);
                favouriteCard = (ImageView) itemView.findViewById(R.id.favourite_card);
                checkCardData = (ImageView) itemView.findViewById(R.id.check_card_data);
                disableCard = (ImageView) itemView.findViewById(R.id.disable_card);
                name = (TextView) itemView.findViewById(R.id.credit_card_name);
                number = (TextView) itemView.findViewById(R.id.credit_card_number);
                date = (TextView) itemView.findViewById(R.id.expiration_date);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if(cards.get(position) instanceof Card) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == 0){
                return new ActualCardHolder(getLayoutInflater().inflate(R.layout.item_my_cards_card, parent, false));
            } else {
                return new ButtonCardHolder(getLayoutInflater().inflate(R.layout.item_new_card_to_list, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder genericHolder, int position) {
            if(getItemViewType(position) == 1){
                ButtonCard buttonCard = (ButtonCard) cards.get(position);
                ButtonCardHolder holder = (ButtonCardHolder) genericHolder;
                holder.cardButton.setImageResource(buttonCard.getImageResource());
                if(buttonCard.getType() == ButtonCard.eCard){
                    holder.cardButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogUtil.toast(MyCardsActivity.this, "No implementado aún");
                        }
                    });
                } else {
                    holder.cardButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(MyCardsActivity.this, NewCardActivity.class));
                        }
                    });
                }
            } else if(getItemViewType(position) == 0){
                final Card card = (Card) cards.get(position);
                ActualCardHolder holder = (ActualCardHolder) genericHolder;
                if(card.getBin() != null) {
                    ImageUtils.loadCardImage(MyCardsActivity.this, holder.card, card.getBin().getImage());
                }
                holder.number.setText(CardsUtils.getMaskedCardNumber(card.getLastDigits()));
                holder.date.setText(CardsUtils.getMaskedExpirationDate());
                holder.name.setText(card.getOwnerName());
                holder.card.setOnClickListener(getCardOnClickListener(card));

                CardState cardState = CardState.getCardState(card);
                CardState.CardButtonsState buttonsState = CardState.getButtonsState(cardState);
                makeAlphaImage(holder, CardState.blurredImage(cardState));
                makeBlackAndWhiteImage(holder, CardState.blackAndWhiteImage(cardState));

                setButtonsState(holder, card, buttonsState);
                /*
                if(card.getActivate()){
                    makeAlphaImage(holder, false);
                    holder.checkCardData.setVisibility(View.VISIBLE);
                    holder.favouriteCard.setVisibility(View.VISIBLE);
                    holder.favouriteCard.setImageResource(card.getDefaultCard() ? R.drawable.my_cards_favourite_card_indicator : R.drawable.my_cards_not_favourite_card);
                    holder.disableCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            card.setActivate(false);
                            notifyDataSetChanged();
                        }
                    });
                } else {
                    makeAlphaImage(holder, true);
                    holder.favouriteCard.setVisibility(View.GONE);
                    holder.checkCardData.setVisibility(View.GONE);
                    holder.disableCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            card.setActivate(true);
                            notifyDataSetChanged();
                        }
                    });
                }
                */
            }
        }

        private void setButtonsState(ActualCardHolder holder, Card card, CardState.CardButtonsState cardButtonsState) {
            setFavouriteButtonState(holder, card, cardButtonsState);
            setActivateButtonState(holder, card, cardButtonsState);
            setBlockButtonState(holder, card, cardButtonsState);
        }

        private void setFavouriteButtonState(ActualCardHolder holder, Card card, CardState.CardButtonsState cardButtonsState) {
            if(cardButtonsState.favouritetButton == null) {
                holder.favouriteCard.setVisibility(View.INVISIBLE);
            } else {
                holder.favouriteCard.setVisibility(View.VISIBLE);
                holder.favouriteCard.setImageResource(card.getDefaultCard() ? R.drawable.my_cards_favourite_card_indicator : R.drawable.my_cards_not_favourite_card);
            }
        }

        private void setActivateButtonState(ActualCardHolder holder, Card card, CardState.CardButtonsState cardButtonsState) {
            if(cardButtonsState.activateButton == null) {
                holder.checkCardData.setVisibility(View.INVISIBLE);
            } else {
                holder.checkCardData.setVisibility(View.VISIBLE);
                holder.checkCardData.setImageResource(cardButtonsState.activateButton ? R.drawable.my_cards_avalaible_card_indicator : R.drawable.my_cards_avalaible_card_indicator_disabled);
                if(cardButtonsState.activateButton) {

                } else {

                }
            }
        }

        private void setBlockButtonState(ActualCardHolder holder, Card card, CardState.CardButtonsState cardButtonsState) {
            if(cardButtonsState.blockButton == null) {
                holder.disableCard.setVisibility(View.INVISIBLE);
            } else {
                holder.disableCard.setVisibility(View.VISIBLE);
                holder.disableCard.setImageResource(cardButtonsState.blockButton ? R.drawable.my_cards_block_card_button : R.drawable.my_cards_disable_card_button);
                if(cardButtonsState.blockButton) {

                } else {

                }
            }
        }

        @NonNull
        private View.OnClickListener getCardOnClickListener(final Card card) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(card.getPay()) {

                    }
                    startActivity(new Intent(MyCardsActivity.this, CardDetailActivity.class));
                }
            };
        }

        private void makeAlphaImage(ActualCardHolder holder, Boolean alphaImage) {
            if(alphaImage) {
                holder.translucentCovering.setVisibility(View.VISIBLE);
            } else {
                holder.translucentCovering.setVisibility(View.GONE);
            }
        }

        private void makeBlackAndWhiteImage(ActualCardHolder holder, Boolean blackAndWhite) {
            ColorMatrix matrix = new ColorMatrix();
            if(blackAndWhite) {
                matrix.setSaturation(0);
            } else {
                matrix.reset();
            }
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            holder.card.setColorFilter(filter);
        }

        @Override
        public int getItemCount() {
            return cards.size();
        }

        public void setList(List<CardToShowOnList> cards) {
            this.cards = cards;
            notifyDataSetChanged();
        }
    }

    public class GetUserCardsTask extends AsyncTask<Void, Void, ArrayList<CardToShowOnList>> {

        String errorCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected ArrayList<CardToShowOnList> doInBackground(Void... params) {
            ArrayList<CardToShowOnList> response = null;
            try {
                String sid = Session.getCurrentSession(getApplicationContext()).getSid();
                ArrayList<Card> userCards = Service.getUserCards(sid);
                if(userCards != null) {
                    response = new ArrayList<CardToShowOnList>();
                    response.addAll(userCards);
                }
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(ArrayList<CardToShowOnList> response) {
            super.onPostExecute(response);
            hideLoading();
            if(response == null) {
                //Hancdle invalid session error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                if(error != null && error == ErrorMessages.INVALID_SESSION) {
                    handleInvalidSessionError();
                } else {
                    showServiceGenericError();
                }
            } else {
                cardsAdapter.setList(addButtons(response));
            }
        }
    }

}

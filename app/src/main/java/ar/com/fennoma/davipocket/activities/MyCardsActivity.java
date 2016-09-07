package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import ar.com.fennoma.davipocket.model.CardToShowOnList;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
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
                //holder.card.setImageResource(card.getImageResource());
                if(card.getBin() != null) {
                    ImageUtils.loadCardImage(MyCardsActivity.this, holder.card, card.getBin().getImage());
                }
                holder.number.setText(card.getLastDigits());
                //holder.month.setText(card.getMonth());
                //holder.year.setText(card.getYear());
                //holder.cvv.setText(card.getCvv());
                holder.name.setText(card.getOwnerName());
                holder.card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MyCardsActivity.this, CardDetailActivity.class));
                    }
                });
                if(card.getActivate()){
                    holder.translucentCovering.setVisibility(View.INVISIBLE);
                    holder.checkCardData.setVisibility(View.VISIBLE);
                    holder.firstCard.setVisibility(View.VISIBLE);
                    holder.firstCard.setImageResource(card.getDefaultCard() ? R.drawable.my_cards_favourite_card_indicator : R.drawable.my_cards_not_favourite_card);
                    holder.disableCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            card.setActivate(false);
                            notifyDataSetChanged();
                        }
                    });
                } else {
                    holder.translucentCovering.setVisibility(View.VISIBLE);
                    holder.firstCard.setVisibility(View.GONE);
                    holder.checkCardData.setVisibility(View.GONE);
                    holder.disableCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            card.setActivate(true);
                            notifyDataSetChanged();
                        }
                    });
                }

            }
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
        private ImageView firstCard;
        private View checkCardData;
        private View disableCard;
        private TextView name;
        private TextView number;
        private TextView cvv;
        private TextView month;
        private TextView year;

        public ActualCardHolder(View itemView) {
            super(itemView);
            card = (ImageView) itemView.findViewById(R.id.card);
            translucentCovering = itemView.findViewById(R.id.translucent_covering);
            firstCard = (ImageView) itemView.findViewById(R.id.first_card_indicator);
            checkCardData = itemView.findViewById(R.id.check_card_data);
            disableCard = itemView.findViewById(R.id.disable_card);
            name = (TextView) itemView.findViewById(R.id.credit_card_name);
            number = (TextView) itemView.findViewById(R.id.credit_card_number);
            cvv = (TextView) itemView.findViewById(R.id.cvv);
            month = (TextView) itemView.findViewById(R.id.expiration_month);
            year = (TextView) itemView.findViewById(R.id.expiration_year);
        }
    }

    public class GetUserCardsTask extends AsyncTask<Void, Void, ArrayList<Card>> {

        String errorCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected ArrayList<Card> doInBackground(Void... params) {
            ArrayList<Card> response = null;
            try {
                String sid = Session.getCurrentSession(getApplicationContext()).getSid();
                response = Service.getUserCards(sid);
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(ArrayList<Card> response) {
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
                //cardsAdapter.setList(addButtons(response));
            }
        }
    }

}

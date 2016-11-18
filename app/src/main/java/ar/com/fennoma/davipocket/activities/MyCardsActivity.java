package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import ar.com.fennoma.davipocket.model.MyCardsResponse;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.tasks.DaviPayTask;
import ar.com.fennoma.davipocket.utils.CardsUtils;
import ar.com.fennoma.davipocket.utils.DialogUtil;
import ar.com.fennoma.davipocket.utils.ImageUtils;

public class MyCardsActivity extends BaseActivity {

    private static final int EXPLAINING_DIALOG = 11;
    private static final int OPERATION_RESULT = 12;
    private static final int E_CARD_SHOW_DATA = 13;
    private static final int CREATE_E_CARD = 14;
    private static final int BLOCK_CARD_RQ = 15;
    private CardsAdapter cardsAdapter;
    private int selectedCard = -1;
    private boolean refresh = false;
    private boolean showNewEcardButton = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cards);
        setToolbar(R.id.toolbar_layout, false, getString(R.string.my_cards_activity_title));
        setRecycler();
        refreshCardList();
        showNewEcardButton = false;
    }

    private List<CardToShowOnList> addButtons(MyCardsResponse response) {
        ArrayList<CardToShowOnList> cards = new ArrayList<>();
        cards.addAll(response.getCards());

        if(response.getCanGetEcard()){
            ButtonCard eCard = new ButtonCard(R.drawable.add_e_card_button);
            eCard.setType(ButtonCard.eCard);
            cards.add(eCard);
        }
        ButtonCard buttonCard = new ButtonCard(R.drawable.add_portfolio_card_button);
        buttonCard.setType(ButtonCard.portfolioCard);
        cards.add(buttonCard);
        return cards;
    }

    private void setRecycler() {
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        if (recycler == null) {
            return;
        }
        recycler.setLayoutManager(new LinearLayoutManager(this));
        cardsAdapter = new CardsAdapter();
        recycler.setAdapter(cardsAdapter);
    }

    private void blockedActivableCardDialog(Card card) {
        Intent intent = new Intent(MyCardsActivity.this, ActionDialogActivity.class);
        intent.putExtra(ActionDialogActivity.IS_CARD_NUMBER_DIALOG, true);
        if(card != null) {
            intent.putExtra(ActionDialogActivity.CARD_KEY, card);
        }
        startOperationPopUp(intent, getString(R.string.my_cards_activate_card_title),
                getString(R.string.my_cards_activate_card_subtitle),
                getString(R.string.my_cards_activate_card_text));
    }

    private void enrollCardDialog(Card card) {
        Intent intent = new Intent(MyCardsActivity.this, ActionDialogActivity.class);
        intent.putExtra(ActionDialogActivity.IS_CCV_DIALOG, true);
        intent.putExtra(ActionDialogActivity.CARD_KEY, card);
        startOperationPopUp(intent, getString(R.string.my_cards_enrole_card_title),
                getString(R.string.my_cards_enrole_card_subtitle),
                getString(R.string.my_cards_enrole_card_text));
    }

    private void blockedCardDialog() {
        Intent intent = new Intent(MyCardsActivity.this, ActionDialogActivity.class);
        intent.putExtra(ActionDialogActivity.SHOW_CALL_BUTTON_KEY, true);
        intent.putExtra(ActionDialogActivity.CALL_BUTTON_NUMBER_KEY, getString(R.string.login_web_password_phone));
        startOperationPopUp(intent, getString(R.string.my_cards_blocked_call_card_title),
                getString(R.string.my_cards_blocked_call_card_subtitle),
                getString(R.string.my_cards_blocked_call_card_text));
    }

    private void createECard() {
        Intent intent = new Intent(MyCardsActivity.this, ActionDialogActivity.class);
        intent.putExtra(ActionDialogActivity.E_CARD_CREATE, true);
        intent.putExtra(ActionDialogActivity.TITLE_KEY, getString(R.string.my_cards_e_card_create_title));
        intent.putExtra(ActionDialogActivity.SUBTITLE_KEY, getString(R.string.my_cards_e_card_create_subtitle));
        intent.putExtra(ActionDialogActivity.TEXT_KEY, getString(R.string.my_cards_e_card_create_text));
        startActivityForResult(intent, CREATE_E_CARD);
        overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
    }

    private void eCardGetCVV(String lastDigits){
        Intent intent = new Intent(MyCardsActivity.this, ActionDialogActivity.class);
        intent.putExtra(ActionDialogActivity.E_CARD_GET_CVV, true);
        intent.putExtra(ActionDialogActivity.LAST_FOUR_DIGITS, lastDigits);
        startOperationPopUp(intent, getString(R.string.my_cards_e_card_get_cvv_title),
                getString(R.string.my_cards_e_card_get_cvv_subtitle),
                getString(R.string.my_cards_e_card_get_cvv_text));
    }

    private void eCardShowData(String lastDigits) {
        Intent intent = new Intent(MyCardsActivity.this, ActionDialogActivity.class);
        intent.putExtra(ActionDialogActivity.E_CARD_SHOW_DATA, true);
        intent.putExtra(ActionDialogActivity.LAST_FOUR_DIGITS, lastDigits);
        intent.putExtra(ActionDialogActivity.TITLE_KEY, getString(R.string.my_cards_e_card_show_data_title));
        intent.putExtra(ActionDialogActivity.SUBTITLE_KEY, getString(R.string.my_cards_e_card_show_data_subtitle));
        intent.putExtra(ActionDialogActivity.TEXT_KEY, getString(R.string.my_cards_e_card_show_data_text));
        startActivityForResult(intent, E_CARD_SHOW_DATA);
        overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
    }

    private void startOperationPopUp(Intent intent, String title, String subtitle, String text){
        intent.putExtra(ActionDialogActivity.TITLE_KEY, title);
        intent.putExtra(ActionDialogActivity.SUBTITLE_KEY, subtitle);
        intent.putExtra(ActionDialogActivity.TEXT_KEY, text);
        startActivityForResult(intent, OPERATION_RESULT);
        overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
    }

    private void refreshCardList() {
        new GetUserCardsTask(this).execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPERATION_RESULT) {
            if (resultCode == RESULT_OK) {
                String successTitle;
                String successSubtitle;
                String successText;
                if (data != null && !TextUtils.isEmpty(data.getStringExtra(ActionDialogActivity.SUCCESS_TITLE))) {
                    successTitle = data.getStringExtra(ActionDialogActivity.SUCCESS_TITLE);
                } else {
                    successTitle = getString(R.string.my_cards_opperation_success_message_title);
                }
                if (data != null && !TextUtils.isEmpty(data.getStringExtra(ActionDialogActivity.SUCCESS_SUBTITLE))) {
                    successSubtitle = data.getStringExtra(ActionDialogActivity.SUCCESS_SUBTITLE);
                } else {
                    successSubtitle = getString(R.string.my_cards_opperation_success_message_subtitle);
                }
                if (data != null && !TextUtils.isEmpty(data.getStringExtra(ActionDialogActivity.SUCCESS_TEXT))) {
                    successText = data.getStringExtra(ActionDialogActivity.SUCCESS_TEXT);
                } else {
                    successText = getString(R.string.my_cards_opperation_success_message);
                }
                DialogUtil.toastWithResult(this, EXPLAINING_DIALOG, successTitle, successSubtitle, successText);
                refresh = true;
            } else if (resultCode == ActionDialogActivity.RESULT_FAILED) {
                generateErrorDialog(data);
            }
        } else if (requestCode == EXPLAINING_DIALOG) {
            refresh = false;
            if (resultCode == ActionDialogActivity.RESULT_FAILED) {
                generateErrorDialog(data);
            }
        } else if (requestCode == E_CARD_SHOW_DATA) {
            if (resultCode == RESULT_OK && data != null && data.getParcelableExtra(ActionDialogActivity.E_CARD_SHOW_DATA) != null) {
                Card cardData = data.getParcelableExtra(ActionDialogActivity.E_CARD_SHOW_DATA);
                refresh = false;
                cardsAdapter.updateCardData(selectedCard, cardData.getFullNumber(), cardData.getExpirationMonth(),
                        cardData.getExpirationYear());
                if(cardData.getMessage() != null && cardData.getMessage().length() > 0) {
                    if(cardData.getMessage().equals("error.show_three_times")) {
                        Intent intent = new Intent(MyCardsActivity.this, ActionDialogActivity.class);
                        intent.putExtra(ActionDialogActivity.TITLE_KEY, getString(R.string.my_cards_show_ecard_three_times_card_title));
                        intent.putExtra(ActionDialogActivity.SUBTITLE_KEY, getString(R.string.my_cards_show_ecard_three_times_card_subtitle));
                        intent.putExtra(ActionDialogActivity.TEXT_KEY, getString(R.string.my_cards_show_ecard_three_times_card_text));
                        intent.putExtra(ActionDialogActivity.SHOW_CALL_BUTTON_KEY, true);
                        intent.putExtra(ActionDialogActivity.CALL_BUTTON_NUMBER_KEY, getString(R.string.my_cards_show_ecard_three_times_card_phone));
                        startActivityForResult(intent, EXPLAINING_DIALOG);
                        overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                    }
                }
            } else if (resultCode == ActionDialogActivity.RESULT_FAILED) {
                generateErrorDialog(data);
            }
        } else if (requestCode == CREATE_E_CARD) {
            if (resultCode == RESULT_OK && data != null && data.getParcelableExtra(ActionDialogActivity.E_CARD_SHOW_DATA) != null) {
                Card cardData = data.getParcelableExtra(ActionDialogActivity.E_CARD_SHOW_DATA);
                showEcardCreatedPopup(cardData);
                refresh = true;
            } else if (resultCode == ActionDialogActivity.RESULT_FAILED) {
                generateErrorDialog(data);
            }
        } else if (requestCode == BLOCK_CARD_RQ) {
            if (resultCode == RESULT_OK) {
                DialogUtil.toastWithResult(this, EXPLAINING_DIALOG, getString(R.string.my_cards_activate_blocked_card_title),
                        getString(R.string.my_cards_activate_blocked_card_subtitle),
                        getString(R.string.my_cards_activate_blocked_card_text));
                refresh = true;
            } else if (resultCode == ActionDialogActivity.RESULT_FAILED) {
                generateErrorDialog(data);
            }
        }
    }

    private void generateErrorDialog(Intent data) {
        String errorTitle;
        String errorSubtitle = "";
        String errorText;
        if(data != null && !TextUtils.isEmpty(data.getStringExtra(ActionDialogActivity.ERROR_TITLE))){
            errorTitle = data.getStringExtra(ActionDialogActivity.ERROR_TITLE);
        }else{
            errorTitle = getString(R.string.my_cards_failed_opperation_message_title);
        }
        if(data != null && !TextUtils.isEmpty(data.getStringExtra(ActionDialogActivity.ERROR_SUBTITLE))){
            errorSubtitle = data.getStringExtra(ActionDialogActivity.ERROR_SUBTITLE);
        }
        if(data != null && !TextUtils.isEmpty(data.getStringExtra(ActionDialogActivity.ERROR_MESSAGE))){
            errorText = data.getStringExtra(ActionDialogActivity.ERROR_MESSAGE);
        }else{
            errorText = getString(R.string.my_cards_failed_opperation_message);
        }
        DialogUtil.toastWithResult(this,
                EXPLAINING_DIALOG,
                errorTitle, errorSubtitle,
                errorText);
        refresh = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDaviPoints();
        if (refresh) {
            refreshCardList();
        }
    }

    private class CardsAdapter extends RecyclerView.Adapter {

        private List<CardToShowOnList> cards;

        CardsAdapter() {
            cards = new ArrayList<>();
        }

        @Override
        public int getItemViewType(int position) {
            return cards.get(position).getTypeOfCard();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == CardToShowOnList.CARD) {
                return new ActualCardHolder(getLayoutInflater().inflate(R.layout.item_my_cards_card, parent, false));
            } else {
                return new ButtonCardHolder(getLayoutInflater().inflate(R.layout.item_new_card_to_list, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder genericHolder, int position) {
            if (getItemViewType(position) == CardToShowOnList.BUTTON_CARD) {
                ButtonCard buttonCard = (ButtonCard) cards.get(position);
                ButtonCardHolder holder = (ButtonCardHolder) genericHolder;
                holder.cardButton.setImageResource(buttonCard.getImageResource());
                if (buttonCard.getType() == ButtonCard.portfolioCard) {
                    holder.cardButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            blockedActivableCardDialog(null);
                        }
                    });
                } else {
                    holder.cardButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            createECard();
                        }
                    });
                }
            } else if (getItemViewType(position) == CardToShowOnList.CARD) {
                final Card card = (Card) cards.get(position);
                ActualCardHolder holder = (ActualCardHolder) genericHolder;
                if (card.getBin() != null) {
                    ImageUtils.loadCardImage(MyCardsActivity.this, holder.card, card.getBin().getImage());
                }
                if(TextUtils.isEmpty(card.getFullNumber())) {
                    holder.number.setText(CardsUtils.getMaskedCardNumber(card.getLastDigits()));
                }else{
                    holder.number.setText(CardsUtils.formatFullNumber(card.getFullNumber()));
                }
                if(TextUtils.isEmpty(card.getExpirationMonth()) || TextUtils.isEmpty(card.getExpirationYear())) {
                    holder.date.setText(CardsUtils.getMaskedExpirationDate());
                }else{
                    holder.date.setText(String.format("%s%s%s", card.getExpirationMonth(), " / ", card.getExpirationYear()));
                }
                holder.name.setText(card.getOwnerName());
                holder.card.setOnClickListener(getCardOnClickListener(card));

                CardState cardState = CardState.getCardState(card);
                CardState.CardButtonsState buttonsState = CardState.getButtonsState(cardState);
                makeAlphaImage(holder, CardState.blurredImage(cardState));
                makeBlackAndWhiteImage(holder, CardState.blackAndWhiteImage(cardState));

                setButtonsState(holder, card, cardState, buttonsState);
            }
        }

        private void setButtonsState(ActualCardHolder holder, Card card, CardState cardState, CardState.CardButtonsState cardButtonsState) {
            setFavouriteButtonState(holder, card, cardButtonsState);
            setActivateButtonState(holder, card, cardState, cardButtonsState);
            setBlockButtonState(holder, card, cardState, cardButtonsState);
            setECardDataButtonState(holder, card);

        }

        private void setECardDataButtonState(final ActualCardHolder holder, final Card card) {
            if(card.getECard() != null && card.getECard()) {
                holder.eCardData.setVisibility(View.VISIBLE);
                holder.eCardData.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        eCardShowData(card.getLastDigits());
                        selectedCard = holder.getAdapterPosition();
                    }
                });
            }else{
                holder.eCardData.setVisibility(View.GONE);
            }
        }

        private void setFavouriteButtonState(ActualCardHolder holder, Card card, CardState.CardButtonsState cardButtonsState) {
            if (cardButtonsState.favouritetButton == null || !card.getEnrolled()) {
                holder.favouriteCard.setVisibility(View.INVISIBLE);
            } else {
                holder.favouriteCard.setVisibility(View.VISIBLE);
                holder.favouriteCard.setImageResource(card.getDefaultCard() ? R.drawable.my_cards_favourite_card_indicator : R.drawable.my_cards_not_favourite_card);
                holder.favouriteCard.setOnClickListener(getFavouriteOnclickListener(card));
                if (card.getDefaultCard()) {
                    holder.favouriteCard.setOnClickListener(getFavouriteOnclickListener(null));
                } else {
                    holder.favouriteCard.setOnClickListener(getFavouriteOnclickListener(card));
                }
            }
        }

        private View.OnClickListener getFavouriteOnclickListener(final Card card) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SetFavouriteCardTask(MyCardsActivity.this, card != null ? card.getLastDigits() : "").execute();
                }
            };
        }

        private void setActivateButtonState(ActualCardHolder holder, Card card, CardState cardState, CardState.CardButtonsState cardButtonsState) {
            if (cardButtonsState.activateButton == null) {
                holder.checkCardData.setVisibility(View.INVISIBLE);
            } else {
                holder.checkCardData.setVisibility(View.VISIBLE);
                holder.checkCardData.setImageResource(cardButtonsState.activateButton ? R.drawable.my_cards_avalaible_card_indicator : R.drawable.my_cards_avalaible_card_indicator_disabled);
                if (cardButtonsState.activateButton) {
                    holder.checkCardData.setOnClickListener(getCheckCardOnClickListener(card, cardState));
                } else {
                    holder.checkCardData.setOnClickListener(null);
                }
            }
        }

        private void setBlockButtonState(ActualCardHolder holder, Card card, CardState cardState, CardState.CardButtonsState cardButtonsState) {
            if (cardButtonsState.blockButton == null) {
                holder.disableCard.setVisibility(View.INVISIBLE);
            } else {
                holder.disableCard.setVisibility(View.VISIBLE);
                holder.disableCard.setImageResource(cardButtonsState.blockButton ? R.drawable.my_cards_block_card_button : R.drawable.my_cards_disable_card_button);
                if (cardButtonsState.blockButton) {
                    holder.disableCard.setOnClickListener(getBlockCardOnClickListener(card));
                } else {
                    holder.disableCard.setOnClickListener(null);
                }
            }
        }

        private View.OnClickListener getBlockCardOnClickListener(final Card card) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBlockCardPopup(card);
                }
            };
        }

        private View.OnClickListener getCheckCardOnClickListener(final Card card, final CardState cardState) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cardState == CardState.ACTIVE_NOT_ENROLED) {
                        //Popup para enrolar tarjeta
                        enrollCardDialog(card);
                    }
                    if (cardState == CardState.BLOCKED_ACTIVABLE) {
                        //Popup para activar tarjeta
                        blockedActivableCardDialog(card);
                    }
                    if (cardState == CardState.BLOCKED) {
                        //Si lo indica el mensaje, popup para llamar al call center.
                        blockedCardDialog();
                    }
                }
            };
        }

        private void showBlockCardPopup(Card card) {
            //Popup para enrolar tarjeta
            Intent intent = new Intent(MyCardsActivity.this, ActionDialogActivity.class);
            intent.putExtra(ActionDialogActivity.TITLE_KEY, getString(R.string.my_cards_block_card_title));
            intent.putExtra(ActionDialogActivity.TEXT_KEY, getString(R.string.my_cards_block_card_text));
            intent.putExtra(ActionDialogActivity.IS_BLOCK_CARD_DIALOG, true);
            intent.putExtra(ActionDialogActivity.CARD_KEY, card);
            startActivityForResult(intent, BLOCK_CARD_RQ);
            overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
        }

        @NonNull
        private View.OnClickListener getCardOnClickListener(final Card card) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (card.getPay() && card.getMessage() != null) {
                            Intent intent = new Intent(MyCardsActivity.this, ActionDialogActivity.class);
                        intent.putExtra(ActionDialogActivity.TITLE_KEY, getString(R.string.my_cards_pay_card_title));
                        intent.putExtra(ActionDialogActivity.SUBTITLE_KEY, getString(R.string.my_cards_pay_card_subtitle));
                        if (card.getMessage().equalsIgnoreCase("card_status.blocked_sobrecupo")) {
                            intent.putExtra(ActionDialogActivity.TEXT_KEY, getString(R.string.my_cards_pay_card_text_1));
                        }
                        if (card.getMessage().equalsIgnoreCase("card_status.blocked_rediferido")) {
                            intent.putExtra(ActionDialogActivity.TEXT_KEY, getString(R.string.my_cards_pay_card_text_2));
                        }
                        intent.putExtra(ActionDialogActivity.SHOW_PAY_BUTTON_KEY, true);
                        intent.putExtra(ActionDialogActivity.CARD_KEY, card);
                        startActivityForResult(intent, EXPLAINING_DIALOG);
                        overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                        return;
                    }
                    if (!TextUtils.isEmpty(card.getMessage())) {
                        Intent intent = new Intent(MyCardsActivity.this, ActionDialogActivity.class);
                        if(card.getMessage().equals("card_status.blocked_call_center")) {
                            intent.putExtra(ActionDialogActivity.TITLE_KEY, getString(R.string.my_cards_blocked_call_card_title));
                            intent.putExtra(ActionDialogActivity.SUBTITLE_KEY, getString(R.string.my_cards_blocked_call_card_subtitle));
                            intent.putExtra(ActionDialogActivity.TEXT_KEY, getString(R.string.my_cards_blocked_call_card_text));
                            intent.putExtra(ActionDialogActivity.SHOW_CALL_BUTTON_KEY, true);
                            intent.putExtra(ActionDialogActivity.CALL_BUTTON_NUMBER_KEY, getString(R.string.my_cards_blocked_call__phone));
                            startActivityForResult(intent, EXPLAINING_DIALOG);
                            overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                            return;
                        } else if(card.getMessage().equals("card_status.next_to_expire")){
                            intent.putExtra(ActionDialogActivity.TITLE_KEY, getString(R.string.my_cards_next_to_expire_card_title));
                            intent.putExtra(ActionDialogActivity.SUBTITLE_KEY, getString(R.string.my_cards_next_to_expire_card_subtitle));
                            intent.putExtra(ActionDialogActivity.TEXT_KEY, getString(R.string.my_cards_next_to_expire_card_text));
                            intent.putExtra(ActionDialogActivity.NEXT_TO_EXPIRE, true);
                            intent.putExtra(CardDetailActivity.CARD_KEY, card);
                            startActivityForResult(intent, EXPLAINING_DIALOG);
                            overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                            return;
                        }
                    }
                    if (card.getActivate()) {
                        blockedActivableCardDialog(card);
                        return;
                    }
                    if (card.getEnrolling() && !card.getEnrolled()) {
                        enrollCardDialog(card);
                        return;
                    }
                    if (card.getEnrolled() && !card.getActivate()) {
                        Intent intent = new Intent(MyCardsActivity.this, CardDetailActivity.class);
                        intent.putExtra(CardDetailActivity.CARD_KEY, card);
                        startActivity(intent);
                    }
                }
            };
        }

        private void makeAlphaImage(ActualCardHolder holder, Boolean alphaImage) {
            if (alphaImage) {
                holder.translucentCovering.setVisibility(View.VISIBLE);
            } else {
                holder.translucentCovering.setVisibility(View.GONE);
            }
        }

        private void makeBlackAndWhiteImage(ActualCardHolder holder, Boolean blackAndWhite) {
            ColorMatrix matrix = new ColorMatrix();
            if (blackAndWhite) {
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

        public void updateCardData(int selectedCard, String cardNumber, String expirationMonth, String expirationYear) {
            Card card = (Card) cards.get(selectedCard);
            card.setFullNumber(cardNumber);
            card.setExpirationMonth(expirationMonth);
            card.setExpirationYear(expirationYear);
            notifyItemChanged(selectedCard);
        }

        private class ButtonCardHolder extends RecyclerView.ViewHolder {

            private ImageView cardButton;

            ButtonCardHolder(View itemView) {
                super(itemView);
                cardButton = (ImageView) itemView.findViewById(R.id.card_image);
            }
        }

        private class ActualCardHolder extends RecyclerView.ViewHolder {

            private ImageView card;
            private View translucentCovering;
            private ImageView favouriteCard;
            private ImageView checkCardData;
            private ImageView disableCard;
            private ImageView eCardData;
            private TextView name;
            private TextView number;
            private TextView date;

            ActualCardHolder(View itemView) {
                super(itemView);
                card = (ImageView) itemView.findViewById(R.id.card);
                translucentCovering = itemView.findViewById(R.id.translucent_covering);
                favouriteCard = (ImageView) itemView.findViewById(R.id.favourite_card);
                checkCardData = (ImageView) itemView.findViewById(R.id.check_card_data);
                disableCard = (ImageView) itemView.findViewById(R.id.disable_card);
                eCardData = (ImageView) itemView.findViewById(R.id.e_card_data);
                name = (TextView) itemView.findViewById(R.id.credit_card_name);
                number = (TextView) itemView.findViewById(R.id.credit_card_number);
                date = (TextView) itemView.findViewById(R.id.expiration_date);
            }
        }
    }

    public class SetFavouriteCardTask extends DaviPayTask<Boolean> {

        String cardNumber;

        public SetFavouriteCardTask(BaseActivity activity, String cardNumber) {
            super(activity);
            this.cardNumber = cardNumber;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean response = null;
            try {
                String sid = Session.getCurrentSession(getApplicationContext()).getSid();
                response = Service.setFavouriteCard(sid, cardNumber.isEmpty() ? null : cardNumber);
            } catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);
            if(!processedError) {
                refreshCardList();
            }
        }

    }

    public class GetUserCardsTask extends DaviPayTask<MyCardsResponse> {

        public GetUserCardsTask(BaseActivity activity) {
            super(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected MyCardsResponse doInBackground(Void... params) {
            MyCardsResponse response = null;
            try {
                String sid = Session.getCurrentSession(getApplicationContext()).getSid();
                response = Service.getUserCards(sid, getTodo1Data());
            } catch (ServiceException e) {
                e.printStackTrace();
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(MyCardsResponse response) {
            super.onPostExecute(response);
            if(!processedError) {
                cardsAdapter.setList(addButtons(response));
            }
        }
    }

}
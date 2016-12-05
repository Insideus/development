package com.davivienda.billetera.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

import com.davivienda.billetera.R;
import com.davivienda.billetera.model.LoginSteps;
import com.davivienda.billetera.model.ServiceException;
import com.davivienda.billetera.model.UserInterest;
import com.davivienda.billetera.service.Service;
import com.davivienda.billetera.session.Session;
import com.davivienda.billetera.tasks.DaviPayTask;

public class InterestsPickerActivity extends BaseActivity{

    private InterestAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interets_picker_activity_layout);
        setActionBar(getString(R.string.interests_picker_title), false);
        setRecycler();
        setContinueButton();
        Session.getCurrentSession(this).setPendingStep(LoginSteps.CATEGORIES_OF_INTEREST.getStep());
    }

    private void setRecycler() {
        RecyclerView recycler = (RecyclerView) findViewById(R.id.interests_recycler);
        if(recycler == null){
            return;
        }
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InterestAdapter();
        recycler.setAdapter(adapter);
        adapter.setList(Session.getCurrentSession(this).getUserInterests());
    }

    private void setContinueButton() {
        findViewById(R.id.send_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SetUserInterestsTask(InterestsPickerActivity.this, getSelectedInterests()).execute();
            }
        });
    }

    private class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.InterestHolder> {

        private List<UserInterest> interests;

        public InterestAdapter(){
            interests = new ArrayList<>();
        }

        public class InterestHolder extends RecyclerView.ViewHolder {

            private CheckBox checkBox;

            public InterestHolder(View itemView) {
                super(itemView);
                checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            }
        }

        @Override
        public InterestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new InterestHolder(getLayoutInflater().inflate(R.layout.interest_item, parent, false));
        }

        @Override
        public void onBindViewHolder(InterestAdapter.InterestHolder holder, final int position) {
            UserInterest interest = interests.get(position);
            holder.checkBox.setChecked(interest.getSelected());
            holder.checkBox.setText(interest.getName());
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    interests.get(position).setSelected(isChecked);
                }
            });
        }

        @Override
        public int getItemCount() {
            return interests.size();
        }

        public void setList(List<UserInterest> interests) {
            this.interests = interests;
            notifyDataSetChanged();
        }

        public List<UserInterest> getInterests() {
            return interests;
        }
    }

    public class SetUserInterestsTask extends DaviPayTask<Boolean> {

        private String interestsArray;

        public SetUserInterestsTask(BaseActivity activity, String interestsArray) {
            super(activity);
            this.interestsArray = interestsArray;
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
                response = Service.setUserInterests(sid, interestsArray);
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);
            if(!processedError) {
                if (!response) {
                    //Service error.
                    showServiceGenericError();
                } else {
                    goToEcardActivity();
                }
            }
        }

    }

    private String getSelectedInterests() {
        List<UserInterest> interests = adapter.getInterests();
        String result = "[]";
        Boolean first = true;
        if(interests != null && interests.size() > 0) {
            result = "[";
            for(UserInterest i : interests){
                if(i.getSelected()) {
                    if(!first) {
                        result = result.concat(",");
                    } else {
                        first = false;
                    }
                    result = result.concat(i.getCode());
                }
            }
            result = result.concat("]");
        }
        return result;
    }

    private void goToEcardActivity() {
        startActivity(new Intent(this, EcardLoginActivity.class));
        this.finish();
    }

}

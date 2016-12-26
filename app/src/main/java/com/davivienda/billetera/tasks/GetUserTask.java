package com.davivienda.billetera.tasks;

import com.davivienda.billetera.activities.BaseActivity;
import com.davivienda.billetera.model.User;
import com.davivienda.billetera.service.Service;
import com.davivienda.billetera.session.Session;

/**
 * Created by Julian Vega on 05/07/2016.
 */
public class GetUserTask extends DaviPayTask<User> {

    private TaskCallback callback;
    private BaseActivity act;
    private boolean running;

    public GetUserTask(BaseActivity act, TaskCallback callback) {
        super(act);
        this.act = act;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        running = true;
    }

    @Override
    protected User doInBackground(Void... params) {
        User user = null;
        try {
            String sid = Session.getCurrentSession(act).getSid();
            if (sid != null && sid.length() > 0) {
                user = Service.getUser(sid);
            }
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    protected void onPostExecute(User user) {
        running = false;
        if(callback != null) {
            callback.execute(user);
        }
    }

}

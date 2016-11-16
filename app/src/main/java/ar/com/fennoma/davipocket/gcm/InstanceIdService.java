package ar.com.fennoma.davipocket.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Julian Vega on 27/05/2016.
 */
public class InstanceIdService extends InstanceIDListenerService {

    public void onTokenRefresh() {
        refreshAllTokens();
    }

    private void refreshAllTokens() {
        // assuming you have defined TokenList as
        // some generalized store for your tokens
        //ArrayList<TokenList> tokenList = TokensList.get();
        InstanceID iid = InstanceID.getInstance(this);
        //iid.getToken(
        //for(tokenItem : tokenList) {
            //tokenItem.token = iid.getToken(tokenItem.authorizedEntity,tokenItem.scope,tokenItem.options);
            // send this tokenItem.token to your server
        //}
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }

}

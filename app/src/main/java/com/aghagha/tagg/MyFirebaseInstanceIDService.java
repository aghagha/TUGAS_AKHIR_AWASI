package com.aghagha.tagg;

import android.util.Log;

import com.aghagha.tagg.data.AntaraSessionManager;
import com.aghagha.tagg.utilities.NetworkUtils;
import com.aghagha.tagg.utilities.VolleyUtil;
import com.android.volley.VolleyError;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by aghagha on 19/05/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("TOKEN", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        AntaraSessionManager sessionManager = new AntaraSessionManager(this.getBaseContext());
        String oldToken = sessionManager.getDeviceToken();
        Log.d("OLD TOKEN IS: ", oldToken);
        sessionManager.setDeviceToken(refreshedToken);
        final String id = sessionManager.getKeyId();
        VolleyUtil volleyUtil = new VolleyUtil("sendingtoken",this.getBaseContext(), NetworkUtils.token+"/"+id);
        HashMap<String,String> params = new HashMap<>();
        params.put("old_token",oldToken);
        params.put("token",refreshedToken);
        Log.d("sendToken","oke2");
        volleyUtil.SendRequestPOST(params,new VolleyUtil.VolleyResponseListener() {
            @Override
            public void onError(VolleyError error) {
                Log.d("FireBaseInstantID","Failed to save token "+id);
                error.printStackTrace();
            }

            @Override
            public void onResponse(String response) {
                Log.d("sendToken","oke3");
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("FireBaseInstantID","Token refreshed and saved");
                    if(jsonObject.getString("code").equals("0")){
                        Log.d("FireBaseInstantID",jsonObject.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

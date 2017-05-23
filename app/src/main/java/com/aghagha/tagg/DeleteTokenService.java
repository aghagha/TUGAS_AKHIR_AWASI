package com.aghagha.tagg;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.aghagha.tagg.data.AntaraSessionManager;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

public class DeleteTokenService extends IntentService {

    public DeleteTokenService() {
        super("DeleteTokenService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
            FirebaseInstanceId.getInstance().getToken();

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}

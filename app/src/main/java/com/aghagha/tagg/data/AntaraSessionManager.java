package com.aghagha.tagg.data;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;

import com.aghagha.tagg.LoginActivity;
import com.aghagha.tagg.utilities.NetworkUtils;
import com.aghagha.tagg.utilities.VolleyUtil;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aghagha on 20/04/2017.
 */

public class AntaraSessionManager {
    private static String TAG = AntaraSessionManager.class.getSimpleName();

    SharedPreferences pref;
    Editor editor;
    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "AntaraLogin";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_MURID_ID = "muridId";
    public static final String KEY_TIPE_USER_ID = "userTipeId";
    public static final String KEY_NAME = "userName";
    public static final String KEY_EMAIL = "userEmail";
    public static final String DEVICE_TOKEN = "deviceToken";

    public AntaraSessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String userId, String tipe, String name, String email) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_TIPE_USER_ID, tipe);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.commit();
        Log.d(TAG, "User login session modified!");
    }

    public void setMurid(String muridId){
        editor.putString(KEY_MURID_ID,muridId);
        editor.commit();
    }

    public void setDeviceToken(String token){
        editor.putString(DEVICE_TOKEN,token);
        editor.commit();
    }

    public void checkLogin(){
        if(!this.isLoggedIn()){
            Intent intent = new Intent(_context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // Starting Login Activity
            _context.startActivity(intent);
        }
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<>();

        user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));
        user.put(KEY_TIPE_USER_ID, pref.getString(KEY_TIPE_USER_ID, null));
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        return user;
    }

    public void logoutUser(final Context ctx){
        // Clearing all data from Shared Preferences
        final ProgressDialog progressDialog = new ProgressDialog(ctx);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Mencoba logout...");
        progressDialog.show();

        String oldToken = this.getDeviceToken();
        final Boolean[] success = {false};
        VolleyUtil volleyUtil = new VolleyUtil("req_login",_context, NetworkUtils.token);
        Map<String,String> param = new HashMap<>();
        param.put("old_token",oldToken);
        volleyUtil.SendRequestPOST(param,new VolleyUtil.VolleyResponseListener() {
            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(_context, "Logout gagal...", Toast.LENGTH_SHORT).show();
                Log.d("FireBaseInstantID","Failed to delete old token");
                error.printStackTrace();
            }

            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d("delete token","new delete token");
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("code").equals("0")){
                        Toast.makeText(_context, "Logout gagal...", Toast.LENGTH_SHORT).show();
                        Log.d("FireBaseInstantID","Failed to delete old token");
                    } else {
                        Log.d("FireBaseInstantID","Token deleted");
                        success[0] = true;
                        editor.clear();
                        editor.commit();
                        Intent intent = new Intent(_context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        // Starting Login Activity
                        _context.startActivity(intent);
                        ((Activity)ctx).finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public String getKeyEmail(){
        return pref.getString(KEY_EMAIL,null);
    }
    public String getDeviceToken(){
        return pref.getString(DEVICE_TOKEN,"kosong");
    }

    public void setKeyEmail(String email){
        editor.putString(KEY_EMAIL,email);
        editor.apply();
        return;
    }
    public String getKeyId(){
        return pref.getString(KEY_USER_ID,null);
    }
    public String getKeyMuridId(){
        return pref.getString(KEY_MURID_ID,null);
    }


    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

}

package com.aghagha.tagg.data;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.aghagha.tagg.LoginActivity;

import java.util.HashMap;

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

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        Intent intent = new Intent(_context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Staring Login Activity
        _context.startActivity(intent);

    }

    public String getKeyEmail(){
        return pref.getString(KEY_EMAIL,null);
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

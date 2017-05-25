package com.aghagha.tagg;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aghagha.tagg.utilities.NetworkUtils;
import com.aghagha.tagg.utilities.VolleyUtil;
import com.aghagha.tagg.data.AntaraSessionManager;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    EditText et_email, et_password;
    TextView tv_register;
    ProgressDialog pDialog;
    private AntaraSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        tv_register= (TextView) findViewById(R.id.tv_register);
        Button bt_submit = (Button) findViewById(R.id.bt_submit);

        tv_register.setOnClickListener(operation);
        bt_submit.setOnClickListener(operation);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        session = new AntaraSessionManager(getApplicationContext());

        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            HashMap<String,String> user = session.getUserDetails();
            Intent intent;
            if(user.get(AntaraSessionManager.KEY_TIPE_USER_ID).equals("3")){
                intent = new Intent(LoginActivity.this, GuruActivity.class);
            } else intent = new Intent(LoginActivity.this, OrangTuaActivity.class);

            startActivity(intent);
            finish();
        }

        if (shouldAskPermissions()) {
            askPermissions();
        }
    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Tekan Kembali sekali lagi untuk keluar",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }

    View.OnClickListener operation = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch(id){
                case R.id.bt_submit:
                    Log.d(TAG,"Logging in..");
                    kirimRequest(et_email.getText().toString(), et_password.getText().toString());
                    break;
                case R.id.tv_register:
                    Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }

        }
    };

    private void kirimRequest(final String email, final String password){
        pDialog.setMessage("Sedang masuk...");
        showDialog();
        VolleyUtil volleyUtil = new VolleyUtil("req_login",this,NetworkUtils.login);
        Map<String,String> param = new HashMap<>();
        param.put("email",email);
        param.put("password",password);
        volleyUtil.SendRequestPOST(param, new VolleyUtil.VolleyResponseListener() {
            @Override
            public void onError(VolleyError error) {
                hideDialog();
                Toast.makeText(LoginActivity.this, "Login gagal...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String pesan = jsonObject.getString("message");
                    String kode = jsonObject.getString("code");
                    if(kode.equals("1")){
                        JSONObject user = jsonObject.getJSONObject("user");
                        String userId = user.getString("id");
                        String userNama = user.getString("nama");
                        String userEmail = user.getString("email");
                        String userTipe = user.getString("id_tipe_user");

                        session.createLoginSession(userId,userTipe,userNama,userEmail);
                        startService(new Intent(getBaseContext(), DeleteTokenService.class));

                        if(userTipe.equals("4")){
                            Intent intent = new Intent(LoginActivity.this,OrangTuaActivity.class);
                            Toast.makeText(LoginActivity.this, pesan, Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        } else if(userTipe.equals("3")){
                            Toast.makeText(LoginActivity.this, pesan, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this,GuruActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(LoginActivity.this, pesan, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, pesan, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                Log.d(TAG,response);
            }
        });
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}

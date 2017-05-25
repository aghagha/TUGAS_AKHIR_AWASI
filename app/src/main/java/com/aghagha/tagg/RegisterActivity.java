package com.aghagha.tagg;

import android.app.ProgressDialog;
import android.content.Intent;
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

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText et_email, et_password, et_nama, et_telepon, et_password2;
    TextView tv_login;
    ProgressDialog pDialog;
    private AntaraSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_email = (EditText) findViewById(R.id.et_email);
        et_telepon = (EditText) findViewById(R.id.et_telepon);
        et_nama = (EditText) findViewById(R.id.et_nama);
        et_password = (EditText) findViewById(R.id.et_password);
        et_password2 = (EditText) findViewById(R.id.et_password2);
        tv_login = (TextView) findViewById(R.id.tv_login);
        Button bt_submit = (Button) findViewById(R.id.bt_submit);

        tv_login.setOnClickListener(operation);
        bt_submit.setOnClickListener(operation);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        session = new AntaraSessionManager(getApplicationContext());

//        if (session.isLoggedIn()) {
//            // User is already logged in. Take him to main activity
//            Intent intent = new Intent(RegisterActivity.this,
//                    MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
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

    View.OnClickListener operation = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch(id){
                case R.id.tv_login:
                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.bt_submit:
                    String nama = et_nama.getText().toString();
                    String email = et_email.getText().toString();
                    String telepon = et_telepon.getText().toString();
                    String password = et_password.getText().toString();
                    String password2 = et_password2.getText().toString();

                    if(!nama.isEmpty() && !email.isEmpty() && !telepon.isEmpty() && !password.isEmpty() & !password2.isEmpty()){
                        if(!password.equals(password2)) {
                            Toast.makeText(RegisterActivity.this, "Password tidak cocok", Toast.LENGTH_SHORT).show();
                        } else {
                            kirimRequest(et_nama.getText().toString(),
                                    et_email.getText().toString(),
                                    et_telepon.getText().toString(),
                                    et_password.getText().toString(),
                                    et_password2.getText().toString());
                        }
                    } else Toast.makeText(RegisterActivity.this, "Isi semua field terlebih dahulu", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void kirimRequest(final String nama, final String email, final String telepon, final String password, final String password2){
        pDialog.setMessage("Sedang didaftarkan...");
        showDialog();
        VolleyUtil volleyUtil = new VolleyUtil("req_registrasi",this,NetworkUtils.register);
        Map<String,String> params = new HashMap<>();
        params.put("nama",nama);
        params.put("email",email);
        params.put("telepon",telepon);
        params.put("password",password);
        params.put("password2",password2);
        volleyUtil.SendRequestPOST(params, new VolleyUtil.VolleyResponseListener() {
            @Override
            public void onError(VolleyError error) {
                hideDialog();
                error.printStackTrace();
                Log.d("###",error.toString());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(RegisterActivity.this, pesan, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    } else {
                        Toast.makeText(RegisterActivity.this, pesan, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
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

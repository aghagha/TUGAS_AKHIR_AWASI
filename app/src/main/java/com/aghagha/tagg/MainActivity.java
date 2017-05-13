package com.aghagha.tagg;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aghagha.tagg.data.AntaraSessionManager;

import java.util.HashMap;

public class MainActivity extends FragmentActivity {
    HashMap<String,String> user;
    AntaraSessionManager session;
    TextView tv_nama, tv_email;
    Button bt_logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tv_nama = (TextView)findViewById(R.id.tv_nama);
        tv_email = (TextView)findViewById(R.id.tv_email);
        bt_logout = (Button)findViewById(R.id.bt_logout);

        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.logoutUser();
            }
        });

        session = new AntaraSessionManager(getApplicationContext());
        user = session.getUserDetails();
        tv_nama.setText(user.get("userName"));
        tv_email.setText(user.get("userEmail"));
    }
}

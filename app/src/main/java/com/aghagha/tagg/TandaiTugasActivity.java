package com.aghagha.tagg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.aghagha.tagg.data.AntaraSessionManager;

public class TandaiTugasActivity extends AppCompatActivity {
    AntaraSessionManager session;
    ProgressDialog pDialog;
    String tugasId;

    TextView judul, dibuat, deadline, konten;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tandai_tugas);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        tugasId = String.valueOf(intent.getIntExtra("id",0));
        pDialog = new ProgressDialog(this);
        session = new AntaraSessionManager(this);

        judul = (TextView)findViewById(R.id.tvJudul);
        dibuat = (TextView)findViewById(R.id.tvCreated);
        deadline = (TextView)findViewById(R.id.tvDeadline);
        konten = (TextView)findViewById(R.id.tvKonten);

        judul.setText(intent.getStringExtra("judul"));
        dibuat.setText(intent.getStringExtra("dibuat"));
        deadline.setText(intent.getStringExtra("deadline"));
        konten.setText(intent.getStringExtra("konten"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }


}

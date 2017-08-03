package com.aghagha.tagg;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aghagha.tagg.data.AntaraSessionManager;
import com.aghagha.tagg.utilities.NetworkUtils;
import com.aghagha.tagg.utilities.VolleyUtil;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public class TandaiTugasActivity extends AppCompatActivity {
    AntaraSessionManager session;
    ProgressDialog pDialog;
    String tugasId, cek, deadline_, status;
    String[] idMurid;

    TextView judul, dibuat, deadline, konten;
    LinearLayout llCekDeadline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tandai_tugas);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);

        final Intent intent = getIntent();
        tugasId = String.valueOf(intent.getStringExtra("id"));
        cek = intent.getStringExtra("cek");
        status = intent.getStringExtra("status");
        deadline_ = intent.getStringExtra("deadline");
        toolbar.setTitle(intent.getStringExtra("judul"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        pDialog = new ProgressDialog(this);
        session = new AntaraSessionManager(this);

        if(session.getKeyMuridId()==null){
            idMurid = intent.getStringArrayExtra("murid");
        } else idMurid = null;

        judul = (TextView)findViewById(R.id.tvJudul);
        dibuat = (TextView)findViewById(R.id.tvCreated);
        deadline = (TextView)findViewById(R.id.tvDeadline);
        konten = (TextView)findViewById(R.id.tvKonten);
        llCekDeadline = (LinearLayout)findViewById(R.id.llCekDeadline);

        String judul_ = intent.getStringExtra("judul");

        judul.setText(judul_);
        dibuat.setText(intent.getStringExtra("dibuat"));
        if(cek.equals("0")){
            deadline.setText("deadline: "+deadline_);
        } else {
            deadline.setText("Sudah dikerjakan");
        }
        konten.setText(intent.getStringExtra("konten"));

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(judul_);
        builder.setMessage("Apakah tugas ini sudah dikerjakan?");
        builder.setPositiveButton("Sudah", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!cek.equals("1"))setStatus("1", intent.getStringExtra("id"));
            }
        });
        builder.setNegativeButton("Belum", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(cek.equals("1"))setStatus("0",intent.getStringExtra("id"));
            }
        });

        deadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.show();
            }
        });

        if(status.equals("1")){
            llCekDeadline.setVisibility(View.GONE);
            ImageView centang = (ImageView)findViewById(R.id.iv_centang);
            TextView tvNilai = (TextView)findViewById(R.id.tvNilai);
            LinearLayout layout_grafik = (LinearLayout)findViewById(R.id.layout_grafik);
            Spinner spinner = (Spinner)findViewById(R.id.tipe_grafik);
            centang.setVisibility(View.VISIBLE);
            tvNilai.setVisibility(View.VISIBLE);
            layout_grafik.setVisibility(View.VISIBLE);

            String[] spinnerArray = new String[2];
            spinnerArray[0] = "Mata Pelajaran";
            spinnerArray[1] = "Kelas";
            ArrayAdapter<String> grafikAdapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_spinner_dropdown_item,spinnerArray);
            spinner.setAdapter(grafikAdapter);
        }
    }

    public void setStatus(String status, String id_tugas){
        pDialog.setMessage("Sedang memuat...");
        pDialog.show();

        String id;
        if(idMurid == null) id = session.getKeyMuridId();
        else id = idMurid[Integer.parseInt(session.getKeyMuridId())].toString();

        VolleyUtil volleyUtil = new VolleyUtil("req_update_status_penilaian",this.getBaseContext(), NetworkUtils.tugasMurid+"/"+id+"/"+id_tugas+"/"+status);
        volleyUtil.SendRequestGET(new VolleyUtil.VolleyResponseListener() {
            @Override
            public void onError(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(TandaiTugasActivity.this, "Gagal menandai tugas...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                try {
                    String code = new JSONObject(response).getString("code");
                    if(code.equals("1")){
                        Toast.makeText(TandaiTugasActivity.this, "Tugas sudah ditandai!", Toast.LENGTH_SHORT).show();
                        if(cek.equals("1")){
                            cek = "0";
                            deadline.setText("deadline: "+deadline_);
                        } else {
                            cek = "0";
                            deadline.setText("Sudah dikerjakan");
                        }
                    } else {
                        Toast.makeText(TandaiTugasActivity.this,new JSONObject(response).getString("message")+",dari:", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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

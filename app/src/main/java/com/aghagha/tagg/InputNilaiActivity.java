package com.aghagha.tagg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.aghagha.tagg.data.AntaraSessionManager;
import com.aghagha.tagg.models.Nilai;
import com.aghagha.tagg.utilities.NetworkUtils;
import com.aghagha.tagg.utilities.VolleyUtil;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InputNilaiActivity extends AppCompatActivity {
    AntaraSessionManager session;
    ProgressDialog pDialog;
    String tugasId;

    private RecyclerView mRecyclerView;
    private NilaiGuruAdapter mAdapter;
    private List<Nilai> nilaiList;

    TextView judul, dibuat, deadline, konten;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_nilai);
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
        mRecyclerView = (RecyclerView)findViewById(R.id.rvNilai);

        judul.setText(intent.getStringExtra("judul"));
        dibuat.setText(intent.getStringExtra("dibuat"));
        deadline.setText(intent.getStringExtra("deadline"));
        konten.setText(intent.getStringExtra("konten"));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(InputNilaiActivity.this, LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.hasFixedSize();
        nilaiList = new ArrayList<>();
        mAdapter = new NilaiGuruAdapter(nilaiList);
        mRecyclerView.setAdapter(mAdapter);

        getNilaiList();
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

    public void getNilaiList(){
        pDialog.setMessage("Sedang memuat...");
        pDialog.show();

        VolleyUtil volleyUtil = new VolleyUtil("req_list_tugas",InputNilaiActivity.this, NetworkUtils.nilaGuru+"/"+tugasId);
        volleyUtil.SendRequestGET(new VolleyUtil.VolleyResponseListener() {
            @Override
            public void onError(VolleyError error) {
                Toast.makeText(InputNilaiActivity.this, "Halaman gagal dimuat, coba lagi", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }

            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                Log.d("###",response);
                mAdapter.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    if(code.equals("1")){
                        JSONArray listNilai = jsonObject.getJSONArray("nilai");
                        if(listNilai.length()>0){
                            Log.d("JUMLAH NILAI", String.valueOf(listNilai.length()));
                            Nilai data = new Nilai("0","NAMA","NILAI","Status");
                            nilaiList.add(data);
                            for(int i = 0; i < listNilai.length(); i++) {
                                JSONObject nilai = listNilai.getJSONObject(i);
                                data = new Nilai(nilai.getString("id"),
                                                        nilai.getString("nama"),
                                                        nilai.getString("nilai"),
                                                        nilai.getString("status"));
                                nilaiList.add(data);
                                Log.d("nilai "+i, String.valueOf(nilaiList.get(i).getNama()));
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(InputNilaiActivity.this, "Halaman gagal dimuat, coba lagi", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(InputNilaiActivity.this, "Halaman gagal dimuat, coba lagi", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}

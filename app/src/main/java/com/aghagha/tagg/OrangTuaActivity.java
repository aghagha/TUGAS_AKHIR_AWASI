package com.aghagha.tagg;

import android.app.ProgressDialog;
import android.icu.text.LocaleDisplayNames;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.aghagha.tagg.data.AntaraSessionManager;
import com.aghagha.tagg.models.Murid;
import com.aghagha.tagg.utilities.NetworkUtils;
import com.aghagha.tagg.utilities.VolleyUtil;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrangTuaActivity extends AppCompatActivity {
    private MuridAdapter mAdapter;
    private List<Murid> muridList;

    private ProgressDialog progressDialog;
    private AntaraSessionManager session;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orang_tua);

        session = new AntaraSessionManager(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        mRecyclerView = (RecyclerView)findViewById(R.id.rvMurid);
        muridList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.hasFixedSize();

        mAdapter = new MuridAdapter(muridList);
        mRecyclerView.setAdapter(mAdapter);

        getMurid();
    }

    @Override
    public void onResume(){
        super.onResume();
        getMurid();
    }

    private void getMurid(){
        progressDialog.setMessage("Sedang memuat...");
        progressDialog.show();

        VolleyUtil volleyUtil = new VolleyUtil("req_murid_list",this, NetworkUtils.murid+"/"+session.getKeyId());
        volleyUtil.SendRequestGET(new VolleyUtil.VolleyResponseListener() {
            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(OrangTuaActivity.this, "Gagal memuat halaman...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                mAdapter.clear();
                Log.d("INI RESPONSE",response);
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    if(code.equals("1")){
                        JSONArray list = jsonObject.getJSONArray("murid");
                        String wali = jsonObject.getString("wali");
                        if(list.length()>0){
                            for (int i = 0;i<list.length();i++){
                                JSONObject murid = list.getJSONObject(i);
                                Murid data = new Murid(murid.getString("id"),
                                                        murid.getString("nama"),
                                                        murid.getString("kelas"),
                                                        wali,
                                                        murid.getString("gambar"),
                                                        murid.getString("sekolah"));
                                muridList.add(data);
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(OrangTuaActivity.this, "Gagal memuat halaman...", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}

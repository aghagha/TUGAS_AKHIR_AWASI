package com.aghagha.tagg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.icu.text.LocaleDisplayNames;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
    private LinearLayout errorLayout, empty, layout;
    private Button reload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orang_tua);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        Drawable drawable = ContextCompat.getDrawable(this,R.drawable.ic_option);
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        toolbar.setOverflowIcon(drawable);
        setSupportActionBar(toolbar);

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

        errorLayout = (LinearLayout)findViewById(R.id.error);
        empty = (LinearLayout)findViewById(R.id.empty);
        layout = (LinearLayout)findViewById(R.id.layout);
        reload = (Button)findViewById(R.id.btReload);

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMurid();
            }
        });

        getMurid();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main_guru,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.profile:
                Intent intent = new Intent(OrangTuaActivity.this, ProfilActivity.class);
                intent.putExtra("idMurid","0");
                startActivity(intent);
                break;
            case R.id.logout:
                session.logoutUser(OrangTuaActivity.this);
                break;
        }
        return super.onOptionsItemSelected(item);
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
                errorLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onResponse(String response) {
                mAdapter.clear();
                errorLayout.setVisibility(View.GONE);
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    if(code.equals("1")){
                        empty.setVisibility(View.GONE);
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
                        } else {
                            empty.setVisibility(View.VISIBLE);
                        }
                    } else {
                        errorLayout.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}

package com.aghagha.tagg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aghagha.tagg.data.AntaraSessionManager;
import com.aghagha.tagg.models.Komentar;
import com.aghagha.tagg.utilities.NetworkUtils;
import com.aghagha.tagg.utilities.VolleyUtil;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopikActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private KomentarAdapter mAdapter;

    TextView judul, tanggal, konten;
    EditText et_konten;
    Button bt_post;
    ImageView ivGambar;

    private ProgressDialog progressDialog;
    private AntaraSessionManager session;

    private List<Komentar> komentarList;
    String idTopik;
    String judul_, tanggal_, konten_;
    String gambar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topik);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        idTopik = intent.getStringExtra("id_topik");

        progressDialog =  new ProgressDialog(this);
        progressDialog.setCancelable(false);

        session = new AntaraSessionManager(this);
        session.checkLogin();

        komentarList = new ArrayList<>();

        mRecyclerView = (RecyclerView)findViewById(R.id.rvKomentar);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.hasFixedSize();

        mAdapter = new KomentarAdapter(komentarList);
        mRecyclerView.setAdapter(mAdapter);

        judul = (TextView)findViewById(R.id.tvJudul);
        tanggal = (TextView)findViewById(R.id.tvCreated);
        konten = (TextView)findViewById(R.id.tvKonten);
        ivGambar = (ImageView)findViewById(R.id.ivGambar);
        et_konten = (EditText)findViewById(R.id.et_komentar);
        bt_post = (Button) findViewById(R.id.bt_post);

        judul_ = intent.getStringExtra("judul");
        tanggal_ = intent.getStringExtra("tanggal");
        konten_ = intent.getStringExtra("konten");

        judul.setText(judul_);
        tanggal.setText(tanggal_);
        konten.setText(konten_);
        gambar = intent.getStringExtra("gambar");
        if(gambar!=null){
            new DownloadImageTask(ivGambar).execute(NetworkUtils.topik_gambar+gambar);
        }

        getKomentar(idTopik);

        bt_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_konten.getText().toString().equals("")){
                    Toast.makeText(TopikActivity.this, "Komentar masih kosong!", Toast.LENGTH_SHORT).show();
                }
                else postKomentar();
            }
        });
    }

    private void postKomentar() {
        progressDialog.setMessage("Sedang memuat...");
        progressDialog.show();

        VolleyUtil volleyUtil = new VolleyUtil("req_post_komentar",this, NetworkUtils.komentar+"/"+ idTopik);
        Map<String, String> params = new HashMap<>();
        params.put("konten",et_konten.getText().toString());
        params.put("id_user",session.getKeyId());
        volleyUtil.SendRequestPOST(params, new VolleyUtil.VolleyResponseListener() {
            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(TopikActivity.this, "Gagal menambahkan komentar..", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d("SADJSAKDH",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    if(code.equals("1")){
                        Toast.makeText(TopikActivity.this, "Komentar berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                        Komentar data = new Komentar(jsonObject.getString("nama"),
                                et_konten.getText().toString(),
                                jsonObject.getString("waktu"),
                                jsonObject.getString("gambar"));
                        komentarList.add(data);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(TopikActivity.this, "Gagal menambahkan komentar..", Toast.LENGTH_SHORT).show();
                    }
                    et_konten.setText("");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getKomentar(String id){
        progressDialog.setMessage("Sedang memuat...");
        progressDialog.show();

        VolleyUtil volleyUtil = new VolleyUtil("req_komentar_topik",this, NetworkUtils.komentar+"/"+ idTopik);
        volleyUtil.SendRequestGET(new VolleyUtil.VolleyResponseListener() {
            @Override
            public void onError(VolleyError error) {
                Toast.makeText(TopikActivity.this,
                        "Halaman gagal dimuat, coba lagi", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                finish();
            }

            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    if(code.equals("1")){
                        JSONArray komentarL = jsonObject.getJSONArray("komentar");
                        if(komentarL.length()>0){
                            for(int i = 0; i<komentarL.length();i++){
                                JSONObject komentar = komentarL.getJSONObject(i);
                                Komentar data = new Komentar(komentar.getString("nama"),
                                                    komentar.getString("konten"),
                                                    komentar.getString("waktu"),
                                                    komentar.getString("gambar"));
                                komentarList.add(data);
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(TopikActivity.this,
                                "Halaman gagal dimuat, coba lagi", Toast.LENGTH_LONG).show();
                        finish();
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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}

package com.aghagha.tagg;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.aghagha.tagg.data.AntaraSessionManager;
import com.aghagha.tagg.utilities.ImageManager;
import com.aghagha.tagg.utilities.NetworkUtils;
import com.aghagha.tagg.utilities.PermissionUtility;
import com.aghagha.tagg.utilities.VolleyUtil;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.aghagha.tagg.R.id.et_judul;
import static com.aghagha.tagg.R.id.wrap_content;

public class CreateTopikActivity extends AppCompatActivity {
    private String id_kelas;
    private String idTujuan;
    private boolean firstTimeLoad = true;
    private Uri filePath;

    private Spinner kelasSpinner;
    private ArrayAdapter<String> kelasAdapter;
    private HashMap<Integer,String> spinnerMap;
    private Button tambah, ubah, hapus, simpan;
    private ImageView ivGambar;
    private Bitmap bm;
    LinearLayout llSettingGambar;
    EditText judul, konten;


    private ProgressDialog progressDialog;

    private AntaraSessionManager session;
    private ImageManager imageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_topik);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        id_kelas = intent.getStringExtra("id_kelas");

        progressDialog =  new ProgressDialog(this);
        progressDialog.setCancelable(false);

        spinnerMap = new HashMap<>();

        imageManager = new ImageManager(this);

        session = new AntaraSessionManager(this);
        session.checkLogin();

        judul = (EditText)findViewById(R.id.et_judul);
        konten = (EditText)findViewById(R.id.et_konten);
        tambah = (Button)findViewById(R.id.btGambar);
        ubah = (Button)findViewById(R.id.btUbahGambar);
        hapus = (Button)findViewById(R.id.btHapusGambar);
        simpan = (Button)findViewById(R.id.btSimpan);
        ivGambar = (ImageView)findViewById(R.id.ivGambar);
        kelasSpinner = (Spinner)findViewById(R.id.tujuan);
        llSettingGambar = (LinearLayout)findViewById(R.id.llSettingGambar);
        llSettingGambar.setVisibility(View.GONE);

        View.OnClickListener operation = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutupKeyboard();
                switch (view.getId()){
                    case R.id.btGambar:
                        tutupKeyboard();
                        imageManager.selectImage();
                        break;
                    case R.id.btUbahGambar:
                        imageManager.selectImage();
                        break;
                    case R.id.btHapusGambar:
                        hapusGambar();
                        break;
                    case R.id.btSimpan:
                        if(judul.getText().toString().equals("") || konten.getText().toString().equals("")){
                            Toast.makeText(CreateTopikActivity.this, "Judul dan/atau konten tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        } else {
                            imageManager.createTopik(getApplicationContext(),
                                    session.getKeyId(),
                                    id_kelas,
                                    judul.getText().toString(),
                                    idTujuan,
                                    konten.getText().toString(),
                                    "Mengunggah topik",
                                    "Topik berhasil diunggah",
                                    "Topik gagal diunggah");
                        }
                        break;
                }
            }
        };

        simpan.setOnClickListener(operation);
        tambah.setOnClickListener(operation);
        ubah.setOnClickListener(operation);
        hapus.setOnClickListener(operation);

        setUpSpinner(this);
        kelasSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!firstTimeLoad){
                    idTujuan = spinnerMap.get(i);
                } else firstTimeLoad = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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

    public void setUpSpinner(final Context context){
        progressDialog.setMessage("Sedang memuat...");
        progressDialog.show();

        VolleyUtil volleyUtil = new VolleyUtil("req_open_create_topik",this, NetworkUtils.add_topik+"/"+id_kelas);
        volleyUtil.SendRequestGET(new VolleyUtil.VolleyResponseListener() {
            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(String response) {
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    if(code.equals("1")){
                        JSONArray muridArray = jsonObject.getJSONArray("murid");
                        String[] spinnerArray = new String[muridArray.length()+1];
                        spinnerArray[0] = "Kelas";
                        spinnerMap.put(0,"0");
                        for (int i = 1; i <= muridArray.length(); i++){
                            JSONObject murid = muridArray.getJSONObject(i-1);
                            spinnerArray[i] = murid.getString("nama");
                            spinnerMap.put(i,murid.getString("id"));
                        }

                        kelasAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item,spinnerArray);
                        kelasSpinner.setAdapter(kelasAdapter);

                        idTujuan = spinnerMap.get(0);
                    } else {
                        Toast.makeText(CreateTopikActivity.this, "Halaman gagal dimuat,coba lagi", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionUtility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(imageManager.userChoosenTask.equals("Ambil Foto"))
                        imageManager.cameraIntent();
                    else if(imageManager.userChoosenTask.equals("Pilih dari Galeri"))
                        imageManager.galleryIntent();
                } else {

                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == imageManager.SELECT_FILE) {
                bm = imageManager.onSelectFromGalleryResult(data);
                if(bm!=null){
                    ivGambar.setImageBitmap(bm);
                    toggleButton(false);
                    filePath = imageManager.filePath;
                }
            }
            else if (requestCode == imageManager.REQUEST_CAMERA) {
                bm = imageManager.onCaptureImageResult(data);
                if(bm!=null){
                    ivGambar.setImageBitmap(bm);
                    toggleButton(false);
                    filePath = imageManager.filePath;
                }
            }
        }
    }

    private void toggleButton(boolean b) {
        if(!b){
            Log.d("###","ngilangin tombol tambah");
            tambah.setVisibility(View.GONE);
            llSettingGambar.setVisibility(View.VISIBLE);
            ivGambar.getLayoutParams().height= ViewGroup.MarginLayoutParams.WRAP_CONTENT;
        } else {
            tambah.setVisibility(View.VISIBLE);
            llSettingGambar.setVisibility(View.GONE);
            ivGambar.getLayoutParams().height=0;
        }
    }

    private void tutupKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void hapusGambar(){
        toggleButton(true);
        bm = null;
        filePath = null;
        ivGambar.setImageBitmap(bm);
    }
}

package com.aghagha.tagg;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.LinearGradient;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.aghagha.tagg.data.AntaraSessionManager;
import com.aghagha.tagg.utilities.DownloadImageTask;
import com.aghagha.tagg.utilities.ImageManager;
import com.aghagha.tagg.utilities.NetworkUtils;
import com.aghagha.tagg.utilities.PermissionUtility;
import com.aghagha.tagg.utilities.VolleyUtil;
import com.android.volley.VolleyError;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilActivity extends AppCompatActivity implements FragmentGantiPassword.OnSubmitPassword {
    String nama, nomor, email, alamat, gambar, isMurid, oldEmail;
    EditText et_nama, et_nomor, et_email, et_alamat, et_password, et_password2;
    Button edit, editPW, save;
    CircleImageView iv_gambar, bt_pp;

    AntaraSessionManager session;
    ProgressDialog pDialog;
    ImageManager imageManager;

    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        if(intent.hasExtra("idMurid"))
        isMurid = intent.getStringExtra("idMurid");
        else isMurid = "0";

        pDialog = new ProgressDialog(this);
        imageManager = new ImageManager(this, isMurid);

        session = new AntaraSessionManager(this);

        et_nama = (EditText)findViewById(R.id.et_nama);
        et_nomor = (EditText)findViewById(R.id.et_nomor);
        et_email = (EditText)findViewById(R.id.et_email);
        et_alamat = (EditText)findViewById(R.id.et_alamat);
        iv_gambar = (CircleImageView) findViewById(R.id.iv_profil);
        bt_pp = (CircleImageView) findViewById(R.id.bt_pp);
        edit = (Button)findViewById(R.id.edit);
        editPW = (Button)findViewById(R.id.editPW);
        save = (Button)findViewById(R.id.save);
        save.setVisibility(View.GONE);

        View.OnClickListener operation = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.edit:
                        toggleForm(true);
                        edit.setVisibility(View.GONE);
                        save.setVisibility(View.VISIBLE);
                        break;
                    case R.id.save:
                        saveData(et_nama.getText().toString(),
                                et_email.getText().toString(),
                                et_nomor.getText().toString(),
                                et_alamat.getText().toString());
                        toggleForm(false);
                        edit.setVisibility(View.VISIBLE);
                        save.setVisibility(View.GONE);
                        break;
                    case R.id.editPW:
                        FragmentGantiPassword dialog = new FragmentGantiPassword();
                        dialog.show(getSupportFragmentManager(),"FragmentGantiPassword");
                        break;
                    case R.id.bt_pp:
                        imageManager.selectImage();
                        break;
                }
            }
        };

        edit.setOnClickListener(operation);
        editPW.setOnClickListener(operation);
        save.setOnClickListener(operation);
        bt_pp.setOnClickListener(operation);

        if(!isMurid.equals("0")){
            editPW.setVisibility(View.GONE);
        }
        setForm();

        toggleForm(false);
    }

    private void setForm() {
        pDialog.setMessage("Sedang memuat...");
        showDialog();
        VolleyUtil volleyUtil;
        if(!isMurid.equals("0")){
            volleyUtil = new VolleyUtil("req_detail_user",this, NetworkUtils.userdetail+"/"+session.getKeyEmail()+"/"+session.getKeyMuridId());
        } else {
            volleyUtil = new VolleyUtil("req_detail_user",this, NetworkUtils.userdetail+"/"+session.getKeyEmail());
        }
        volleyUtil.SendRequestGET(new VolleyUtil.VolleyResponseListener() {
            @Override
            public void onError(VolleyError error) {
                hideDialog();
                Log.d("req_detail_user",error.getMessage());
                Toast.makeText(ProfilActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onResponse(String response) {
                hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject user = jsonObject.getJSONObject("user");
                    nama = user.get("nama").toString();
                    nomor = user.get("telepon").toString();
                    email = user.get("email").toString();
                    oldEmail = email;
                    alamat = user.get("alamat").toString();
                    gambar = jsonObject.getString("gambar");
                    if(!gambar.equals("")){
                        new DownloadImageTask(iv_gambar).execute(NetworkUtils.profil_image+gambar);
                    }

                    setForm(nama,email,nomor,alamat);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("req_detail_user",response);
            }
        });
    }

    public void toggleForm(boolean b){
        et_nama.setEnabled(b);
        et_email.setEnabled(b);
        et_nomor.setEnabled(b);
        et_alamat.setEnabled(b);
    }

    public void setForm(String nama, String email, String nomor, String alamat){
        et_nama.setText(nama);
        et_nomor.setText(nomor);
        et_email.setText(email);
        et_alamat.setText(alamat);
    }

    public void saveData(String _nama, String _email, String _nomor, String _alamat){
        pDialog.setMessage("Sedang menyimpan...");
        showDialog();
        VolleyUtil volleyUtil = new VolleyUtil("req_edit_profil",this,NetworkUtils.userdetail);
        Map<String,String> params = new HashMap<>();
        params.put("nama",_nama);
        params.put("email",_email);
        params.put("old_email",oldEmail);
        params.put("telepon",_nomor);
        params.put("alamat",_alamat);
        volleyUtil.SendRequestPOST(params, new VolleyUtil.VolleyResponseListener() {
            @Override
            public void onError(VolleyError error) {
                hideDialog();
                Toast.makeText(ProfilActivity.this, "Data gagal disimpan...", Toast.LENGTH_SHORT).show();
                setForm(nama,email,nomor,alamat);
            }

            @Override
            public void onResponse(String response) {
                hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.get("code").toString();
                    String message = jsonObject.get("message").toString();
                    Toast.makeText(ProfilActivity.this, message, Toast.LENGTH_SHORT).show();
                    if(code.equals("0")) {
                        setForm(nama,email,nomor,alamat);
                        Log.d("GANTI PROFIL",message);
                    }
                    else if(code.equals("1")){
                        nama = et_nama.getText().toString();
                        email = et_email.getText().toString();
                        if(oldEmail.equals(session.getKeyEmail())){
                            session.setKeyEmail(email);
                        }
                        oldEmail = email;
                        nomor = et_nomor.getText().toString();
                        alamat = et_alamat.getText().toString();

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

    @Override
    public void onSubmitPassword(String pw, String pw2, String pw3) {
        pDialog.setMessage("Sedang menyimpan...");
        showDialog();
        if(!pw2.equals(pw3)){
            hideDialog();
            Toast.makeText(this, "Password baru tidak cocok", Toast.LENGTH_SHORT).show();
            return;
        }
        if(pw2.equals("")){
            hideDialog();
            Toast.makeText(this, "Password baru masih kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        VolleyUtil volleyUtil = new VolleyUtil("req_edit_password",this,NetworkUtils.userpass);
        Map<String,String> params = new HashMap<>();
        params.put("password",pw);
        params.put("email",session.getKeyEmail());
        volleyUtil.SendRequestPOST(params, new VolleyUtil.VolleyResponseListener() {
            @Override
            public void onError(VolleyError error) {
                hideDialog();
                Toast.makeText(ProfilActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.get("code").toString();
                    String message = jsonObject.get("message").toString();
                    Toast.makeText(ProfilActivity.this, message, Toast.LENGTH_SHORT).show();
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
                imageManager.onSelectFromGalleryResult(data);
                filePath = imageManager.filePath;
                imageManager.uploadMultipart(this,session.getKeyEmail(),iv_gambar,"Mengunggah gambar...","Gambar berhasil diunggah!","Gambar gagal diunggah..");
            }
            else if (requestCode == imageManager.REQUEST_CAMERA) {
                imageManager.onCaptureImageResult(data);
                filePath = imageManager.filePath;
                imageManager.uploadMultipart(this,session.getKeyEmail(),iv_gambar,"Mengunggah gambar...","Gambar berhasil diunggah!","Gambar gagal diunggah..");
            }
        }
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
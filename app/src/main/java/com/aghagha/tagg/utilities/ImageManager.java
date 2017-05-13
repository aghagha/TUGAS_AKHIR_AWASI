package com.aghagha.tagg.utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by aghagha on 09/05/2017.
 */

public class ImageManager {
    Activity ac;
    public Uri filePath;
    public Bitmap bm;
    public String userChoosenTask;

    public static final int REQUEST_CAMERA = 321;
    public static final int SELECT_FILE = 12321;

    public ImageManager(Activity ac){
        this.ac = ac;
    }

    public void selectImage(){
        final CharSequence[] items = {"Ambil Foto", "Pilih dari Galeri", "Batal"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ac);
        builder.setTitle("Ganti Foto");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = PermissionUtility.checkPermission(ac);
                if (items[item].equals("Ambil Foto")) {
                    userChoosenTask="Ambil Foto";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Pilih dari Galeri")) {
                    userChoosenTask="Pilih dari Galeri";
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Batal")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ac.startActivityForResult(intent, REQUEST_CAMERA);
    }

    public void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        ac.startActivityForResult(Intent.createChooser(intent, "Pilih file"),SELECT_FILE);
    }

    public Bitmap onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            try {
                filePath = data.getData();
                bm = MediaStore.Images.Media.getBitmap(ac.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //@TODO batasi maksimal ukuran file jadi 2MB
        return bm;
    }

    public Bitmap onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        bm = thumbnail;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        filePath = Uri.fromFile(destination);
        return bm;
    }

    public void uploadMultipart(final Context context, String email, final ImageView iv_gambar, final String judul, final String pesanSukses, final String pesanGagal) {
        try {
            UploadNotificationConfig notificationConfig = new UploadNotificationConfig()
                    .setTitle(judul)
                    .setInProgressMessage("Menyimpan gambar")
                    .setErrorMessage(pesanGagal)
                    .setCompletedMessage(pesanSukses);
            String uploadId =
                    new MultipartUploadRequest(context, NetworkUtils.post_profil_image)
                            .setMethod("POST")
                            .setUtf8Charset()
                            .addFileToUpload(filePath.getPath(), "image")
                            .addParameter("email",email)
                            .setNotificationConfig(notificationConfig)
                            .setMaxRetries(2)
                            .setDelegate(new UploadStatusDelegate() {
                                @Override
                                public void onProgress(Context context, UploadInfo uploadInfo) {
                                    Log.d("####","upload on going");
                                }

                                @Override
                                public void onError(Context context, UploadInfo uploadInfo, Exception exception) {

                                }

                                @Override
                                public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(serverResponse.getBodyAsString());
                                        String code = jsonObject.get("code").toString();
                                        String message = jsonObject.get("status").toString();
                                        if(code.equals("1"))iv_gambar.setImageBitmap(bm);
                                        Toast.makeText(ac, message, Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCancelled(Context context, UploadInfo uploadInfo) {

                                }
                            })
                            .startUpload();
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
    }

    public void createTopik(final Context context, String id_, String id_kelas, String judul_, String idK_, String konten_, final String judul, final String pesanSukses, final String pesanGagal){
        try {
            final ProgressDialog progressDialog = new ProgressDialog(ac);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Sedang menyimpan...");
            progressDialog.show();

            UploadNotificationConfig notificationConfig = new UploadNotificationConfig()
                    .setTitle(judul)
                    .setInProgressMessage("Menyimpan gambar")
                    .setErrorMessage(pesanGagal)
                    .setCompletedMessage(pesanSukses);
            MultipartUploadRequest multipartUploadRequest = new MultipartUploadRequest(context, NetworkUtils.add_topik+"/"+id_kelas)
                    .setMethod("POST")
                    .setUtf8Charset()
                    .addParameter("id",id_)
                    .addParameter("judul",judul_)
                    .addParameter("id_tujuan",idK_)
                    .addParameter("konten",konten_);

            if(!(filePath==null)) multipartUploadRequest.addFileToUpload(filePath.getPath(),"gambar");

            multipartUploadRequest.setNotificationConfig(notificationConfig)
                    .setMaxRetries(2)
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {
                            Log.d("####","upload on going");
                        }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(ac, "Gagal menyimpan..", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                                progressDialog.dismiss();
                            try {
                                JSONObject jsonObject = new JSONObject(serverResponse.getBodyAsString());
                                String code = jsonObject.get("code").toString();
                                Toast.makeText(ac, "Berhasil disimpan", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                                String code = jsonObject.get("code").toString();
//                                String message = jsonObject.get("status").toString();
//                                Toast.makeText(ac, message, Toast.LENGTH_SHORT).show();
                                ac.finish();
                        }

                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {

                        }
                    })
                    .startUpload();
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
    }
}

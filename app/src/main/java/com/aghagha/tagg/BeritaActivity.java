package com.aghagha.tagg;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aghagha.tagg.utilities.DownloadImageTask;
import com.aghagha.tagg.utilities.NetworkUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class BeritaActivity extends AppCompatActivity {
    public TextView judul;
    public TextView konten;
    public TextView tanggal;
    public ImageView gambar;
    public TextView lampiran;
    public LinearLayout layout2;
    public ImageView download;
    public static String PACKAGE_NAME;

    String lampiran_;

    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_berita);

        progressDialog = new ProgressDialog(BeritaActivity.this);
        progressDialog.setMessage("Sedang mengunduh...");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(true);

        PACKAGE_NAME = getApplicationContext().getPackageName();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        judul = (TextView)findViewById(R.id.tvJudul);
        konten = (TextView)findViewById(R.id.tvKonten);
        tanggal = (TextView)findViewById(R.id.tvCreated);
        gambar = (ImageView)findViewById(R.id.ivGambar);
        lampiran = (TextView)findViewById(R.id.tvLampiran);
        layout2 = (LinearLayout)findViewById(R.id.layout2);
        download = (ImageView) findViewById(R.id.download);

        download.setColorFilter(ContextCompat.getColor(BeritaActivity.this,R.color.colorAccent));

        Intent intent = getIntent();
        judul.setText(intent.getStringExtra("judul"));
        konten.setText(intent.getStringExtra("konten"));
        tanggal.setText(intent.getStringExtra("tanggal"));

        String gambar_ = intent.getStringExtra("gambar");
        lampiran_ = intent.getStringExtra("lampiran");

        if(!gambar_.equals("null")){
            new DownloadImageTask(gambar).execute(NetworkUtils.berita_image+gambar_);
        } else {
            gambar.setVisibility(View.GONE);
        }

        if(!lampiran_.equals("null")) {
            lampiran.setText(lampiran_);
            final DownloadTask[] downloadTask = new DownloadTask[1];
            layout2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downloadTask[0] = download(lampiran_);
                }
            });
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    downloadTask[0].cancel(true);
                }
            });
        } else {
            layout2.setVisibility(View.GONE);
        }
    }

    public DownloadTask download(String lampiran){
        final DownloadTask downloadTask = new DownloadTask(BeritaActivity.this);
        downloadTask.execute(NetworkUtils.serverDir+"/lampiran/berita/"+lampiran);
        return downloadTask;
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

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                File filedir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                output = new FileOutputStream(filedir+"/"+lampiran_);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            progressDialog.dismiss();
            if (result != null) {
                Toast.makeText(context, "Unduhan gagal: " + result, Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(context,"Berkas diunduh!", Toast.LENGTH_SHORT).show();
                showCompletedDownload();
            }

        }

        public void showCompletedDownload(){
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+lampiran_);
            intent.setDataAndType(Uri.fromFile(file), URLConnection.guessContentTypeFromName(lampiran_));

            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

            Notification noti = new NotificationCompat.Builder(context)
                    .setContentTitle("Unduhan selesai")
                    .setContentText(lampiran_)
                    .setSmallIcon(R.drawable.ic_home)
                    .setContentIntent(pIntent).build();

            noti.flags |= Notification.FLAG_AUTO_CANCEL;

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, noti);
        }
    }
}

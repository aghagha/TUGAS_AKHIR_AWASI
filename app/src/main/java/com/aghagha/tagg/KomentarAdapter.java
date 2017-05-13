package com.aghagha.tagg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aghagha.tagg.models.Komentar;
import com.aghagha.tagg.models.Topik;
import com.aghagha.tagg.utilities.NetworkUtils;

import java.io.InputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by aghagha on 10/05/2017.
 */

public class KomentarAdapter extends RecyclerView.Adapter<KomentarAdapter.ViewHolder> {
    private Context mContext;
    private List<Komentar> komentarList;

    public KomentarAdapter(List<Komentar> list){
        this.komentarList = list;
    }

    @Override
    public KomentarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.komentar_item, parent, false);
        mContext = itemView.getContext();
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(KomentarAdapter.ViewHolder holder, int position) {
        String gambar = komentarList.get(position).getGambar();
        holder.nama.setText(komentarList.get(position).getNama());
        holder.waktu.setText(komentarList.get(position).getWaktu());
        holder.komentar.setText(komentarList.get(position).getKonten());

        if(!gambar.equals(""))
            new DownloadImageTask(holder.iv_profil).execute(NetworkUtils.profil_image+"/"+komentarList.get(position).getGambar());
    }

    @Override
    public int getItemCount() {
        return komentarList.size();
    }

    public void clear(){
        komentarList.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nama;
        public TextView waktu;
        public TextView komentar;
        public CircleImageView iv_profil;

        public ViewHolder(View itemView) {
            super(itemView);
            nama = (TextView) itemView.findViewById(R.id.tvNama);
            waktu = (TextView) itemView.findViewById(R.id.tvWaktu);
            komentar = (TextView) itemView.findViewById(R.id.tvKomentar);
            iv_profil = (CircleImageView) itemView.findViewById(R.id.iv_profil);
        }
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

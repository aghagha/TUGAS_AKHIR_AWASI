package com.aghagha.tagg;

import android.content.Context;
import android.content.Intent;
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

import com.aghagha.tagg.models.Topik;
import com.aghagha.tagg.utilities.NetworkUtils;

import java.io.InputStream;
import java.util.List;

/**
 * Created by aghagha on 08/05/2017.
 */

public class ForumGuruAdapter extends RecyclerView.Adapter<ForumGuruAdapter.ViewHolder>{
    private Context mContext;
    private List<Topik> topikList;

    public ForumGuruAdapter(List<Topik> list){
        this.topikList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.topik_item, parent, false);
        mContext = itemView.getContext();
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String gambar = topikList.get(position).getGambar();

        holder.judul.setText(topikList.get(position).getJudul());
        holder.tanggal.setText(topikList.get(position).getTanggal());
        holder.konten.setText(topikList.get(position).getKonten());
        holder.komentar.setText(topikList.get(position).getKomentar()+" komentar");
        if(!gambar.equals("null")){
            new DownloadImageTask(holder.gambar).execute(NetworkUtils.topik_gambar+gambar);
        }

        //@TODO tambahin tujuan dari pesan
        View.OnClickListener operation = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,TopikActivity.class);
                intent.putExtra("id_topik",topikList.get(position).getId());
                intent.putExtra("judul",topikList.get(position).getJudul());
                intent.putExtra("tanggal",topikList.get(position).getTanggal());
                intent.putExtra("konten",topikList.get(position).getKonten());
                if(!gambar.equals("null")){
                    intent.putExtra("gambar",gambar);
                }
                mContext.startActivity(intent);
            }
        };
        holder.judul.setOnClickListener(operation);
        holder.komentar.setOnClickListener(operation);
    }

    @Override
    public int getItemCount() {
        return topikList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView judul;
        public TextView konten;
        public TextView tanggal;
        public TextView komentar;
        public ImageView gambar;
        public ViewHolder(View itemView) {
            super(itemView);
            judul = (TextView)itemView.findViewById(R.id.tvJudul);
            konten = (TextView)itemView.findViewById(R.id.tvKonten);
            tanggal = (TextView)itemView.findViewById(R.id.tvCreated);
            gambar = (ImageView)itemView.findViewById(R.id.ivGambar);
            komentar = (TextView) itemView.findViewById(R.id.tvKomentar);
        }
    }

    public void clear(){
        topikList.clear();
        notifyDataSetChanged();
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

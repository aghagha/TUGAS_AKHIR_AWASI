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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aghagha.tagg.utilities.NetworkUtils;
import com.aghagha.tagg.models.Berita;
import com.aghagha.tagg.utilities.PermissionUtility;

import java.io.InputStream;
import java.util.List;

/**
 * Created by aghagha on 24/04/2017.
 */

public class BerandaGuruAdapter extends RecyclerView.Adapter<BerandaGuruAdapter.ViewHolder>{

//    final private BerandaGuruAdapterOnClickHandler mClickHandler;

    public interface BerandaGuruAdapterOnClickHandler {
        void onClick(String judul);
    }

    public Context mContext;
    private List<Berita> beritaList;

    BerandaGuruAdapter(List<Berita> beritaList) {
        this.beritaList = beritaList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.berita_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String konten = beritaList.get(position).getKonten();

        String gambar = beritaList.get(position).getGambar();
        String lampiran = beritaList.get(position).getLampiran();

        holder.judul.setText(beritaList.get(position).getJudul());
        holder.tanggal.setText(beritaList.get(position).getCreated_at());

        if(konten.length()>=160){
            konten = konten.substring(0,159)+"...";
        }
        holder.konten.setText(konten);
        if(!gambar.equals("null")){
            new DownloadImageTask(holder.gambar).execute(NetworkUtils.berita_image+gambar);
        } else {
            holder.gambar.setVisibility(View.GONE);
        }

        if(!lampiran.equals("null")) {
            holder.lampiran.setText(lampiran);
        } else holder.lampiran.setHeight(20);

        if(konten.length()<160 && gambar.equals("null") && lampiran.equals("null")){
            holder.more.setVisibility(View.GONE);
        } else holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,BeritaActivity.class);
                intent.putExtra("judul",beritaList.get(position).getJudul());
                intent.putExtra("tanggal",beritaList.get(position).getCreated_at());
                intent.putExtra("konten",beritaList.get(position).getKonten());
                intent.putExtra("gambar",beritaList.get(position).getGambar());
                intent.putExtra("lampiran",beritaList.get(position).getLampiran());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return beritaList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView judul;
        public TextView konten;
        public TextView tanggal;
        public ImageView gambar;
        public TextView lampiran;
        public TextView more;
        public LinearLayout layout;
        public ViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            judul = (TextView)itemView.findViewById(R.id.tvJudul);
            konten = (TextView)itemView.findViewById(R.id.tvKonten);
            tanggal = (TextView)itemView.findViewById(R.id.tvCreated);
            gambar = (ImageView)itemView.findViewById(R.id.ivGambar);
            lampiran = (TextView)itemView.findViewById(R.id.tvLampiran);
            layout = (LinearLayout)itemView.findViewById(R.id.layout);
            more = (TextView) itemView.findViewById(R.id.more);
//            itemView.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View view) {
//            int adapterPosition = getAdapterPosition();
////            mClickHandler.onClick(beritaList.get(adapterPosition).getJudul());
//        }
    }

    public void clear(){
        beritaList.clear();
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

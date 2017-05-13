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

import com.aghagha.tagg.data.AntaraSessionManager;
import com.aghagha.tagg.models.Murid;
import com.aghagha.tagg.utilities.NetworkUtils;

import java.io.InputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by aghagha on 11/05/2017.
 */

public class MuridAdapter extends RecyclerView.Adapter<MuridAdapter.ViewHolder>{
    Context mContext;
    private List<Murid> listMurid;

    public MuridAdapter(List<Murid> list){
        listMurid = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.murid_item,parent,false);
        mContext = parent.getContext();
        return new ViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(MuridAdapter.ViewHolder holder, final int position) {
        final String gambar = listMurid.get(position).getGambar();
        holder.nama.setText(listMurid.get(position).getNama());
        holder.kelas.setText(listMurid.get(position).getKelas());
        if(!gambar.equals(""))
            new DownloadImageTask(holder.iv_gambar).equals(NetworkUtils.profil_image+gambar);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,MuridActivity.class);
                new AntaraSessionManager(mContext).setMurid(listMurid.get(position).getId());
                mContext.startActivity(intent);
            }
        });
    }

    public void clear(){
        listMurid.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listMurid.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nama, kelas;
        CircleImageView iv_gambar;
        LinearLayout layout;
        public ViewHolder(View itemView) {
            super(itemView);
            nama = (TextView)itemView.findViewById(R.id.tv_nama);
            kelas = (TextView)itemView.findViewById(R.id.tv_kelas);
            iv_gambar = (CircleImageView)itemView.findViewById(R.id.iv_profil);
            layout = (LinearLayout)itemView.findViewById(R.id.layout);
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

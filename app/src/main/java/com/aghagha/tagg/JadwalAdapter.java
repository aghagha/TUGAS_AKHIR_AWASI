package com.aghagha.tagg;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aghagha.tagg.data.AntaraSessionManager;
import com.aghagha.tagg.models.Jadwal;

import java.util.List;

/**
 * Created by aghagha on 20/05/2017.
 */

class JadwalAdapter extends RecyclerView.Adapter<JadwalAdapter.ViewHolder> {

    public Context mContext;
    private List<Jadwal> jadwalList;

    JadwalAdapter(List<Jadwal> list){
        this.jadwalList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.jadwal_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.waktu.setText(jadwalList.get(position).getWaktu());
        if (new AntaraSessionManager(mContext).getKeyMuridId()==null){
            holder.kelas.setText(jadwalList.get(position).getKelas());
        } else {
            holder.kelas.setVisibility(View.GONE);
        }
        holder.mapel.setText(jadwalList.get(position).getMapel());
    }

    @Override
    public int getItemCount() {
        return jadwalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView waktu;
        public TextView kelas;
        public TextView mapel;
        public ViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            waktu = (TextView)itemView.findViewById(R.id.tvWaktu);
            kelas = (TextView)itemView.findViewById(R.id.tvKelas);
            mapel = (TextView)itemView.findViewById(R.id.tvMapel);
        }
    }

    public void clear(){
        jadwalList.clear();
        notifyDataSetChanged();
    }
}

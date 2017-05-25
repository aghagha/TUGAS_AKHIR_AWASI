package com.aghagha.tagg;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aghagha.tagg.models.Laporan;

import java.util.List;

/**
 * Created by aghagha on 18/05/2017.
 */

public class LaporanAdapter extends RecyclerView.Adapter<LaporanAdapter.ViewHolder>{
    private List<Laporan> laporanList;
    private Context mContext;

    LaporanAdapter(List<Laporan> list){
        this.laporanList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.laporan_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String judul = laporanList.get(position).getJudul();
        String dibuat = laporanList.get(position).getDibuat();
        String nilai = laporanList.get(position).getNilai();
        String mean = laporanList.get(position).getMean();
        String max = laporanList.get(position).getMax();

        Drawable comparison;

        holder.judul.setText(judul);
        holder.dibuat.setText(dibuat);
        holder.nilai.setText(nilai);
        holder.mean.setText(mean);
        holder.max.setText(max);

        if(!judul.equals("")) {
            if (nilai.compareTo(mean) >= 0) {
                comparison = ContextCompat.getDrawable(mContext, R.drawable.ic_up);
                ColorFilter filter = new LightingColorFilter(Color.parseColor("#176936"), Color.parseColor("#176936"));
                comparison.setColorFilter(filter);
                holder.arrow.setImageDrawable(comparison);
            } else {
                comparison = ContextCompat.getDrawable(mContext, R.drawable.ic_down);
                ColorFilter filter = new LightingColorFilter(Color.parseColor("#DB1616"), Color.parseColor("#DB1616"));
                comparison.setColorFilter(filter);
                holder.arrow.setImageDrawable(comparison);
            }
        }
    }

    @Override
    public int getItemCount() {
        return laporanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView judul, dibuat, nilai, mean, max;
        public ImageView arrow;
        public ViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            judul = (TextView)itemView.findViewById(R.id.tv_judul);
            dibuat = (TextView)itemView.findViewById(R.id.tv_dibuat);
            arrow = (ImageView) itemView.findViewById(R.id.iv_arrow);
            nilai = (TextView)itemView.findViewById(R.id.tv_nilai);
            mean = (TextView)itemView.findViewById(R.id.tv_mean);
            max = (TextView)itemView.findViewById(R.id.tv_max);
        }
    }

    public void clear(){
        laporanList.clear();
        notifyDataSetChanged();
    }
}

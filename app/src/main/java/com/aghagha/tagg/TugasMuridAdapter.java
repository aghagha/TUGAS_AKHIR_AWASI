package com.aghagha.tagg;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
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
import com.aghagha.tagg.models.Tugas;

import java.util.List;

/**
 * Created by aghagha on 06/05/2017.
 */

public class TugasMuridAdapter extends RecyclerView.Adapter<TugasMuridAdapter.ViewHolder> {
    private List<Tugas> tugasList;
    private Context mContext;

    TugasMuridAdapter(List<Tugas> list){
        this.tugasList = list;
    }

    @Override
    public TugasMuridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tugas_item_murid, parent, false);
        return new TugasMuridAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TugasMuridAdapter.ViewHolder holder, final int position) {
        final String judul = tugasList.get(position).getMapel()+": "+tugasList.get(position).getJudul();
        final String konten = tugasList.get(position).getKonten();
        final String dibuat = "dipost "+tugasList.get(position).getDibuat()+" oleh Bu Umayah";
        final String deadline = tugasList.get(position).getDeadline();
        final String status = tugasList.get(position).getStatus();
        final String cek = tugasList.get(position).getCek();
        holder.judul.setText(judul);
        holder.konten.setText(konten);
        holder.dibuat.setText(dibuat);
//        if(cek.equals("1")){
//            holder.label_deadline.setVisibility(View.GONE);
//            holder.deadline.setText("Sudah dikerjakan");
//        } else {
//            holder.label_deadline.setVisibility(View.VISIBLE);
//            holder.deadline.setText(deadline);
//        }
        holder.deadline.setText(deadline);
        Log.d("Tugas ","dengan judul "+judul+" statusnya "+status+"dan cek="+status.equals("1"));

        if(status.equals("1")){
            //kelewat deadline
            holder.icon.setVisibility(View.VISIBLE);
            if(tugasList.get(position).getTelat()){
                //telat dan belum ditandai
                holder.deadline_bg.setBackgroundColor(ContextCompat.getColor(mContext,R.color.colorTextRed));
                holder.deadline.setText("BELUM DIKERJAKAN");
                Drawable silang = ContextCompat.getDrawable(mContext,R.drawable.ic_silang);
                holder.icon.setImageDrawable(silang);
                holder.icon.setColorFilter(Color.WHITE);
            } else {
                //tepat waktu
                holder.deadline_bg.setBackgroundColor(ContextCompat.getColor(mContext,R.color.colorAccent));
                holder.deadline.setText("SELESAI");
                Drawable centang = ContextCompat.getDrawable(mContext,R.drawable.ic_centang);
                holder.icon.setImageDrawable(centang);
                holder.icon.setColorFilter(Color.WHITE);

                holder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, TandaiTugasActivity.class);
                        intent.putExtra("id", String.valueOf(tugasList.get(position).getId_tugas()));
                        intent.putExtra("judul", judul);
                        intent.putExtra("konten", konten);
                        intent.putExtra("dibuat", dibuat);
                        intent.putExtra("deadline", deadline);
                        intent.putExtra("status", status);
                        intent.putExtra("cek", cek);
                        mContext.startActivity(intent);
//                        Toast.makeText(mContext, "Lihat/tunggu nilai tugas di halaman laporan", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            //masih belum deadline
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, TandaiTugasActivity.class);
                    intent.putExtra("id", String.valueOf(tugasList.get(position).getId_tugas()));
                    intent.putExtra("judul", judul);
                    intent.putExtra("konten", konten);
                    intent.putExtra("dibuat", dibuat);
                    intent.putExtra("deadline", deadline);
                    intent.putExtra("status", status);
                    intent.putExtra("cek", cek);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    public void clear(){
        tugasList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tugasList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView judul, dibuat, deadline, konten;
        public LinearLayout deadline_bg, layout;
        public ImageView icon;
        public ViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            judul = (TextView)itemView.findViewById(R.id.tvJudul);
            konten = (TextView)itemView.findViewById(R.id.tvKonten);
            dibuat = (TextView)itemView.findViewById(R.id.tvCreated);
            deadline = (TextView)itemView.findViewById(R.id.tvDeadline);
            icon = (ImageView) itemView.findViewById(R.id.ikon);
            deadline_bg = (LinearLayout) itemView.findViewById(R.id.deadline_bg);
            layout = (LinearLayout) itemView.findViewById(R.id.layout);
        }
    }
}

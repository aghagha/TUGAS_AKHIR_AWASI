package com.aghagha.tagg;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aghagha.tagg.data.AntaraSessionManager;
import com.aghagha.tagg.models.Tugas;

import java.util.List;

/**
 * Created by aghagha on 06/05/2017.
 */

public class TugasGuruAdapter extends RecyclerView.Adapter<TugasGuruAdapter.ViewHolder> {
    private List<Tugas> tugasList;
    private Context mContext;

    TugasGuruAdapter(List<Tugas> list){
        this.tugasList = list;
    }

    @Override
    public TugasGuruAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tugas_item, parent, false);
        return new TugasGuruAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TugasGuruAdapter.ViewHolder holder, final int position) {
        final String judul = tugasList.get(position).getMapel()+": "+tugasList.get(position).getJudul();
        final String konten = tugasList.get(position).getKonten();
        final String dibuat = "dipost "+tugasList.get(position).getDibuat();
        final String deadline = "deadline: "+tugasList.get(position).getDeadline();
        final String status = tugasList.get(position).getStatus();
        holder.judul.setText(judul);
        holder.konten.setText(konten);
        holder.dibuat.setText(dibuat);
        holder.deadline.setText(deadline);
        if(status.equals("0")){
            holder.deadline_bg.setBackgroundColor(mContext.getResources().getColor(R.color.colorTextGray));
        } else {
            holder.deadline.setText("SELESAI");
            holder.deadline_bg.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
        }
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,InputNilaiActivity.class);
                intent.putExtra("id",tugasList.get(position).getId_tugas());
                intent.putExtra("judul",judul);
                intent.putExtra("konten",konten);
                intent.putExtra("dibuat",dibuat);
                intent.putExtra("deadline",deadline);
                intent.putExtra("status",status);
                mContext.startActivity(intent);
            }
        });
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
        public ViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            judul = (TextView)itemView.findViewById(R.id.tvJudul);
            konten = (TextView)itemView.findViewById(R.id.tvKonten);
            dibuat = (TextView)itemView.findViewById(R.id.tvCreated);
            deadline = (TextView)itemView.findViewById(R.id.tvDeadline);
            deadline_bg = (LinearLayout)itemView.findViewById(R.id.deadline_bg);
            layout = (LinearLayout)itemView.findViewById(R.id.layout);
        }
    }
}

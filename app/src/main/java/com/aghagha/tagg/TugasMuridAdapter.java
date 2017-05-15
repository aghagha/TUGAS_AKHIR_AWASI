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
        final String dibuat = "dipost "+tugasList.get(position).getDibuat();
        final String deadline = tugasList.get(position).getDeadline();
        final String status = tugasList.get(position).getStatus();
        final String cek = tugasList.get(position).getCek();
        holder.judul.setText(judul);
        holder.konten.setText(konten);
        holder.dibuat.setText(dibuat);
        holder.deadline.setText(deadline);
        if(status.equals("1")){
            holder.judul.setTextColor(ContextCompat.getColor(mContext,android.R.color.white));
            holder.dibuat.setTextColor(ContextCompat.getColor(mContext,android.R.color.white));
            holder.layout2.setVisibility(View.GONE);
            holder.layout3.setVisibility(View.VISIBLE);
            if(tugasList.get(position).getTelat()){
                holder.layout.setBackgroundColor(ContextCompat.getColor(mContext,R.color.colorTextRed));
            } else {
                holder.layout.setBackgroundColor(ContextCompat.getColor(mContext,R.color.colorAccent));
                holder.judul.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(mContext, "Untuk ini anak anda dapat nilai sekian", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            holder.judul.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, TandaiTugasActivity.class);
                    intent.putExtra("id", tugasList.get(position).getId_tugas());
                    intent.putExtra("judul", judul);
                    intent.putExtra("konten", konten);
                    intent.putExtra("dibuat", dibuat);
                    intent.putExtra("deadline", deadline);
                    intent.putExtra("status", status);
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
        public LinearLayout layout, layout2, layout3;
        public ViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            judul = (TextView)itemView.findViewById(R.id.tvJudul);
            konten = (TextView)itemView.findViewById(R.id.tvKonten);
            dibuat = (TextView)itemView.findViewById(R.id.tvCreated);
            deadline = (TextView)itemView.findViewById(R.id.tvDeadline);
            layout = (LinearLayout) itemView.findViewById(R.id.layout);
            layout2 = (LinearLayout) itemView.findViewById(R.id.layout2);
            layout3 = (LinearLayout) itemView.findViewById(R.id.layout3);
        }
    }
}

package com.aghagha.tagg;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aghagha.tagg.models.Nilai;
import com.aghagha.tagg.utilities.NetworkUtils;
import com.aghagha.tagg.utilities.VolleyUtil;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aghagha on 08/05/2017.
 */

public class NilaiGuruAdapter extends RecyclerView.Adapter<NilaiGuruAdapter.ViewHolder>{
    private List<Nilai> listNilai;
    private Context mContext;
    private ProgressDialog progressDialog;
    private AlertDialog alertbox;

    NilaiGuruAdapter(List<Nilai> nilai){
        this.listNilai = nilai;
    }

    @Override
    public NilaiGuruAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.nilai_item, parent, false);
        return new NilaiGuruAdapter.ViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(final NilaiGuruAdapter.ViewHolder holder, final int position) {
        progressDialog = new ProgressDialog(mContext);

        String no = String.valueOf(position);
        final String id = listNilai.get(position).getId();
        final String nama = listNilai.get(position).getNama();
        String nilai = listNilai.get(position).getNilai();
        String status = listNilai.get(position).getStatus();
        holder.no.setText(no);
        holder.nama.setText(nama);
        if(nilai.equals("-")){
            nilai = "0";
            holder.nilai.setText("belum");
            holder.nilai.setTextColor(ContextCompat.getColor(mContext,R.color.colorTextRed));
        } else holder.nilai.setText(nilai);
        if(id.equals("0")){
            holder.no.setText("NO.");
            holder.no.setTextColor(ContextCompat.getColor(mContext,R.color.colorTextGray));
            holder.nama.setTextColor(ContextCompat.getColor(mContext,R.color.colorTextGray));
            holder.nilai.setTextColor(ContextCompat.getColor(mContext,R.color.colorTextGray));
        } else holder.nilai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAlertBox(nama, holder.nilai, id, position);
                alertbox.show();
            }
        });

    }

    private void setAlertBox(String nama, final TextView nilai, final String id, final int position) {
        if(alertbox!=null)alertbox.dismiss();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        final View view = layoutInflater.inflate(R.layout.dialog_input_nilai,null);
        alertbox = new AlertDialog.Builder(mContext).create();
        alertbox.setTitle(nama);
        final EditText et_nilai = (EditText) view.findViewById(R.id.et_nilai);
        if(!nilai.getText().toString().equals("belum")) et_nilai.setText(nilai.getText());

        alertbox.setButton(alertbox.BUTTON_POSITIVE, "SIMPAN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                simpanNilai(et_nilai.getText().toString(),nilai,id,position);
            }
        });

        alertbox.setButton(alertbox.BUTTON_NEGATIVE, "BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alertbox.setView(view);
    }

    public void clear(){
        listNilai.clear();
        notifyDataSetChanged();
    }

    public void simpanNilai(final String s, final TextView nilai, String id, final int position){
        progressDialog.setMessage("Sedang menyimpan...");
        progressDialog.show();

        VolleyUtil volleyUtil = new VolleyUtil("req_simpan_nilai",mContext, NetworkUtils.nilaGuru+"/"+id);
        Map<String,String> params = new HashMap<>();
        params.put("nilai",s);
        volleyUtil.SendRequestPOST(params, new VolleyUtil.VolleyResponseListener() {
            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(mContext, "Simpan nilai gagal...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject j = new JSONObject(response);
                    String code = j.getString("code");
                    if(code.equals("1")){
                        listNilai.get(position).setNilai(s);
                        Toast.makeText(mContext, j.getString("message"), Toast.LENGTH_SHORT).show();
                        nilai.setText(s);
                        nilai.setTextColor(ContextCompat.getColor(mContext,android.R.color.black));
                        notifyItemChanged(position);
                    } else {
                        Toast.makeText(mContext, j.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listNilai.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView no, nama, nilai;
        public ViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            no = (TextView) itemView.findViewById(R.id.no);
            nama = (TextView) itemView.findViewById(R.id.nama);
            nilai = (TextView) itemView.findViewById(R.id.nilai);
        }
    }
}

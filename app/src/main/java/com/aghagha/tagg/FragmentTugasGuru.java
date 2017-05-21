package com.aghagha.tagg;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aghagha.tagg.data.AntaraSessionManager;
import com.aghagha.tagg.models.Berita;
import com.aghagha.tagg.models.Tugas;
import com.aghagha.tagg.utilities.NetworkUtils;
import com.aghagha.tagg.utilities.VolleyUtil;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTugasGuru extends Fragment {
    private Context mContext;
    private RecyclerView rvTugasGuru;
    private TugasGuruAdapter mAdapter;
    private List<Tugas> tugasList;
    private Spinner kelasSpinner;

    ArrayAdapter<String> kelasAdapter;
    HashMap<Integer,String> spinnerMap;

    private ProgressDialog progressDialog;
    private AntaraSessionManager session;

    private FloatingActionButton fb_tambah;
    private boolean firstTimeLoad = true, secondTimeLoad = true;
    private String kelasTerpilih;
    private String idKelasTerpilih;

    public FragmentTugasGuru() {
        // Required empty public constructor
    }

    public static FragmentTugasGuru newInstance() {
        FragmentTugasGuru fragment = new FragmentTugasGuru();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new AntaraSessionManager(getContext());
        session.checkLogin();

        spinnerMap = new HashMap<>();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
    }

    @Override
    public void onResume(){
        super.onResume();
        getTugasList(session.getKeyId(),idKelasTerpilih);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tugas_guru, container, false);
        rvTugasGuru = (RecyclerView) view.findViewById(R.id.rvTugasGuru);
        kelasSpinner = (Spinner) view.findViewById(R.id.kelas);
        tugasList = new ArrayList<>();
        fb_tambah = (FloatingActionButton) view.findViewById(R.id.fb_tambah);
        Drawable drawable = fb_tambah.getDrawable();
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        fb_tambah.setBackgroundDrawable(drawable);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        rvTugasGuru.setLayoutManager(linearLayoutManager);
        rvTugasGuru.hasFixedSize();

        mAdapter = new TugasGuruAdapter(tugasList);
        rvTugasGuru.setAdapter(mAdapter);

        rvTugasGuru.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0 ||dy<0 && fb_tambah.isShown())
                    fb_tambah.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    fb_tambah.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        getTugasList(session.getKeyId(),"0");

        kelasSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!firstTimeLoad){
                    getTugasList(session.getKeyId(),spinnerMap.get(i));
                    idKelasTerpilih = spinnerMap.get(i);
                    kelasTerpilih = adapterView.getItemAtPosition(i).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

         View.OnClickListener operation = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.fb_tambah:
                        Intent intent = new Intent(getActivity(),CreateTugasActivity.class);
                        intent.putExtra("id_kelas",idKelasTerpilih);
                        startActivity(intent);
                }
            }
         };
         fb_tambah.setOnClickListener(operation);
        return view;
    }

    private void getTugasList(String userId,String kelasId){
        progressDialog.setMessage("Sedang memuat...");
        showDialog();

        VolleyUtil volleyUtil = new VolleyUtil("req_tugas_list",getActivity(), NetworkUtils.tugas+"/"+userId+"/"+kelasId);
        volleyUtil.SendRequestGET(new VolleyUtil.VolleyResponseListener() {
            @Override
            public void onError(VolleyError error) {
                Toast.makeText(getActivity(),
                        "Halaman gagal dimuat, coba lagi", Toast.LENGTH_LONG).show();
                hideDialog();
            }

            @Override
            public void onResponse(String response) {
                String mResponse = response;
                hideDialog();
                Log.d("####",mResponse);
                mAdapter.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    if(code.equals("1")){
                        JSONObject mapel = jsonObject.getJSONObject("mapel");

                        if(firstTimeLoad) {
                            JSONArray kelasList = jsonObject.getJSONArray("kelas");
                            String[] spinnerArray = new String[kelasList.length()];
                            for (int i = 0; i < kelasList.length(); i++) {
                                JSONObject kelas = kelasList.getJSONObject(i);
                                spinnerMap.put(i, kelas.getString("id"));
                                spinnerArray[i] = kelas.getString("nama");
                            }
                            kelasAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinnerArray);
                            kelasSpinner.setAdapter(kelasAdapter);

                            firstTimeLoad=false;
                            idKelasTerpilih = spinnerMap.get(0);
                            kelasTerpilih = kelasAdapter.getItem(0);
                            Log.d("YOW",idKelasTerpilih);
                        }

                        JSONArray listTugas = jsonObject.getJSONArray("tugas");
                        if(listTugas.length()>0){
                            for(int i = 0; i < listTugas.length(); i++){
                                JSONObject tugas = listTugas.getJSONObject(i);
                                Tugas data = new Tugas(tugas.getInt("id"),
                                        tugas.getString("date"),
                                        tugas.getString("date2"),
                                        mapel.getString(tugas.getString("id_mapel")),
                                        tugas.getString("judul"),
                                        tugas.getString("konten"),
                                        tugas.getString("status"));
                                tugasList.add(data);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}

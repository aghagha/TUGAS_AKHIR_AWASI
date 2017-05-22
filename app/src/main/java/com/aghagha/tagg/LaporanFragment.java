package com.aghagha.tagg;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.aghagha.tagg.data.AntaraSessionManager;
import com.aghagha.tagg.models.Laporan;
import com.aghagha.tagg.utilities.NetworkUtils;
import com.aghagha.tagg.utilities.VolleyUtil;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LaporanFragment extends Fragment {
    private LinearLayout errorLayout, kosong;
    private Button btReload;
    private LinearLayout layout;
    private RecyclerView rvLaporan;
    private Spinner mapelSpinner;

    private List<Laporan> laporanList;

    private LaporanAdapter mAdapter;
    private ProgressDialog progressDialog;
    private AntaraSessionManager session;

    private boolean firstTimeLoad;
    private String idMapelTerpilih, idMurid, mapelTerpilih;
    private ArrayAdapter<String> mapelAdapter;
    private HashMap<Integer,String> spinnerMap;

    public LaporanFragment() {
        // Required empty public constructor
    }

    public static LaporanFragment newInstance() {
        LaporanFragment fragment = new LaporanFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new AntaraSessionManager(getContext());
        session.checkLogin();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

        laporanList = new ArrayList<>();
        spinnerMap = new HashMap<>();

        idMurid = session.getKeyMuridId();
        idMapelTerpilih = "0";
        mapelTerpilih = "";
        firstTimeLoad = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_laporan, container, false);
        rvLaporan = (RecyclerView)view.findViewById(R.id.rv_laporan);
        mapelSpinner = (Spinner)view.findViewById(R.id.sp_mapel);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        rvLaporan.setLayoutManager(linearLayoutManager);
        rvLaporan.hasFixedSize();

        mAdapter = new LaporanAdapter(laporanList);
        rvLaporan.setAdapter(mAdapter);

        layout = (LinearLayout) view.findViewById(R.id.layout);
        layout.setVisibility(View.GONE);

        kosong = (LinearLayout)view.findViewById(R.id.empty);
        errorLayout= (LinearLayout)view.findViewById(R.id.error);
        btReload = (Button)view.findViewById(R.id.btReload);
        btReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLaporan();
            }
        });

        getLaporan();

        mapelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(firstTimeLoad) firstTimeLoad = false;
                else {
                    idMapelTerpilih = spinnerMap.get(i);
                    mapelTerpilih = adapterView.getItemAtPosition(i).toString();
                    getLaporan();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    public void getLaporan(){
        progressDialog.setMessage("Sedang memuat...");
        progressDialog.show();

        VolleyUtil volleyUtil = new VolleyUtil("req_laporan",getActivity(), NetworkUtils.laporan+"/"+idMurid+"/"+idMapelTerpilih);
        volleyUtil.SendRequestGET(new VolleyUtil.VolleyResponseListener() {
            @Override
            public void onError(VolleyError error) {
                layout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                mAdapter.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    if(code.equals("1")){
                        JSONArray listLaporan = jsonObject.getJSONArray("tugas");
                        Log.d("LAPORAN",response);
                        if(listLaporan.length()>0){
                            kosong.setVisibility(View.GONE);
                            Laporan header = new Laporan(0,"","","Nil","Rata","Max");
                            laporanList.add(header);
                            for(int i = 0; i < listLaporan.length(); i++){
                                JSONObject tugas = listLaporan.getJSONObject(i);
                                Laporan data = new Laporan(tugas.getInt("id"),
                                        tugas.getString("judul"),
                                        tugas.getString("date"),
                                        tugas.getString("nilai"),
                                        tugas.getString("mean"),
                                        tugas.getString("max"));
                                laporanList.add(data);
                            }
                            mAdapter.notifyDataSetChanged();

                            if(firstTimeLoad){
                                JSONArray mapelList = jsonObject.getJSONArray("mapel");
                                String[] spinnerArray = new String[mapelList.length()+1];
                                spinnerMap.put(0, "0");
                                spinnerArray[0] = "Semua mata pelajaran";
                                for (int i = 1; i <= mapelList.length(); i++) {
                                    JSONObject kelas = mapelList.getJSONObject(i-1);
                                    spinnerMap.put(i, kelas.getString("id"));
                                    spinnerArray[i] = kelas.getString("nama");
                                }
                                mapelAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinnerArray);
                                mapelSpinner.setAdapter(mapelAdapter);
                                idMapelTerpilih = spinnerMap.get(0);
                                mapelTerpilih = mapelAdapter.getItem(0);
                            }
                        } else {
                            kosong.setVisibility(View.VISIBLE);
                        }
                        layout.setVisibility(View.VISIBLE);
                        errorLayout.setVisibility(View.GONE);
                    } else {
                        layout.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

}

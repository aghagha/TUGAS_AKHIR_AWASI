package com.aghagha.tagg;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.TextView;
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
    private TextView tvlabel;
    private FloatingActionButton fb_download;

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

        tvlabel = (TextView)view.findViewById(R.id.tvlabel);

        layout = (LinearLayout) view.findViewById(R.id.layout);

        kosong = (LinearLayout)view.findViewById(R.id.empty);
        errorLayout= (LinearLayout)view.findViewById(R.id.error);
        btReload = (Button)view.findViewById(R.id.btReload);
        btReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLaporan();
            }
        });

        fb_download = (FloatingActionButton)view.findViewById(R.id.fb_download);
        Drawable drawable = fb_download.getDrawable();
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        fb_download.setBackgroundDrawable(drawable);

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
                        errorLayout.setVisibility(View.GONE);
                        layout.setVisibility(View.VISIBLE);

                        JSONArray listLaporan = jsonObject.getJSONArray("tugas");
                        JSONArray mapelList = jsonObject.getJSONArray("mapel");

                        Log.d("LAPORAN",response);
                        if(mapelList.length()>0) {
                            setEmpty(false);

                            if (listLaporan.length() > 0) {
                                kosong.setVisibility(View.GONE);
                                Laporan header = new Laporan(0, "", "", "Nil", "Rata", "Max");
                                laporanList.add(header);
                                for (int i = 0; i < listLaporan.length(); i++) {
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

                                if (firstTimeLoad) {
                                    String[] spinnerArray = new String[mapelList.length() + 1];
                                    spinnerMap.put(0, "0");
                                    spinnerArray[0] = "Semua mata pelajaran";
                                    for (int i = 1; i <= mapelList.length(); i++) {
                                        JSONObject kelas = mapelList.getJSONObject(i - 1);
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
                        } else {
                            setEmpty(true);
                        }
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

    private void setEmpty(Boolean empty){
        if(empty){
            tvlabel.setVisibility(View.GONE);
            mapelSpinner.setVisibility(View.GONE);
            kosong.setVisibility(View.VISIBLE);
        } else {
            tvlabel.setVisibility(View.VISIBLE);
            mapelSpinner.setVisibility(View.VISIBLE);
            kosong.setVisibility(View.GONE);
        }
    }

}

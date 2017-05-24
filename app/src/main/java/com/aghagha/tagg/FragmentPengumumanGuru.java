package com.aghagha.tagg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
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
import com.aghagha.tagg.models.Topik;
import com.aghagha.tagg.utilities.NetworkUtils;
import com.aghagha.tagg.utilities.VolleyUtil;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FragmentPengumumanGuru extends Fragment {
    private LinearLayout errorLayout, kosong;
    private TextView tvlabel;
    private CoordinatorLayout layout;
    private Button btReload;
    private RecyclerView mRecyclerView;
    private FloatingActionButton fb_tambah;
    private Spinner kelasSpinner;

    private ForumGuruAdapter mAdapter;
    private ArrayAdapter<String> kelasAdapter;

    private List<Topik> topikList;
    private HashMap<Integer,String> spinnerMap;
    private ProgressDialog progressDialog;
    private AntaraSessionManager session;

    private String idKelasTerpilih;
    private boolean firstTimeLoad = true;

    public FragmentPengumumanGuru() {

    }
    public static FragmentPengumumanGuru newInstance() {
        FragmentPengumumanGuru fragment = new FragmentPengumumanGuru();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        session = new AntaraSessionManager(getContext());
        session.checkLogin();

        idKelasTerpilih = "0";

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

        spinnerMap=new HashMap<>();

        super.onCreate(savedInstanceState);


    }

    @Override
    public void onResume(){
        super.onResume();
        getTopikList(idKelasTerpilih);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View.OnClickListener operation;
        View view = inflater.inflate(R.layout.fragment_fragment_pengumuman_guru, container, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.rvForumGuru);
        topikList = new ArrayList<>();

        tvlabel = (TextView)view.findViewById(R.id.tvlabel);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.hasFixedSize();

        mAdapter=new ForumGuruAdapter(topikList);
        mRecyclerView.setAdapter(mAdapter);

        kelasSpinner = (Spinner)view.findViewById(R.id.kelas);

        fb_tambah = (FloatingActionButton)view.findViewById(R.id.fb_tambah);
        Drawable drawable = fb_tambah.getDrawable();
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        fb_tambah.setBackgroundDrawable(drawable);

        layout = (CoordinatorLayout) view.findViewById(R.id.layout);
        layout.setVisibility(View.GONE);

        kosong = (LinearLayout)view.findViewById(R.id.empty);
        errorLayout= (LinearLayout)view.findViewById(R.id.error);
        btReload = (Button)view.findViewById(R.id.btReload);
        btReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTopikList(idKelasTerpilih);
            }
        });

        operation = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case(R.id.fb_tambah):
                        Intent intent = new Intent(getActivity(),CreateTopikActivity.class);
                        intent.putExtra("id_kelas",idKelasTerpilih);
                        startActivity(intent);
                        break;
                }
            }
        };
        fb_tambah.setOnClickListener(operation);

        getTopikList(idKelasTerpilih);
        kelasSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!firstTimeLoad) {
                    getTopikList(spinnerMap.get(i));
                    idKelasTerpilih = spinnerMap.get(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    private void getTopikList(final String id_kelas) {
        progressDialog.setMessage("Sedang memuat...");
        progressDialog.show();
        VolleyUtil volleyUtil = new VolleyUtil("req_forum_list",getActivity(), NetworkUtils.forum_guru + "/" + session.getKeyId() +"/"+ id_kelas);
        volleyUtil.SendRequestGET(new VolleyUtil.VolleyResponseListener() {
            @Override
            public void onError(VolleyError error) {
                layout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(String response) {
                Log.d("getTopikList",response);
                progressDialog.dismiss();
                mAdapter.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    if(code.equals("1")){
                        JSONArray listTopik = jsonObject.getJSONArray("topik");
                        if(listTopik.length()>0){
                            for (int i=0;i<listTopik.length();i++){
                                JSONObject topik = listTopik.getJSONObject(i);
                                Topik data = new Topik(topik.getString("id"),
                                        topik.getString("lampiran"),
                                        topik.getString("judul"),
                                        topik.getString("konten"),
                                        topik.getString("date"),
                                        topik.getString("gambar"),
                                        topik.getString("komentar"));
                                topikList.add(data);
                            }
                            mAdapter.notifyDataSetChanged();

                            kosong.setVisibility(View.GONE);
                        } else {
                            kosong.setVisibility(View.VISIBLE);
                        }
                        if(firstTimeLoad){
                            JSONArray kelasArray = jsonObject.getJSONArray("kelas");
                            String[] spinnerArray = new String[kelasArray.length()];
                            if(kelasArray.length()>0) {
                                setEmpty(false);

                                for (int i = 0; i < kelasArray.length(); i++) {
                                    JSONObject kelas = kelasArray.getJSONObject(i);
                                    spinnerMap.put(i, kelas.getString("id"));
                                    spinnerArray[i] = kelas.getString("nama");
                                }
                                kelasAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinnerArray);
                                kelasSpinner.setAdapter(kelasAdapter);

                                idKelasTerpilih = spinnerMap.get(0);
                                firstTimeLoad = false;
                            } else {
                                setEmpty(true);
                            }
                        }
                    } else {
                        layout.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                    }
                    layout.setVisibility(View.VISIBLE);
                    errorLayout.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setEmpty(Boolean empty){
        if(empty){
            tvlabel.setVisibility(View.GONE);
            kelasSpinner.setVisibility(View.GONE);
            fb_tambah.setVisibility(View.GONE);
            kosong.setVisibility(View.VISIBLE);
        } else {
            tvlabel.setVisibility(View.VISIBLE);
            kelasSpinner.setVisibility(View.VISIBLE);
            fb_tambah.setVisibility(View.VISIBLE);
            kosong.setVisibility(View.GONE);
        }
    }


}

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aghagha.tagg.data.AntaraSessionManager;
import com.aghagha.tagg.models.Tugas;
import com.aghagha.tagg.utilities.NetworkUtils;
import com.aghagha.tagg.utilities.VolleyUtil;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TugasMuridFragment extends Fragment {
    private NestedScrollView nested;
    private LinearLayout errorLayout, kosong;
    private Button btReload;
    private RecyclerView rvTugasMurid;

    private List<Tugas> tugasList;

    private TugasMuridAdapter mAdapter;
    private ProgressDialog progressDialog;
    private AntaraSessionManager session;

    public TugasMuridFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TugasMuridFragment newInstance() {
        TugasMuridFragment fragment = new TugasMuridFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new AntaraSessionManager(getContext());
        session.checkLogin();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

        tugasList = new ArrayList<>();
    }

    @Override
    public void onResume(){
        super.onResume();
        getTugasList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tugas_murid, container, false);

        rvTugasMurid = (RecyclerView)view.findViewById(R.id.rvTugasMurid);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        rvTugasMurid.setLayoutManager(linearLayoutManager);
        rvTugasMurid.hasFixedSize();

        mAdapter = new TugasMuridAdapter(tugasList);
        rvTugasMurid.setAdapter(mAdapter);
        rvTugasMurid.setNestedScrollingEnabled(false);

        kosong = (LinearLayout)view.findViewById(R.id.empty);
        errorLayout= (LinearLayout)view.findViewById(R.id.error);
        btReload = (Button)view.findViewById(R.id.btReload);
        btReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTugasList();
            }
        });

        nested = (NestedScrollView)view.findViewById(R.id.nested);

        getTugasList();

        return view;
    }

    private void getTugasList(){
        progressDialog.setMessage("Sedang memuat...");
        showDialog();

        VolleyUtil volleyUtil = new VolleyUtil("req_tugas_list",getActivity(), NetworkUtils.tugasMurid+"/"+session.getKeyMuridId());
        volleyUtil.SendRequestGET(new VolleyUtil.VolleyResponseListener() {
            @Override
            public void onError(VolleyError error) {
                nested.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
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
                        nested.setVisibility(View.VISIBLE);
                        errorLayout.setVisibility(View.GONE);

                        JSONObject mapel = jsonObject.getJSONObject("mapel");

                        JSONArray listTugas = jsonObject.getJSONArray("tugas");
                        if(listTugas.length()>0){
                            kosong.setVisibility(View.GONE);
                            for(int i = 0; i < listTugas.length(); i++){
                                JSONObject tugas = listTugas.getJSONObject(i);
                                Tugas data = new Tugas(tugas.getInt("id"),
                                        tugas.getString("date"),
                                        tugas.getString("date2"),
                                        mapel.getString(tugas.getString("id_mapel")),
                                        tugas.getString("judul"),
                                        tugas.getString("konten"),
                                        tugas.getString("status"),
                                        tugas.getString("cek"),
                                        tugas.getBoolean("telat"));
                                tugasList.add(data);
                            }
                        } else {
                            nested.setVisibility(View.GONE);
                            kosong.setVisibility(View.VISIBLE);
                        }
                        mAdapter.notifyDataSetChanged();

                    } else {
                        nested.setVisibility(View.GONE);
                        kosong.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
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

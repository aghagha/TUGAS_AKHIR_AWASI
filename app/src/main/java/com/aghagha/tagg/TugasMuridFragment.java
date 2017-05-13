package com.aghagha.tagg;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
    private RecyclerView rvTugasMurid;


    private List<Tugas> tugasList;

    private TugasGuruAdapter mAdapter;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tugas_murid, container, false);

        rvTugasMurid = (RecyclerView)view.findViewById(R.id.rvTugasMurid);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        rvTugasMurid.setLayoutManager(linearLayoutManager);
        rvTugasMurid.hasFixedSize();

        mAdapter = new TugasGuruAdapter(tugasList);
        rvTugasMurid.setAdapter(mAdapter);

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

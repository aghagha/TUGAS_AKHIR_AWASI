package com.aghagha.tagg;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aghagha.tagg.data.AntaraSessionManager;
import com.aghagha.tagg.models.Berita;
import com.aghagha.tagg.utilities.NetworkUtils;
import com.aghagha.tagg.utilities.VolleyUtil;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FragmentBerandaMurid extends Fragment {

    private RecyclerView mRecyclerView;
    private BerandaGuruAdapter mAdapter;
    private List<Berita> beritaList;

    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeContainer;

    private String userId;

    private AntaraSessionManager session;

    public FragmentBerandaMurid() {
        // Required empty public constructor
    }

    public static FragmentBerandaMurid newInstance() {
        FragmentBerandaMurid fragment = new FragmentBerandaMurid();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new AntaraSessionManager(getContext());
        session.checkLogin();

        userId = session.getKeyMuridId();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view  = inflater.inflate(R.layout.fragment_fragment_beranda_murid, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvBerandaMurid);
        beritaList = new ArrayList<>();

        NestedScrollView scrollView = (NestedScrollView) view.findViewById (R.id.nest_scrollview);
        scrollView.setFillViewport (true);
        mRecyclerView.setNestedScrollingEnabled(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.hasFixedSize();

        mAdapter = new BerandaGuruAdapter(beritaList);
        mRecyclerView.setAdapter(mAdapter);

        getBeritaList();
        return view;
    }

    private void getBeritaList() {
        progressDialog.setMessage("Sedang memuat...");
        progressDialog.show();
        VolleyUtil volleyUtil = new VolleyUtil("req_berita_list", getActivity(), NetworkUtils.berita_list + '/' + userId);
        final String[] mResponse = new String[1];
        volleyUtil.SendRequestGET(new VolleyUtil.VolleyResponseListener() {
            @Override
            public void onError(VolleyError error) {
                Toast.makeText(getActivity(),
                        "Halaman gagal dimuat, coba lagi", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(String response) {
                mResponse[0] = response;
                progressDialog.dismiss();
                mAdapter.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.length() > 0){
                        JSONArray listBerita = jsonObject.getJSONArray("berita");
                        Log.d("jumlah berita", String.valueOf(listBerita.length()));
                        for(int i = 0; i < listBerita.length(); i++){
                            JSONObject berita = listBerita.getJSONObject(i);
                            Berita data = new Berita(berita.getInt("id_t_berita"),
                                    berita.getString("judul"),
                                    berita.getString("konten"),
                                    berita.getString("gambar"),
                                    berita.getString("lampiran"),
                                    berita.getString("date"));
                            beritaList.add(data);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

}

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
import com.aghagha.tagg.models.Topik;
import com.aghagha.tagg.utilities.NetworkUtils;
import com.aghagha.tagg.utilities.VolleyUtil;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentPengumumanMurid extends Fragment {
    private LinearLayout errorLayout, kosong;
    private NestedScrollView layout;
    private Button btReload;
    private RecyclerView mRecyclerView;

    private ForumGuruAdapter mAdapter;
    private ProgressDialog progressDialog;
    private AntaraSessionManager session;
    private Context context;

    private List<Topik> topikList;


    public FragmentPengumumanMurid() {
        // Required empty public constructor
    }

    public static FragmentPengumumanMurid newInstance() {
        FragmentPengumumanMurid fragment = new FragmentPengumumanMurid();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        session = new AntaraSessionManager(getContext());
        session.checkLogin();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_pengumuman_murid, container, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.rvForumMurid);
        topikList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.hasFixedSize();

        mAdapter=new ForumGuruAdapter(topikList);
        mRecyclerView.setAdapter(mAdapter);

        layout = (NestedScrollView) view.findViewById(R.id.layout);
        layout.setVisibility(View.GONE);

        kosong = (LinearLayout)view.findViewById(R.id.empty);
        errorLayout= (LinearLayout)view.findViewById(R.id.error);
        btReload = (Button)view.findViewById(R.id.btReload);
        btReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTopikList();
            }
        });

        getTopikList();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        getTopikList();
    }

    private void getTopikList() {
        progressDialog.setMessage("Sedang memuat...");
        progressDialog.show();
        VolleyUtil volleyUtil = new VolleyUtil("req_forum_list", getActivity(), NetworkUtils.forum_murid + "/" + session.getKeyMuridId());
        volleyUtil.SendRequestGET(new VolleyUtil.VolleyResponseListener() {
            @Override
            public void onError(VolleyError error) {
                layout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(String response) {
                Log.d("getTopikList", response);
                progressDialog.dismiss();
                mAdapter.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        errorLayout.setVisibility(View.GONE);
                        kosong.setVisibility(View.GONE);
                        layout.setVisibility(View.VISIBLE);

                        JSONArray listTopik = jsonObject.getJSONArray("topik");
                        if (listTopik.length() > 0) {
                            for (int i = 0; i < listTopik.length(); i++) {
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
                        } else {
                            kosong.setVisibility(View.VISIBLE);
                            layout.setVisibility(View.GONE);
                        }
                    } else {
                        layout.setVisibility(View.GONE);
                        kosong.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

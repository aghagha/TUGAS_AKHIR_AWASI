package com.aghagha.tagg;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aghagha.tagg.utilities.NetworkUtils;
import com.aghagha.tagg.utilities.VolleyUtil;
import com.aghagha.tagg.data.AntaraSessionManager;
import com.aghagha.tagg.models.Berita;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FragmentBerandaGuru extends Fragment implements BerandaGuruAdapter.BerandaGuruAdapterOnClickHandler{
    private String title;
    private int page;

    private Context mContext;
    private RecyclerView mRecyclerView;
    private BerandaGuruAdapter mAdapter;
    private List<Berita> beritaList;

    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeContainer;

    private String userId;

    private AntaraSessionManager session;

    public FragmentBerandaGuru() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public static FragmentBerandaGuru newInstance(int page, String title) {
        FragmentBerandaGuru fragment = new FragmentBerandaGuru();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new AntaraSessionManager(getContext());
        session.checkLogin();

        HashMap<String, String> user = session.getUserDetails();
        userId = user.get(AntaraSessionManager.KEY_USER_ID);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
    }

    private void getBeritaList(final String userId) {
        progressDialog.setMessage("Sedang memuat...");
        showDialog();
        VolleyUtil volleyUtil = new VolleyUtil("req_berita_list", getActivity(), NetworkUtils.berita_list + '/' + userId);
        final String[] mResponse = new String[1];
        volleyUtil.SendRequestGET(new VolleyUtil.VolleyResponseListener() {
            @Override
            public void onError(VolleyError error) {
                Toast.makeText(getActivity(),
                        "Halaman gagal dimuat, coba lagi", Toast.LENGTH_LONG).show();
                hideDialog();
            }

            @Override
            public void onResponse(String response) {
                mResponse[0] = response;
                hideDialog();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beranda_guru, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvBerandaGuru);
        beritaList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.hasFixedSize();

        mAdapter = new BerandaGuruAdapter(beritaList);
        mRecyclerView.setAdapter(mAdapter);

        getBeritaList(userId);
        return view;
    }

    @Override
    public void onClick(String judul) {

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

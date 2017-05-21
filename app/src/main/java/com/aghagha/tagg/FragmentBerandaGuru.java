package com.aghagha.tagg;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aghagha.tagg.models.Jadwal;
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
    private RecyclerView mRecyclerView, rvJadwal;
    private BerandaGuruAdapter mAdapter;
    private JadwalAdapter mJadwalAdapter;

    private List<Berita> beritaList;
    private List<Jadwal> jadwalList;

    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeContainer;

    private TextView hari, tanggal, bulan, tv_jadwal;
    private LinearLayout errorLayout, empty;
    private NestedScrollView nestedScrollView;
    private Button reload;

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
                errorLayout.setVisibility(View.VISIBLE);
                nestedScrollView.setVisibility(View.GONE);
                hideDialog();
            }

            @Override
            public void onResponse(String response) {
                errorLayout.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.VISIBLE);
                mResponse[0] = response;
                hideDialog();
                mAdapter.clear();
                mJadwalAdapter.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.length() > 0){
                        hari.setText(jsonObject.getString("hari"));
                        tanggal.setText(jsonObject.getString("tanggal"));
                        bulan.setText(jsonObject.getString("bulan").toUpperCase());
                        JSONArray listJadwal = jsonObject.getJSONArray("jadwal");
                        if(listJadwal.length()>0){
                            tv_jadwal.setVisibility(View.GONE);rvJadwal.setVisibility(View.VISIBLE);
                            JSONObject listMapel = jsonObject.getJSONObject("mapel");
                            for (int j = 0; j< listJadwal.length();j++){
                                JSONObject jadwal = listJadwal.getJSONObject(j);
                                Jadwal data = new Jadwal(jadwal.getString("mulai")+"-"+jadwal.getString("berakhir"),
                                        jadwal.getString("kelas"),
                                        listMapel.getString(jadwal.getString("id_mapel")));
                                jadwalList.add(data);
                            }
                        } else {
                            tv_jadwal.setVisibility(View.VISIBLE);rvJadwal.setVisibility(View.GONE);
                        }

                        JSONArray listBerita = jsonObject.getJSONArray("berita");
                        if(listBerita.length()>0){
                            empty.setVisibility(View.GONE);
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
                        } else {
                            empty.setVisibility(View.VISIBLE);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    mJadwalAdapter.notifyDataSetChanged();
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
        rvJadwal = (RecyclerView) view.findViewById(R.id.rvJadwal);
        beritaList = new ArrayList<>();
        jadwalList = new ArrayList<>();
        mRecyclerView.setNestedScrollingEnabled(false);
        rvJadwal.setNestedScrollingEnabled(false);

        hari = (TextView)view.findViewById(R.id.hari);
        tanggal = (TextView)view.findViewById(R.id.tanggal);
        bulan = (TextView)view.findViewById(R.id.bulan);
        tv_jadwal = (TextView)view.findViewById(R.id.tv_jadwal);
        errorLayout = (LinearLayout)view.findViewById(R.id.error);
        empty = (LinearLayout)view.findViewById(R.id.empty);
        nestedScrollView = (NestedScrollView)view.findViewById(R.id.nested);
        reload = (Button)view.findViewById(R.id.btReload);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBeritaList(userId);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.hasFixedSize();

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        rvJadwal.setLayoutManager(linearLayoutManager2);
        rvJadwal.hasFixedSize();

        mAdapter = new BerandaGuruAdapter(beritaList);
        mJadwalAdapter = new JadwalAdapter(jadwalList);

        mRecyclerView.setAdapter(mAdapter);
        rvJadwal.setAdapter(mJadwalAdapter);

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

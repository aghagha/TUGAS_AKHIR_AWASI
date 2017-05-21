package com.aghagha.tagg;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.aghagha.tagg.data.AntaraSessionManager;
import com.aghagha.tagg.models.Berita;
import com.aghagha.tagg.models.Jadwal;
import com.aghagha.tagg.utilities.DownloadImageTask;
import com.aghagha.tagg.utilities.NetworkUtils;
import com.aghagha.tagg.utilities.VolleyUtil;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentBerandaMurid extends Fragment {

    private RecyclerView mRecyclerView, rvJadwal;
    private BerandaGuruAdapter mAdapter;
    private JadwalAdapter jadwalAdapter;
    private List<Berita> beritaList;
    private List<Jadwal> jadwalList;
    private TextView tv_nama, tv_sekolah, tv_kelas, hari, tanggal, bulan, tv_jadwal;
    private CircleImageView iv_profile;
    private NestedScrollView nested;
    private LinearLayout errorLayout, empty;
    private Button btReload;

    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeContainer;

    private String userId;

    private AntaraSessionManager session;

    public FragmentBerandaMurid() {
        // Required empty public constructor
    }

    public static FragmentBerandaMurid newInstance(String nama, String sekolah, String kelas, String gambar) {
        FragmentBerandaMurid fragment = new FragmentBerandaMurid();
        Bundle bundle = new Bundle();
        bundle.putString("nama",nama);
        bundle.putString("sekolah",sekolah);
        bundle.putString("kelas",kelas);
        bundle.putString("gambar",gambar);
        fragment.setArguments(bundle);
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
        Bundle bundle = getArguments();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvBerandaMurid);
        beritaList = new ArrayList<>();

        rvJadwal = (RecyclerView)view.findViewById(R.id.rvJadwal);
        jadwalList = new ArrayList<>();

        NestedScrollView scrollView = (NestedScrollView) view.findViewById (R.id.nest_scrollview);
        scrollView.setFillViewport (true);
        mRecyclerView.setNestedScrollingEnabled(false);
        rvJadwal.setNestedScrollingEnabled(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.hasFixedSize();

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        rvJadwal.setLayoutManager(linearLayoutManager2);
        rvJadwal.hasFixedSize();

        mAdapter = new BerandaGuruAdapter(beritaList);
        mRecyclerView.setAdapter(mAdapter);

        jadwalAdapter = new JadwalAdapter(jadwalList);
        rvJadwal.setAdapter(jadwalAdapter);

        tv_nama = (TextView)view.findViewById(R.id.nama);
        tv_sekolah = (TextView)view.findViewById(R.id.sekolah);
        tv_kelas = (TextView)view.findViewById(R.id.kelas);
        hari = (TextView)view.findViewById(R.id.hari);
        tanggal = (TextView)view.findViewById(R.id.tanggal);
        bulan = (TextView)view.findViewById(R.id.bulan);
        tv_jadwal = (TextView)view.findViewById(R.id.tv_jadwal);

        iv_profile = (CircleImageView)view.findViewById(R.id.gambar);

        tv_nama.setText(bundle.getString("nama"));
        tv_sekolah.setText(bundle.getString("sekolah"));
        tv_kelas.setText(bundle.getString("kelas"));
        String gambar = bundle.getString("gambar");
        if(!gambar.equals("")){
            new DownloadImageTask(iv_profile).execute(NetworkUtils.profil_image+gambar);
        }

        nested = (NestedScrollView)view.findViewById(R.id.nest_scrollview);
        nested.setVisibility(View.GONE);
        errorLayout = (LinearLayout)view.findViewById(R.id.error);
        empty = (LinearLayout)view.findViewById(R.id.empty);
        btReload = (Button)view.findViewById(R.id.btReload);

        btReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBeritaList();
            }
        });

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
                nested.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(String response) {
                mResponse[0] = response;
                progressDialog.dismiss();
                mAdapter.clear();
                jadwalAdapter.clear();
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
                        } else empty.setVisibility(View.VISIBLE);
                    }
                    nested.setVisibility(View.VISIBLE);
                    errorLayout.setVisibility(View.GONE);
                    jadwalAdapter.notifyDataSetChanged();
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

}

package com.aghagha.tagg;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.aghagha.tagg.data.AntaraSessionManager;
import com.aghagha.tagg.utilities.NetworkUtils;
import com.aghagha.tagg.utilities.VolleyUtil;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateTugasActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    AntaraSessionManager session;
    ProgressDialog pDialog;
    EditText judul, konten, deadline;
    Spinner mapelSpinner;
    Button save;

    Map<Integer,String> spinnerMap;
    String kelasId, idMapelTerpilih;
    boolean firstTimeLoad = true, secondTimeLoad = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tugas);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        kelasId = intent.getStringExtra("id_kelas");
        pDialog = new ProgressDialog(this);
        session = new AntaraSessionManager(this);

        judul = (EditText)findViewById(R.id.et_judul);
        konten = (EditText)findViewById(R.id.et_konten);
        deadline = (EditText)findViewById(R.id.et_deadline);
        mapelSpinner = (Spinner)findViewById(R.id.mapel);
        save = (Button)findViewById(R.id.save);

        getMapel();
        final Calendar c = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        View.OnClickListener operation = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case(R.id.et_deadline):
                        datePickerDialog.show();
                        break;
                    case(R.id.save):
                        storeTugas();
                        break;
                }
            }
        };
        mapelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!secondTimeLoad){
                    idMapelTerpilih = spinnerMap.get(i);
                } else {
                    secondTimeLoad = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        deadline.setOnClickListener(operation);
        save.setOnClickListener(operation);
    }

    public void getMapel(){
        pDialog.setMessage("Sedang memuat...");
        pDialog.show();
        VolleyUtil volleyUtil = new VolleyUtil("req_get_mapel",this, NetworkUtils.mapel);
        volleyUtil.SendRequestGET(new VolleyUtil.VolleyResponseListener() {
            @Override
            public void onError(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(CreateTugasActivity.this, "Halaman gagal dimuat, coba lagi", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                Log.d("####",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    if(code.equals("1")){
                        JSONArray mapel_ = jsonObject.getJSONArray("mapel");
                        String[] spinnerArray = new String[mapel_.length()];
                        spinnerMap = new HashMap<>();
                        for (int i = 0; i < mapel_.length(); i++) {
                            JSONObject city = mapel_.getJSONObject(i);
                            spinnerMap.put(i, city.getString("id"));
                            spinnerArray[i] = city.getString("nama");
                        }
                        ArrayAdapter<String> kelasAdapter = new ArrayAdapter<>(CreateTugasActivity.this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
                        mapelSpinner.setAdapter(kelasAdapter);
                        idMapelTerpilih = spinnerMap.get(0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void storeTugas() {
        pDialog.setMessage("Sedang menyimpan...");
        pDialog.show();

        VolleyUtil volleyUtil = new VolleyUtil("req_store_tugas",this, NetworkUtils.tugas);
        Map<String,String> params = new HashMap<>();
        params.put("judul",judul.getText().toString());
        params.put("konten",konten.getText().toString());
        params.put("deadline",deadline.getText().toString());
        params.put("id_guru",session.getKeyId());
        params.put("id_kelas",kelasId);
        params.put("id_mapel",idMapelTerpilih);
        volleyUtil.SendRequestPOST(params, new VolleyUtil.VolleyResponseListener() {
            @Override
            public void onError(VolleyError error) {
                pDialog.dismiss();
                error.printStackTrace();
                Log.d("eee",error.getMessage().toString());
                Toast.makeText(CreateTugasActivity.this, "Tugas gagal ditambahkan, coba lagi", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")){
                        Toast.makeText(CreateTugasActivity.this, "Tugas berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else Toast.makeText(CreateTugasActivity.this, "Tugas gagal ditambahkan, coba lagi", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        String m, d;
        i1++;
        if(i1<10) m = "0"+i1; else m = String.valueOf(i1);
        if(i2<10) d = "0"+i2; else d = String.valueOf(i2);
        deadline.setText(i+"-"+m+"-"+d);
    }
}

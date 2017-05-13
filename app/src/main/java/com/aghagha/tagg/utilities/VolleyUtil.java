package com.aghagha.tagg.utilities;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;

/**
 * Created by aghagha on 28/04/2017.
 */

public class VolleyUtil {
    private String tag_string_req;
    private StringRequest stringRequest;
    private Context mcontext;
    private String url;
    private RequestQueue requestQueue;

    public VolleyUtil(String req, Context context, String _url){
        tag_string_req = req;
        mcontext = context;
        url = _url;
        requestQueue = Volley.newRequestQueue(context);
    }

    public void SendRequestGET(final VolleyResponseListener listener){
        stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });
        stringRequest.setTag(tag_string_req);
        requestQueue.add(stringRequest);
    }

    public void SendRequestPOST(final Map<String,String> param, final VolleyResponseListener listener){
        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        }){
            @Override
            public Map<String,String> getParams(){
                return param;
            }
        };
        stringRequest.setTag(tag_string_req);
        requestQueue.add(stringRequest);
    }

    public interface VolleyResponseListener {
        void onError(VolleyError error);

        void onResponse(String response);
    }
}

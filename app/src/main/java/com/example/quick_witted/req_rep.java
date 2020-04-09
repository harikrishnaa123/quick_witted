package com.example.quick_witted;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class req_rep extends AppCompatActivity {

    Spinner emergencies;
    Button submit, check;
    TextView hosp_resp;
    RequestQueue queue;
    String req_url, option, res_url;
    int num=1;

    private ArrayList<QueryDocumentSnapshot> mSnapshot;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_req_rep);
        emergencies = findViewById(R.id.list);
        submit = findViewById(R.id.req_button);
        check = findViewById(R.id.check_button);
        hosp_resp = findViewById(R.id.response);
        queue = Volley.newRequestQueue(this);
        req_url = "http://192.168.1.4:3000/client/request";
        SharedPreferences sp = getSharedPreferences("user_details", MODE_PRIVATE);
        final String cellno = sp.getString("cell_no", "null");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest request =new StringRequest(Request.Method.POST, req_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("posterror", error.toString());
                    }
                }){
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<String,String>();
                        params.put("cellno", cellno);
                        params.put("hospital", "HarikrishnaaFertilizationCenter");
                        params.put("emergency", option);
                        return params;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(
                        50000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(request);
            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuffer sb = new StringBuffer(50);
                res_url = sb.append("http://192.168.1.4:3000/client/response?cellno=").toString();
                res_url = sb.append(cellno+"&start=").toString();
                res_url = sb.append(num).toString();
                num++;
                Log.d("res_url", res_url);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, res_url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.d("hospital", response.getString("name"));
                                    hosp_resp.setText("hospital:"+response.getString("name")+"\n");
                                    Object res = response.get("res");
                                    if(res instanceof JSONArray){
                                        JSONArray ja = response.getJSONArray("res");
                                        for(int i=0;i<ja.length();i++){
                                            Log.d("response", ja.getString(i));
                                            hosp_resp.append(ja.getString(i)+"\n");
                                        }
                                    }else{
                                        Log.d("response", response.getString("res"));
                                        hosp_resp.append(response.getString("res"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error", error.toString());
                    }
                });
                request.setRetryPolicy(new DefaultRetryPolicy(
                        50000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(request);
            }
        });


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.emergencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emergencies.setAdapter(adapter);
        emergencies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                option = (String) emergencies.getItemAtPosition(i);
                Log.d("option", option);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

}

package com.vitaliy_petuhov.quickjob.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.vitaliy_petuhov.quickjob.R;
import com.vitaliy_petuhov.quickjob.application.AppConfig;
import com.vitaliy_petuhov.quickjob.application.AppController;
import com.vitaliy_petuhov.quickjob.helper.SQLiteHandler;
import com.vitaliy_petuhov.quickjob.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HistoryResult extends AppCompatActivity {

    private static final String TAG = HistoryResult.class.getSimpleName();
    private SessionManager session;
    private SQLiteHandler db;
    private ProgressDialog pDialog;
    TextView tv_name, tv_type, tv_city;
    ImageButton close_menu, deletehistory;
    String typejob_str;

    ArrayList<HashMap<String, String>> jobsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_result);

        session = new SessionManager(getApplicationContext());
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        db = new SQLiteHandler(getApplicationContext());

        jobsList = new ArrayList<HashMap<String, String>>();

        getAllHistory();

        tv_name = (TextView) findViewById(R.id.jobname_history);
        tv_type = (TextView) findViewById(R.id.typejob_history);
        tv_city = (TextView) findViewById(R.id.city_history);

        close_menu = (ImageButton) findViewById(R.id.histroy_closemenu_butt);

        close_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryResult.this, FindJobActivity.class);
                startActivity(intent);
            }
        });

        deletehistory = (ImageButton) findViewById(R.id.history_delete);

        deletehistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String,String> detail = db.getUserDetails();

                String uName = detail.get("name");

                deleteHistory(uName);

                showMessage("История поиска очищена успешно!");

                Intent intent = new Intent(HistoryResult.this, FindJobActivity.class);
                startActivity(intent);

            }
        });

        ListView listV = (ListView)findViewById(R.id.history_job_lV);

        listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String tv_name_str = ((TextView) view.findViewById(R.id.jobname_history)).getText().toString();
                String tv_type_str = ((TextView) view.findViewById(R.id.typejob_history)).getText().toString();
                String tv_city_str = ((TextView) view.findViewById(R.id.city_history)).getText().toString();

                if(tv_type_str.equals("Постоянная работа")) typejob_str = "1";
                else if(tv_type_str.equals("Временная работа")) typejob_str = "2";
                else if(tv_type_str.equals("Разовая работа")) typejob_str = "3";
                else if(tv_type_str.equals("Подработка")) typejob_str = "4";

                Intent intent = new Intent(HistoryResult.this, HistorySearch.class);
                intent.putExtra("jName_historyresult_historysearch",tv_name_str);
                intent.putExtra("jType_historyresult_historysearch",typejob_str);
                intent.putExtra("jCity_historyresult_historysearch", tv_city_str);
                startActivity(intent);

               // showMessage("Name = [" + tv_name_str + "] Type = [" + tv_type_str +"] TypeNum = [" + typejob_str + "] City = [" + tv_city_str + "]");
            }
        });


    }


    private void deleteHistory(final String Uname){

        String tag_string_req = "req_delete_history";


        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.DELETE_HISTORY_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Add Job Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (error) {
                        String errorMsg = jObj.getString("error_msg");
                        showMessage(errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Add History Error: " + error.getMessage());
               // showMessage(error.getMessage());
                showMessage("Отсутствует интернет соединение");
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to job url
                Map<String, String> params = new HashMap<String, String>();

                params.put("tag", "deletehistory");
                params.put("job_username_h",Uname);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void getAllHistory(){
        String tag_string_req = "req_all_history";

        HashMap<String,String> detail = db.getUserDetails();

        String uName = detail.get("name");

        if(uName.contains(" ")) {
            uName = uName.replace(" ", "+");
        }

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.GET_ALL_HISTORY_URL + "&job_username_h=" + uName,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "History Response: " + response.toString());
                        hideDialog();

                        try {

                            JSONArray jsonarray = new JSONArray(response);
                            JSONObject jMsg = jsonarray.getJSONObject(0);
                            boolean msg = jMsg.getBoolean("msg");
                            if(msg){

                                jobsList.clear();

                                for (int i=1; i < jsonarray.length(); i++){
                                    JSONObject jObj = jsonarray.getJSONObject(i);

                                    String jobname = jObj.getString("job_name_h");

                                    String jobtype = jObj.getString("job_type_h");

                                    String city = jObj.getString("job_city_h");

                                    HashMap<String, String> map = new HashMap<String, String>();

                                    map.put("job_name_h", jobname);

                                    map.put("job_type_h", jobtype);

                                    map.put("job_city_h", city);

                                    jobsList.add(map);
                                }
                                addInfoAllHistory();

                            }


                        }
                        catch (JSONException e){

                            e.printStackTrace();

                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Find All Jobs Error: " + error.getMessage());
                //showMessage(error.getMessage());
                showMessage("Отсутствует интернет соединение");
                hideDialog();
            }
        }) {

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void addInfoAllHistory() {

        ListView lw = (ListView) findViewById(R.id.history_job_lV);

        String [] headers = new String[]
                {
                        "job_name_h",
                        "job_type_h",
                        "job_city_h"
                };

        int [] to =  new int[]
                {
                        R.id.jobname_history,
                        R.id.typejob_history,
                        R.id.city_history
                };

        ListAdapter adapter = new SimpleAdapter(HistoryResult.this, jobsList, R.layout.item_history, headers, to);
        lw.setAdapter(adapter);

       // String jName = tv_name.getText().toString();
       // if(jName.equals("")) tv_name.setText("-");
        //TextView tv_city = (TextView) findViewById(R.id.city_history);
       // if(tv_city.equals("")) tv_city.setText("-");
    }

    public void showDialog() {
        if (!pDialog.isShowing()) {
            pDialog.show();
        }
    }

    public void hideDialog() {
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    private void showMessage(String msg) {
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

}

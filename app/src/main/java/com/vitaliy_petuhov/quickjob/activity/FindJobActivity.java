package com.vitaliy_petuhov.quickjob.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
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

public class FindJobActivity extends AppCompatActivity {

    public static String TAG = FindJobActivity.class.getSimpleName();
    private SessionManager session;
    private SQLiteHandler db;
    private ProgressDialog pDialog;

    private Button findjob,find_job_today, findhistory;
    private EditText findjob_jobname, findjob_city;//,findjob_type;

    private Spinner findjob_typejob;

    public String jSearchTypeJobSp;

    String typejob_str;

    ArrayList<HashMap<String, String>> jobsList;
    private ArrayList<String> array_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_job);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_find);
        toolbar.setLogo(R.drawable.findjobmenu);
        setSupportActionBar(toolbar);

        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        setTitle("");

        findjob = (Button) findViewById(R.id.find_job_butt);
        findjob_jobname = (EditText) findViewById(R.id.find_job_jobname);
        findjob_city = (EditText) findViewById(R.id.find_job_city);
        //findjob_type = (EditText) findViewById(R.id.find_job_type);
        findjob_typejob = (Spinner) findViewById(R.id.find_job_type);

        find_job_today = (Button) findViewById(R.id.job_search_today);

        find_job_today.setVisibility(View.INVISIBLE);


        find_job_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String jSearchType = "Поиск сегодня";

                Intent intent = new Intent(FindJobActivity.this, ResultSearchActivity.class);
                intent.putExtra("jSearchType_finjobactivity_resultsearchactivity", jSearchType);
                startActivity(intent);
            }
        });

        findjob_typejob.setPrompt("Поиск по типу работы");


        findjob_typejob.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();

                if (position == 0) {
                    jSearchTypeJobSp = "1";
                } else if (position == 1) {
                    jSearchTypeJobSp = "2";
                } else if (position == 2) {
                    jSearchTypeJobSp = "3";
                } else if (position == 3) {
                    jSearchTypeJobSp = "4";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findhistory = (Button) findViewById(R.id.history_search);

        findhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FindJobActivity.this, HistoryResult.class);
                startActivity(intent);
            }
        });

        session = new SessionManager(getApplicationContext());

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        getJobTypeSearch();

        getCountJobToday();

        getCountHistory();


        jobsList = new ArrayList<HashMap<String, String>>();

        findjob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String jName = findjob_jobname.getText().toString();
                String jCity = findjob_city.getText().toString();
                String jType = findjob_typejob.getSelectedItem().toString();

                String jSearchType = "Простой поиск";

                if(jType.equals("Постоянная работа")) typejob_str = "1";
                else if(jType.equals("Временная работа")) typejob_str = "2";
                else if (jType.equals("Разовая работа")) typejob_str = "3";
                else if(jType.equals("Подработка")) typejob_str = "4";

                HashMap<String,String> detail = db.getUserDetails();
                String uName = detail.get("name");

                addHistory(uName, jName, jType, jCity);

                Intent intent = new Intent(FindJobActivity.this, ResultSearchActivity.class);
                intent.putExtra("jName_finjobactivity_resultsearchactivity",jName);
                intent.putExtra("jCity_finjobactivity_resultsearchactivity", jCity);
                intent.putExtra("jType_finjobactivity_resultsearchactivity", typejob_str);
                intent.putExtra("jSearchType_finjobactivity_resultsearchactivity", jSearchType);
                startActivity(intent);

            }
        });

    }

    //добавление в историю
    private void addHistory(final String Uname, final String Jname, final String Jtype,final String Jcity){

        String tag_string_req = "req_add_history";


        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.ADD_HISTORY_URL, new Response.Listener<String>() {

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
                //showMessage(error.getMessage());
                showMessage("Отсутствует интернет соединение");
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to job url
                Map<String, String> params = new HashMap<String, String>();

                params.put("tag", "addhistory");
                params.put("job_username_h",Uname);
                params.put("job_name_h",Jname);
                params.put("job_city_h",Jcity);
                params.put("job_type_h",Jtype);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    //подсчет истории
    private void getCountHistory(){
        String tag_string_req = "req_get_count_history";

        HashMap<String,String> detail = db.getUserDetails();

        String uName = detail.get("name");

        if(uName.contains(" ")) {
            uName = uName.replace(" ", "+");
        }

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.GET_COUNT_HISTORY_URL +"&job_user_name="+ uName, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Count job today Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    String countjob = jObj.getString("count_actv");
                    if(countjob.equals("0"))
                    {
                        findhistory.setVisibility(View.GONE);
                    }
                    else
                    {
                        findhistory.setVisibility(View.VISIBLE);
                        //find_job_today.setText("Новые объявления (" + countjob + ")");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Count job today Error: " + error.getMessage());
                //showMessage(error.getMessage());
                showMessage("Отсутствует интернет соединение");
                hideDialog();
            }
        }) {

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    //подсчет для работ сегодня
    private void getCountJobToday(){
        String tag_string_req = "req_get_count_job_today";

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.GET_COUNT_JOB_TODAY_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Count job today Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    String countjob = jObj.getString("count_actv");
                    if(countjob.equals("0"))
                    {
                        find_job_today.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        find_job_today.setVisibility(View.VISIBLE);
                        find_job_today.setText("Новые объявления ("+countjob+")");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Count job today Error: " + error.getMessage());
                //showMessage(error.getMessage());
                showMessage("Отсутствует интернет соединение");
                hideDialog();
            }
        }) {

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    //Получение типа работы
    private void getJobTypeSearch(){
        String tag_string_req = "req_get_job_type_search";

        pDialog.setMessage("Идет получение данных, пожалуйста подождите !");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.GET_JOB_TYPE_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Get Job Type Response: " + response.toString());
                hideDialog();

                try {
                    JSONArray jsonarray = new JSONArray(response);
                    JSONObject jMsg = jsonarray.getJSONObject(0);
                    boolean msg = jMsg.getBoolean("msg");
                    if(msg){
                        array_spinner = new ArrayList<String>();
                        for (int i=1; i < jsonarray.length(); i++){
                            JSONObject jObj = jsonarray.getJSONObject(i);
                            array_spinner.add(jObj.getString("type_actv"));
                        }
                        addInfoOnSpinner_Type();
                    }
                }
                catch (JSONException e){

                    e.printStackTrace();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get Job Type Error: " + error.getMessage());
                //showMessage(error.getMessage());
                showMessage("Отсутствует интернет соединение");
                hideDialog();
            }
        }) {

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void addInfoOnSpinner_Type() {

        //String jType = getIntent().getExtras().getString("job_type_intent_to_edit");

        Spinner s = (Spinner) findViewById(R.id.find_job_type);
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_dropdown_item_1line, array_spinner);
        s.setAdapter(adapter);

        /*for(int i=0; i<array_spinner.size();i++) {
            if(array_spinner.get(i).indexOf(jType)==0)
                s.setSelection(i);
        }*/
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

    public void goToResult(){
        Intent intent = new Intent(FindJobActivity.this, ResultSearchActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.search_back_menu:

                // logoutUser();
                Intent intent = new Intent(FindJobActivity.this, MenuActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

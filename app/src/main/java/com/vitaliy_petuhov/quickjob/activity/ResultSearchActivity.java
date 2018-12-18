package com.vitaliy_petuhov.quickjob.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class ResultSearchActivity extends AppCompatActivity {

    public static String TAG = ResultSearchActivity.class.getSimpleName();
    private SessionManager session;
    private SQLiteHandler db;
    private ProgressDialog pDialog;
    private ImageButton close_menu;
    private TextView searchTV;

    AlertDialog.Builder ad;
    private final int IDD_LIST_MAIN = 1;

    ArrayList<HashMap<String, String>> jobsList;

    String job_ID_int_test,job_name_int_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_search);

        session = new SessionManager(getApplicationContext());

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        searchTV = (TextView) findViewById(R.id.search_tv);

        db = new SQLiteHandler(getApplicationContext());

        jobsList = new ArrayList<HashMap<String, String>>();

        HashMap<String,String> detail = db.getUserDetails();

        GetSearch();

        ListView listV = (ListView)findViewById(R.id.searchjob);

        listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 job_ID_int_test = ((TextView) view.findViewById(R.id.jobIDitem_search)).getText().toString();
                 job_name_int_test = ((TextView) view.findViewById(R.id.jobnameitem_search)).getText().toString();

                showDialog(IDD_LIST_MAIN);

            }
        });

        close_menu = (ImageButton) findViewById(R.id.search_closemenu_butt);

        close_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ResultSearchActivity.this, FindJobActivity.class);
                startActivity(intent);
            }
        });


    }

    //выборка типа поиска истории
    public void GetSearch() {

        String str_jSearchType = getIntent().getExtras().getString("jSearchType_finjobactivity_resultsearchactivity");

        if(str_jSearchType.equals("Простой поиск"))
        {
            String str_jName = getIntent().getExtras().getString("jName_finjobactivity_resultsearchactivity");
            String str_jCity = getIntent().getExtras().getString("jCity_finjobactivity_resultsearchactivity");
            String str_jType = getIntent().getExtras().getString("jType_finjobactivity_resultsearchactivity");

            if((str_jCity.isEmpty()) && (str_jName.isEmpty()) && (str_jType.isEmpty()))
            {
                getSearchJobForNameNull();
            }
            //поиск только по городу
            else if ((!str_jCity.isEmpty()) && (str_jName.isEmpty()) &&(str_jType.isEmpty()))
            {
                getSearchJobForCity();
            }
            //поиск только по имени
            else if ((!str_jName.isEmpty()) && (str_jCity.isEmpty()) &&(str_jType.isEmpty()))
            {
                getSearchJobForName();
            }
            //поиск только по типу
            else if ((!str_jType.isEmpty()) && (str_jName.isEmpty()) && (str_jCity.isEmpty()))
            {
                getSearchJobForType();
            }
            //поиск только по имени и типу
            else if((!str_jName.isEmpty()) && (!str_jType.isEmpty()) && (str_jCity.isEmpty()))
            {
                getSearchJobForNameAndType();
            }
            //поиск по имени и городу
            else if((!str_jName.isEmpty())&&(str_jType.isEmpty())&&(!str_jCity.isEmpty()))
            {
                getSearchJobForNameAndCity();
            }
            //поиск по типу и городу
            else if((str_jName.isEmpty()) && (!str_jType.isEmpty()) && (!str_jCity.isEmpty()))
            {
                getSearchJobForCityAndType();
            }
            //поиск по всем 3 параметрам
            else if ((!str_jName.isEmpty()) && (!str_jType.isEmpty()) && (!str_jCity.isEmpty()))
            {
                getSearchJobForNameAndCityAndType();
            }
        }
        else if(str_jSearchType.equals("Поиск сегодня"))
        {
            getSearchJobToday();
        }

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id)
        {

            case IDD_LIST_MAIN:

                final String[] mJobWorks =
                        {
                                "Добавить в избранное",
                                "Удалить из избранного",
                                "Подробная информация"
                        };

                ad = new AlertDialog.Builder(this);
                ad.setTitle("Выберите желаемое действие:");
                ad.setIcon(R.drawable.icon);

                ad.setItems(mJobWorks, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if (mJobWorks[item] == "Добавить в избранное") {
                            CheckForAddFav();
                        } else if (mJobWorks[item] == "Удалить из избранного") {
                            CheckForDeleteFav();
                        } else if (mJobWorks[item] == "Подробная информация") {

                            String str_jName = getIntent().getExtras().getString("jName_finjobactivity_resultsearchactivity");
                            String str_jType = getIntent().getExtras().getString("jType_finjobactivity_resultsearchactivity");
                            String str_jCity = getIntent().getExtras().getString("jCity_finjobactivity_resultsearchactivity");

                            String str_jSearchType = getIntent().getExtras().getString("jSearchType_finjobactivity_resultsearchactivity");

                            Intent intent = new Intent(ResultSearchActivity.this,AllResult.class);

                            intent.putExtra("job_ID_intent_result",job_ID_int_test);
                            intent.putExtra("job_name_intent_result",job_name_int_test);

                            intent.putExtra("jName_finjobactivity_resultsearchactivity", str_jName);
                            intent.putExtra("jType_finjobactivity_resultsearchactivity",str_jType);
                            intent.putExtra("jCity_finjobactivity_resultsearchactivity",str_jCity);

                            intent.putExtra("jSearchType_finjobactivity_resultsearchactivity",str_jSearchType);
                            startActivity(intent);

                        }
                    }
                });
                ad.setCancelable(true);
                return ad.create();

            default:
                return null;
        }
    }

    // проверка добавления в избранное
    private void CheckForAddFav(){
        String tag_string_req = "req_get_count_favour_job_ad";

        HashMap<String,String> detail = db.getUserDetails();

        String uName = detail.get("name");

        String jobName = job_name_int_test;
        if(jobName.contains(" ")) {
            jobName = jobName.replace(" ", "+");
        }

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.GET_COUNT_FAVOUR_JOB_AD_URL +"&job_user_name="+ uName +"&job_name="+ jobName, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Get Job PaydayValue Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    String countjob = jObj.getString("count_actv");
                    if(countjob.equals("0"))
                    {
                        HashMap<String,String> detail = db.getUserDetails();
                        String user_name = detail.get("name");
                        addJobToFav(job_name_int_test, user_name);
                        showMessage("Объявление добавлено в избранное!");
                    }
                    else
                    {
                        showMessage("Объявление уже было добавлено в избранное !");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get Job Type Error: " + error.getMessage());
               // showMessage(error.getMessage());
                showMessage("Отсутствует интернет соединение");
                hideDialog();
            }
        }) {

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    // проверка удаления из избранного
    private void CheckForDeleteFav(){
        String tag_string_req = "req_get_count_favour_job_ad";

        HashMap<String,String> detail = db.getUserDetails();

        String uName = detail.get("name");

        String jobName = job_name_int_test;
        if(jobName.contains(" ")) {
            jobName = jobName.replace(" ", "+");
        }

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.GET_COUNT_FAVOUR_JOB_AD_URL +"&job_user_name="+ uName +"&job_name="+ jobName, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Get Job PaydayValue Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    String countjob = jObj.getString("count_actv");
                    if(countjob.equals("0"))
                    {
                        showMessage("Объявление ещё не добавлено в избранное !");
                    }
                    else
                    {
                        HashMap<String,String> detail = db.getUserDetails();
                        String user_name = detail.get("name");
                        deleteJobFav(job_name_int_test, user_name);
                        showMessage("Объявление удалено из избранного!");
                    }

                } catch (JSONException e) {
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

    //добавление в избранное
    private void addJobToFav(final String jobname, final String username){

        String tag_string_req = "req_add_job_fav";

        //pDialog.setMessage("Идет добавление в избранное, пожалуйста подождите!");
        //showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.ADD_FAV_JOB_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Add Fav Job Response: " + response.toString());
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
                Log.e(TAG, "Add Fav Job Error: " + error.getMessage());
                //showMessage(error.getMessage());
                showMessage("Отсутствует интернет соединение");
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to job url
                Map<String, String> params = new HashMap<String, String>();

                params.put("tag", "favour");
                params.put("job_name",jobname);
                params.put("user_name",username);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    //удаление из избранное
    private void deleteJobFav(final String jobname, final String username){

        String tag_string_req = "req_delete_job_fav";

       // pDialog.setMessage("Идет удаление из избранного, пожалуйста подождите!");
       // showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.DELETE_FAV_JOB_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Delete Fav Job Response: " + response.toString());
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
                Log.e(TAG, "Delete Fav Job Error: " + error.getMessage());
                showMessage(error.getMessage());
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to job url
                Map<String, String> params = new HashMap<String, String>();

                params.put("tag", "del_favour");
                params.put("job_name",jobname);
                params.put("user_name",username);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    // поиск новых объявлений
    private void getSearchJobToday(){
        String tag_string_req = "req_search_job_today";

        pDialog.setMessage("Идет получение данных, пожалуйста подождите !");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.GET_SEARCH_JOB_TODAY_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Search Job For Null Response: " + response.toString());
                hideDialog();

                try {

                    JSONArray jsonarray = new JSONArray(response);
                    JSONObject jMsg = jsonarray.getJSONObject(0);
                    boolean msg = jMsg.getBoolean("msg");
                    if(msg){

                        jobsList.clear();

                        for (int i=1; i < jsonarray.length(); i++){
                            JSONObject jObj = jsonarray.getJSONObject(i);

                            String jobID = jObj.getString("job_ID_actv");
                            String jobtype = jObj.getString("job_type_actv");
                            String jobname = jObj.getString("job_name_actv");
                            String country = jObj.getString("job_country_actv");
                            String region = jObj.getString("job_region_actv");
                            String city = jObj.getString("job_city_actv");
                            String payday = jObj.getString("job_payday_actv");
                            String paydayvalue = jObj.getString("job_paydayvalue_actv");

                            HashMap<String, String> map = new HashMap<String, String>();

                            map.put("job_ID_actv",jobID);
                            map.put("job_type_actv",jobtype);
                            map.put("job_name_actv",jobname);
                            map.put("job_country_actv",country);
                            map.put("job_region_actv",region);
                            map.put("job_city_actv",city);
                            map.put("job_payday_actv",payday);
                            map.put("job_paydayvalue_actv",paydayvalue);

                            jobsList.add(map);

                            //job_name_intent = jobname;
                        }
                        addInfoAllJobsOnView();

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

    // поиск работы по имени
    private void getSearchJobForName(){
        String tag_string_req = "req_search_job_for_name";

        pDialog.setMessage("Идет получение данных, пожалуйста подождите !");
        showDialog();

        // String jobName = findjob_jobname.getText().toString();

        String jobName = getIntent().getExtras().getString("jName_finjobactivity_resultsearchactivity");
        if(jobName.contains(" ")) {
            jobName = jobName.replace(" ", "+");
        }

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.GET_SEARCH_JOB_FOR_NAME_URL +"&job_name="+ jobName, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Search Job For Name Response: " + response.toString());
                hideDialog();

                try {

                    JSONArray jsonarray = new JSONArray(response);
                    JSONObject jMsg = jsonarray.getJSONObject(0);
                    boolean msg = jMsg.getBoolean("msg");
                    if(msg){

                        jobsList.clear();

                        for (int i=1; i < jsonarray.length(); i++){
                            JSONObject jObj = jsonarray.getJSONObject(i);

                            String jobID = jObj.getString("job_ID_actv");
                            String jobtype = jObj.getString("job_type_actv");
                            String jobname = jObj.getString("job_name_actv");
                            String country = jObj.getString("job_country_actv");
                            String region = jObj.getString("job_region_actv");
                            String city = jObj.getString("job_city_actv");
                            String payday = jObj.getString("job_payday_actv");
                            String paydayvalue = jObj.getString("job_paydayvalue_actv");

                            HashMap<String, String> map = new HashMap<String, String>();

                            map.put("job_ID_actv",jobID);
                            map.put("job_type_actv",jobtype);
                            map.put("job_name_actv",jobname);
                            map.put("job_country_actv",country);
                            map.put("job_region_actv",region);
                            map.put("job_city_actv",city);
                            map.put("job_payday_actv",payday);
                            map.put("job_paydayvalue_actv",paydayvalue);


                            jobsList.add(map);

                            //job_name_intent = jobname;
                        }
                        if(jobsList.isEmpty())
                            addInfoAllJobsOnView();

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
                showMessage(error.getMessage());
                hideDialog();
            }
        }) {

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    // поиск работы по городу
    private void getSearchJobForCity(){
        String tag_string_req = "req_search_job_for_city";

        pDialog.setMessage("Идет получение данных, пожалуйста подождите !");
        showDialog();

        // String jobName = findjob_jobname.getText().toString();

        String jobCity = getIntent().getExtras().getString("jCity_finjobactivity_resultsearchactivity");

        if(jobCity.contains(" ")) {
            jobCity = jobCity.replace(" ", "+");
        }

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.GET_SEARCH_JOB_FOR_CITY_URL +"&job_pos_city="+ jobCity, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Search Job For City Response: " + response.toString());
                hideDialog();

                try {

                    JSONArray jsonarray = new JSONArray(response);
                    JSONObject jMsg = jsonarray.getJSONObject(0);
                    boolean msg = jMsg.getBoolean("msg");
                    if(msg){

                        jobsList.clear();

                        for (int i=1; i < jsonarray.length(); i++){
                            JSONObject jObj = jsonarray.getJSONObject(i);

                            String jobID = jObj.getString("job_ID_actv");
                            String jobtype = jObj.getString("job_type_actv");
                            String jobname = jObj.getString("job_name_actv");
                            String country = jObj.getString("job_country_actv");
                            String region = jObj.getString("job_region_actv");
                            String city = jObj.getString("job_city_actv");
                            String payday = jObj.getString("job_payday_actv");
                            String paydayvalue = jObj.getString("job_paydayvalue_actv");

                            HashMap<String, String> map = new HashMap<String, String>();

                            map.put("job_ID_actv",jobID);
                            map.put("job_type_actv",jobtype);
                            map.put("job_name_actv",jobname);
                            map.put("job_country_actv",country);
                            map.put("job_region_actv",region);
                            map.put("job_city_actv",city);
                            map.put("job_payday_actv",payday);
                            map.put("job_paydayvalue_actv",paydayvalue);


                            jobsList.add(map);

                            //job_name_intent = jobname;
                        }
                        addInfoAllJobsOnView();

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
                showMessage(error.getMessage());
                hideDialog();
            }
        }) {

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    // поиск работы по типу
    private void getSearchJobForType(){
        String tag_string_req = "req_search_job_for_type";

        pDialog.setMessage("Идет получение данных, пожалуйста подождите !");
        showDialog();

        // String jobName = findjob_jobname.getText().toString();

        String jobType = getIntent().getExtras().getString("jType_finjobactivity_resultsearchactivity");

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.SEARCH_BY_TYPE +"&job_ty="+ jobType,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Search Job For City Response: " + response.toString());
                        hideDialog();

                        try {

                            JSONArray jsonarray = new JSONArray(response);
                            JSONObject jMsg = jsonarray.getJSONObject(0);
                            boolean msg = jMsg.getBoolean("msg");
                            if(msg){

                                jobsList.clear();

                                for (int i=1; i < jsonarray.length(); i++){
                                    JSONObject jObj = jsonarray.getJSONObject(i);

                                    String jobID = jObj.getString("job_ID_actv");
                                    String jobtype = jObj.getString("job_type_actv");
                                    String jobname = jObj.getString("job_name_actv");
                                    String country = jObj.getString("job_country_actv");
                                    String region = jObj.getString("job_region_actv");
                                    String city = jObj.getString("job_city_actv");
                                    String payday = jObj.getString("job_payday_actv");
                                    String paydayvalue = jObj.getString("job_paydayvalue_actv");

                                    HashMap<String, String> map = new HashMap<String, String>();

                                    map.put("job_ID_actv",jobID);
                                    map.put("job_type_actv",jobtype);
                                    map.put("job_name_actv",jobname);
                                    map.put("job_country_actv",country);
                                    map.put("job_region_actv",region);
                                    map.put("job_city_actv",city);
                                    map.put("job_payday_actv",payday);
                                    map.put("job_paydayvalue_actv",paydayvalue);

                                    jobsList.add(map);
                                }
                                addInfoAllJobsOnView();

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

    // поиск работы по имени и типу
    /*private void getSearchJobForNameAndType(){
        String tag_string_req = "req_search_job_for_name_and_type";

        // String jobName = findjob_jobname.getText().toString();

        String jobName = getIntent().getExtras().getString("jName_finjobactivity_resultsearchactivity");
        String jobType = getIntent().getExtras().getString("jType_finjobactivity_resultsearchactivity");

        if(jobName.contains(" ")) {
            jobName = jobName.replace(" ", "+");
        }

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.GET_SEARCH_JOB_FOR_NAME_AND_TYPE_URL + "&job_name=" + jobName + "&job_ty=" + jobType,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Search Job For Name and Type Response: " + response.toString());
                        hideDialog();

                        try {

                            JSONArray jsonarray = new JSONArray(response);
                            JSONObject jMsg = jsonarray.getJSONObject(0);
                            boolean msg = jMsg.getBoolean("msg");
                            if(msg){

                                jobsList.clear();

                                for (int i=1; i < jsonarray.length(); i++){
                                    JSONObject jObj = jsonarray.getJSONObject(i);

                                    String jobID = jObj.getString("job_ID_actv");
                                    String jobtype = jObj.getString("job_type_actv");
                                    String jobname = jObj.getString("job_name_actv");
                                    String country = jObj.getString("job_country_actv");
                                    String region = jObj.getString("job_region_actv");
                                    String city = jObj.getString("job_city_actv");
                                    String payday = jObj.getString("job_payday_actv");
                                    String paydayvalue = jObj.getString("job_paydayvalue_actv");

                                    HashMap<String, String> map = new HashMap<String, String>();

                                    map.put("job_ID_actv",jobID);
                                    map.put("job_type_actv",jobtype);
                                    map.put("job_name_actv",jobname);
                                    map.put("job_country_actv",country);
                                    map.put("job_region_actv",region);
                                    map.put("job_city_actv",city);
                                    map.put("job_payday_actv",payday);
                                    map.put("job_paydayvalue_actv",paydayvalue);


                                    jobsList.add(map);

                                    //job_name_intent = jobname;
                                }
                                addInfoAllJobsOnView();

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
                showMessage(error.getMessage());
                hideDialog();
            }
        }) {

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }*/

    private void getSearchJobForNameAndType(){
        final String tag_string_req = "req_search_job_for_name_and_type";

        pDialog.setMessage("Идет получение данных, пожалуйста подождите !");
        showDialog();

        // String jobName = findjob_jobname.getText().toString();

         String jobName = getIntent().getExtras().getString("jName_finjobactivity_resultsearchactivity");
         final String jobType = getIntent().getExtras().getString("jType_finjobactivity_resultsearchactivity");

        if(jobName.contains(" ")) {
            jobName = jobName.replace(" ", "+");
        }

        final String finalJobName = jobName;
        final StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.LOGIN_URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Search Job For Name and Type Response: " + response.toString());
                        hideDialog();

                        try {

                            JSONArray jsonarray = new JSONArray(response);
                            JSONObject jMsg = jsonarray.getJSONObject(0);
                            boolean msg = jMsg.getBoolean("msg");
                            if(msg){

                                jobsList.clear();

                                for (int i=1; i < jsonarray.length(); i++){
                                    JSONObject jObj = jsonarray.getJSONObject(i);

                                    String jobID = jObj.getString("job_ID_actv");
                                    String jobtype = jObj.getString("job_type_actv");
                                    String jobname = jObj.getString("job_name_actv");
                                    String country = jObj.getString("job_country_actv");
                                    String region = jObj.getString("job_region_actv");
                                    String city = jObj.getString("job_city_actv");
                                    String payday = jObj.getString("job_payday_actv");
                                    String paydayvalue = jObj.getString("job_paydayvalue_actv");

                                    HashMap<String, String> map = new HashMap<String, String>();

                                    map.put("job_ID_actv",jobID);
                                    map.put("job_type_actv",jobtype);
                                    map.put("job_name_actv",jobname);
                                    map.put("job_country_actv",country);
                                    map.put("job_region_actv",region);
                                    map.put("job_city_actv",city);
                                    map.put("job_payday_actv",payday);
                                    map.put("job_paydayvalue_actv",paydayvalue);


                                    jobsList.add(map);

                                    //job_name_intent = jobname;
                                }
                                addInfoAllJobsOnView();

                            }


                        }
                        catch (JSONException e){

                            e.printStackTrace();

                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Search Error: " + error.getMessage());
               // showMessage(error.getMessage());
                showMessage("Отсутствует интернет соединение");
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to job url
                Map<String, String> params = new HashMap<String, String>();

                params.put("tag", "search_job_name_type");
                params.put("job_name", finalJobName);
                params.put("job_ty",jobType);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    // поиск работы по имени и городу
    /*private void getSearchJobForNameAndCity(){
        String tag_string_req = "req_search_job_for_name_and_city";

        // String jobName = findjob_jobname.getText().toString();

        String jobName = getIntent().getExtras().getString("jName_finjobactivity_resultsearchactivity");
        String jobCity = getIntent().getExtras().getString("jCity_finjobactivity_resultsearchactivity");


        if(jobName.contains(" ")) {
            jobName = jobName.replace(" ", "+");
        }
        if(jobCity.contains(" ")){
            jobCity = jobCity.replace(" ", "+");
        }

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.GET_SEARCH_JOB_FOR_NAME_AND_CITY_URL + "&job_name=" + jobName + "&job_pos_city=" + jobCity,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Search Job For Name and City Response: " + response.toString());
                        hideDialog();

                        try {

                            JSONArray jsonarray = new JSONArray(response);
                            JSONObject jMsg = jsonarray.getJSONObject(0);
                            boolean msg = jMsg.getBoolean("msg");
                            if(msg){

                                jobsList.clear();

                                for (int i=1; i < jsonarray.length(); i++){
                                    JSONObject jObj = jsonarray.getJSONObject(i);

                                    String jobID = jObj.getString("job_ID_actv");
                                    String jobtype = jObj.getString("job_type_actv");
                                    String jobname = jObj.getString("job_name_actv");
                                    String country = jObj.getString("job_country_actv");
                                    String region = jObj.getString("job_region_actv");
                                    String city = jObj.getString("job_city_actv");
                                    String payday = jObj.getString("job_payday_actv");
                                    String paydayvalue = jObj.getString("job_paydayvalue_actv");

                                    HashMap<String, String> map = new HashMap<String, String>();

                                    map.put("job_ID_actv",jobID);
                                    map.put("job_type_actv",jobtype);
                                    map.put("job_name_actv",jobname);
                                    map.put("job_country_actv",country);
                                    map.put("job_region_actv",region);
                                    map.put("job_city_actv",city);
                                    map.put("job_payday_actv",payday);
                                    map.put("job_paydayvalue_actv",paydayvalue);


                                    jobsList.add(map);

                                    //job_name_intent = jobname;
                                }
                                addInfoAllJobsOnView();

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
                showMessage(error.getMessage());
                hideDialog();
            }
        }) {

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }*/
    private void getSearchJobForNameAndCity(){
        String tag_string_req = "req_search_job_for_name_and_city";

        pDialog.setMessage("Идет получение данных, пожалуйста подождите !");
        showDialog();

        // String jobName = findjob_jobname.getText().toString();

        String jobName = getIntent().getExtras().getString("jName_finjobactivity_resultsearchactivity");
        String jobCity = getIntent().getExtras().getString("jCity_finjobactivity_resultsearchactivity");


        if(jobName.contains(" ")) {
            jobName = jobName.replace(" ", "+");
        }
        if(jobCity.contains(" ")){
            jobCity = jobCity.replace(" ", "+");
        }

        final String finalJobName = jobName;
        final String finalJobCity = jobCity;
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.LOGIN_URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Search Job For Name and City Response: " + response.toString());
                        hideDialog();

                        try {

                            JSONArray jsonarray = new JSONArray(response);
                            JSONObject jMsg = jsonarray.getJSONObject(0);
                            boolean msg = jMsg.getBoolean("msg");
                            if(msg){

                                jobsList.clear();

                                for (int i=1; i < jsonarray.length(); i++){
                                    JSONObject jObj = jsonarray.getJSONObject(i);

                                    String jobID = jObj.getString("job_ID_actv");
                                    String jobtype = jObj.getString("job_type_actv");
                                    String jobname = jObj.getString("job_name_actv");
                                    String country = jObj.getString("job_country_actv");
                                    String region = jObj.getString("job_region_actv");
                                    String city = jObj.getString("job_city_actv");
                                    String payday = jObj.getString("job_payday_actv");
                                    String paydayvalue = jObj.getString("job_paydayvalue_actv");

                                    HashMap<String, String> map = new HashMap<String, String>();

                                    map.put("job_ID_actv",jobID);
                                    map.put("job_type_actv",jobtype);
                                    map.put("job_name_actv",jobname);
                                    map.put("job_country_actv",country);
                                    map.put("job_region_actv",region);
                                    map.put("job_city_actv",city);
                                    map.put("job_payday_actv",payday);
                                    map.put("job_paydayvalue_actv",paydayvalue);


                                    jobsList.add(map);

                                    //job_name_intent = jobname;
                                }
                                addInfoAllJobsOnView();

                            }


                        }
                        catch (JSONException e){

                            e.printStackTrace();

                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Search Error: " + error.getMessage());
                //showMessage(error.getMessage());
                showMessage("Отсутствует интернет соединение");
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to job url
                Map<String, String> params = new HashMap<String, String>();

                params.put("tag", "search_job_name_city");
                params.put("job_name", finalJobName);
                params.put("job_pos_city", finalJobCity);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    // поиск работы по городу и типу
    /*private void getSearchJobForCityAndType(){
        String tag_string_req = "req_search_job_for_city_and_type";

        // String jobName = findjob_jobname.getText().toString();

        String jobCity = getIntent().getExtras().getString("jCity_finjobactivity_resultsearchactivity");
        String jobType = getIntent().getExtras().getString("jType_finjobactivity_resultsearchactivity");

        if(jobCity.contains(" ")) {
            jobCity = jobCity.replace(" ", "+");
        }

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.GET_SEARCH_JOB_FOR_CITY_AND_TYPE_URL + "&job_pos_city=" + jobCity +"&job_ty=" + jobType,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Search Job For City and Type Response: " + response.toString());
                        hideDialog();

                        try {

                            JSONArray jsonarray = new JSONArray(response);
                            JSONObject jMsg = jsonarray.getJSONObject(0);
                            boolean msg = jMsg.getBoolean("msg");
                            if(msg){

                                jobsList.clear();

                                for (int i=1; i < jsonarray.length(); i++){
                                    JSONObject jObj = jsonarray.getJSONObject(i);

                                    String jobID = jObj.getString("job_ID_actv");
                                    String jobtype = jObj.getString("job_type_actv");
                                    String jobname = jObj.getString("job_name_actv");
                                    String country = jObj.getString("job_country_actv");
                                    String region = jObj.getString("job_region_actv");
                                    String city = jObj.getString("job_city_actv");
                                    String payday = jObj.getString("job_payday_actv");
                                    String paydayvalue = jObj.getString("job_paydayvalue_actv");

                                    HashMap<String, String> map = new HashMap<String, String>();

                                    map.put("job_ID_actv",jobID);
                                    map.put("job_type_actv",jobtype);
                                    map.put("job_name_actv",jobname);
                                    map.put("job_country_actv",country);
                                    map.put("job_region_actv",region);
                                    map.put("job_city_actv",city);
                                    map.put("job_payday_actv",payday);
                                    map.put("job_paydayvalue_actv",paydayvalue);


                                    jobsList.add(map);

                                    //job_name_intent = jobname;
                                }
                                addInfoAllJobsOnView();

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
                showMessage(error.getMessage());
                hideDialog();
            }
        }) {

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }*/

    private void getSearchJobForCityAndType(){
        String tag_string_req = "req_search_job_for_city_and_type";

        pDialog.setMessage("Идет получение данных, пожалуйста подождите !");
        showDialog();

        // String jobName = findjob_jobname.getText().toString();

        String jobCity = getIntent().getExtras().getString("jCity_finjobactivity_resultsearchactivity");
        final String jobType = getIntent().getExtras().getString("jType_finjobactivity_resultsearchactivity");

        if(jobCity.contains(" ")) {
            jobCity = jobCity.replace(" ", "+");
        }

        final String finalJobCity = jobCity;
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.LOGIN_URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Search Job For City and Type Response: " + response.toString());
                        hideDialog();

                        try {

                            JSONArray jsonarray = new JSONArray(response);
                            JSONObject jMsg = jsonarray.getJSONObject(0);
                            boolean msg = jMsg.getBoolean("msg");
                            if(msg){

                                jobsList.clear();

                                for (int i=1; i < jsonarray.length(); i++){
                                    JSONObject jObj = jsonarray.getJSONObject(i);

                                    String jobID = jObj.getString("job_ID_actv");
                                    String jobtype = jObj.getString("job_type_actv");
                                    String jobname = jObj.getString("job_name_actv");
                                    String country = jObj.getString("job_country_actv");
                                    String region = jObj.getString("job_region_actv");
                                    String city = jObj.getString("job_city_actv");
                                    String payday = jObj.getString("job_payday_actv");
                                    String paydayvalue = jObj.getString("job_paydayvalue_actv");

                                    HashMap<String, String> map = new HashMap<String, String>();

                                    map.put("job_ID_actv",jobID);
                                    map.put("job_type_actv",jobtype);
                                    map.put("job_name_actv",jobname);
                                    map.put("job_country_actv",country);
                                    map.put("job_region_actv",region);
                                    map.put("job_city_actv",city);
                                    map.put("job_payday_actv",payday);
                                    map.put("job_paydayvalue_actv",paydayvalue);


                                    jobsList.add(map);

                                    //job_name_intent = jobname;
                                }
                                addInfoAllJobsOnView();

                            }


                        }
                        catch (JSONException e){

                            e.printStackTrace();

                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Search Error: " + error.getMessage());
                //showMessage(error.getMessage());
                showMessage("Отсутствует интернет соединение");
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to job url
                Map<String, String> params = new HashMap<String, String>();

                params.put("tag", "search_job_city_type");

                params.put("job_pos_city", finalJobCity);
                params.put("job_ty", jobType);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    // поиск работы по 3 полям
    /*private void getSearchJobForNameAndCityAndType(){
        String tag_string_req = "req_search_job_for_name_and_city_and_type";

        // String jobName = findjob_jobname.getText().toString();

        String jobName = getIntent().getExtras().getString("jName_finjobactivity_resultsearchactivity");
        String jobCity = getIntent().getExtras().getString("jCity_finjobactivity_resultsearchactivity");
        String jobType = getIntent().getExtras().getString("jType_finjobactivity_resultsearchactivity");


        if(jobName.contains(" ")) {
            jobName = jobName.replace(" ", "+");
        }
        if(jobCity.contains(" ")){
            jobCity = jobCity.replace(" ", "+");
        }

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.GET_SEARCH_JOB_FOR_NAME_AND_CITY_AND_TYPE_URL + "&job_name=" + jobName +"&job_ty=" + jobType + "&job_pos_city=" + jobCity,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Search Job For Name Response: " + response.toString());
                        hideDialog();

                        try {

                            JSONArray jsonarray = new JSONArray(response);
                            JSONObject jMsg = jsonarray.getJSONObject(0);
                            boolean msg = jMsg.getBoolean("msg");
                            if(msg){

                                jobsList.clear();

                                for (int i=1; i < jsonarray.length(); i++){
                                    JSONObject jObj = jsonarray.getJSONObject(i);

                                    String jobID = jObj.getString("job_ID_actv");
                                    String jobtype = jObj.getString("job_type_actv");
                                    String jobname = jObj.getString("job_name_actv");
                                    String country = jObj.getString("job_country_actv");
                                    String region = jObj.getString("job_region_actv");
                                    String city = jObj.getString("job_city_actv");
                                    String payday = jObj.getString("job_payday_actv");
                                    String paydayvalue = jObj.getString("job_paydayvalue_actv");

                                    HashMap<String, String> map = new HashMap<String, String>();

                                    map.put("job_ID_actv",jobID);
                                    map.put("job_type_actv",jobtype);
                                    map.put("job_name_actv",jobname);
                                    map.put("job_country_actv",country);
                                    map.put("job_region_actv",region);
                                    map.put("job_city_actv",city);
                                    map.put("job_payday_actv",payday);
                                    map.put("job_paydayvalue_actv",paydayvalue);


                                    jobsList.add(map);

                                    //job_name_intent = jobname;
                                }
                                addInfoAllJobsOnView();

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
                showMessage(error.getMessage());
                hideDialog();
            }
        }) {

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }*/

    private void getSearchJobForNameAndCityAndType(){
        String tag_string_req = "req_search_job_for_name_and_city_and_type";

        pDialog.setMessage("Идет получение данных, пожалуйста подождите !");
        showDialog();

        // String jobName = findjob_jobname.getText().toString();

        String jobName = getIntent().getExtras().getString("jName_finjobactivity_resultsearchactivity");
        String jobCity = getIntent().getExtras().getString("jCity_finjobactivity_resultsearchactivity");
        final String jobType = getIntent().getExtras().getString("jType_finjobactivity_resultsearchactivity");


        if(jobName.contains(" ")) {
            jobName = jobName.replace(" ", "+");
        }
        if(jobCity.contains(" ")){
            jobCity = jobCity.replace(" ", "+");
        }

        final String finalJobName = jobName;
        final String finalJobCity = jobCity;
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.LOGIN_URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Search Job For Name Response: " + response.toString());
                        hideDialog();

                        try {

                            JSONArray jsonarray = new JSONArray(response);
                            JSONObject jMsg = jsonarray.getJSONObject(0);
                            boolean msg = jMsg.getBoolean("msg");
                            if(msg){

                                jobsList.clear();

                                for (int i=1; i < jsonarray.length(); i++){
                                    JSONObject jObj = jsonarray.getJSONObject(i);

                                    String jobID = jObj.getString("job_ID_actv");
                                    String jobtype = jObj.getString("job_type_actv");
                                    String jobname = jObj.getString("job_name_actv");
                                    String country = jObj.getString("job_country_actv");
                                    String region = jObj.getString("job_region_actv");
                                    String city = jObj.getString("job_city_actv");
                                    String payday = jObj.getString("job_payday_actv");
                                    String paydayvalue = jObj.getString("job_paydayvalue_actv");

                                    HashMap<String, String> map = new HashMap<String, String>();

                                    map.put("job_ID_actv",jobID);
                                    map.put("job_type_actv",jobtype);
                                    map.put("job_name_actv",jobname);
                                    map.put("job_country_actv",country);
                                    map.put("job_region_actv",region);
                                    map.put("job_city_actv",city);
                                    map.put("job_payday_actv",payday);
                                    map.put("job_paydayvalue_actv",paydayvalue);


                                    jobsList.add(map);

                                    //job_name_intent = jobname;
                                }
                                addInfoAllJobsOnView();

                            }


                        }
                        catch (JSONException e){

                            e.printStackTrace();

                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Search Error: " + error.getMessage());
                //showMessage(error.getMessage());
                showMessage("Отсутствует интернет соединение");
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to job url
                Map<String, String> params = new HashMap<String, String>();

                params.put("tag", "search_job_name_type_city");
                params.put("job_name", finalJobName);
                params.put("job_pos_city", finalJobCity);
                params.put("job_ty", jobType);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    // поиск всех работ
    private void getSearchJobForNameNull(){
        String tag_string_req = "req_search_job_null";

        pDialog.setMessage("Идет получение данных, пожалуйста подождите !");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.GET_SEARCH_JOB_NULL_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Search Job For Null Response: " + response.toString());
                hideDialog();

                try {

                    JSONArray jsonarray = new JSONArray(response);
                    JSONObject jMsg = jsonarray.getJSONObject(0);
                    boolean msg = jMsg.getBoolean("msg");
                    if(msg){

                        jobsList.clear();

                        for (int i=1; i < jsonarray.length(); i++){
                            JSONObject jObj = jsonarray.getJSONObject(i);

                            String jobID = jObj.getString("job_ID_actv");
                            String jobtype = jObj.getString("job_type_actv");
                            String jobname = jObj.getString("job_name_actv");
                            String country = jObj.getString("job_country_actv");
                            String region = jObj.getString("job_region_actv");
                            String city = jObj.getString("job_city_actv");
                            String payday = jObj.getString("job_payday_actv");
                            String paydayvalue = jObj.getString("job_paydayvalue_actv");

                            HashMap<String, String> map = new HashMap<String, String>();

                            map.put("job_ID_actv",jobID);
                            map.put("job_type_actv",jobtype);
                            map.put("job_name_actv",jobname);
                            map.put("job_country_actv",country);
                            map.put("job_region_actv",region);
                            map.put("job_city_actv",city);
                            map.put("job_payday_actv",payday);
                            map.put("job_paydayvalue_actv",paydayvalue);

                            jobsList.add(map);

                            //job_name_intent = jobname;
                        }
                        addInfoAllJobsOnView();

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

    //вывод списка ваших работ
    private void addInfoAllJobsOnView() {

        pDialog.setMessage("Идет получение данных, пожалуйста подождите !");
        showDialog();

        ListView lw = (ListView) findViewById(R.id.searchjob);

        String [] headers = new String[]
                {
                        "job_ID_actv",
                        "job_type_actv",
                        "job_name_actv",
                        "job_country_actv",
                        "job_region_actv",
                        "job_city_actv",
                        "job_payday_actv",
                        "job_paydayvalue_actv"
                };

        int [] to =  new int[]
                {
                        R.id.jobIDitem_search,
                        R.id.typejob_search,
                        R.id.jobnameitem_search,
                        R.id.countryitem_search,
                        R.id.regionitem_search,
                        R.id.cityitem_search,
                        R.id.paydayitem_search,
                        R.id.paydayvalueitem_search
                };

        ListAdapter adapter = new SimpleAdapter(ResultSearchActivity.this, jobsList, R.layout.item_search, headers, to);
        lw.setAdapter(adapter);

        if (jobsList.size() == 0)
        {

            searchTV.setVisibility(View.VISIBLE);
            String fontPath = "fonts/agcrownstyle.ttf";
            Typeface typeface = Typeface.createFromAsset(getAssets(), fontPath);
            searchTV.setTypeface(typeface);
            //hideDialog();
        }
        else
        {
            searchTV.setVisibility(View.GONE);

        }

        hideDialog();
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

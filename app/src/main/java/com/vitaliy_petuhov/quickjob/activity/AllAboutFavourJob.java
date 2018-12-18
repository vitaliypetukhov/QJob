package com.vitaliy_petuhov.quickjob.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

public class AllAboutFavourJob extends AppCompatActivity {

    private static final String TAG = AllAboutFavourJob.class.getSimpleName();
    private Button backtomenu_fav; //delOnFav,
    private SessionManager session;
    private SQLiteHandler db;
    private ProgressDialog pDialog;
    private TextView job_name_actv_fav, job_country_actv_fav, job_region_actv_fav, job_city_actv_fav, job_payday_actv_fav, job_paydayvalue_actv_fav;
    private TextView job_type_actv_fav, job_grap_actv_fav, job_treb_actv_fav, job_Fname_actv_fav, job_Sname_actv_fav, job_phone_actv_fav, job_email_actv_fav;
    private ImageButton addNewFav;
    boolean flag_check = false;

    ArrayList<HashMap<String, String>> jobsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_about_favour_job);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new SessionManager(getApplicationContext());
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        db = new SQLiteHandler(getApplicationContext());

        //delOnFav = (Button) findViewById(R.id.delOnFav);
        backtomenu_fav = (Button) findViewById(R.id.backToAllJobs_fav);

        jobsList = new ArrayList<HashMap<String, String>>();

        final HashMap<String,String> detail = db.getUserDetails();

        //String job_name_int = detail.get("name");

        job_name_actv_fav = (TextView) findViewById(R.id.jobname_all_job_fav);

        job_type_actv_fav = (TextView) findViewById(R.id.type_job_all_job_fav);

        job_country_actv_fav = (TextView) findViewById(R.id.country_text_all_job_fav);
        job_region_actv_fav = (TextView) findViewById(R.id.region_text_all_job_fav);
        job_city_actv_fav = (TextView) findViewById(R.id.city_text_all_job_fav);

        job_payday_actv_fav = (TextView) findViewById(R.id.payday_all_job_fav);
        job_paydayvalue_actv_fav = (TextView) findViewById(R.id.value_all_job_fav);

        job_treb_actv_fav = (TextView) findViewById(R.id.treb_all_job_fav);
        job_grap_actv_fav = (TextView) findViewById(R.id.graphik_all_job_fav);

        job_Fname_actv_fav = (TextView) findViewById(R.id.name_text_all_job_fav);
        job_Sname_actv_fav = (TextView) findViewById(R.id.otchestvo_all_job_fav);
        job_phone_actv_fav = (TextView) findViewById(R.id.phone_text_all_job_fav);
        job_email_actv_fav = (TextView) findViewById(R.id.email_text_all_job_fav);

        getFindAllInfoAboutJob_Fav();

        CheckFavourAddingNew();

        addNewFav = (ImageButton) findViewById(R.id.addOnFavNew_fav);

        addNewFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag_check == false)
                {
                    addNewFav.setImageResource(R.drawable.add_fav_2);
                    String user_name = detail.get("name");
                    String job_name = getIntent().getExtras().getString("job_name_intent");
                    AddJobToFav(job_name, user_name);

                    //showMessage("Добавлено успешно");

                    flag_check = true;
                }
                else if(flag_check == true)
                {
                    addNewFav.setImageResource(R.drawable.add_fav);
                    String user_name = detail.get("name");
                    String job_name = getIntent().getExtras().getString("job_name_intent");
                    DeleteJobFav(job_name, user_name);

                    //showMessage("Удалено успешно");

                    flag_check = false;
                }
            }
        });

        /*delOnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user_name = detail.get("name");
                String job_name = getIntent().getExtras().getString("job_name_intent");

                //showMessage("YDALENIE iz IZbrannogo");

                DeleteJobFav(job_name, user_name);
                gotoMenu();

            }
        });*/

        backtomenu_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoMenu();
            }
        });
        setTitle("");

    }
    // проверка добавления в избранное
    private void CheckFavourAddingNew(){
        String tag_string_req = "req_check_favour_add";

        HashMap<String,String> detail = db.getUserDetails();

        String uName = detail.get("name");

        String jNameID = getIntent().getExtras().getString("job_ID_intent");

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.CHECK_FAVOUR_URL +"&job_user_name="+ uName +"&job_name_ID="+ jNameID, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Check Favour Response: " + response.toString());
                hideDialog();
                try
                {
                        JSONObject jObj = new JSONObject(response);
                        String countjob = jObj.getString("count_actv");
                        if(countjob.contains("0"))
                        {
                            addNewFav.setImageResource(R.drawable.add_fav);
                            flag_check = false;
                        }
                        else
                        {
                            addNewFav.setImageResource(R.drawable.add_fav_2);
                            flag_check = true;
                        }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Check Favour Error: " + error.getMessage());
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
    private void AddJobToFav(final String jobname, final String username){

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

    //удаление из избранного
    private void DeleteJobFav(final String jobname, final String username){

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
                //showMessage(error.getMessage());
                showMessage("Отсутствует интернет соединение");
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

    // получения списка созданных пользователем работ
    private void getFindAllInfoAboutJob_Fav(){
        String tag_string_req = "req_get_find_all_info_fav_job";

        pDialog.setMessage("Идет получение данных, пожалуйста подождите !");
        showDialog();

        HashMap<String,String> detail = db.getUserDetails();

        String uName = detail.get("name");

        String jNameID = getIntent().getExtras().getString("job_ID_intent");


        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.GET_FIND_ALL_JOB_INFO_FAV_URL +"&job_user_name="+ uName +"&job_name_ID="+ jNameID, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Find All Fav Jobs Response: " + response.toString());
                hideDialog();

                try {

                    JSONArray jsonarray = new JSONArray(response);
                    JSONObject jMsg = jsonarray.getJSONObject(0);
                    boolean msg = jMsg.getBoolean("msg");
                    if(msg){

                        for (int i=1; i < jsonarray.length(); i++){
                            JSONObject jObj = jsonarray.getJSONObject(i);

                            //String jobID = jObj.getString("job_ID_actv");
                            String jobname = jObj.getString("job_name_actv");

                            String jobtype = jObj.getString("job_type_actv");

                            String payday = jObj.getString("job_payday_actv");
                            String paydayvalue = jObj.getString("job_paydayvalue_actv");

                            String country = jObj.getString("job_country_actv");
                            String region = jObj.getString("job_region_actv");
                            String city = jObj.getString("job_city_actv");

                            String trebovaniya = jObj.getString("job_treb_actv");
                            String graphik = jObj.getString("job_graphik_actv");

                            String Fname = jObj.getString("job_Fname_actv");
                            String Sname = jObj.getString("job_Sname_actv");
                            String phone = jObj.getString("job_phone_actv");
                            String email = jObj.getString("job_email_actv");


                            job_name_actv_fav.setText(jobname);
                            job_type_actv_fav.setText(jobtype);
                            job_payday_actv_fav.setText(payday);
                            job_paydayvalue_actv_fav.setText(paydayvalue);
                            job_country_actv_fav.setText(country);
                            job_region_actv_fav.setText(region);
                            job_city_actv_fav.setText(city);
                            job_treb_actv_fav.setText(trebovaniya);
                            job_grap_actv_fav.setText(graphik);
                            job_Fname_actv_fav.setText(Fname);
                            job_Sname_actv_fav.setText(Sname);
                            job_phone_actv_fav.setText(phone);
                            job_email_actv_fav.setText(email);
                        }

                    }


                }
                catch (JSONException e){

                    e.printStackTrace();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Find All Fav Jobs Error: " + error.getMessage());
               // showMessage(error.getMessage());
                showMessage("Отсутствует интернет соединение");
                hideDialog();
            }
        }) {

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showMessage(String msg) {
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
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

    public void gotoMenu(){

        Intent intent = new Intent(AllAboutFavourJob.this, FavourActivity.class);
        startActivity(intent);
    }

}

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

public class AllAboutJob extends AppCompatActivity {

    private static final String TAG = AllAboutJob.class.getSimpleName();
    private Button deletejob,editjob,backtomenu; //addOnFav
    private SessionManager session;
    private SQLiteHandler db;
    private ProgressDialog pDialog;
    private TextView job_name_actv, job_country_actv, job_region_actv, job_city_actv, job_payday_actv, job_paydayvalue_actv;
    private TextView job_type_actv, job_grap_actv, job_treb_actv, job_Fname_actv, job_Sname_actv, job_phone_actv, job_email_actv;

    ArrayList<HashMap<String, String>> jobsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_about_job);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new SessionManager(getApplicationContext());
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        db = new SQLiteHandler(getApplicationContext());

        //addOnFav = (Button) findViewById(R.id.addOnFav);
        backtomenu = (Button) findViewById(R.id.backToAllJobs);

        deletejob = (Button) findViewById(R.id.deleteButt);
        editjob = (Button) findViewById(R.id.addOnFav);


        jobsList = new ArrayList<HashMap<String, String>>();

         HashMap<String,String> detail = db.getUserDetails();

        //String job_name_int = detail.get("name");

        job_name_actv = (TextView) findViewById(R.id.jobname_all_job);

        job_type_actv = (TextView) findViewById(R.id.type_job_all_job);

        job_country_actv = (TextView) findViewById(R.id.country_text_all_job);
        job_region_actv = (TextView) findViewById(R.id.region_text_all_job);
        job_city_actv = (TextView) findViewById(R.id.city_text_all_job);

        job_payday_actv = (TextView) findViewById(R.id.payday_all_job);
        job_paydayvalue_actv = (TextView) findViewById(R.id.value_all_job);

        job_treb_actv = (TextView) findViewById(R.id.treb_all_job);
        job_grap_actv = (TextView) findViewById(R.id.graphik_all_job);

        job_Fname_actv = (TextView) findViewById(R.id.name_text_all_job);
        job_Sname_actv = (TextView) findViewById(R.id.otchestvo_all_job);
        job_phone_actv = (TextView) findViewById(R.id.phone_text_all_job);
        job_email_actv = (TextView) findViewById(R.id.email_text_all_job);


        getFindAllInfoAboutJob();


        editjob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String jNameID = getIntent().getExtras().getString("job_ID_intent");
                String job_name_int_test =  ((TextView) findViewById(R.id.jobname_all_job)).getText().toString();

                String job_country_int_test =  ((TextView) findViewById(R.id.country_text_all_job)).getText().toString();
                String job_region_int_test =  ((TextView) findViewById(R.id.region_text_all_job)).getText().toString();
                String job_city_int_test =  ((TextView) findViewById(R.id.city_text_all_job)).getText().toString();

                String job_payday_int_test =  ((TextView) findViewById(R.id.payday_all_job)).getText().toString();

                String job_treb_int_test =  ((TextView) findViewById(R.id.treb_all_job)).getText().toString();
                String job_graphik_int_test =  ((TextView) findViewById(R.id.graphik_all_job)).getText().toString();

                String job_Fname_int_test =  ((TextView) findViewById(R.id.name_text_all_job)).getText().toString();
                String job_Sname_int_test =  ((TextView) findViewById(R.id.otchestvo_all_job)).getText().toString();
                String job_phone_int_test =  ((TextView) findViewById(R.id.phone_text_all_job)).getText().toString();
                String job_email_int_test =  ((TextView) findViewById(R.id.email_text_all_job)).getText().toString();

                String job_type_int_test = ((TextView) findViewById(R.id.type_job_all_job)).getText().toString();

                String job_paydayvalue_int_test = ((TextView) findViewById(R.id.value_all_job)).getText().toString();

                Intent intent = new Intent(AllAboutJob.this, EditJobActivity.class);

                intent.putExtra("job_ID_intent_to_edit",jNameID);


               // showMessage("job_ID_intent_to_edit"+jNameID);

                intent.putExtra("job_name_intent_to_edit",job_name_int_test);

                intent.putExtra("job_country_intent_to_edit",job_country_int_test);
                intent.putExtra("job_region_intent_to_edit",job_region_int_test);
                intent.putExtra("job_city_intent_to_edit",job_city_int_test);

                intent.putExtra("job_payday_intent_to_edit",job_payday_int_test);

                intent.putExtra("job_treb_intent_to_edit",job_treb_int_test);
                intent.putExtra("job_graphik_intent_to_edit",job_graphik_int_test);

                intent.putExtra("job_Fname_intent_to_edit",job_Fname_int_test);
                intent.putExtra("job_Sname_intent_to_edit",job_Sname_int_test);
                intent.putExtra("job_phone_intent_to_edit",job_phone_int_test);
                intent.putExtra("job_email_intent_to_edit",job_email_int_test);

                intent.putExtra("job_type_intent_to_edit",job_type_int_test);

                intent.putExtra("job_paydayvalue_intent_to_edit",job_paydayvalue_int_test);

                startActivity(intent);
            }
        });

        deletejob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //showMessage("Удаление Объявления");

                HashMap<String,String> detail = db.getUserDetails();
                String jobusername = detail.get("name");

                String jNameID = getIntent().getExtras().getString("job_ID_intent");
                String job_name =  ((TextView) findViewById(R.id.jobname_all_job)).getText().toString();

                String job_country =  ((TextView) findViewById(R.id.country_text_all_job)).getText().toString();
                String job_region =  ((TextView) findViewById(R.id.region_text_all_job)).getText().toString();
                String job_city =  ((TextView) findViewById(R.id.city_text_all_job)).getText().toString();

                String job_payday =  ((TextView) findViewById(R.id.payday_all_job)).getText().toString();

                String job_trebt =  ((TextView) findViewById(R.id.treb_all_job)).getText().toString();
                String job_graphik =  ((TextView) findViewById(R.id.graphik_all_job)).getText().toString();

                String job_Fname =  ((TextView) findViewById(R.id.name_text_all_job)).getText().toString();
                String job_Sname =  ((TextView) findViewById(R.id.otchestvo_all_job)).getText().toString();
                String job_phone =  ((TextView) findViewById(R.id.phone_text_all_job)).getText().toString();
                String job_email =  ((TextView) findViewById(R.id.email_text_all_job)).getText().toString();

                String job_type = ((TextView) findViewById(R.id.type_job_all_job)).getText().toString();

                String job_paydayvalue = ((TextView) findViewById(R.id.value_all_job)).getText().toString();

                deleteJob(
                        job_name,
                        job_payday,
                        job_country,
                        job_region,
                        job_city,
                        job_Fname,
                        job_Sname,
                        job_phone,
                        job_email,
                        job_paydayvalue,
                        job_trebt,
                        job_graphik,
                        job_type,
                        jobusername,
                        jNameID
                );
                gotoMenu();

            }
        });


        /*CheckFavourAdding();


       addOnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user_name = detail.get("name");
                String job_name = getIntent().getExtras().getString("job_name_intent");

                addOnFav.setClickable(false);

                addJobToFav(job_name, user_name);
                gotoMenu();

            }
        });*/

        backtomenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoMenu();
            }
        });
        setTitle("");
    }


    // удаления объявления
    private void deleteJob(final String name, final String payday,
                           final String pos_country, final String pos_region,
                           final String pos_city ,final String cont_fname,
                           final String cont_sname, final String cont_phone,
                           final String cont_email, final String paydayvalue_value,
                           final String aboutj_requirements, final String aboutj_schedulej,
                           final String typejob_type, final String user_name,
                           final String job_ID){

        String tag_string_req = "req_delete_job";

        pDialog.setMessage("Идет удаление объявления, пожалуйста подождите!");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.DELETE_JOB_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Delete Job Response: " + response.toString());
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
                Log.e(TAG, "Delete Job Error: " + error.getMessage());
                //showMessage(error.getMessage());
                showMessage("Отсутствует интернет соединение");
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to job url
                Map<String, String> params = new HashMap<String, String>();

                params.put("tag", "deletejob");
                params.put("job_name",name);
                params.put("job_payday",payday);

                // таблица местоположения
                params.put("job_pos_country",pos_country);
                params.put("job_pos_region",pos_region);
                params.put("job_pos_city",pos_city);

                // таблица контктов
                params.put("job_cont_fname",cont_fname);
                params.put("job_cont_sname",cont_sname);
                params.put("job_cont_phone",cont_phone);
                params.put("job_cont_email",cont_email);

                // таблица валюты
                params.put("job_paydayvalue_value",paydayvalue_value);

                //таблица дополнительные сведения
                params.put("job_aboutj_requirements",aboutj_requirements);
                params.put("job_aboutj_schedulej",aboutj_schedulej);

                // таблица тип работы
                params.put("job_typejob_type",typejob_type);

                //ид пользователя
                params.put("job_user_name",user_name);

                params.put("job_name_ID",job_ID);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    // получения списка созданных пользователем работ
    private void getFindAllInfoAboutJob(){
        String tag_string_req = "req_get_find_all_info_job";

        pDialog.setMessage("Идет получение данных, пожалуйста подождите !");
        showDialog();

        HashMap<String,String> detail = db.getUserDetails();

        String uName = detail.get("name");

        String jNameID = getIntent().getExtras().getString("job_ID_intent");

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.GET_FIND_ALL_JOB_INFO_URL +"&job_user_name="+ uName +"&job_name_ID="+ jNameID, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Find All Jobs Response: " + response.toString());
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


                            job_name_actv.setText(jobname);
                            job_type_actv.setText(jobtype);
                            job_payday_actv.setText(payday);
                            job_paydayvalue_actv.setText(paydayvalue);
                            job_country_actv.setText(country);
                            job_region_actv.setText(region);
                            job_city_actv.setText(city);
                            job_treb_actv.setText(trebovaniya);
                            job_grap_actv.setText(graphik);
                            job_Fname_actv.setText(Fname);
                            job_Sname_actv.setText(Sname);
                            job_phone_actv.setText(phone);
                            job_email_actv.setText(email);


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
                Log.e(TAG, "Find All Jobs Error: " + error.getMessage());
               // showMessage(error.getMessage());
                showMessage("Отсутствует интернет соединение");
                hideDialog();
            }
        }) {

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    // проверка добавления в избранное
   /* private void CheckFavourAdding(){
        String tag_string_req = "req_check_favour_add";

        HashMap<String,String> detail = db.getUserDetails();

        String uName = detail.get("name");

        String jNameID = getIntent().getExtras().getString("job_ID_intent");

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.CHECK_FAVOUR_URL +"&job_user_name="+ uName +"&job_name_ID="+ jNameID, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Check Favour Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    String countjob = jObj.getString("count_actv");

                   // showMessage("countjob = " + countjob);

                    if(countjob.contains("0"))
                    {
                        //addOnFav.setClickable(false);
                        //showMessage("кнопка активна");

                        addOnFav.setClickable(true);
                        addOnFav.setTextColor(getResources().getColor(R.color.colorBlack));
                        addOnFav.setBackgroundResource(R.drawable.rect_text_add_butt);

                    }
                    else
                    {
                        //showMessage("кнопка неактивна");
                        addOnFav.setClickable(false);
                        addOnFav.setTextColor(getResources().getColor(R.color.icons));
                        addOnFav.setBackgroundResource(R.drawable.favour_butt);
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Check Favour Error: " + error.getMessage());
                showMessage(error.getMessage());
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

        pDialog.setMessage("Идет добавление в избранное, пожалуйста подождите!");
        showDialog();

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
                showMessage(error.getMessage());
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
    }*/

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

        Intent intent = new Intent(AllAboutJob.this, MenuActivity.class);
        startActivity(intent);
    }


}

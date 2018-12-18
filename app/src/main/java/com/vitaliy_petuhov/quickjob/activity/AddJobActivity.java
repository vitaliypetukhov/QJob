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
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.vitaliy_petuhov.quickjob.R;
import com.vitaliy_petuhov.quickjob.helper.EmailValidator;
import com.vitaliy_petuhov.quickjob.helper.SQLiteHandler;
import com.vitaliy_petuhov.quickjob.helper.SessionManager;
import com.vitaliy_petuhov.quickjob.application.AppConfig;
import com.vitaliy_petuhov.quickjob.application.AppController;

public class AddJobActivity extends AppCompatActivity {
    public static String TAG = AddJobActivity.class.getSimpleName();
    private SessionManager session;
    private SQLiteHandler db;
    private ProgressDialog pDialog;
    private ArrayList<String> array_spinner;


    private EditText jobnameFT, paydayFT, pos_countryFT, pos_regionFT, pos_cityFT, cont_fnameFT;
    private EditText cont_snameFT, cont_phoneFT, cont_emailFT, requieFT, scheduleFT;
    private Spinner typejobSP, paydayvalueSP;
    private Button addJobBtn,gotomainmenuBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job);

        //toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        //setSupportActionBar(toolbar);

        addJobBtn = (Button) findViewById(R.id.AddJobButton);
        gotomainmenuBtn = (Button) findViewById(R.id.BackToMenu);

        jobnameFT = (EditText) findViewById(R.id.JobName);
        paydayFT = (EditText) findViewById(R.id.JobPayday);
        pos_countryFT = (EditText) findViewById(R.id.JobCountry);
        pos_regionFT = (EditText) findViewById(R.id.JobRegion);
        pos_cityFT = (EditText) findViewById(R.id.JobCity);
        cont_fnameFT = (EditText) findViewById(R.id.JobContFName);
        cont_snameFT = (EditText) findViewById(R.id.JobContSName);
        cont_emailFT = (EditText) findViewById(R.id.JobContEmail);
        cont_phoneFT = (EditText) findViewById(R.id.JobContPhone);
       // paydayvalueFT = (EditText) findViewById(R.id.JobPaydayValue);
        requieFT = (EditText) findViewById(R.id.JobRequirements);
        scheduleFT = (EditText) findViewById(R.id.JobSchedule);
        //typejobFT = (EditText) findViewById(R.id.JobType);
        typejobSP = (Spinner) findViewById(R.id.JobType);
        paydayvalueSP = (Spinner) findViewById(R.id.JobPaydayValue);


        session = new SessionManager(getApplicationContext());
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        addSpinner();

        //setTitle("");

        final EmailValidator emailValidator = new EmailValidator();

        db = new SQLiteHandler(getApplicationContext());
        final HashMap<String,String> detail = db.getUserDetails();

        //String user_name = detail.get("name");

        //showMessage(user_name);

        gotomainmenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoMenu();
            }
        });

        addJobBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String jobusername = detail.get("name");

                String jobname = jobnameFT.getText().toString();
                String jobpayday = paydayFT.getText().toString();
                String jobcountry = pos_countryFT.getText().toString();
                String jobregion = pos_regionFT.getText().toString();
                String jobcity = pos_cityFT.getText().toString();
                String jobcontfname = cont_fnameFT.getText().toString();
                String jobcontsname = cont_snameFT.getText().toString();
                String jobcontemail = cont_emailFT.getText().toString();
                String jobcontphone = cont_phoneFT.getText().toString();
                //String jobpaydayvalue = paydayvalueFT.getText().toString();
                String jobrequie = requieFT.getText().toString();
                String jobschedule = scheduleFT.getText().toString();

                //String jobtype = typejobFT.getText().toString();


                String jobtype = typejobSP.getSelectedItem().toString();
                String jobpaydayvalue = paydayvalueSP.getSelectedItem().toString();

                if ( jobname.isEmpty()&& jobpayday.isEmpty()&&
                        jobcountry.isEmpty()&& jobregion.isEmpty()&&
                        jobcity.isEmpty()&& jobcontfname.isEmpty()&&
                        jobcontsname.isEmpty()&& jobcontphone.isEmpty()&&
                        jobcontemail.isEmpty()&&
                        jobrequie.isEmpty()&& jobschedule.isEmpty())
                {
                    showMessage("Заполните все поля для ввода!");
                }
                else
                {
                    if(emailValidator.validate(jobcontemail) == true)
                    {
                        addNewJob(
                                jobname,
                                jobpayday,
                                jobcountry,
                                jobregion,
                                jobcity,
                                jobcontfname,
                                jobcontsname,
                                jobcontphone,
                                jobcontemail,
                                jobpaydayvalue,
                                jobrequie,
                                jobschedule,
                                jobtype,
                                jobusername
                        );
                        gotoMenu();
                    }
                    else
                    {
                        showMessage("Некорректный email, формат ввода (example@email.com)");
                    }


                }
            }
        });


    }

    //Получение типа работы
    private void getJobType(){
        String tag_string_req = "req_get_job_type";

        pDialog.setMessage("Идет получение данных, пожалуйста подождите !");
        showDialog();

        StringRequest strReq = new StringRequest(Method.GET, AppConfig.GET_JOB_TYPE_URL, new Response.Listener<String>() {

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
                        addInfoTypeOnSpinner();
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
                showMessage(error.getMessage());
                hideDialog();
            }
        }) {

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    //Получение типа валюты
    private void getJobPaydayValue(){
        String tag_string_req = "req_get_job_paydayvalue";

        pDialog.setMessage("Идет получение данных, пожалуйста подождите !");
        showDialog();

        StringRequest strReq = new StringRequest(Method.GET, AppConfig.GET_JOB_PAYDAYVALUE_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Get Job PaydayValue Response: " + response.toString());
                hideDialog();

                try {
                    JSONArray jsonarray = new JSONArray(response);
                    JSONObject jMsg = jsonarray.getJSONObject(0);
                    boolean msg = jMsg.getBoolean("msg");
                    if(msg){
                        array_spinner = new ArrayList<String>();
                        for (int i=1; i < jsonarray.length(); i++){
                            JSONObject jObj = jsonarray.getJSONObject(i);
                            array_spinner.add(jObj.getString("paydayvalue_actv"));
                        }
                        addInfoPaydayvalueOnSpinner();
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


    private void addInfoPaydayvalueOnSpinner() {
        Spinner s = (Spinner) findViewById(R.id.JobPaydayValue);
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_dropdown_item_1line, array_spinner);
        s.setAdapter(adapter);

        /*for(int i=0; i<array_spinner.size();i++)
        {
            if(array_spinner.get(i).indexOf(cafe_type)==0)
                typeCafe.setSelection(i);
        }*/
    }

    private void addInfoTypeOnSpinner() {
        Spinner s = (Spinner) findViewById(R.id.JobType);
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_dropdown_item_1line, array_spinner);
        s.setAdapter(adapter);
    }

    // Создание объявления
    private void addNewJob(final String name, final String payday,
                           final String pos_country, final String pos_region,
                           final String pos_city ,final String cont_fname,
                           final String cont_sname, final String cont_phone,
                           final String cont_email, final String paydayvalue_value,
                           final String aboutj_requirements, final String aboutj_schedulej,
                           final String typejob_type, final String user_name){

        String tag_string_req = "req_add_job";

        pDialog.setMessage("Идет добавление нового объявления, пожалуйста подождите!");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST, AppConfig.ADD_JOB_URL, new Response.Listener<String>() {

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
                Log.e(TAG, "Add Job Error: " + error.getMessage());
                //showMessage(error.getMessage());
                showMessage("Отсутствует интернет соединение");
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to job url
                Map<String, String> params = new HashMap<String, String>();

                params.put("tag", "addjob");
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

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.viewjobtoolbar, menu);


        return true;
    }

    public void gotoMenu(){
        Intent intent = new Intent(AddJobActivity.this, MenuActivity.class);
        startActivity(intent);
    }

    private void showMessage(String msg) {
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    /*public void gotoLogin() {
        Intent intent = new Intent(AddJobActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }*/

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

    public void addSpinner(){
        getJobType();
        getJobPaydayValue();
    }

}

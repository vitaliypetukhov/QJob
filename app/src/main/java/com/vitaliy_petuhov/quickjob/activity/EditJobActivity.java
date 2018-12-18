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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.vitaliy_petuhov.quickjob.R;
import com.vitaliy_petuhov.quickjob.application.AppConfig;
import com.vitaliy_petuhov.quickjob.application.AppController;
import com.vitaliy_petuhov.quickjob.helper.EmailValidator;
import com.vitaliy_petuhov.quickjob.helper.SQLiteHandler;
import com.vitaliy_petuhov.quickjob.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditJobActivity extends AppCompatActivity {

    public static String TAG = EditJobActivity.class.getSimpleName();
    private SessionManager session;
    private SQLiteHandler db;
    private ProgressDialog pDialog;
    private ArrayList<String> array_spinner;


    private EditText jobnameFT, paydayFT, pos_countryFT, pos_regionFT, pos_cityFT, cont_fnameFT;
    private EditText cont_snameFT, cont_phoneFT, cont_emailFT, requieFT, scheduleFT;
    private Spinner typejobSP, paydayvalueSP;
    private Button editJobBtn,gotomainmenuBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editJobBtn = (Button) findViewById(R.id.AddJobButton_edit);
        gotomainmenuBtn = (Button) findViewById(R.id.BackToMenu_edit);

        jobnameFT = (EditText) findViewById(R.id.JobName_edit);
        paydayFT = (EditText) findViewById(R.id.JobPayday_edit);
        pos_countryFT = (EditText) findViewById(R.id.JobCountry_edit);
        pos_regionFT = (EditText) findViewById(R.id.JobRegion_edit);
        pos_cityFT = (EditText) findViewById(R.id.JobCity_edit);
        cont_fnameFT = (EditText) findViewById(R.id.JobContFName_edit);
        cont_snameFT = (EditText) findViewById(R.id.JobContSName_edit);
        cont_emailFT = (EditText) findViewById(R.id.JobContEmail_edit);
        cont_phoneFT = (EditText) findViewById(R.id.JobContPhone_edit);
        requieFT = (EditText) findViewById(R.id.JobRequirements_edit);
        scheduleFT = (EditText) findViewById(R.id.JobSchedule_edit);

        typejobSP = (Spinner) findViewById(R.id.JobType_edit);
        paydayvalueSP = (Spinner) findViewById(R.id.JobPaydayValue_edit);

        String jName = getIntent().getExtras().getString("job_name_intent_to_edit");

        String jCountry = getIntent().getExtras().getString("job_country_intent_to_edit");
        String jRegion = getIntent().getExtras().getString("job_region_intent_to_edit");
        String jCity = getIntent().getExtras().getString("job_city_intent_to_edit");

        String jPayday = getIntent().getExtras().getString("job_payday_intent_to_edit");

        String jTreb = getIntent().getExtras().getString("job_treb_intent_to_edit");
        String jGraphik = getIntent().getExtras().getString("job_graphik_intent_to_edit");

        String jSName = getIntent().getExtras().getString("job_Fname_intent_to_edit");
        String jFName = getIntent().getExtras().getString("job_Sname_intent_to_edit");
        String jPhone = getIntent().getExtras().getString("job_phone_intent_to_edit");
        String jEmail = getIntent().getExtras().getString("job_email_intent_to_edit");



        jobnameFT.setText(jName);

        pos_countryFT.setText(jCountry);
        pos_regionFT.setText(jRegion);
        pos_cityFT.setText(jCity);

        paydayFT.setText(jPayday);

        requieFT.setText(jTreb);
        scheduleFT.setText(jGraphik);

        cont_fnameFT.setText(jFName);
        cont_snameFT.setText(jSName);
        cont_phoneFT.setText(jPhone);
        cont_emailFT.setText(jEmail);


        final EmailValidator emailValidator = new EmailValidator();
        //showMessage("ID" + jNameID);


        session = new SessionManager(getApplicationContext());
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        addSpinnerEdit();

        db = new SQLiteHandler(getApplicationContext());
        final HashMap<String,String> detail = db.getUserDetails();

        gotomainmenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoMenu();
            }
        });

        editJobBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String jobusername = detail.get("name");

                String job_name_ID = getIntent().getExtras().getString("job_ID_intent_to_edit");

                String jobname = jobnameFT.getText().toString();
                String jobpayday = paydayFT.getText().toString();
                String jobcountry = pos_countryFT.getText().toString();
                String jobregion = pos_regionFT.getText().toString();
                String jobcity = pos_cityFT.getText().toString();
                String jobcontfname = cont_fnameFT.getText().toString();
                String jobcontsname = cont_snameFT.getText().toString();
                String jobcontemail = cont_emailFT.getText().toString();
                String jobcontphone = cont_phoneFT.getText().toString();
                String jobrequie = requieFT.getText().toString();
                String jobschedule = scheduleFT.getText().toString();

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
                        editJob(
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
                                jobusername,
                                job_name_ID
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
    private void getJobTypeEdit(){
        String tag_string_req = "req_get_job_type_edit";

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

    //Получение типа валюты
    private void getJobPaydayValueEdit(){
        String tag_string_req = "req_get_job_paydayvalue_edit";

        pDialog.setMessage("Идет получение данных, пожалуйста подождите !");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.GET_JOB_PAYDAYVALUE_URL, new Response.Listener<String>() {

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
                        addInfoOnSpinner_Paydayvalue();
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

    private void addInfoOnSpinner_Paydayvalue() {

        String jPaydayValue = getIntent().getExtras().getString("job_paydayvalue_intent_to_edit");

        Spinner s = (Spinner) findViewById(R.id.JobPaydayValue_edit);
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_dropdown_item_1line, array_spinner);
        s.setAdapter(adapter);

        for(int i=0; i<array_spinner.size();i++) {
            if(array_spinner.get(i).indexOf(jPaydayValue)==0)
                s.setSelection(i);
        }
    }

    private void addInfoOnSpinner_Type() {

        String jType = getIntent().getExtras().getString("job_type_intent_to_edit");

        Spinner s = (Spinner) findViewById(R.id.JobType_edit);
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_dropdown_item_1line, array_spinner);
        s.setAdapter(adapter);

        for(int i=0; i<array_spinner.size();i++) {
            if(array_spinner.get(i).indexOf(jType)==0)
                s.setSelection(i);
        }
    }

    // Редактирование объявления
    private void editJob(final String name, final String payday,
                           final String pos_country, final String pos_region,
                           final String pos_city ,final String cont_fname,
                           final String cont_sname, final String cont_phone,
                           final String cont_email, final String paydayvalue_value,
                           final String aboutj_requirements, final String aboutj_schedulej,
                           final String typejob_type, final String user_name,
                           final String job_ID){

        String tag_string_req = "req_edit_job";

        pDialog.setMessage("Идет редактирование объявления, пожалуйста подождите!");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.EDIT_JOB_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Edit Job Response: " + response.toString());
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
                Log.e(TAG, "Edit Job Error: " + error.getMessage());
                //showMessage(error.getMessage());
                showMessage("Отсутствует интернет соединение");
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to job url
                Map<String, String> params = new HashMap<String, String>();

                params.put("tag", "editjob");
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
        Intent intent = new Intent(EditJobActivity.this, MenuActivity.class);
        startActivity(intent);
    }

    private void showMessage(String msg) {
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    public void addSpinnerEdit(){
        getJobTypeEdit();
        getJobPaydayValueEdit();
    }

}

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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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

public class FavourActivity extends AppCompatActivity {

    private static final String TAG = FavourActivity.class.getSimpleName();

    private ImageButton close_menu;
    private TextView favouritiesTV;

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    ArrayList<HashMap<String, String>> jobsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favour);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);



        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        jobsList = new ArrayList<HashMap<String, String>>();

        favouritiesTV = (TextView)findViewById(R.id.favour_tv);

        close_menu = (ImageButton) findViewById(R.id.fav_closemenu_butt);

        close_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FavourActivity.this, MenuActivity.class);
                startActivity(intent);

            }
        });

        ListView listV = (ListView)findViewById(R.id.viewalljobs_fav);

        listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String job_ID_int_test = ((TextView) view.findViewById(R.id.jobIDitem)).getText().toString();
                String job_name_int_test = ((TextView) view.findViewById(R.id.jobnameitem)).getText().toString();
                //передаем job_name
                Intent intent = new Intent(FavourActivity.this, AllAboutFavourJob.class);
                intent.putExtra("job_ID_intent",job_ID_int_test);
                intent.putExtra("job_name_intent",job_name_int_test);
                startActivity(intent);

            }
        });

        setTitle("Мои Избранные Объявления");
        getFindAllJob_Fav();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.fav_back_menu:

            // logoutUser();
                Intent intent = new Intent(FavourActivity.this, MenuActivity.class);
                startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // получения списка созданных пользователем работ
    private void getFindAllJob_Fav(){
        String tag_string_req = "req_get_find_all_job_fav";

        HashMap<String,String> detail = db.getUserDetails();

        String uName = detail.get("name");


        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.GET_FIND_ALL_JOB_FAV_URL +"&job_user_name="+ uName, new Response.Listener<String>() {

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

                            String jobID = jObj.getString("job_ID_actv");
                            String jobname = jObj.getString("job_name_actv");
                            String country = jObj.getString("job_country_actv");
                            String region = jObj.getString("job_region_actv");
                            String city = jObj.getString("job_city_actv");
                            String payday = jObj.getString("job_payday_actv");
                            String paydayvalue = jObj.getString("job_paydayvalue_actv");

                            HashMap<String, String> map = new HashMap<String, String>();

                            map.put("job_ID_actv",jobID);
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

        ListView lw = (ListView) findViewById(R.id.viewalljobs_fav);

        String [] headers = new String[]
                {
                        "job_ID_actv",
                        "job_name_actv",
                        "job_country_actv",
                        "job_region_actv",
                        "job_city_actv",
                        "job_payday_actv",
                        "job_paydayvalue_actv"
                };

        int [] to =  new int[]
                {
                        R.id.jobIDitem,
                        R.id.jobnameitem,
                        R.id.countryitem,
                        R.id.regionitem,
                        R.id.cityitem,
                        R.id.paydayitem,
                        R.id.paydayvalueitem
                };

        ListAdapter adapter = new SimpleAdapter(FavourActivity.this, jobsList, R.layout.item, headers, to);
        lw.setAdapter(adapter);

        if (jobsList.size() == 0)
        {
            favouritiesTV.setVisibility(View.VISIBLE);
            String fontPath = "fonts/agcrownstyle.ttf";
            Typeface typeface = Typeface.createFromAsset(getAssets(), fontPath);
            favouritiesTV.setTypeface(typeface);
            hideDialog();
        }
        else
        {
            favouritiesTV.setVisibility(View.GONE);
            hideDialog();
        }
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

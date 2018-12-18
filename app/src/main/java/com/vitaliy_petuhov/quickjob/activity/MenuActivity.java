package com.vitaliy_petuhov.quickjob.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MenuActivity.class.getSimpleName();

    private TextView nameTV, emailTV;
    private ProgressDialog pDialog;
    private TextView favbage, findjobbage, menu_tv;
    private SessionManager session;
    private SQLiteHandler db;

    ArrayList<HashMap<String, String>> jobsList;

    String job_ID_int_test, job_name_int_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.created_adv);
        setSupportActionBar(toolbar);


        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View header = navigationView.getHeaderView(0);
        nameTV = (TextView) header.findViewById(R.id.idFnameMM);
        emailTV = (TextView) header.findViewById(R.id.idEmailMM);

        menu_tv = (TextView) findViewById(R.id.menuTV);

        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        if(!session.isLoggedIn()){
            logoutUser();
        }

        jobsList = new ArrayList<HashMap<String, String>>();

        ListView listV = (ListView)findViewById(R.id.viewalljobs);

        listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                job_ID_int_test = ((TextView) view.findViewById(R.id.jobIDitem)).getText().toString();
                job_name_int_test = ((TextView) view.findViewById(R.id.jobnameitem)).getText().toString();

                //передаем job_name
                Intent intent = new Intent(MenuActivity.this, AllAboutJob.class);

                intent.putExtra("job_ID_intent",job_ID_int_test);

                intent.putExtra("job_name_intent",job_name_int_test);
                startActivity(intent);

            }
        });

        setTitle("");

        HashMap<String,String> detail = db.getUserDetails();

        String namemm = detail.get("name");
        String emailmm = detail.get("email");

        String fontPath = "fonts/agcrownstyle.ttf";
        Typeface typeface = Typeface.createFromAsset(getAssets(), fontPath);

        nameTV.setText(namemm);
        nameTV.setTypeface(typeface);

        emailTV.setText(emailmm);
        emailTV.setTypeface(typeface);

        findjobbage = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_findjob));
        favbage = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_favourites));

        initializeCountDrawer();

        getFindAllJob();

    }

    private void initializeCountDrawer() {
        getCountFavourJob();
        getCountJob();
    }

    // получения списка созданных пользователем работ
    private void getFindAllJob(){
        String tag_string_req = "req_get_find_all_job";

        pDialog.setMessage("Идет получение данных, пожалуйста подождите !");
        showDialog();

        HashMap<String,String> detail = db.getUserDetails();

        String uName = detail.get("name");


        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.GET_FIND_ALL_JOB_URL +"&job_user_name="+ uName, new Response.Listener<String>() {

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
                            String typejob = jObj.getString("job_typejob_actv");


                            HashMap<String, String> map = new HashMap<String, String>();

                            map.put("job_ID_actv",jobID);
                            map.put("job_name_actv",jobname);
                            map.put("job_country_actv",country);
                            map.put("job_region_actv",region);
                            map.put("job_city_actv",city);
                            map.put("job_payday_actv",payday);
                            map.put("job_paydayvalue_actv",paydayvalue);
                            map.put("job_typejob_actv",typejob);
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

    //вывод списка ваших работ
    private void addInfoAllJobsOnView() {

        //pDialog.setMessage("Идет получение данных, пожалуйста подождите !");
        //showDialog();

        ListView lw = (ListView) findViewById(R.id.viewalljobs);

        String [] headers = new String[]
                {
                        "job_ID_actv",
                        "job_name_actv",
                        "job_country_actv",
                        "job_region_actv",
                        "job_city_actv",
                        "job_payday_actv",
                        "job_paydayvalue_actv",
                        "job_typejob_actv"
                };
        int [] to =  new int[]
                {
                        R.id.jobIDitem,
                        R.id.jobnameitem,
                        R.id.countryitem,
                        R.id.regionitem,
                        R.id.cityitem,
                        R.id.paydayitem,
                        R.id.paydayvalueitem,
                        R.id.typejobitem
                };
        ListAdapter adapter = new SimpleAdapter(MenuActivity.this, jobsList, R.layout.item, headers, to);
        lw.setAdapter(adapter);

        if (jobsList.size() == 0)
        {

            menu_tv.setVisibility(View.VISIBLE);
            String fontPath = "fonts/agcrownstyle.ttf";
            Typeface typeface = Typeface.createFromAsset(getAssets(), fontPath);
            menu_tv.setTypeface(typeface);
            //hideDialog();
        }
        else
        {
            menu_tv.setVisibility(View.GONE);

        }

        //hideDialog();

    }

    //подсчет кол-ва избранных в меню
    private void getCountFavourJob(){
        String tag_string_req = "req_get_count_favour_job";

        HashMap<String,String> detail = db.getUserDetails();

        String uName = detail.get("name");

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.GET_COUNT_FAVOUR_JOB_URL +"&job_user_name="+ uName, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Get Job PaydayValue Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    String countjob = jObj.getString("count_actv");
                    if(countjob.equals("0"))
                    {
                        favbage.setGravity(Gravity.CENTER_VERTICAL);
                        favbage.setTypeface(null, Typeface.BOLD);
                        favbage.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15.f);
                        favbage.setTextColor(getResources().getColor(R.color.colorPrimary));
                        favbage.setText("");
                    }
                    else
                    {
                        favbage.setGravity(Gravity.CENTER_VERTICAL);
                        favbage.setTypeface(null, Typeface.BOLD);
                        favbage.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15.f);
                        favbage.setTextColor(getResources().getColor(R.color.colorPrimary));
                        favbage.setText("(" + countjob + ")");
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

    // подсчет кол-ва работ
    private void getCountJob(){
        String tag_string_req = "req_get_count_job";

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.GET_COUNT_JOB_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Get Job PaydayValue Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    String countjob = jObj.getString("count_actv");

                    findjobbage.setGravity(Gravity.CENTER_VERTICAL);
                    findjobbage.setTypeface(null, Typeface.BOLD);
                    findjobbage.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15.f);
                    findjobbage.setTextColor(getResources().getColor(R.color.colorPrimary));
                    findjobbage.setText("(" + countjob + ")");


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

    private void logoutUser(){
        session.setLogin(false);
        db.deleteUsers();

        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            //case R.id.logout:

               // logoutUser();
               // return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_addjob) {

            //setTitle("Создать Объявление");

            Intent intent = new Intent(MenuActivity.this, AddJobActivity.class);
            startActivity(intent);

            //fragmentManager.beginTransaction().replace(R.id.content_frame, new AddJob()).commit();

        }
        /*else if (id == R.id.nav_editjob){
           // setTitle("Редактировать Объявление");
            getCountJobForEdit();
        }*/


        else if (id == R.id.nav_findjob) {

            //setTitle("Поиск Работы");

            Intent intent = new Intent(MenuActivity.this, FindJobActivity.class);
            startActivity(intent);


        }
        else if (id == R.id.nav_favourites) {

            //setTitle("Избранное");
            Intent intent = new Intent(MenuActivity.this, FavourActivity.class);
            startActivity(intent);

        }

        else if (id == R.id.nav_change) {

           // setTitle("Изменение Данных");
            //fragmentManager.beginTransaction().replace(R.id.content_frame, new MyFavourities()).commit();

            Intent intent = new Intent(MenuActivity.this, ChangeUserActivity.class);
            startActivity(intent);

        }

        else if (id == R.id.nav_about) {

            //setTitle("О Приложении");
            //fragmentManager.beginTransaction().replace(R.id.content_frame, new MyFavourities()).commit();

            Intent intent = new Intent(MenuActivity.this, AboutJobActivity.class);
            startActivity(intent);

        }
        else if (id == R.id.nav_logout) {

            logoutUser();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}

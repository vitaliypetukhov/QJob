package com.vitaliy_petuhov.quickjob.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangeUserActivity extends AppCompatActivity {

    public static String TAG = ChangeUserActivity.class.getSimpleName();
    private SessionManager session;
    private SQLiteHandler db;
    private ProgressDialog pDialog;

    private Button change_name, change_passwrd,backToMenu;
    private TextView nameTV, passwrdTV;
    private EditText newName_changeName;
    private TextView starName_changeName, email_changeName;
    private EditText newPass_changePass;
    private TextView starName_changePass, email_changePass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user);

        session = new SessionManager(getApplicationContext());
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        db = new SQLiteHandler(getApplicationContext());
        final HashMap<String,String> detail = db.getUserDetails();

        nameTV = (TextView) findViewById(R.id.changeNameTV);
        passwrdTV = (TextView) findViewById(R.id.changePassTV);

        starName_changeName = (TextView) findViewById(R.id.starName);
        String UserName = detail.get("name");
        starName_changeName.setText(UserName);

        email_changeName = (TextView) findViewById(R.id.starEmail);
        String EmailUser = detail.get("email");
        email_changeName.setText(EmailUser);

        newName_changeName = (EditText) findViewById(R.id.newName);

        starName_changePass = (TextView) findViewById(R.id.starName_1);
        String UserName_1 = detail.get("name");
        starName_changePass.setText(UserName_1);

        email_changePass = (TextView) findViewById(R.id.starEmail_1);
        String EmailUser_1 = detail.get("email");
        email_changePass.setText(EmailUser_1);

        newPass_changePass = (EditText) findViewById(R.id.newPassword);

        change_name = (Button) findViewById(R.id.changeNameButt);
        change_passwrd = (Button) findViewById(R.id.changePasswordButt);

        backToMenu = (Button) findViewById(R.id.BackToMenu_changeuser);

        final EmailValidator emailValidator = new EmailValidator();

        backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gotoMenu();
            }
        });

        change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = starName_changeName.getText().toString();
                //String name = detail.get("name");
                String new_name = newName_changeName.getText().toString();
                String email = email_changeName.getText().toString();
               // String email = detail.get("email");

                if(name.isEmpty() || new_name.isEmpty() || email.isEmpty())
                {
                    showMessage("Заполните все поля для ввода!");
                }
                else
                {
                    if(emailValidator.validate(email) == true)
                    {
                        editUserName(name,new_name,email);
                        logoutUser();
                    }
                    else
                    {
                        showMessage("Некорректный email, формат ввода (example@email.com)");
                    }
                }
            }
        });

        change_passwrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = starName_changePass.getText().toString();
                String new_passwrd = newPass_changePass.getText().toString();
                String email = email_changePass.getText().toString();

                if(name.isEmpty() || new_passwrd.isEmpty() || email.isEmpty())
                {
                    showMessage("Заполните все поля для ввода!");
                }
                else
                {
                    if(emailValidator.validate(email) == true)
                    {
                        editPassword(name,new_passwrd,email);
                        logoutUser();
                    }
                    else
                    {
                        showMessage("Некорректный email, формат ввода (example@email.com)");
                    }
                }

            }
        });
    }

    // Редактирование пароля
    private void editPassword(final String name, final String new_passwrd,final String email ){

        String tag_string_req = "req_edit_user_passwrd";

        pDialog.setMessage("Идет редактирование пароля, пожалуйста подождите!");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.EDIT_USER_NAME_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Edit Passwrd Response: " + response.toString());
                hideDialog();

                try
                {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (error)
                    {
                        String errorMsg = jObj.getString("error_msg");
                        showMessage(errorMsg);
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
                Log.e(TAG, "Edit Passwrd Error: " + error.getMessage());
                //showMessage(error.getMessage());
                showMessage("Отсутствует интернет соединение");
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to job url
                Map<String, String> params = new HashMap<String, String>();

                params.put("tag", "edit_password");
                params.put("name",name);
                params.put("password",new_passwrd);
                params.put("email",email);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    // Редактирование имени
    private void editUserName(final String name, final String new_name,final String email ){

        String tag_string_req = "req_edit_user_name";

        pDialog.setMessage("Идет редактирование имени пользователя, пожалуйста подождите!");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.EDIT_USER_NAME_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Edit User Name Response: " + response.toString());
                hideDialog();

                try
                {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (error)
                    {
                        String errorMsg = jObj.getString("error_msg");
                        showMessage(errorMsg);
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

                params.put("tag", "edit_user_name");
                params.put("name",name);
                params.put("new_name",new_name);
                params.put("email",email);
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

    private void logoutUser(){
        session.setLogin(false);
        db.deleteUsers();

        Intent i = new Intent(ChangeUserActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    public void gotoMenu(){
        Intent intent = new Intent(ChangeUserActivity.this, MenuActivity.class);
        startActivity(intent);
    }

    private void showMessage(String msg) {
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

}

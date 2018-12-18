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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NewPassword extends AppCompatActivity {

    public static String TAG = NewPassword.class.getSimpleName();
    private Button getnewpassButton, gotologinButton;
    private EditText nameEdit, emailEdit, passEdit;
    private TextView passView;

    private SessionManager session;
    private SQLiteHandler db;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getnewpassButton = (Button) findViewById(R.id.getNewPass);
        gotologinButton = (Button) findViewById(R.id.goToLogin);
        nameEdit = (EditText) findViewById(R.id.NameNewPass);
        emailEdit = (EditText) findViewById(R.id.EmailNewPass);
        passEdit = (EditText) findViewById(R.id.NewPassword);
        passView = (TextView) findViewById(R.id.textViewNPass);

        passEdit.setVisibility(View.INVISIBLE);
        passView.setVisibility(View.INVISIBLE);

        final EmailValidator emailValidator = new EmailValidator();

        session = new SessionManager(getApplicationContext());

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        db = new SQLiteHandler(getApplicationContext());

        getnewpassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailEdit.getText().toString();
                String name = nameEdit.getText().toString();

                if((!name.isEmpty()) && (!email.isEmpty()))
                {
                    if(emailValidator.validate(email) == true)
                    {
                      // restorePassword(name,email);

                        restorePassword();
                    }
                    else
                    {
                        showMessage("Некорректный email, формат ввода (example@email.com)");
                    }
                }
                else
                {
                    showMessage("Пожалуйста заполните все поля для ввода!");
                }

            }
        });

        gotologinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(NewPassword.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }

    private void restorePassword()
    {
        String tag_string_req = "req_restore_password";

        String email = emailEdit.getText().toString();
        String name = nameEdit.getText().toString();

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.GET_RESTORE_URL +"&name="+ name +"&email="+ email, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Restore Password Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("msg");
                    if (error)
                    {
                        passEdit.setVisibility(View.VISIBLE);
                        passView.setVisibility(View.VISIBLE);
                        String new_password = jObj.getString("pass_actv");
                        passEdit.setText(new_password);
                    }
                    else
                    {
                        String errorMsg = jObj.getString("error_msg");
                        showMessage(errorMsg);
                    }

                }
                catch (JSONException e){

                    e.printStackTrace();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Restore PAssword Error: " + error.getMessage());
                //showMessage(error.getMessage());
                showMessage("Отсутствует интернет соединение");
                hideDialog();
            }
        }) {
        };
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
        Toast toast = Toast.makeText(getApplicationContext(),
                msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

}

package com.vitaliy_petuhov.quickjob.activity;

import java.util.HashMap;
import java.util.Map;
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
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {
    public static String TAG = RegisterActivity.class.getSimpleName();

    private Button goToLoginBtn, registerBtn;
    private EditText nameFT, emailFT, passwordFT;
    private SessionManager session;
    private SQLiteHandler db;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        goToLoginBtn = (Button) findViewById(R.id.idLogButt);
        registerBtn = (Button) findViewById(R.id.idRegButt);
        nameFT = (EditText) findViewById(R.id.idFullnameR);
        emailFT = (EditText) findViewById(R.id.idEmailR);
        passwordFT = (EditText) findViewById(R.id.idPassR);

        final EmailValidator emailValidator = new EmailValidator();

        session = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        db = new SQLiteHandler(getApplicationContext());

        if (session.isLoggedIn()) {
            gotoLogin();
        }
        goToLoginBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }

        });
        registerBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String name = nameFT.getText().toString();
                String email = emailFT.getText().toString();
                String password = passwordFT.getText().toString();

                if (name.isEmpty() && email.isEmpty() && password.isEmpty())
                {
                    showMessage("Заполните все поля для ввода!");
                }
                else
                {
                    if(emailValidator.validate(email) == true)
                    {
                        registerUser(name, email, password);
                    }
                    else
                    {
                        showMessage("Некорректный email, формат ввода (example@email.com)");
                    }
                }
            }
        });

    }

    private void registerUser(final String name, final String email, final String password) {
        String tag_string_req = "req_register";
        pDialog.setMessage("Регистрация, пожалуйста подождите !");
        showDialog();
        StringRequest strReq = new StringRequest(Method.POST, AppConfig.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Register Response: " + response.toString());
                        hideDialog();
                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            if (!error) {
                                String uid = jObj.getString("uid");
                                JSONObject user = jObj.getJSONObject("user");
                                String name = user.getString("name");
                                String email = user.getString("email");
                                String created_at = user.getString("created_at");
                                db.addUser(name, email, uid, created_at);
                                gotoLogin();
                            } else {
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
                Log.e(TAG, "Registration Error: " + error.getMessage());
                showMessage(error.getMessage());
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "register");
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showMessage(String msg) {
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    public void gotoLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
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
}

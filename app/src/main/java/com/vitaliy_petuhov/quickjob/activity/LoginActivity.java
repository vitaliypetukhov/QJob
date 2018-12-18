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

public class LoginActivity extends Activity {

    public static String TAG = LoginActivity.class.getSimpleName();

    private EditText emailFT, passwordFT;
    private Button loginButton, notRegButton, newpassButton;
    private ProgressDialog dialog;
    private SessionManager session;
    private SQLiteHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.getNewPass);
        notRegButton = (Button) findViewById(R.id.goToLogin);
        newpassButton = (Button) findViewById(R.id.idToNewPassword);
        emailFT = (EditText) findViewById(R.id.idEmailL);
        passwordFT = (EditText) findViewById(R.id.EmailNewPass);

        final EmailValidator emailValidator = new EmailValidator();

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        if(session.isLoggedIn()){
            Intent i = new Intent(getApplicationContext(), MenuActivity.class);//LoginRegisterActivity.class);
            startActivity(i);
            finish();
        }

        newpassButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), NewPassword.class);
                startActivity(i);
            }
        });

        loginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = emailFT.getText().toString();
                String password = passwordFT.getText().toString();

                if (!email.isEmpty() && !password.isEmpty())
                {
                    if (emailValidator.validate(email) == true)
                    {
                        checkLogin(email, password);
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
        notRegButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    protected void checkLogin(final String email, final String password) {
        String tag_string_reg = "login_request";

        dialog.setMessage("Авторизация, пожалуйста подождите !");
        showDialog();
        StringRequest strReq = new StringRequest(Method.POST, AppConfig.LOGIN_URL,
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Login Response: " + response.toString());
                        hideDialog();
                        try{
                            JSONObject jsb = new JSONObject(response);
                            boolean error = jsb.getBoolean("error");
                            if(!error){

                               String uid = jsb.getString("uid");

                                JSONObject user = jsb.getJSONObject("user");
                                String name = user.getString("name");
                                String email = user.getString("email");
                                String created_at = user.getString("created_at");
                                db.addUser(name, email, uid, created_at);

                                session.setLogin(true);
                                Intent i = new Intent(LoginActivity.this, MenuActivity.class);//LoginRegisterActivity.class);
                                startActivity(i);
                                finish();
                            }else{
                                String errorMsg = jsb.getString("error_msg");
                                showMessage(errorMsg);
                            }
                        }
                        catch(JSONException je){
                            je.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage());
                showMessage(error.getMessage());
                hideDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "login");
                params.put("email", email);
                //params.put("login_name", log_name);
                params.put("password",password);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_reg);

    }

    private void showMessage(String msg) {
        Toast toast = Toast.makeText(getApplicationContext(),
                msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    private void showDialog(){
        if(!dialog.isShowing()){
            dialog.show();
        }
    }

    private void hideDialog(){
        if(dialog.isShowing()){
            dialog.dismiss();
        }
    }

}
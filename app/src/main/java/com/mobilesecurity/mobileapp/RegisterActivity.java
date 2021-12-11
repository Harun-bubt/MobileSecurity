package com.mobilesecurity.mobileapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mobilesecurity.mobileapp.base.AppController;
import com.mobilesecurity.mobileapp.db.SQLiteHandler;
import com.mobilesecurity.mobileapp.db.SessionManager;
import com.mobilesecurity.mobileapp.utils.MyOnItemSelectedListener;

import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private TextView btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
	private EditText inputcity;
    private EditText inputimei;
	private Spinner inputcountry;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
	 private Spinner spinner2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        addListenerOnSpinnerItemSelection();
        inputFullName = (EditText) findViewById(R.id.name);
		inputimei = (EditText) findViewById(R.id.imei);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
		inputcity = (EditText) findViewById(R.id.city);
		inputcountry = (Spinner) findViewById(R.id.spinner2);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (TextView) findViewById(R.id.btnLinkToLoginScreen);

		//inputimei.setText(getImei());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        if (session.isLoggedIn()) {
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
				String imei = inputimei.getText().toString().trim();
                String city = inputcity.getText().toString().trim();
				String country = inputcountry.getSelectedItem().toString().trim();
                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !imei.isEmpty() && !city.isEmpty() && !country.isEmpty()) {
                    registerUser(name, email, password, imei, city, country);
                } else {
                    FancyToast.makeText(getApplicationContext(),"Please enter your details!",FancyToast.LENGTH_LONG,FancyToast.INFO,R.mipmap.ic_launcher).show();
                   // Toast.makeText(getApplicationContext(),
                            //"Please enter your details!", Toast.LENGTH_LONG)
                          //  .show();
                }
            }
        });
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }



    public void addListenerOnSpinnerItemSelection() {
	spinner2 = (Spinner) findViewById(R.id.spinner2);
	spinner2.setOnItemSelectedListener(new MyOnItemSelectedListener());
  }



	
	//private String getImei() {
	//	//TelephonyManager telephonyManager = (TelephonyManager) this
	//		//	.getSystemService(Context.TELEPHONY_SERVICE);
//		return telephonyManager.getDeviceId();
	//}
	
    private void registerUser(final String name, final String email,
                              final String password, final String imei, final String city, final String country) {

        String tag_string_req = "req_register";

        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Registering ...");
        progressDialog.show();

        StringRequest strReq = new StringRequest(Method.POST,
                Constant.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                progressDialog.dismiss();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        String uid = jObj.getString("uid");
                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user
                                .getString("created_at");
					    String imei = user.getString("imei");
						String city = user.getString("city");
                        String country = user.getString("country");

                        db.addUser(name, email, uid, created_at, imei, city, country);

                        FancyToast.makeText(getApplicationContext(),"User successfully registered. Try login now!",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,R.mipmap.ic_launcher).show();
                        //Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(
                                RegisterActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        FancyToast.makeText(getApplicationContext(),
                                errorMsg,FancyToast.LENGTH_LONG,FancyToast.ERROR,R.mipmap.ic_launcher).show();
                        //Toast.makeText(getApplicationContext(),
                                //errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                FancyToast.makeText(getApplicationContext(),
                        error.getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,R.mipmap.ic_launcher).show();
                //Toast.makeText(getApplicationContext(),
                        //error.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
				params.put("imei", imei);
				params.put("city", city);
                params.put("country", country);

                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}

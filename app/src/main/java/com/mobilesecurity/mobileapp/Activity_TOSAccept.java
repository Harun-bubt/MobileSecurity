package com.mobilesecurity.mobileapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.mobilesecurity.mobileapp.base.BaseActivity;

import static com.mobilesecurity.mobileapp.Constant.PrivacyPolicyUrl;

public class Activity_TOSAccept extends BaseActivity {
    private WebView webView;
    private SharedPreferences saveFirstTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_permissionaccept);

        boolean strPref = false;
        SharedPreferences shf = this.getSharedPreferences("config", MODE_PRIVATE);
        strPref = shf.getBoolean("True", false);

        if(strPref)
        {
            Intent i = new Intent(this,SplishActivity.class);
            startActivity(i);
            finish();


        }
        else {

        }
       // String PREFS_NAME = "first_hence";

       // SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

      //  if (settings.getBoolean("first_time", true)) {
       //     settings.edit().putBoolean("first_time", false).commit();
      //      Intent i = new Intent(this,SplishActivity.class);
       //     startActivity(i);
       //     finish();
      //  } else {

          //  }

        TextView enduser = (TextView) findViewById(R.id.textView13);
        enduser.setPaintFlags(enduser.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        enduser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(Activity_TOSAccept.this);
                alert.setTitle("End User License Agreement");

                WebView wv = new WebView(Activity_TOSAccept.this);
                wv.loadUrl("file:///android_asset/LicenceAgreement.htm");
                wv.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);

                        return true;
                    }
                });

                alert.setView(wv);
                alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                alert.show();

               // showtos();
            }
        });

        TextView tos = (TextView) findViewById(R.id.textView15);
        tos.setPaintFlags(tos.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tos.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                privacypolicy();
            }
        });

        Button getstarted = (Button) findViewById(R.id.button2);
        getstarted.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent activityChangeIntent = new Intent(Activity_TOSAccept.this, SplishActivity.class);
                saveFirstTime = getSharedPreferences("config", MODE_PRIVATE);
                saveFirstTime.edit().putBoolean("True", true).apply();
                //String PREFS_NAME = "first_hence";
                //SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
               // settings.edit().putBoolean("first_time", false).commit();
                startActivity(activityChangeIntent);
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void privacypolicy()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Privacy Policy");

        WebView wv = new WebView(this);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadUrl(PrivacyPolicyUrl);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.getSettings().setJavaScriptEnabled(true);
                view.loadUrl(url);

                return true;
            }
        });

        alert.setView(wv);
        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.show();
    }
    public void showtos()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("End User License Agreement");

        WebView wv = new WebView(this);
        wv.loadUrl("file:///android_asset/LicenceAgreement.htm");
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return true;
            }
        });

        alert.setView(wv);
        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

}


package com.mobilesecurity.mobileapp.applocker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilesecurity.mobileapp.BuildConfig;
import com.mobilesecurity.mobileapp.Internetconnection;
import com.amnix.materiallockview.MaterialLockView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mobilesecurity.mobileapp.R;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import static com.mobilesecurity.mobileapp.Constant.UpgradePro;


public class EnterPatternLock extends AppCompatActivity {
    public String getPattern() {
        String PACKAGE_NAME = BuildConfig.APPLICATION_ID;
        String Shine = "/data/data/";
        String Url = "/files/pattern";
        File file = new File(Shine+PACKAGE_NAME+Url);
        //File file = new File("/data/data/com.abc.security/files/pattern");
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        } catch (IOException e) {
            Log.d("okuma hatası", "no 1");
        }
        return text.toString();
    }
    MaterialLockView materialLockView;
    Context context;
    RelativeLayout re;
    private AdView mAdMobAdView;
    String app = null;
    TextView appNamePattern;
    ImageView appIconPattern;
    SaveState saveState;
    String main;
    boolean abc;
    SaveLogs saveLogs;
    int WRONG_PATTERN_COUNTER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_pattern_lock);
        context = EnterPatternLock.this;
        Intent iin = getIntent();
        procheck();
        Bundle b = iin.getExtras();
        app = b.getString("app");
        try {
            main = b.getString("main");
            abc = main.equals("true");
        } catch (Exception e) {

        }
        saveLogs = new SaveLogs(this);

        Random drftgyhj = new Random();
        int abcdefg = drftgyhj.nextInt(5) + 1;

        appIconPattern = (ImageView) findViewById(R.id.appIconPattern);
        appNamePattern = (TextView) findViewById(R.id.appNamePattern);
        saveState = new SaveState(this);
        appIconPattern.setImageDrawable(getAppIcon(app));
        appNamePattern.setText(getAppName(app));
        materialLockView = (MaterialLockView) findViewById(R.id.enterPatternLockView);
        re = (RelativeLayout) findViewById(R.id.re);
        materialLockView.setOnPatternListener(new MaterialLockView.OnPatternListener() {
            @Override
            public void onPatternStart() {
                super.onPatternStart();
            }

            @Override
            public void onPatternDetected(List<MaterialLockView.Cell> pattern, String SimplePattern) {
                super.onPatternDetected(pattern, SimplePattern);
                if (getPattern().equals(SimplePattern)) {
                    if (abc) {
                        saveLogs.saveLogs(getString(R.string.app_name),true);
                        startMain();
                    } else {
                        saveState.saveState("false");
                        saveLogs.saveLogs(app,true);
                        startApp(app);
                        finish();
                    }
                } else {
                    materialLockView.clearPattern();
                    saveLogs.saveLogs(app,false);
                    WRONG_PATTERN_COUNTER = WRONG_PATTERN_COUNTER + 1;
                    if (WRONG_PATTERN_COUNTER == 3) {
                        finish();
                    }
                    Toast.makeText(context, getString(R.string.wrong_pattern), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    @Override
    public void onDestroy() {

        super.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();

    }
    private void startMain() {
        Intent i = new Intent(EnterPatternLock.this, MainActivity.class);
        startActivity(i);
        finish();
    }
    @Override
    public void onBackPressed() {

    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!hasFocus) {
            Log.d("Focus debug", "Lost focus !");

        }
    }

    @Override
    protected void onPause() {

        super.onPause();
    }
    private Drawable getAppIcon(String packagename) {
        Drawable icon = null;
        try {
            icon = getPackageManager().getApplicationIcon(packagename);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return icon;
    }

    private void startApp(String packagename) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packagename);
        if (launchIntent != null) {
            startActivity(launchIntent);
        }
    }
    private String getAppName(String packagename) {
        PackageManager packageManager = getApplicationContext().getPackageManager();
        String appName = null;
        try {
            appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packagename, PackageManager.GET_META_DATA));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;
    }


    private void admobbanner(){
        mAdMobAdView = ((AdView) findViewById(R.id.ads1));
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdMobAdView.loadAd(adRequest);
    }
    private void internet()
    {
        if (Internetconnection.checkConnection(this)) {
            admobbanner();


        } else {
            AdView ads1 = (AdView) findViewById(R.id.ads1);
            ads1.setVisibility(View.GONE);
        }
    }

    public void procheck()
    {
        boolean strPref = false;
        SharedPreferences shf = this.getSharedPreferences("config", MODE_PRIVATE);
        strPref = shf.getBoolean(UpgradePro, false);

        if(strPref)
        {
            AdView ads1 = (AdView) findViewById(R.id.ads1);
            ads1.setVisibility(View.GONE);


        }
        else {

            if (Internetconnection.checkConnection(this)) {


                mAdMobAdView = ((AdView) findViewById(R.id.ads1));
                AdRequest adRequest = new AdRequest.Builder()
                        .build();
                mAdMobAdView.loadAd(adRequest);


            } else {
                AdView ads1 = (AdView) findViewById(R.id.ads1);
                ads1.setVisibility(View.GONE);
            }


        }
    }

}

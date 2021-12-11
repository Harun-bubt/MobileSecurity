package com.mobilesecurity.mobileapp.applocker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilesecurity.mobileapp.Internetconnection;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.mobilesecurity.mobileapp.R;

import java.util.Random;

import static com.mobilesecurity.mobileapp.Constant.UpgradePro;

public class EnterNormalPIN extends AppCompatActivity {

    Button one,two,three,four,five,six,seven,eight,nine,zero,delete,done;
    EditText pin_enter;
    String a = "";
    SaveState saveState;
    String app;
    String main;
    boolean abc;
    private AdView mAdView;
    ImageView appIconIv;
    TextView appNameTv;
    SaveLogs saveLogs;
    InterstitialAd mInterstitialAd;
    private AdView mAdMobAdView;

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.enter_normal_pin);
        saveState = new SaveState(this);
        procheck();
        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        saveLogs = new SaveLogs(this);
        app = b.getString("app");
        try {
            main = b.getString("main");
            abc = main.equals("true");
        }catch (Exception e){

        }
        Random drftgyhj = new Random();
        int abcdefg = drftgyhj.nextInt(5) + 1;

        pin_enter = (EditText) findViewById(R.id.pin_enter);
        appIconIv = (ImageView) findViewById(R.id.appIconPIN);
        appNameTv = (TextView) findViewById(R.id.appNameTv);
        one = (Button) findViewById(R.id.one);
        two = (Button) findViewById(R.id.two);
        three = (Button) findViewById(R.id.three);
        four = (Button) findViewById(R.id.four);
        five = (Button) findViewById(R.id.five);
        six = (Button) findViewById(R.id.six);
        seven = (Button) findViewById(R.id.seven);
        eight = (Button) findViewById(R.id.eight);
        nine = (Button) findViewById(R.id.nine);
        zero = (Button) findViewById(R.id.zero);
        delete = (Button) findViewById(R.id.delete);
        done = (Button) findViewById(R.id.done);

        appIconIv.setImageDrawable(getAppIcon(app));
        appNameTv.setText(getAppName(app));

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a = pin_enter.getText().toString();
                pin_enter.setText(a+"1");
            }
        });
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a = pin_enter.getText().toString();
                pin_enter.setText(a+"2");
            }
        });
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a = pin_enter.getText().toString();
                pin_enter.setText(a+"3");
            }
        });
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a = pin_enter.getText().toString();
                pin_enter.setText(a+"4");
            }
        });
        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a = pin_enter.getText().toString();
                pin_enter.setText(a+"5");
            }
        });
        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a = pin_enter.getText().toString();
                pin_enter.setText(a+"6");
            }
        });
        seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a = pin_enter.getText().toString();
                pin_enter.setText(a+"7");
            }
        });
        eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a = pin_enter.getText().toString();
                pin_enter.setText(a+"8");
            }
        });
        nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a = pin_enter.getText().toString();
                pin_enter.setText(a+"9");
            }
        });
        zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a = pin_enter.getText().toString();
                pin_enter.setText(a+"0");
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a = pin_enter.getText().toString();
                String[] b = a.split("");
                String c = a.replace(b[b.length-1],"");
                pin_enter.setText(c);
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a = pin_enter.getText().toString();
                if (abc){
                    if (PINManager.getPIN().equals(a)){
                        saveLogs.saveLogs(getString(R.string.app_name),true);
                        startMain();
                    }
                }else {
                    if (PINManager.getPIN().equals(a)){
                        saveState.saveState("false");
                        saveLogs.saveLogs(app,true);
                        startApp(app);
                        finish();
                    }else {
                        Toast.makeText(EnterNormalPIN.this,getString(R.string.wrong_pin),Toast.LENGTH_LONG).show();
                        saveLogs.saveLogs(app,false);
                    }
                }
            }
        });
    }
    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
    private Drawable getAppIcon(String packagename){
        Drawable icon = null;
        try {
            icon = getPackageManager().getApplicationIcon(packagename);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return icon;
    }
    private void startApp(String packagename){
        if (abc){
            startMain();
        }else {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packagename);
            if (launchIntent != null) {
                startActivity(launchIntent);
            }
        }
    }
    private String getAppName(String packagename){
        PackageManager packageManager= getApplicationContext().getPackageManager();
        String appName = null;
        try {
            appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packagename, PackageManager.GET_META_DATA));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;
    }
    private void startMain(){
        Intent i = new Intent(EnterNormalPIN.this,MainActivity.class);
        startActivity(i);
        finish();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

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
            AdView ads1 = (AdView)findViewById(R.id.ads1);
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


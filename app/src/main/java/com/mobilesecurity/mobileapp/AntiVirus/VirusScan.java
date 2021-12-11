package com.mobilesecurity.mobileapp.AntiVirus;

import android.content.Intent;
import android.app.ActionBar;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobilesecurity.mobileapp.Internetconnection;
import com.mobilesecurity.mobileapp.Log.L;
import com.mobilesecurity.mobileapp.R;
import com.mobilesecurity.mobileapp.base.BaseSwipeBackActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;
import com.ypyproductions.utils.ApplicationUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.mobilesecurity.mobileapp.Constant.UpgradePro;

//import butterknife.InjectView;
public class VirusScan extends BaseSwipeBackActivity implements View.OnClickListener {
    ActionBar ab;



    private TextView mlastTimeTv;
    private TextView apptime;
    private TextView fulltime;
    private TextView sdtime;
    private SharedPreferences mSp;
    private SharedPreferences mSppp;
    private SharedPreferences FullScan;
    private SharedPreferences SDScan;
    private L log;
    private AdView mAdMobAdView;
    InterstitialAd mInterstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_virus_scan);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        checkshowint();
        mSp = getSharedPreferences("config", MODE_PRIVATE);
        mSppp = getSharedPreferences("config", MODE_PRIVATE);
        FullScan = getSharedPreferences("config", MODE_PRIVATE);
        SDScan = getSharedPreferences("config", MODE_PRIVATE);


        log = new L();
        copyDB("antivirus.db");
        initView();
        procheck();
       ImageView image1 = ((ImageView) findViewById(R.id.image1));
        image1.setImageResource(R.drawable.fullscann);
        ImageView image2 = ((ImageView) findViewById(R.id.image2));
        image2.setImageResource(R.drawable.card31);
        ImageView image3 = ((ImageView) findViewById(R.id.image3));
        image3.setImageResource(R.drawable.appicon);
        ImageView pharid1 = ((ImageView) findViewById(R.id.b1));
        pharid1.setImageResource(R.drawable.entericon);
        ImageView pharid2 = ((ImageView) findViewById(R.id.b2));
        pharid2.setImageResource(R.drawable.entericon);
        ImageView pharid3 = ((ImageView) findViewById(R.id.b4));
        pharid3.setImageResource(R.drawable.entericon);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void admobbanner(){
        mAdMobAdView = (AdView) findViewById(R.id.admob_adview);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdMobAdView.loadAd(adRequest);
    }




    private void admobint() {

        if (ApplicationUtils.isOnline(this)) {
            boolean b = true;
            if (b) {
                mInterstitial = new InterstitialAd(this);
                mInterstitial.setAdUnitId(getString(R.string.interstitial_ad_unit));
                mInterstitial.loadAd(new AdRequest.Builder().build());

                mInterstitial.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        // TODO Auto-generated method stub
                        super.onAdLoaded();
                        if (mInterstitial.isLoaded()) {
                            mInterstitial.show();
                        }
                    }
                });

            }
        }
    }

    private void initView() {
        mlastTimeTv = (TextView) findViewById(R.id.tv_lastscantime);
        apptime = (TextView) findViewById(R.id.apptime);
        fulltime = (TextView) findViewById(R.id.fulltime);
        sdtime = (TextView) findViewById(R.id.sdtime);
        findViewById(R.id.card1).setOnClickListener(this);
		 findViewById(R.id.card2).setOnClickListener(this);
		  findViewById(R.id.card3).setOnClickListener(this);
    }
	  

    private void copyDB(final String dbname) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {

                

                    setDir();


                    String dbPath = Environment.getExternalStorageDirectory() + File.separator + "SMOM" + File.separator + "db";
                    log.d(dbPath);
                    File file = new File(dbPath, dbname);

                    log.d(file.getAbsolutePath());
                    if (file.exists() && file.length() > 0) {
                        log.d("The database exists");
                        return;
                    }
                    InputStream is = getAssets().open(dbname);
                
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                        log.d("Write in");
                    }
                    is.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        }.start();

    }

    private void setDir() {
        String path = Environment.getExternalStorageDirectory() + File.separator + "SMOM" + File.separator + "db";
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
  
            case R.id.card1:
                startActivity(new Intent(this, FullscanActivity.class));
                break;
			 case R.id.card2:
                startActivity(new Intent(this, SdcardScanActivity.class));
                break;
			 case R.id.card3:
                startActivity(new Intent(this, AntivirusActivity.class));
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String str = mSp.getString("lastVirusScan", "You have not killed the virus yet");
        mlastTimeTv.setText(str);





        try {
            String strrrr = SDScan.getString("SDScanAgo", "0");

            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            Date past = format.parse(strrrr);

            Date now = new Date();
            long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

            if (seconds < 60) {
                sdtime.setText(seconds + " seconds ago");
            } else if (minutes < 60) {
                sdtime.setText(minutes + " minutes ago");
            } else if (hours < 24) {
                sdtime.setText(hours + " hours ago");
            } else {
                sdtime.setText(days + " days ago");
            }

        }
        catch (Exception j)
        {
            j.printStackTrace();
        }










        try {
            String strrr = FullScan.getString("fullScanAgo", "0");

            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            Date past = format.parse(strrr);

            Date now = new Date();
            long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

            if (seconds < 60) {
                fulltime.setText(seconds + " seconds ago");
            } else if (minutes < 60) {
                fulltime.setText(minutes + " minutes ago");
            } else if (hours < 24) {
                fulltime.setText(hours + " hours ago");
            } else {
                fulltime.setText(days + " days ago");
            }

        }
        catch (Exception j)
        {
            j.printStackTrace();
        }





















        try {
            String strr = mSppp.getString("appScanAgo", "0");

            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            Date past = format.parse(strr);

            Date now = new Date();
            long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

            if (seconds < 60) {
                apptime.setText(seconds + " seconds ago");
            } else if (minutes < 60) {
                apptime.setText(minutes + " minutes ago");
            } else if (hours < 24) {
                apptime.setText(hours + " hours ago");
            } else {
                apptime.setText(days + " days ago");
            }

        }
        catch (Exception j)
        {
            j.printStackTrace();
        }
    }
    private void internet()
    {
        if (Internetconnection.checkConnection(this)) {
            admobbanner();
            admobint();

        } else {
            LinearLayout ads1 = (LinearLayout)findViewById(R.id.ads1);
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
            LinearLayout ads1 = (LinearLayout)findViewById(R.id.ads1);
            ads1.setVisibility(View.GONE);


        }
        else {


            if (Internetconnection.checkConnection(this)) {

                mAdMobAdView = (AdView) findViewById(R.id.admob_adview);
                AdRequest adRequest = new AdRequest.Builder()
                        .build();
                mAdMobAdView.loadAd(adRequest);

                if (ApplicationUtils.isOnline(this)) {
                    boolean b = true;
                    if (b) {
                        mInterstitial = new InterstitialAd(this);
                        mInterstitial.setAdUnitId(getString(R.string.interstitial_ad_unit));
                        mInterstitial.loadAd(new AdRequest.Builder().build());

                        mInterstitial.setAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                // TODO Auto-generated method stub
                                super.onAdLoaded();
                                if (mInterstitial.isLoaded()) {
                                    mInterstitial.show();
                                }
                            }
                        });

                    }
                }

            } else {
                LinearLayout ads1 = (LinearLayout)findViewById(R.id.ads1);
                ads1.setVisibility(View.GONE);
            }




        }
    }

    public void checkshowint()
    {
        boolean strPref = false;
        SharedPreferences shf = this.getSharedPreferences("config", MODE_PRIVATE);
        strPref = shf.getBoolean("virusscanint", false);

        if(strPref)
        {

        }
        else {
            showint();
        }
    }
    public void showint()
    {
        new FancyAlertDialog.Builder(this)
                .setTitle("Virus Scan")
                .setBackgroundColor(Color.parseColor("#4076b2"))  //Don't pass R.color.colorvalue
                .setMessage(getApplicationContext().getResources().getString(R.string.virusscanint))
                .setPositiveBtnBackground(Color.parseColor("#FF4081"))  //Don't pass R.color.colorvalue
                .setPositiveBtnText("Ok")
                .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  //Don't pass R.color.colorvalue
                .setNegativeBtnText("Cancel")
                .setAnimation(com.shashank.sony.fancydialoglib.Animation.POP)
                .isCancellable(false)
                .setIcon(R.drawable.antivirusicon, Icon.Visible)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        SharedPreferences saveint = getSharedPreferences("config", MODE_PRIVATE);
                        saveint.edit().putBoolean("virusscanint", true).apply();

                    }
                })
                .OnNegativeClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        SharedPreferences saveint = getSharedPreferences("config", MODE_PRIVATE);
                        saveint.edit().putBoolean("virusscanint", true).apply();

                    }
                })

                .build();
    }

}

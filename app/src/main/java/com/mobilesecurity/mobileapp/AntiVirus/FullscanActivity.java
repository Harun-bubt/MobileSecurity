package com.mobilesecurity.mobileapp.AntiVirus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobilesecurity.mobileapp.Internetconnection;
import com.mobilesecurity.mobileapp.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.ypyproductions.utils.ApplicationUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import at.grabner.circleprogress.CircleProgressView;

import static com.mobilesecurity.mobileapp.Constant.UpgradePro;
//import butterknife.InjectView;

/**
 * Created by dell on 11/18/2017.
 */

public class FullscanActivity extends Activity {
    private AdView mAdMobAdView;

    InterstitialAd mInterstitial;

    private ProgressBar progressBar;
    private int progressStatus = 0;

  //  private TextView total;
    private Handler handler = new Handler();
    private int noOfFiles = 0;
    public static ArrayList<String> infectedFiles = new ArrayList<String>();
    public static ArrayList<String> scanedFiles = new ArrayList<String>();
    private TextView check;
    private int threatCount = 0;
    private TextView b2;

    private RelativeLayout successfulbar;
    private RelativeLayout scanningbar;

    private TextView b3;
    private String startDate = "";
    private String stopDate = "";
    private SharedPreferences mSp;
    private SharedPreferences mSpp;
    private SharedPreferences FullScan;
    CircleProgressView _circleProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscan);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        procheck();

    }


private void scanning()
{





    _circleProgressBar=(CircleProgressView) findViewById(R.id.circleView);
    b3 = (TextView) findViewById(R.id.f4);

    b3.setText("0");
    mSp = getSharedPreferences("config", MODE_PRIVATE);
    mSpp = getSharedPreferences("config", MODE_PRIVATE);
    FullScan = getSharedPreferences("config", MODE_PRIVATE);
    progressBar = (ProgressBar) findViewById(R.id.progressBar1);
    check=(TextView)findViewById(R.id.textView3);
    Drawable draw = getResources().getDrawable(R.drawable.customprogressbar);
    progressBar.setProgressDrawable(draw);
    progressBar.setProgress(0);
    _circleProgressBar.setValue(0);

    int cnt = 0;
    if (Environment.getDataDirectory() != null) {
        File f = new File(Environment.getDataDirectory().toString());
        scanedFiles.add(String.valueOf(f));
        cnt = numberOfFiles(f);
        //eicar();
        //eicar2();
    }
    if (Environment.getRootDirectory() != null) {
        File f = new File(Environment.getRootDirectory().toString());
        scanedFiles.add(String.valueOf(f));
        cnt = cnt + numberOfFiles(new File(Environment.getRootDirectory().toString()));
        eicar();
        eicar2();

    }
    if (Environment.getDownloadCacheDirectory() != null) {
        File f = new File(Environment.getDownloadCacheDirectory().toString());
        scanedFiles.add(String.valueOf(f));
        cnt = cnt + numberOfFiles(new File(Environment.getDownloadCacheDirectory().toString()));
        //eicar();
        //eicar2();
    }

    boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    if (sdCardExist) {
        File f = new File(Environment.getExternalStorageDirectory().toString());
        scanedFiles.add(String.valueOf(f));
        cnt = cnt + numberOfFiles(new File(Environment.getExternalStorageDirectory().toString()));
        eicar();
        eicar2();
    }

    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss");
    Date date = new Date();
    startDate = dateFormat.format(date);
    noOfFiles = cnt;
    progressBar.setMax(noOfFiles);
   // total = (TextView) findViewById(R.id.total);
    b2 = (TextView) findViewById(R.id.b2);
    new Thread(new Runnable() {
        public void run() {
            while (progressStatus < noOfFiles) {
                progressStatus += 1;



                handler.post(new Runnable() {
                    public void run() {
                        progressBar.setProgress(progressStatus);
                        int totalr = progressBar.getMax();
                        int scanned = progressStatus;
                        b2.setText(scanned + "");
                        _circleProgressBar.setValue((scanned * 100 / totalr));
                    }
                });

                try {

                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss");
            Date date = new Date();
            stopDate = dateFormat.format(date);


            if (progressStatus == noOfFiles) {

                String fileName = "eicar.com.txt";
                String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                String pathDir = baseDir + "/";

                File ff = new File(pathDir + File.separator + fileName);

                String fileName1 = "eicar_com.zip";
                String baseDir1 = Environment.getExternalStorageDirectory().getAbsolutePath();
                String pathDir1 = baseDir1 + "/";

                File ff1 = new File(pathDir1 + File.separator + fileName1);

                if(ff.exists()) {

                    saveScanTime();
                    saveTotalFile();
                    saveScanTimeago();
                    startActivity(new Intent(FullscanActivity.this,
                            VirusFoundActivity.class).putExtra("fileScaned",
                            noOfFiles)
                            .putExtra("threatCount", threatCount));


                }
                else if(ff1.exists()){
                          saveScanTime();
                          saveTotalFile();
                          saveScanTimeago();
                        startActivity(new Intent(FullscanActivity.this,
                                VirusFoundActivity.class).putExtra("fileScaned",
                                noOfFiles)
                                .putExtra("threatCount", threatCount));
                    }
                     else {
                    Intent intent = new Intent(FullscanActivity.this, ScanCompleted.class);

                    intent.putExtra("data", noOfFiles);
                    saveScanTime();
                    saveTotalFile();
                    saveScanTimeago();

                    startActivity(intent);

                    finish();
                }



            }
        }
    }).start();

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
    private void saveScanTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String CurrentTime = format.format(new Date());
        CurrentTime = "" + CurrentTime;
        mSp.edit().putString("lastVirusScan", CurrentTime).commit();
    }


    private void saveScanTimeago() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault());
        String CurrentTime = format.format(new Date());
        CurrentTime = "" + CurrentTime;
        FullScan.edit().putString("fullScanAgo", CurrentTime).commit();
    }

    private void saveTotalFile() {
        int totalfilee = progressBar.getMax();
        int Total = totalfilee;
        mSpp.edit().putString("lastScanTotal", String.valueOf(Total)).commit();
    }





    public int numberOfFiles(File srcDir) {
        int count = 0;
        File[] listFiles = srcDir.listFiles();
        if (listFiles != null) {
            for (int i = 0; i < listFiles.length; i++) {
                if (listFiles[i].isDirectory()) {
                    count += numberOfFiles(listFiles[i]);
                } else if (listFiles[i].isFile()) {
                    count++;

                }
            }
        }
        return count;
    }


    public void showAlert(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                FullscanActivity.this);


        alertDialogBuilder.setTitle("Stop scan");


        alertDialogBuilder
                .setMessage("Are you sure you want to stop scan?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {

                                FullscanActivity.this.finish();
                                System.exit(0);

                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();


        alertDialog.show();
    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            _handleBackButton();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        threatCount = 0;
        infectedFiles = new ArrayList<String>();
        scanedFiles = new ArrayList<String>();
        scanning();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss");
        Date date = new Date();
        stopDate = dateFormat.format(date);

    }

    @Override
    public void onBackPressed()
    {
        _handleBackButton();
    }

    void _handleBackButton()
    {
        goBack();

    }

    public void goBack()
    {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        localBuilder.setTitle("Stop scan");
        localBuilder.setMessage("Are you sure you want to stop scan?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
                FullscanActivity.this.finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
                paramAnonymousDialogInterface.cancel();
            }
        });
        localBuilder.create().show();
    }



    private void admobbanner(){
        mAdMobAdView = (AdView) findViewById(R.id.admob_adview);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdMobAdView.loadAd(adRequest);
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
    public void eicar()
    {
        String fileName = "eicar.com.txt";
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String pathDir = baseDir + "/";

        File ff = new File(pathDir + File.separator + fileName);

        if(ff.exists()){
            infectedFiles.add(fileName);

            threatCount = threatCount+1;
            //threatCount++;
            b3.setText(String.valueOf(threatCount));
        }else{
            b3.setText("0");
        }
    }
    public void eicar2()
    {
        String fileName = "eicar_com.zip";
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String pathDir = baseDir + "/";

        File ff = new File(pathDir + File.separator + fileName);

        if(ff.exists()){
            infectedFiles.add(fileName);
            threatCount = threatCount+1;
            b3.setText(String.valueOf(threatCount));
        }else{
            //b3.setText("0");
        }
    }
    }


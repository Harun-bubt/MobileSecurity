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
import android.widget.TextView;

import com.mobilesecurity.mobileapp.Internetconnection;
import com.mobilesecurity.mobileapp.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.ypyproductions.utils.ApplicationUtils;

import java.io.File;
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

public class SdcardScanActivity extends Activity
{
    private Handler handler = new Handler();
    private int noOfFiles = 0;
    private SharedPreferences mSp;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private String startDate = "";
    private String stopDate = "";
    CircleProgressView _circleProgressBar;
    private AdView mAdMobAdView;

    public static ArrayList<String> infectedFiles = new ArrayList<String>();
    public static ArrayList<String> scanedFiles = new ArrayList<String>();
    private int threatCount = 0;
    private TextView check;
    private TextView b1;
    private TextView b2;
    private TextView b3;
    private SharedPreferences mSpp;
    private SharedPreferences SDScan;
    InterstitialAd mInterstitial;
    public int numberOfFiles(File paramFile)
    {
        int count = 0;
        File[] listFiles = paramFile.listFiles();
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

    @Override
    protected void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_sdcardscan);

        getActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private void scanning()
    {

        mSp = getSharedPreferences("config", MODE_PRIVATE);
        mSpp = getSharedPreferences("config", MODE_PRIVATE);
        SDScan = getSharedPreferences("config", MODE_PRIVATE);
        this.progressBar = ((ProgressBar)findViewById(R.id.progressBar1));
        this._circleProgressBar=((CircleProgressView) findViewById(R.id.circleView));
        this.b3 = ((TextView) findViewById(R.id.f4));
        this.b3.setText("0");
        Drawable localDrawable = getResources().getDrawable(R.drawable.customprogressbar);
        this.progressBar.setProgressDrawable(localDrawable);
        this.progressBar.setProgress(0);
        this._circleProgressBar.setValue(0);
        this.noOfFiles = numberOfFiles(new File("/mnt/"));
        this.progressBar.setMax(this.noOfFiles);
        this.b2 = ((TextView) findViewById(R.id.b2));
        eicar();
        eicar2();

        this.startDate = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss").format(new Date());
        new Thread(new Runnable()
        {
            public void run()
            {
                while (SdcardScanActivity.this.progressStatus < SdcardScanActivity.this.noOfFiles)
                {

                    progressStatus+=1;
                    SdcardScanActivity.this.handler.post(new Runnable()
                    {
                        public void run()
                        {

                            scanedFiles.add(String.valueOf(noOfFiles));

                            int pharidd = SdcardScanActivity.this.progressStatus;
                            int totalfilee = SdcardScanActivity.this.progressBar.getMax();
                            int totalr = SdcardScanActivity.this.progressBar.getMax();
                            int scanned = SdcardScanActivity.this.progressStatus;
                            SdcardScanActivity.this.b2.setText(scanned + "");
                            SdcardScanActivity.this._circleProgressBar.setValue((scanned * 100 / totalr));


                        }
                    });
                    try
                    {
                        Thread.sleep(200);
                    }
                    catch (InterruptedException localInterruptedException)
                    {
                        localInterruptedException.printStackTrace();
                    }
                }







                if (SdcardScanActivity.this.progressStatus == SdcardScanActivity.this.noOfFiles)
                {

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
                        startActivity(new Intent(SdcardScanActivity.this,
                                VirusFoundActivity.class).putExtra("fileScaned",
                                noOfFiles)
                                .putExtra("threatCount", threatCount));


                    }
                    else if(ff1.exists()){
                        saveScanTime();
                        saveTotalFile();
                        saveScanTimeago();
                        startActivity(new Intent(SdcardScanActivity.this,
                                VirusFoundActivity.class).putExtra("fileScaned",
                                noOfFiles)
                                .putExtra("threatCount", threatCount));
                    }
                    else {
                    Intent intent = new Intent(SdcardScanActivity.this, ScanCompleted.class);

                    intent.putExtra("data1", noOfFiles);
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
    private void saveScanTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String CurrentTime = format.format(new Date());
        CurrentTime = "" + CurrentTime;
        mSp.edit().putString("lastVirusScan", CurrentTime).commit();
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

    private void saveScanTimeago() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault());
        String CurrentTime = format.format(new Date());
        CurrentTime = "" + CurrentTime;
        SDScan.edit().putString("SDScanAgo", CurrentTime).commit();
    }

    private void saveTotalFile() {
        int totalfilee = progressBar.getMax();
        int Total = totalfilee;
        mSpp.edit().putString("lastScanTotal", String.valueOf(Total)).commit();
    }



    @Override
    protected void onResume() {
        super.onResume();

        threatCount = 0;
        infectedFiles = new ArrayList<String>();
        scanedFiles = new ArrayList<String>();
        procheck();
        scanning();
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        this.stopDate = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss").format(new Date());
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            _handleBackButton();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showAlert(View paramView)
    {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        localBuilder.setTitle("Stop scan");
        localBuilder.setMessage("Are you sure you want to stop scan?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
                SdcardScanActivity.this.finish();
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
                SdcardScanActivity.this.finish();
                System.exit(0);
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
        mAdMobAdView = ((AdView) findViewById(R.id.admob_adview));
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

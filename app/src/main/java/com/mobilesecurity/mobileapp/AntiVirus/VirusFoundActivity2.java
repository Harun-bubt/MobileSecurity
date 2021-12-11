package com.mobilesecurity.mobileapp.AntiVirus;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilesecurity.mobileapp.Internetconnection;
import com.mobilesecurity.mobileapp.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;

import at.grabner.circleprogress.CircleProgressView;

import static com.mobilesecurity.mobileapp.Constant.UpgradePro;

/**
 * Created by dell on 12/18/2017.
 */

public class VirusFoundActivity2 extends Activity {
    private AdView mAdMobAdView;
    CircleProgressView _circleProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscanresult);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        procheck();
        TextView tScan = (TextView) findViewById(R.id.tv_scaned);
        TextView tThread = (TextView) findViewById(R.id.tv_thread);

        _circleProgressBar=(CircleProgressView) findViewById(R.id.circleView);
        _circleProgressBar.setValue(100);

        if (getIntent().getExtras() != null) {

            int fileScaned = getIntent().getExtras().getInt("fileScaned");
            int threatCount = getIntent().getExtras().getInt("threatCount");

            tScan.setText("" + fileScaned);
            tThread.setText("" + threatCount);
        }

        findViewById(R.id.btnDelete).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub

                        if (!SdcardScanActivity.infectedFiles.isEmpty()) {

                            for (int i = 0; i < SdcardScanActivity.infectedFiles
                                    .size(); i++) {

                                File file = new File(SdcardScanActivity.infectedFiles
                                        .get(i));

                                boolean b = deleteDir(file);
                                if (b) {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            SdcardScanActivity.infectedFiles.get(i)
                                                    + " delete successfully.",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            SdcardScanActivity.infectedFiles.get(i)
                                                    + " can not delete.",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {

                            Toast.makeText(getApplicationContext(),
                                    "Virus not found.", Toast.LENGTH_LONG)
                                    .show();
                        }

                    }
                });

        findViewById(R.id.btnLogs).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub

                        ListView lv = (ListView) findViewById(R.id.lv_logs);

                        if (!SdcardScanActivity.scanedFiles.isEmpty()) {

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                    VirusFoundActivity2.this,
                                    android.R.layout.simple_list_item_1,
                                    FullscanActivity.infectedFiles);

                            lv.setAdapter(adapter);
                        } else {

                            Toast.makeText(getApplicationContext(),
                                    "Scan result not found.", Toast.LENGTH_LONG)
                                    .show();
                        }

                    }
                });
    }

    public static boolean deleteDir(File dir) {

        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, VirusScan.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
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


            } else {
                LinearLayout ads1 = (LinearLayout)findViewById(R.id.ads1);
                ads1.setVisibility(View.GONE);
            }




        }
    }

}


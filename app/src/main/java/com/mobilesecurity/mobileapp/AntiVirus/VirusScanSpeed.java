package com.mobilesecurity.mobileapp.AntiVirus;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.view.MenuItem;

import com.mobilesecurity.mobileapp.adapter.ScanVirusAdapter;
import com.mobilesecurity.mobileapp.R;
import com.mobilesecurity.mobileapp.utils.MD5Utils;
import com.mobilesecurity.mobileapp.base.BaseSwipeBackActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VirusScanSpeed extends BaseSwipeBackActivity implements View.OnClickListener {

    public static final int SCAN_BEGIN = 100;
    public static final int SCANNING = 101;
    public static final int SCAN_FINISH = 102;
    private int total;
    private int progress;
    private TextView mprogressTv;
    private PackageManager pm;
    private boolean flag;
    private boolean isStop;
    private TextView mSanAppTv;
    private Button mCancleBtn;
    private ImageView mScanningIcon;
    private RotateAnimation rani;
    private ListView mScanListView;
    private List<ScanAppInfo> scanAppInfos = new ArrayList<ScanAppInfo>();
    private SharedPreferences mSp;
    private ScanVirusAdapter adapter;
    private SharedPreferences mSpp;
    private SharedPreferences mSppp;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCAN_BEGIN:
                    mSanAppTv.setText("Initialize antivirus engine ...");
                    break;
                case SCANNING:
                    ScanAppInfo info = (ScanAppInfo) msg.obj;
                    mSanAppTv.setText("Scanning:" + info.appName);
                    int speed = msg.arg1;
                    mprogressTv.setText((speed * 100 / total) + "%");
                    scanAppInfos.add(info);
                    adapter.notifyDataSetChanged();
                    mScanListView.setSelection(scanAppInfos.size());
                    break;
                case SCAN_FINISH:
                    mSanAppTv.setText("Scan done!");
                    mScanningIcon.setBackgroundResource(R.drawable.scan_complete);
					mCancleBtn.setText("Scan Completed");
                    saveScanTime();
                    saveTotalFile();
                    saveScanTimeago();
                    break;
            }
        }
    };

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
        mSppp.edit().putString("appScanAgo", CurrentTime).commit();
    }

    private void saveTotalFile() {
        int totalfilee = total;
        int Total = totalfilee;
        mSpp.edit().putString("lastScanTotal", String.valueOf(Total)).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virus_scan_speed);
		getActionBar().setDisplayHomeAsUpEnabled(true);
        pm = getPackageManager();
        mSp = getSharedPreferences("config", MODE_PRIVATE);
        mSpp = getSharedPreferences("config", MODE_PRIVATE);
        mSppp = getSharedPreferences("config", MODE_PRIVATE);
        initView();
        scanVirus();
    }
	
	    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void scanVirus() {
        flag = true;
        isStop = false;
        progress = 0;
        scanAppInfos.clear();
        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = Message.obtain();
                msg.what = SCAN_BEGIN;
                mHandler.sendMessage(msg);
                List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
                total = installedPackages.size();
                for (PackageInfo info : installedPackages) {

                    if (!flag) {
                        isStop = true;
                        return;
                    }
                    String apkpath = info.applicationInfo.sourceDir;
                   
                    String md5info = MD5Utils.getFileMd5(apkpath);
                    String result = AntiVirusDao.checkVirus(md5info);
                    msg = Message.obtain();
                    msg.what = SCANNING;
                    ScanAppInfo scanAppInfo = new ScanAppInfo();
                    if (result == null) {
                        scanAppInfo.description = "Scan safe";
                        scanAppInfo.isVirus = false;
                    } else {
                        scanAppInfo.description = result;
                        scanAppInfo.isVirus = true;
                    }
                    progress++;
                    scanAppInfo.packagename = info.packageName;
                    scanAppInfo.appName = info.applicationInfo.loadLabel(pm).toString();
                    scanAppInfo.appicon = info.applicationInfo.loadIcon(pm);
                    msg.obj = scanAppInfo;
                    msg.arg1 = progress;
                    mHandler.sendMessage(msg);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                msg = Message.obtain();
                msg.what = SCAN_FINISH;
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    private void initView() {
        mprogressTv = (TextView) findViewById(R.id.tv_scanprogress);
        mSanAppTv = (TextView) findViewById(R.id.tv_scansapp);
        mCancleBtn = (Button) findViewById(R.id.btn_cancelscan);
        mCancleBtn.setOnClickListener(this);
        mScanListView = (ListView) findViewById(R.id.lv_scanapps);
        adapter = new ScanVirusAdapter(scanAppInfos, this);
        mScanListView.setAdapter(adapter);
        mScanningIcon = (ImageView) findViewById(R.id.imgv_scanningicon);
        startAnim();

    }

    private void startAnim() {
        if (rani == null) {
            rani = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        }
        rani.setRepeatCount(Animation.INFINITE);
        rani.setDuration(2000);
        mScanningIcon.startAnimation(rani);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            
            case R.id.btn_cancelscan:
                if (progress == total & progress > 0) {
                  
                    finish();
                } else if (progress > 0 & progress < total & isStop == false) {
                    mScanningIcon.clearAnimation();
                  
                    flag = false;
					
					mCancleBtn.setText("Restart Scan");
                } else if (isStop) {
                    startAnim();
                    scanVirus();
					mCancleBtn.setText("Cancel Scan");
                }
                break;
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag = false;

    }
}

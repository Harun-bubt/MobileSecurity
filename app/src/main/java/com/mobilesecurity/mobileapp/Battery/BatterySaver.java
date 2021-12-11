package com.mobilesecurity.mobileapp.Battery;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
//import android.support.v7.app.AlertDialog;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobilesecurity.mobileapp.Internetconnection;
import com.mobilesecurity.mobileapp.R;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;
import com.ypyproductions.utils.ApplicationUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.media.AudioManager.RINGER_MODE_NORMAL;
import static android.media.AudioManager.RINGER_MODE_SILENT;
import static android.media.AudioManager.RINGER_MODE_VIBRATE;
import static com.mobilesecurity.mobileapp.Constant.UpgradePro;

/**
 * Created by dell on 12/12/2017.
 */

public class BatterySaver extends Activity implements View.OnClickListener {


    ImageView Tools_WiFi, Tools_Rotate, Tools_Brightness, Tools_Bluetooth, Tools_Timeout, Tools_Mode;
    Button PowerSavingMode;
    BluetoothAdapter AdapterForBluetooth;
    CardView CardViewTools, batteryDetail;
    TextView TxtLevel, TxtVoltage, TxtTemperature;
    private AdView mAdMobAdView;
    InterstitialAd mInterstitial;
    public BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            TxtTemperature.setText(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10 + Character.toString((char) 176) + " C");
            TxtVoltage.setText((float) intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) / (float) 1000 + Character.toString((char) 176) + " V");
            TxtLevel.setText(Integer.toString(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)) + " %");
        }
    };
    Intent i;
    AudioManager am;
    Integer Issue = 0;
    BroadcastReceiver batteryLevelReceiver;
    AlertDialog.Builder writesetting_dialog;
    private ArcProgress arcProgress;
    private Timer timer;
    private int brightness;
    private int rotate;
    private int timeout;
    private ContentResolver cResolver;
    private Window window;
    Integer Profile;

    private static boolean isAirplaneModeOn(Context context) {

        return Settings.System.getInt(context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) != 0;

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batterysaver);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        checkshowint();
        procheck();
        setupdialog();
        getBaseContext().getApplicationContext().sendBroadcast(new Intent("BatteryReciver"));

        arcProgress = (ArcProgress) findViewById(R.id.arc_progress);
        TxtLevel = (TextView) findViewById(R.id.TxtLevel);
        TxtVoltage = (TextView) findViewById(R.id.TxtVoltage);
        TxtTemperature = (TextView) findViewById(R.id.TxtTemperature);
        Tools_WiFi = (ImageView) findViewById(R.id.tool_wifi);
        Tools_Rotate = (ImageView) findViewById(R.id.tool_rotate);
        Tools_Brightness = (ImageView) findViewById(R.id.tool_brightness);
        Tools_Bluetooth = (ImageView) findViewById(R.id.tool_bluetooth);
        Tools_Timeout = (ImageView) findViewById(R.id.tool_timeout);
        Tools_Mode = (ImageView) findViewById(R.id.tool_mode);
        PowerSavingMode = (Button) findViewById(R.id.PowerSavingMode);
        CardViewTools = (CardView) findViewById(R.id.CardViewTools);
        batteryDetail = (CardView) findViewById(R.id.batteryDetail);


        registerReceiver(this.batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));


        cResolver = getContentResolver();
        window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) {
                try {
                    Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                    brightness = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
                    rotate = Settings.System.getInt(cResolver, Settings.System.ACCELEROMETER_ROTATION);
                    timeout = Settings.System.getInt(cResolver, Settings.System.SCREEN_OFF_TIMEOUT);

                    if (timeout > 40000) {
                        setTimeout(3);
                        timeout = 40000;
                    }

                    CheckOnAndOff();

                } catch (Settings.SettingNotFoundException e) {
                    Log.e("Error", "Cannot access system brightness");
                    e.printStackTrace();
                }
            } else {
                NotificationManager notificationManager =
                        (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

                writesetting_dialog.show();

            }

        } else {
            try {
                Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                brightness = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
                rotate = Settings.System.getInt(cResolver, Settings.System.ACCELEROMETER_ROTATION);
                timeout = Settings.System.getInt(cResolver, Settings.System.SCREEN_OFF_TIMEOUT);

                Log.e("timeout", "" + timeout);

                if (timeout > 40000) {
                    setTimeout(3);
                    timeout = 40000;
                }

                CheckOnAndOff();

            } catch (Settings.SettingNotFoundException e) {
                Log.e("Error", "Cannot access system brightness");
                e.printStackTrace();
            }
        }

        getBatteryPercentage();
        SetClickListner();
        CheckIntentToolsOnOrOff();


    }


    private void SetClickListner() {
        Tools_WiFi.setOnClickListener(this);
        Tools_Rotate.setOnClickListener(this);
        Tools_Bluetooth.setOnClickListener(this);
        Tools_Brightness.setOnClickListener(this);
        Tools_Mode.setOnClickListener(this);
        Tools_Timeout.setOnClickListener(this);
        batteryDetail.setOnClickListener(this);
        PowerSavingMode.setOnClickListener(this);

    }

    private void getBatteryPercentage() {
        batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int level = -1;
                if (currentLevel >= 0 && scale > 0) {
                    level = (currentLevel * 100) / scale;
                    Log.e("%", "" + level);
                }

                timer = new Timer();
                final int finalLevel = level;
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (arcProgress.getProgress() == finalLevel) {
                                    arcProgress.setProgress(finalLevel);
                                    arcProgress.setBottomText("");
                                    timer.cancel();
                                } else {
                                    arcProgress.setProgress(arcProgress.getProgress() + 1);
                                    arcProgress.setBottomText("");
                                }


                            }
                        });
                    }
                }, 1000, level);
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

    private void CheckOnAndOff() {

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            Tools_WiFi.setImageResource(R.drawable.tool_wifi_on);
        } else {
            Tools_WiFi.setImageResource(R.drawable.tool_wifi_off);
        }

        if (rotate == 1) {
            Tools_Rotate.setImageResource(R.drawable.tool_rotate_autorotate);
        } else {
            Tools_Rotate.setImageResource(R.drawable.tool_rotate_portiat);
        }

        AdapterForBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (AdapterForBluetooth == null) {
        } else {
            if (AdapterForBluetooth.isEnabled()) {
                Tools_Bluetooth.setImageResource(R.drawable.tool_bluetooth_on);
            } else {
                Tools_Bluetooth.setImageResource(R.drawable.tool_bluetooth_off);
            }
        }


        if (brightness > 20) {
            Tools_Brightness.setImageResource(R.drawable.tool_brightness_on);
        } else {
            Tools_Brightness.setImageResource(R.drawable.tool_brightness_off);
        }


        if (timeout == 10000) {
            Tools_Timeout.setImageResource(R.drawable.tool_timeout_ten);
        } else if (timeout == 20000) {
            Tools_Timeout.setImageResource(R.drawable.tool_timeout_twenty);
        } else if (timeout == 30000) {
            Tools_Timeout.setImageResource(R.drawable.tool_timeout_thirty);
        } else if (timeout == 40000) {
            Tools_Timeout.setImageResource(R.drawable.tool_timeout_fourty);
        } else {
            Tools_Timeout.setImageResource(R.drawable.tool_timeout_fourty);
            setTimeout(3);
            timeout = 40000;
        }

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        switch (am.getRingerMode()) {
            case RINGER_MODE_SILENT:
                Tools_Mode.setImageResource(R.drawable.tool_profile_silent);
                Profile = RINGER_MODE_SILENT;
                break;
            case RINGER_MODE_VIBRATE:
                Tools_Mode.setImageResource(R.drawable.tool_profile_vibrate);
                Profile = RINGER_MODE_VIBRATE;
                break;
            case RINGER_MODE_NORMAL:
                Tools_Mode.setImageResource(R.drawable.tool_profile_normal);
                Profile = RINGER_MODE_NORMAL;
                break;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        CheckIntentToolsOnOrOff();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) {
                try {
                    Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                    brightness = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
                    rotate = Settings.System.getInt(cResolver, Settings.System.ACCELEROMETER_ROTATION);
                    timeout = Settings.System.getInt(cResolver, Settings.System.SCREEN_OFF_TIMEOUT);

                    if (timeout > 40000) {
                        setTimeout(3);
                        timeout = 40000;
                    }

                    CheckOnAndOff();
                } catch (Settings.SettingNotFoundException e) {
                    Log.e("Error", "Cannot access system brightness");
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {


            case R.id.tool_wifi:
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(false);
                    Tools_WiFi.setImageResource(R.drawable.tool_wifi_off);
                } else {
                    wifiManager.setWifiEnabled(true);
                    Tools_WiFi.setImageResource(R.drawable.tool_wifi_on);
                }
                break;

            case R.id.tool_rotate:

                if (rotate == 1) {
                    Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, true ? 1 : 0);
                    Tools_Rotate.setImageResource(R.drawable.tool_rotate_autorotate);
                    rotate = 0;
                } else {
                    Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, true ? 0 : 1);
                    Tools_Rotate.setImageResource(R.drawable.tool_rotate_portiat);
                    rotate = 1;
                }


                break;


            case R.id.tool_mode:

                am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                switch (am.getRingerMode()) {
                    case RINGER_MODE_SILENT:
                        Tools_Mode.setImageResource(R.drawable.tool_profile_vibrate);
                        am.setRingerMode(RINGER_MODE_VIBRATE);
                        break;
                    case RINGER_MODE_VIBRATE:
                        Tools_Mode.setImageResource(R.drawable.tool_profile_normal);
                        am.setRingerMode(RINGER_MODE_NORMAL);
                        break;
                    case RINGER_MODE_NORMAL:
                        Tools_Mode.setImageResource(R.drawable.tool_profile_silent);
                        am.setRingerMode(RINGER_MODE_SILENT);
                        break;
                }


                break;

            case R.id.tool_bluetooth:

                AdapterForBluetooth = BluetoothAdapter.getDefaultAdapter();
                if (AdapterForBluetooth == null) {
                } else {
                    if (AdapterForBluetooth.isEnabled()) {
                        Tools_Bluetooth.setImageResource(R.drawable.tool_bluetooth_off);
                        AdapterForBluetooth.disable();
                    } else {
                        Tools_Bluetooth.setImageResource(R.drawable.tool_bluetooth_on);
                        AdapterForBluetooth.enable();
                    }
                }
                break;

            case R.id.tool_brightness:

                if (brightness > 20) {
                    Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, 20);
                    WindowManager.LayoutParams layoutpars = window.getAttributes();
                    layoutpars.screenBrightness = 20;
                    window.setAttributes(layoutpars);
                    Tools_Brightness.setImageResource(R.drawable.tool_brightness_off);
                    brightness = 20;
                } else {
                    Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, 254);
                    WindowManager.LayoutParams layoutpars = window.getAttributes();
                    layoutpars.screenBrightness = 254;
                    window.setAttributes(layoutpars);
                    brightness = 254;
                    Tools_Brightness.setImageResource(R.drawable.tool_brightness_on);
                }

                break;



            case R.id.PowerSavingMode:

                StartPowerSavingMode();

                break;

            case R.id.tool_timeout:

                if (timeout == 10000) {
                    Tools_Timeout.setImageResource(R.drawable.tool_timeout_twenty);
                    setTimeout(1);
                    timeout = 20000;
                } else if (timeout == 20000) {
                    Tools_Timeout.setImageResource(R.drawable.tool_timeout_thirty);
                    setTimeout(2);
                    timeout = 30000;
                } else if (timeout == 30000) {
                    Tools_Timeout.setImageResource(R.drawable.tool_timeout_fourty);
                    setTimeout(3);
                    timeout = 40000;
                } else if (timeout == 40000) {
                    Tools_Timeout.setImageResource(R.drawable.tool_timeout_ten);
                    setTimeout(0);
                    timeout = 10000;
                } else {
                    Tools_Timeout.setImageResource(R.drawable.tool_timeout_fourty);
                    setTimeout(3);
                    timeout = 40000;
                }



        }

    }

    public void CheckIntentToolsOnOrOff() {

        Issue = 0;

        if (isMobileDataEnabled()) {
            Issue = Issue + 1;
        }

        LocationManager ManagerForLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Boolean statusOfLocation = ManagerForLocation.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (statusOfLocation) {
            Issue = Issue + 1;
        }

        if (!isAirplaneModeOn(getApplicationContext())) {
            Issue = Issue + 1;
        }


        if (Issue == 0) {
        }
    }

    public Boolean isMobileDataEnabled() {
        Object connectivityService = getSystemService(Context.CONNECTIVITY_SERVICE);
        ConnectivityManager cm = (ConnectivityManager) connectivityService;

        try {
            Class<?> c = Class.forName(cm.getClass().getName());
            Method m = c.getDeclaredMethod("getMobileDataEnabled");
            m.setAccessible(true);
            return (Boolean) m.invoke(cm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void StartPowerSavingMode() {
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = getPackageManager();
        packages = pm.getInstalledApplications(0);

        ActivityManager mActivityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        for (ApplicationInfo packageInfo : packages) {
            Log.e("pakages", packages + "");
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) continue;
            mActivityManager.killBackgroundProcesses(packageInfo.packageName);
        }

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
        Tools_WiFi.setImageResource(R.drawable.tool_wifi_off);

        AdapterForBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (AdapterForBluetooth == null) {
        } else {
            if (AdapterForBluetooth.isEnabled()) {
                AdapterForBluetooth.disable();
                Tools_Bluetooth.setImageResource(R.drawable.tool_bluetooth_off);
            }
        }


        if (brightness > 20) {
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, 20);
            WindowManager.LayoutParams layoutpars = window.getAttributes();
            layoutpars.screenBrightness = 20;
            window.setAttributes(layoutpars);
            Tools_Brightness.setImageResource(R.drawable.tool_brightness_off);
            brightness = 20;
        }

        Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0); //0 means off, 1 means on
        Tools_Rotate.setImageResource(R.drawable.tool_rotate_portiat);

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setRingerMode(RINGER_MODE_SILENT);
        Tools_Mode.setImageResource(R.drawable.tool_profile_silent);

        setTimeout(0);
        timeout = 10000;
        Tools_Timeout.setImageResource(R.drawable.tool_timeout_ten);

        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);


        PowerSavingMode.startAnimation(fadeInAnimation);
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                PowerSavingMode.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }



    private void setTimeout(int screenOffTimeout) {
        int time;
        switch (screenOffTimeout) {
            case 0:
                time = 10000;
                break;
            case 1:
                time = 20000;
                break;
            case 2:
                time = 30000;
                break;
            case 3:
                time = 40000;
                break;
            default:
                time = -1;
        }
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, time);
    }
    private void setupdialog() {



        writesetting_dialog = new AlertDialog.Builder(BatterySaver.this);
        writesetting_dialog.setTitle("Important!");
        writesetting_dialog.setCancelable(false);
        writesetting_dialog.setMessage("Need write setting permission to set screen brightness, screen timeout, screen rotation, sound profile.");
        writesetting_dialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                });




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
                                 /*  mInterstitial = new InterstitialAd(this);
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
                        }); */

                    }
                }

            } else {
                LinearLayout ads1 = (LinearLayout)findViewById(R.id.ads1);
                ads1.setVisibility(View.GONE);
            }




        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkshowint()
    {
        boolean strPref = false;
        SharedPreferences shf = this.getSharedPreferences("config", MODE_PRIVATE);
        strPref = shf.getBoolean("batterysaverint", false);

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
                .setTitle("Battery Saver")
                .setBackgroundColor(Color.parseColor("#4076b2"))  //Don't pass R.color.colorvalue
                .setMessage(getApplicationContext().getResources().getString(R.string.batterysaverint))
                .setPositiveBtnBackground(Color.parseColor("#FF4081"))  //Don't pass R.color.colorvalue
                .setPositiveBtnText("Ok")
                .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  //Don't pass R.color.colorvalue
                .setNegativeBtnText("Cancel")
                .setAnimation(com.shashank.sony.fancydialoglib.Animation.POP)
                .isCancellable(false)
                .setIcon(R.drawable.batterysavericon, Icon.Visible)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        SharedPreferences saveint = getSharedPreferences("config", MODE_PRIVATE);
                        saveint.edit().putBoolean("batterysaverint", true).apply();

                    }
                })
                .OnNegativeClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        SharedPreferences saveint = getSharedPreferences("config", MODE_PRIVATE);
                        saveint.edit().putBoolean("batterysaverint", true).apply();

                    }
                })

                .build();
    }
}

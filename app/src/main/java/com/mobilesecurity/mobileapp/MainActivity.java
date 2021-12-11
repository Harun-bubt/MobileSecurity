package com.mobilesecurity.mobileapp;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mobilesecurity.mobileapp.AntiVirus.VirusScan;
import com.mobilesecurity.mobileapp.Battery.BatterySaver;
import com.mobilesecurity.mobileapp.WifiSecurity.PreferencesActivity;
import com.mobilesecurity.mobileapp.applocker.EnterActivity;
import com.mobilesecurity.mobileapp.db.SQLiteHandler;
import com.mobilesecurity.mobileapp.db.SessionManager;
import com.github.martarodriguezm.rateme.OnRatingListener;
import com.github.martarodriguezm.rateme.RateMeDialog;
import com.github.martarodriguezm.rateme.RateMeDialogTimer;

import static com.mobilesecurity.mobileapp.Constant.UpgradePro;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private SQLiteHandler db;
    private SessionManager session;
    private AdView mAdMobAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int launchTimes = 8;
        final int installDate = 4;

        RateMeDialogTimer.onStart(this);
        if (RateMeDialogTimer.shouldShowRateDialog(this, installDate, launchTimes)) {
            showCustomRateMeDialog();
        }


        final String PREFS_NAME = "notification";

        SharedPreferences noti = getSharedPreferences(PREFS_NAME, 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(getResources().getColor(R.color.HighRiskColor));


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked},
                new int[]{}
        };

        int[] colors = new int[]{
                Color.parseColor("#747474"),
                Color.parseColor("#007f42"),
                Color.parseColor("#747474"),

        };
        ColorStateList navigationViewColorStateList = new ColorStateList(states, colors);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(navigationViewColorStateList);

        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.header, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.Setting:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }


    @Override
    public void onResume() {
        super.onResume();
        procheck();
        fillData();
    }
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void fillData() {
        Button ClickUpgrade = (Button) findViewById(R.id.upgrade);
        ClickUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                launchupgrade();
            }
        });

        RelativeLayout ClickVirus = (RelativeLayout) findViewById(R.id.card1);
        ClickVirus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                launchantivirus();
            }
        });

        RelativeLayout antiTheft = (RelativeLayout) findViewById(R.id.card2);
        antiTheft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                launchantiTheft();
            }
        });

        RelativeLayout appLocker = (RelativeLayout) findViewById(R.id.card3);
        appLocker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                launchappLocker();
            }
        });

        RelativeLayout wifiSecurity = (RelativeLayout) findViewById(R.id.card4);
        wifiSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                launchwifiSecurity();
            }
        });

        RelativeLayout callBlocker = (RelativeLayout) findViewById(R.id.card5);
        callBlocker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                launchcallBlocker();
            }
        });

        RelativeLayout settingActivity = (RelativeLayout) findViewById(R.id.card6);
        settingActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                launchsettingActivity();
            }
        });
    }

    private void launchupgrade() {

        Intent intent = new Intent(this, ProActivity.class);
        startActivity(intent);
    }

    private void launchantivirus() {

        Intent intent = new Intent(this, VirusScan.class);
        startActivity(intent);
    }
    private void launchantiTheft() {

        Intent intent = new Intent(this, PhoneSafeActivity.class);
        startActivity(intent);
    }
    private void launchappLocker() {

        Intent intent = new Intent(this, EnterActivity.class);
        startActivity(intent);
    }
    private void launchwifiSecurity() {

        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
    }
    private void launchcallBlocker() {

        Intent intent = new Intent(this, com.mobilesecurity.mobileapp.CallBlocker.ui.MainActivity.class);
        startActivity(intent);
    }
    private void launchsettingActivity() {

        Intent intent = new Intent(this, BatterySaver.class);
        startActivity(intent);
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
       }
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_virusscan) {
            startActivity(new Intent(this, VirusScan.class));
        } else if (id == R.id.nav_antitheft) {
            startActivity(new Intent(this, PhoneSafeActivity.class));
        } else if (id == R.id.nav_home) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (id == R.id.nav_applock) {
            startActivity(new Intent(this, EnterActivity.class));
        } else if (id == R.id.nav_wifisecurity) {
            startActivity(new Intent(this, PreferencesActivity.class));
        } else if (id == R.id.nav_callblock) {
            startActivity(new Intent(this, com.mobilesecurity.mobileapp.CallBlocker.ui.MainActivity.class));
        } else if (id == R.id.nav_battersaver) {
            startActivity(new Intent(this, BatterySaver.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));
        } else if (id == R.id.nav_terms) {
            startActivity(new Intent(this, PrivacyPolicyActivity.class));
        } else if (id == R.id.nav_logout) {
          //  db = new SQLiteHandler(this);
           // session = new SessionManager(this);
           // logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void procheck()
    {
        boolean strPref = false;
        SharedPreferences shf = this.getSharedPreferences("config", MODE_PRIVATE);
        strPref = shf.getBoolean(UpgradePro, false);

        if(strPref)
        {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            ImageView iconmain = (ImageView) findViewById(R.id.iconmain);
            iconmain.setImageResource(R.drawable.shield_protected_icon);
            LinearLayout background = (LinearLayout) findViewById(R.id.background);
            background.setBackgroundColor(getResources().getColor(R.color.mainEnd));
            TextView textmain = (TextView) findViewById(R.id.textmain);
            textmain.setText(R.string.protected_home);
            Button upgrade = (Button) findViewById(R.id.upgrade);

            toolbar.setBackgroundColor(getResources().getColor(R.color.mainEnd));
            upgrade.setVisibility(View.GONE);


        }
        else {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            ImageView iconmain = (ImageView) findViewById(R.id.iconmain);
            iconmain.setImageResource(R.drawable.shield_protected_icon);
            LinearLayout background = (LinearLayout) findViewById(R.id.background);
            background.setBackgroundColor(getResources().getColor(R.color.mainEnd));
            TextView textmain = (TextView) findViewById(R.id.textmain);
            textmain.setText(R.string.protected_home);
            Button upgrade = (Button) findViewById(R.id.upgrade);

            toolbar.setBackgroundColor(getResources().getColor(R.color.mainEnd));
            upgrade.setVisibility(View.GONE);




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

    private void showCustomRateMeDialog() {
        new RateMeDialog.Builder(getPackageName(), getString(R.string.app_name))
                .setHeaderBackgroundColor(getResources().getColor(R.color.mainEnd))
                .setBodyBackgroundColor(getResources().getColor(R.color.black_gray))
                .setBodyTextColor(getResources().getColor(R.color.white))
                .showAppIcon(R.mipmap.ic_launcher)
                .setDefaultNumberOfStars(5)
                .setShowShareButton(true)
                //.setShowOKButtonByDefault(false)
                .setLineDividerColor(getResources().getColor(R.color.white55))
                .setRateButtonBackgroundColor(getResources().getColor(R.color.mainEnd))
                .setRateButtonPressedBackgroundColor(getResources().getColor(R.color.mainStart))
                .setOnRatingListener(new OnRatingListener() {
                    @Override
                    public void onRating(RatingAction action, float rating) {
                    }

                    @Override
                    public int describeContents() {
                        return 0;
                    }

                    @Override
                    public void writeToParcel(Parcel dest, int flags) {
                        // Nothing to write
                    }
                })
                .build()
                .show(getFragmentManager(), "custom-dialog");
    }

}
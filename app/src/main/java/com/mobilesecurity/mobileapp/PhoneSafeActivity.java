package com.mobilesecurity.mobileapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.mobilesecurity.mobileapp.R;
import com.mobilesecurity.mobileapp.AntiTheft.SetUp1;
import com.mobilesecurity.mobileapp.base.BaseActivityUpEnableWithMenu;
import com.mobilesecurity.mobileapp.fragment.PhoneSafeFragment;
import com.mobilesecurity.mobileapp.utils.ConfigUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;
import com.ypyproductions.utils.ApplicationUtils;

import static com.mobilesecurity.mobileapp.Constant.UpgradePro;


public class PhoneSafeActivity extends BaseActivityUpEnableWithMenu implements View.OnClickListener {
    private AdView mAdMobAdView;

    InterstitialAd mInterstitial;
    public PhoneSafeActivity() {
        super(R.string.phone_security, R.menu.menu_reset_password);
		
    }
    private void internet()
    {
        if (Internetconnection.checkConnection(this)) {
            admobbanner();

        } else {
            LinearLayout ads1 = (LinearLayout)findViewById(R.id.ads1);
            ads1.setVisibility(View.GONE);
        }
    }

    private void admobbanner(){
        mAdMobAdView = (AdView) findViewById(R.id.admob_adview);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdMobAdView.loadAd(adRequest);
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
    protected void init() {
        initData();
        initView();
        initEvent();
    }


    @Override
    protected void initData() {
        String safePhone = ConfigUtils.getString(this, Constant.KEY_SAFE_PHONE, "");
        if (TextUtils.isEmpty(safePhone)) {

            startActivity(new Intent(this, SetUp1.class));
            finish();
            return;
        }
    }

    @Override
   protected void initView() {
        setContentView(R.layout.activity_phone_safe);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        checkshowint();
        procheck();
        getFragmentManager().beginTransaction()
                .replace(R.id.activity_phone_safe, new PhoneSafeFragment())
                .commit();

    }


    @Override
    protected void initEvent() {

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
		}
    }

    public void checkshowint()
    {
        boolean strPref = false;
        SharedPreferences shf = this.getSharedPreferences("config", MODE_PRIVATE);
        strPref = shf.getBoolean("antitheftint", false);

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
                .setTitle("Anti Theft")
                .setBackgroundColor(Color.parseColor("#4076b2"))  //Don't pass R.color.colorvalue
                .setMessage(getApplicationContext().getResources().getString(R.string.antitheftint))
                .setPositiveBtnBackground(Color.parseColor("#FF4081"))  //Don't pass R.color.colorvalue
                .setPositiveBtnText("Ok")
                .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  //Don't pass R.color.colorvalue
                .setNegativeBtnText("Cancel")
                .setAnimation(com.shashank.sony.fancydialoglib.Animation.POP)
                .isCancellable(false)
                .setIcon(R.drawable.antithefticon, Icon.Visible)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        SharedPreferences saveint = getSharedPreferences("config", MODE_PRIVATE);
                        saveint.edit().putBoolean("antitheftint", true).apply();

                    }
                })
                .OnNegativeClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        SharedPreferences saveint = getSharedPreferences("config", MODE_PRIVATE);
                        saveint.edit().putBoolean("antitheftint", true).apply();

                    }
                })

                .build();
    }
}

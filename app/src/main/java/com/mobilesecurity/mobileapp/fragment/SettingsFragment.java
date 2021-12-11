package com.mobilesecurity.mobileapp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.mobilesecurity.mobileapp.R;
import com.mobilesecurity.mobileapp.PhoneSafeSetting1Activity;
import com.mobilesecurity.mobileapp.applocker.SetLockTypeActivity;
import com.mobilesecurity.mobileapp.base.FragmentContainerActivity;

import com.google.android.gms.ads.InterstitialAd;


public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {


    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, SettingsFragment.class, null);
    }
    InterstitialAd mInterstitial;
    private Preference createShortCut;
    private Preference callsetting;
    private Preference changepassword;
    private Preference pFacebook;
    private Preference changePhone;
    //private Preference pShare;
   // private Preference Notifications;
    private Preference pAbout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addPreferencesFromResource(R.xml.ui_settings);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getActionBar().setDisplayShowHomeEnabled(false);
        getActivity().getActionBar().setTitle(R.string.title_settings);

        callsetting = findPreference("callsetting");
        callsetting.setOnPreferenceClickListener(this);

        changepassword = findPreference("changepassword");
        changepassword.setOnPreferenceClickListener(this);

        changePhone = findPreference("changePhone");
        changePhone.setOnPreferenceClickListener(this);
        initData();
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        if ("callsetting".equals(preference.getKey())) {
           // getActivity().startActivity(new Intent(getActivity(), BlockingSettingsActivity.class));
        }
        //else if ("Notifications".equals(preference.getKey())) {
           // getActivity().startActivity(new Intent(getActivity(), PushNotificationMainActivity.class));
      //  }
        else if ("changepassword".equals(preference.getKey())) {
            getActivity().startActivity(new Intent(getActivity(), SetLockTypeActivity.class));
        }
        else if ("changePhone".equals(preference.getKey())) {
            getActivity().startActivity(new Intent(getActivity(), PhoneSafeSetting1Activity.class));
        }
        return false;
    }


    private void initData() {

    }




}

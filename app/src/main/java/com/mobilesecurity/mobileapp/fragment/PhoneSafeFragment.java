package com.mobilesecurity.mobileapp.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.fastaccess.permission.base.PermissionHelper;
import com.fastaccess.permission.base.callback.OnPermissionCallback;


import com.mobilesecurity.mobileapp.R;
import com.mobilesecurity.mobileapp.Constant;
import com.mobilesecurity.mobileapp.receiver.DeviceAdminSampleReceiver;
import com.mobilesecurity.mobileapp.service.PhoneSafeService;
import com.mobilesecurity.mobileapp.utils.ConfigUtils;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

public class PhoneSafeFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, OnPermissionCallback {

    private static final String KEY_CHANGE_SAFE_PHONE = "pref_change_safe_phone";
    private static final int REQUEST_CODE_DEVICE_ADMIN = 1;
    private SharedPreferences mSp;
    private SharedPreferences mSpp;
    private boolean waitForResult = false;
    private PermissionHelper permissionHelper;
    private boolean readPhoneState;
    InterstitialAd mInterstitial;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_phone_safe);

        permissionHelper = PermissionHelper.getInstance(getActivity(), this);
    }


    @Override
    public void onResume() {
        super.onResume();
        Preference preference = findPreference(KEY_CHANGE_SAFE_PHONE);
        preference.setSummary(getString(R.string.current_safe_phone_number) + ConfigUtils.getString(getActivity(), Constant.KEY_SAFE_PHONE, ""));

        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        boolean value = sharedPreferences.getBoolean(key, false);
        System.out.println("key:" + key + ", value:" + value);
        Context context = getActivity();
        if (key.equals(Constant.KEY_CB_PHONE_SAFE)) {
            Intent intent = new Intent(context, PhoneSafeService.class);
            if (value) {
                savedataOn();
                context.startService(intent);
            } else {
                savedataOff();
                context.stopService(intent);
            }
        } else if (key.equals(Constant.KEY_CB_BIND_SIM)) {
            if (waitForResult)
                return;
            if (value) {
                waitForResult = true;
                CheckBoxPreference cbp = (CheckBoxPreference) findPreference(Constant.KEY_CB_DEVICE_ADMIN);
                cbp.setChecked(false);
                readPhoneState = true;
                permissionHelper.request(Manifest.permission.READ_PHONE_STATE);
            }
        } else if (key.equals(Constant.KEY_CB_DEVICE_ADMIN)) {
            if (waitForResult)
                return;
            ComponentName deviceAdminSample = new ComponentName(context, DeviceAdminSampleReceiver.class);
            if (value) {
                System.out.println("KEY_CB_DEVICE_ADMIN enable");
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminSample);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        context.getString(R.string.add_admin_extra_app_text));
                startActivityForResult(intent, REQUEST_CODE_DEVICE_ADMIN);
                waitForResult = true;
                CheckBoxPreference cbp = (CheckBoxPreference) findPreference(Constant.KEY_CB_DEVICE_ADMIN);
                cbp.setChecked(false);
            } else {
                System.out.println("KEY_CB_DEVICE_ADMIN disable");
                DevicePolicyManager mDPM =
                        (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                mDPM.removeActiveAdmin(deviceAdminSample);
            }
        }

    }



    private void savedataOff() {
        mSp = getActivity().getSharedPreferences("config", MODE_PRIVATE);
        String True = "Off";
        mSp.edit().putString("phoneSafe", True).commit();
    }

    private void savedataOn() {
        mSpp = getActivity().getSharedPreferences("config", MODE_PRIVATE);
        String True = "On";
        mSpp.edit().putString("phoneSafe", True).commit();
    }

    private void getSimInfoAndSave(Context context) {

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String simSerialNumber = manager.getSimSerialNumber();

        ConfigUtils.putString(context, Constant.KEY_SIM_INFO, simSerialNumber);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DEVICE_ADMIN) {
            if (Activity.RESULT_OK == resultCode) {
                CheckBoxPreference cbp = (CheckBoxPreference) findPreference(Constant.KEY_CB_DEVICE_ADMIN);
                cbp.setChecked(true);
            }
            waitForResult = false;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    public void onPermissionGranted(@NonNull String[] permissionName) {
        System.out.println("onPermissionGranted:" + Arrays.toString(permissionName));
        if(null == permissionName || Arrays.asList(permissionName).contains(Manifest.permission.READ_PHONE_STATE)) {
            if(readPhoneState) {
                readPhoneState = false;
                getSimInfoAndSave(getActivity());

                CheckBoxPreference cbp = (CheckBoxPreference) findPreference(Constant.KEY_CB_BIND_SIM);
                cbp.setChecked(true);

            }
        }
        waitForResult = false;
    }

    @Override
    public void onPermissionDeclined(@NonNull String[] permissionName) {
        Toast.makeText(getActivity(), R.string.no_permission, Toast.LENGTH_SHORT).show();
        waitForResult = false;
    }

    @Override
    public void onPermissionPreGranted(@NonNull String permissionsName) {
        System.out.println("onPermissionPreGranted:" + permissionsName);
        onPermissionGranted(new String[]{permissionsName});
    }

    @Override
    public void onPermissionNeedExplanation(@NonNull String permissionName) {
        System.out.println("onPermissionNeedExplanation:" + permissionName);
    }

    @Override
    public void onPermissionReallyDeclined(@NonNull String permissionName) {
        System.out.println("onPermissionReallyDeclined:" + permissionName);
        waitForResult = false;
    }

    @Override
    public void onNoPermissionNeeded() {
        onPermissionGranted(null);
    }

}

package com.mobilesecurity.mobileapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fastaccess.permission.base.PermissionHelper;
import com.fastaccess.permission.base.callback.OnPermissionCallback;

import com.mobilesecurity.mobileapp.AntiTheft.SetUp4;
import com.mobilesecurity.mobileapp.base.BaseActivityUpEnable;
import com.mobilesecurity.mobileapp.service.PhoneSafeService;
import com.mobilesecurity.mobileapp.utils.ActivityManagerUtils;
import com.mobilesecurity.mobileapp.utils.ConfigUtils;


import java.util.Arrays;


public class PhoneSafeSetting1Activity extends BaseActivityUpEnable implements View.OnClickListener, OnPermissionCallback {

    private static final int REQUEST_CONTACTS = 1;

    private EditText etSafePhone;

    private PermissionHelper permissionHelper;


    public PhoneSafeSetting1Activity() {
        super(R.string.title_phone_safe_setting1);
    }


    @Override
    protected void initView() {
        setContentView(R.layout.activity_phone_safe_setting1);

        etSafePhone = (EditText) findViewById(R.id.et_safe_phone_number);

    }


    @Override
    protected void initData() {
        etSafePhone.setText(ConfigUtils.getString(this, Constant.KEY_SAFE_PHONE, ""));

        permissionHelper = PermissionHelper.getInstance(this);
    }


    @Override
    protected void initEvent() {
        findViewById(R.id.btn_next).setOnClickListener(this);
        findViewById(R.id.btn_select_from_contact).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                onFinish();
                break;
            case R.id.btn_select_from_contact:
                onSelectFromContact();
                break;

        }
    }


    private void onSelectFromContact() {
        startActivityForResult(new Intent(this, ContactsActivity.class), REQUEST_CONTACTS);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CONTACTS == requestCode) {
            if (data != null) {
                String safePhone = data.getStringExtra(Constant.KEY_CONTACT_PHONE);
                etSafePhone.setText(safePhone);
            }
        }
    }

    private void onFinish() {
        System.out.println("onFinish");
        // request permission
        permissionHelper.request(Manifest.permission.READ_PHONE_STATE);
    }

    private void saveConfig() {
        // get safe phone
        String safePhone = etSafePhone.getText().toString().trim();
        if (TextUtils.isEmpty(safePhone)) {
            Toast.makeText(this, R.string.safe_phone_can_not_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }


        ConfigUtils.putString(this, Constant.KEY_SAFE_PHONE, safePhone);


        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
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

        ConfigUtils.putString(this, Constant.KEY_SIM_INFO, simSerialNumber);


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(Constant.KEY_CB_PHONE_SAFE, true)
                .putBoolean(Constant.KEY_CB_BIND_SIM, true)
                .commit();

        ActivityManagerUtils.checkService(this, PhoneSafeService.class);
        Intent intent = new Intent(this, SetUp4.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onPermissionGranted(@NonNull String[] permissionName) {
        System.out.println("onPermissionGranted:" + Arrays.toString(permissionName));
        saveConfig();
    }

    @Override
    public void onPermissionDeclined(@NonNull String[] permissionName) {
        Toast.makeText(this, R.string.no_permission, Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onNoPermissionNeeded() {
        onPermissionGranted(null);
    }

}

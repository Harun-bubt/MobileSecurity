package com.mobilesecurity.mobileapp.AntiTheft;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.mobilesecurity.mobileapp.R;
import com.mobilesecurity.mobileapp.PhoneSafeSetting1Activity;

public class SetUp2 extends BaseSetUp implements View.OnClickListener {

    private TelephonyManager manager;
    private Button mBindSIMBtn;
    private RadioButton Sec_rb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up2);
        manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        initView();
    }

    private void initView() {
        Sec_rb = (RadioButton) findViewById(R.id.rb_second);
        Sec_rb.setChecked(true);
        mBindSIMBtn = (Button) findViewById(R.id.btn_bind_sim);
        mBindSIMBtn.setOnClickListener(this);
        if (isBind()) {
            mBindSIMBtn.setEnabled(false);
        } else {
            mBindSIMBtn.setEnabled(true);
        }
    }

    private boolean isBind() {
        String simString = sp.getString("sim", null);
        if (TextUtils.isEmpty(simString)) {
            return false;
        }
        return true;
    }

    @Override
    public void showPre() {
        startActivityAndFinishSelf(SetUp1.class);
    }

    @Override
    public void showNext() {
        if (!isBind()) {
            Toast.makeText(getApplicationContext(), "You have not bound the SIM card yet", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivityAndFinishSelf(PhoneSafeSetting1Activity.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bind_sim:

                bindSIM();

                break;
        }
    }

    private void bindSIM() {
        if (!isBind()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            String simSerialNumber = manager.getSimSerialNumber();
            sp.edit().putString("sim",simSerialNumber).commit();
            Toast.makeText(SetUp2.this, "SIM card binding is successful", Toast.LENGTH_SHORT).show();
            mBindSIMBtn.setEnabled(false);

        }else {
            Toast.makeText(getApplicationContext(),"SIM card is bound!",Toast.LENGTH_SHORT).show();
            mBindSIMBtn.setEnabled(false);
        }
    }
}

package com.mobilesecurity.mobileapp.AntiTheft;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mobilesecurity.mobileapp.R;
import com.mobilesecurity.mobileapp.PhoneSafeActivity;

public class SetUp4 extends BaseSetUp {

    private TextView tv_Status;
    private ToggleButton mToggleButton;
    private RadioButton rb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up4);
        initView();
    }

    private void initView() {
        rb = (RadioButton) findViewById(R.id.rb_four);
        rb.setChecked(true);
        tv_Status = (TextView) findViewById(R.id.tv_setup4_status);
        mToggleButton = (ToggleButton) findViewById(R.id.toggle_securityfunction);

        boolean isProtecting = sp.getBoolean("protecting", true);
        if (isProtecting) {
            tv_Status.setText("Anti-theft protection is turned on");
            mToggleButton.setChecked(true);
        } else {
            tv_Status.setText("Anti-theft protection is not yet open");
            mToggleButton.setChecked(false);
        }


        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tv_Status.setText("Anti-theft protection is turned on");
                } else {
                    tv_Status.setText("Anti-theft protection is not yet open");
                }
                sp.edit().putBoolean("protecting", isChecked).commit();
                boolean is=sp.getBoolean("protecting",false);
                System.out.println("The protection state is"+is);
            }
        });
    }


    @Override
    public void showPre() {
        startActivityAndFinishSelf(SetUp2.class);
    }

    @Override
    public void showNext() {
        sp.edit().putBoolean("isSetUp", true).commit();
        startActivityAndFinishSelf(PhoneSafeActivity.class);
    }
}

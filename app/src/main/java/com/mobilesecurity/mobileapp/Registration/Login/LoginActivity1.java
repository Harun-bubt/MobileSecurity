package com.mobilesecurity.mobileapp.Registration.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mobilesecurity.mobileapp.Activity_TOSAccept;
import com.mobilesecurity.mobileapp.R;
import com.mobilesecurity.mobileapp.Registration.Contants.Constant;


public class LoginActivity1 extends AppCompatActivity {

    FrameLayout frameLayout;
    String Fragment_Name;
    final Context context = this;

    FragmentManager fragmentManager;
    FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);
        frameLayout = findViewById(R.id.framlayoutLogin);

        SharedPreferences sharedPreferences = getSharedPreferences(Constant.EXPRESS_PREFERENCE,MODE_PRIVATE);
        String isLogin = sharedPreferences.getString(Constant.ISLOGIN,"0");
        if(isLogin.equals("1"))
        {
            startActivity(new Intent(LoginActivity1.this, Activity_TOSAccept.class));
        }else
        {
            //Support Fragment Manager
            fragmentManager = this.getSupportFragmentManager();
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.framlayoutLogin, new FragmentSendOtp());
            transaction.addToBackStack("tag");
            transaction.commit();
        }







    }


    @Override
    public void onBackPressed() {
        this.finish();
    }



}
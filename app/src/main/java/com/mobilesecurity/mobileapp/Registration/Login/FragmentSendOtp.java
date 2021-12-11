package com.mobilesecurity.mobileapp.Registration.Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.mobilesecurity.mobileapp.Activity_TOSAccept;
import com.mobilesecurity.mobileapp.R;
import com.mobilesecurity.mobileapp.Registration.Contants.Constant;
import com.mobilesecurity.mobileapp.Registration.Domain.OtpResponse;
import com.mobilesecurity.mobileapp.Registration.Network.ExpressServer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentSendOtp extends Fragment {

    TextView wrongFormetText;
    Button btnSendOtp;
    EditText numberEditText;
    View view;
    String number;
    ScrollView scrollView;
    ExpressServer expressServer;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_send_otp, container, false);
        initializeAll();
        onBackPressed(view);




        setNumber();


        btnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(getActivity());
                number = numberEditText.getText().toString();
                if (TextUtils.isEmpty(number)) {
                    wrongFormetText.setVisibility(View.VISIBLE);
                    numberEditText.requestFocus();
                    return;
                }
                if(number.charAt(0) == '0' && number.length() == 11)
                {
                    progressBar.setVisibility(View.VISIBLE);
                    sendOtp(number);
                }else if(number.charAt(0) == '1' && number.length() == 10)
                {
                    progressBar.setVisibility(View.VISIBLE);
                    number = "0"+number;
                    sendOtp(number);
                }else
                {

                    Toast.makeText(getContext(), "Number is not correct", Toast.LENGTH_SHORT).show();
                    numberEditText.requestFocus();
                    return;
                }




            }
        });

        return view;

    }
    public void sendOtp(String number)
    {
        Call<OtpResponse> call = expressServer.getOtpResponse("test","APP_000001",
                number,number);
        call.enqueue(new Callback<OtpResponse>() {
            @Override
            public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                if(response.isSuccessful())
                {
                    progressBar.setVisibility(View.GONE);
                    OtpResponse otpResponse = response.body();
                    //Log.d("Statuss",otpResponse.getStatus());
                 //   Log.d("Statuss",otpResponse.getSession());
                    if(otpResponse.getStatus().equals("verified"))
                    {
                        editor.putString(Constant.NUMBER,number);
                        editor.putString(Constant.ISLOGIN,"1");
                       // editor.putString(Constant.SESSION,otpResponse.getSession());
                        editor.apply();
                        Toast.makeText(getContext(), "verified, Go to Home page", Toast.LENGTH_SHORT).show();
                        //go to splash screen
                        startActivity(new Intent(getActivity(), Activity_TOSAccept.class));
                    }else if(otpResponse.getStatus().equals("success"))
                    {
                       // Toast.makeText(getContext(), "ref"+refence, Toast.LENGTH_SHORT).show();
                        editor.putString(Constant.NUMBER,number);
                        editor.apply();
                        nextScreen();
                    }else
                    {
                        Log.d("Statuss",otpResponse.getStatus());
                        wrongFormetText.setVisibility(View.VISIBLE);
                        wrongFormetText.setText(otpResponse.getStatus());
                        wrongFormetText.requestFocus();
                        return;
                    }

                }
            }

            @Override
            public void onFailure(Call<OtpResponse> call, Throwable t) {
                Toast.makeText(getContext(),"Failure"+ t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                Log.d("Fiksdjfl",t.getMessage().toString());
            }
        });
    }


    public void initializeAll()
    {
        wrongFormetText = view.findViewById(R.id.wrongFormatTextView);
        btnSendOtp = view.findViewById(R.id.btnSendOtp);
        numberEditText = view.findViewById(R.id.numberEditText);
        scrollView = view.findViewById(R.id.scrollView);
        progressBar = view.findViewById(R.id.progressbar);
        expressServer = ExpressServer.retrofit.create(ExpressServer.class);
        sharedPreferences = getContext().getSharedPreferences(Constant.EXPRESS_PREFERENCE,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        scrollView.smoothScrollTo(numberEditText.getLeft(), numberEditText.getTop());




    }
    public void setNumber()
    {
        numberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
                if(i > 0)
                {

                }else
                {

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void nextScreen() {

        // Segment for call next fragment
        FragmentVerifyOtp fragmentVerifyOtp = new FragmentVerifyOtp();
        androidx.fragment.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.framlayoutLogin, fragmentVerifyOtp);
        transaction.addToBackStack("tag");
        transaction.commit();
    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void onBackPressed(View view){
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener( new View.OnKeyListener()
        {

            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    getFragmentManager().popBackStack();
                    return true;
                }
                return false;
            }
        } );

    }






}
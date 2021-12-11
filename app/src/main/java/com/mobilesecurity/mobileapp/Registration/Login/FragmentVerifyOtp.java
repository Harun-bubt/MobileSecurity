package com.mobilesecurity.mobileapp.Registration.Login;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.mobilesecurity.mobileapp.Registration.Receiver.SmsBroadCastReceiver;
import com.chaos.view.PinView;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentVerifyOtp extends Fragment {

    TextView wrongFormetText,numberTextView,progressTextView;
    Button btnVerifyOtp;
    TextView againSendTextView;
    View view;
    ProgressBar progressBar;
    RelativeLayout progressBarLayout;
    AppEventsLogger logger;



    String number;
    String session;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    PinView pinView;
    private static final int REQ_USER_CONSENT = 200;
    SmsBroadCastReceiver smsBroadcastReceiver;
    ExpressServer expressServer;
    int i = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_verify_otp, container, false);
        initializeAll();
        onBackPressed(view);
        logger = AppEventsLogger.newLogger(getContext());

        //pinView.setText("123456")
        numberTextView.setText(number+" নম্বর এ ছয় ডিজিটের ভেরিফিকেশন কোড পাঠানো হয়েছে। অনুগ্রহ করে নিচের ঘরে কোডটি প্রবেশ লিখুন।");
        startSmartUserConsent();

        controlProgressBar(true);
        againSendTextView.setVisibility(View.GONE);

        btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String pin = pinView.getText().toString();
                if(TextUtils.isEmpty(pin) && pin.length() != 6)
                {
                    pinView.requestFocus();
                    Toast.makeText(getContext(), "৬ সংখ্যার পিন দিন।", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendVerifyCode(number,pin);
            }
        });
        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().length() == 6)
                {
                    btnVerifyOtp.performClick();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        againSendTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBarLayout.setVisibility(View.VISIBLE);
                controlProgressBar(false);
                controlProgressBar(true);
                sendOtp(number);

                //call sendOtp api
                Toast.makeText(getContext(), "call send api", Toast.LENGTH_SHORT).show();
            }
        });



       

        return view;

    }

    public void initializeAll()
    {
        wrongFormetText = view.findViewById(R.id.wrongFormatTextView);
        btnVerifyOtp = view.findViewById(R.id.btnVerifyOtp);
        pinView = view.findViewById(R.id.pinViewForVerificationCode);
        numberTextView = view.findViewById(R.id.showNumberTextView);
        progressBar = view.findViewById(R.id.progressbar);
        progressTextView = view.findViewById(R.id.progressTextView);
        progressBarLayout = view.findViewById(R.id.progressbarLayout);
        againSendTextView = view.findViewById(R.id.againSendTextView);
        sharedPreferences =getContext().getSharedPreferences(Constant.EXPRESS_PREFERENCE,MODE_PRIVATE);
        editor = sharedPreferences.edit();
        expressServer = ExpressServer.retrofit.create(ExpressServer.class);
        sharedPreferences = getContext().getSharedPreferences(Constant.EXPRESS_PREFERENCE, Context.MODE_PRIVATE);
        number = sharedPreferences.getString(Constant.NUMBER,"0");
        session = sharedPreferences.getString(Constant.SESSION,"0");



    }
    public void logSentFriendRequestEvent () {
        //  logger.logEvent("sentFriendRequest");
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_REGISTRATION_METHOD, "SMS");
        logger.logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION, params);
    }
    private void nextScreen() {

    /*    // Segment for call next fragment
        Fragment3_OtpVerification fragment3OtpVerification = new Fragment3_OtpVerification();
        androidx.fragment.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.framelayout, fragment3OtpVerification);
        transaction.addToBackStack("tag");
        transaction.commit();
*/
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
                    OtpResponse otpResponse = response.body();
                    //Log.d("Statuss",otpResponse.getStatus());
                    //   Log.d("Statuss",otpResponse.getSession());
                    if(otpResponse.getStatus().equals("already"))
                    {
                        editor.putString(Constant.ISLOGIN,"1");
                        editor.apply();
                        Toast.makeText(getContext(), "Already , Go to Register page", Toast.LENGTH_SHORT).show();
                       // nextScreen();
                    }else if(otpResponse.getStatus().equals("success"))
                    {
                        controlProgressBar(false);
                        controlProgressBar(true);
                        againSendTextView.setVisibility(View.GONE);
                        //nextScreen();
                    }else
                    {
                        editor.putString(Constant.ISLOGIN,"0");
                        editor.apply();
                        Log.d("Statuss",otpResponse.getStatus());
                        wrongFormetText.setVisibility(View.VISIBLE);
                        wrongFormetText.setText(otpResponse.getStatus());
                        wrongFormetText.requestFocus();
                        progressBarLayout.setVisibility(View.GONE);
                        controlProgressBar(false);
                        return;
                    }

                }
            }

            @Override
            public void onFailure(Call<OtpResponse> call, Throwable t) {
                Toast.makeText(getContext(),"Failure"+ t.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void sendVerifyCode(String number,String pin)
    {
        Call<OtpResponse> call = expressServer.getOtpVerification("test","APP_000001",
                pin,number);
        call.enqueue(new Callback<OtpResponse>() {
            @Override
            public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                if(response.isSuccessful())
                {
                    OtpResponse otpResponse = response.body();
                    //Log.d("Statuss",otpResponse.getStatus());
                    //   Log.d("Statuss",otpResponse.getSession());
                    if(otpResponse.getStatus().equals("verified"))
                    {
                        logSentFriendRequestEvent();
                        editor.putString(Constant.ISLOGIN,"1");
                        editor.apply();
                        Toast.makeText(getContext(), "Already , Go to Home page", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), Activity_TOSAccept.class));
                    }else
                    {
                        editor.putString(Constant.ISLOGIN,"0");
                        editor.apply();
                        pinView.setText("");
                        progressBarLayout.setVisibility(View.GONE);
                        againSendTextView.setVisibility(View.VISIBLE);
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
            }
        });
    }

    private void startSmartUserConsent() {

        SmsRetrieverClient client = SmsRetriever.getClient(getContext());
        client.startSmsUserConsent(null);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_USER_CONSENT){

            if ((resultCode == RESULT_OK) && (data != null)){

                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                getOtpFromMessage(message);


            }


        }

    }

    private void getOtpFromMessage(String message) {

        Pattern otpPattern = Pattern.compile("(|^)\\d{6}");
        Matcher matcher = otpPattern.matcher(message);
        if (matcher.find()){

            pinView.setText(matcher.group(0));

        }


    }

    private void registerBroadcastReceiver(){

        smsBroadcastReceiver = new SmsBroadCastReceiver();

        smsBroadcastReceiver.smsBroadcastReceiverListener = new SmsBroadCastReceiver.SmsBroadcastReceiverListener() {
            @Override
            public void onSuccess(Intent intent) {

                startActivityForResult(intent,REQ_USER_CONSENT);

            }

            @Override
            public void onFailure() {

            }
        };

        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);

        getContext().registerReceiver(smsBroadcastReceiver,intentFilter);

    }

    @Override
    public void onStart() {
        super.onStart();
        registerBroadcastReceiver();

    }

    @Override
    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(smsBroadcastReceiver);
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
    public void controlProgressBar(boolean isStart)
    {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isStart)
                {
                    if (i <= 50)
                    {
                        progressTextView.setText(""+i);
                        progressBar.setProgress(i*2);
                        i++;

                        handler.postDelayed(this,1000);
                    }else {
                        i = 0;
                        againSendTextView.setVisibility(View.VISIBLE);
                        progressBarLayout.setVisibility(View.GONE);
                        handler.removeCallbacks(this);
                    }
                }else
                {
                    i = 0;
                    progressTextView.setText("");
                    progressBar.setProgress(0);
                    handler.removeCallbacks(this);
                }

            }
        },10);

    }

}




package com.mobilesecurity.mobileapp;

import android.os.Bundle;
import android.os.Parcel;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


//import com.kyrol.mobile.utils.AppUtil;

import com.mobilesecurity.mobileapp.base.BaseSwipeBackActivity;
import com.github.martarodriguezm.rateme.OnRatingListener;
import com.github.martarodriguezm.rateme.RateMeDialog;

//import butterknife.InjectView;
/**
 * Created by dell on 12/18/2017.
 */

public class AboutActivity extends BaseSwipeBackActivity {

    //@b(R.id.subVersion)
    TextView subVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getActionBar().setDisplayHomeAsUpEnabled(true);


        subVersion = (TextView)  findViewById(R.id.subVersion);
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        subVersion.setText(versionName);

        Button getrate = (Button) findViewById(R.id.button2);
        getrate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showCustomRateMeDialog();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                        // Toast.makeText(MainActivity.this,
                        //       "Rate Me action: " + action + " (rating: " + rating + ")", Toast.LENGTH_LONG).show();
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


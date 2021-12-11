package com.mobilesecurity.mobileapp.AntiVirus;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

import com.mobilesecurity.mobileapp.R;


public class ScanSettings extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
        addPreferencesFromResource(R.xml.virusprotection);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {

        }
        return false;
    }


    @Override
    public void onResume() {
        super.onResume();
    }
}

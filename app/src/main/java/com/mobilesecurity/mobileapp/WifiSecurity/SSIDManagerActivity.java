package com.mobilesecurity.mobileapp.WifiSecurity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import com.mobilesecurity.mobileapp.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SSIDManagerActivity extends NetworkManagerActivity {
    private ScanResultsChecker scanResultsChecker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
        Log.v("WifiSecurity", "Creating SSID manager activity");

        scanResultsChecker = new ScanResultsChecker(this);
        adapter = new SSIDManagerAdapter();
        setListAdapter(adapter);
    }
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        Intent intent = new Intent(this, MACManagerActivity.class);
        // Pass the SSID to the new activity
        NetworkAvailability listItem = (NetworkAvailability) listView.getItemAtPosition(position);
        String networkName = listItem.getName();
        intent.putExtra("SSID", networkName);

        startActivity(intent);
    }

    /**
     * Asks the user for confirmation, and then removes all trusted and untrusted access points.
     */
    @Override
    public void confirmClearAll() {
        // Ask for confirmation first
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_clearhotspots);
        builder.setPositiveButton(R.string.dialog_clearhotspots_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Actually clear the list
                PreferencesStorage prefs = new PreferencesStorage(SSIDManagerActivity.this);
                prefs.clearBSSIDLists();
                SSIDManagerActivity.this.refresh();
            }
        });
        builder.setNegativeButton(R.string.dialog_clearhotspots_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User canceled
            }
        });
        builder.show();
    }

    protected class SSIDManagerAdapter extends NetworkManagerAdapter {
        public void refresh() {
            Log.v("WifiSecurity", "Refreshing the network list adapter");
            // Use an ArrayMap so we can put available networks at the top
            networkList = new ArrayList<>();

            // Combine the SSIDs that we know of with the SSIDs that are available.
            List<ScanResult> scanResults = wifiManager.getScanResults();
            Set<String> knownSSIDs = prefs.getNonemptySSIDs();

            // Add currently available networks that are stored in the preferences to the list
            for (ScanResult scanResult : scanResults) {
                if (knownSSIDs.contains(scanResult.SSID)) {
                    ScanResultsChecker.AccessPointSafety networkSafety = scanResultsChecker.getNetworkSafety(scanResult.SSID, scanResults);
                    networkList.add(new NetworkAvailability(scanResult.SSID, scanResult.level, networkSafety));
                    knownSSIDs.remove(scanResult.SSID);
                }
            }

            // Add all other (non-available) saved SSIDs to the list
            for (String SSID : knownSSIDs) {
                ScanResultsChecker.AccessPointSafety networkSafety = ScanResultsChecker.AccessPointSafety.UNKNOWN;
                networkList.add(new NetworkAvailability(SSID, -9999, networkSafety));
            }
            notifyDataSetChanged();
        }
    }
}
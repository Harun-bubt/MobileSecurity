package com.mobilesecurity.mobileapp.WifiSecurity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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


public class MACManagerActivity extends NetworkManagerActivity {
    private String SSID;
	


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        SSID = getIntent().getStringExtra("SSID");
        Log.v("WifiSecurity", "Creating MAC manager activity for network " + SSID);
        setTitle(SSID);

        adapter = new MACManagerAdapter();
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
        final NetworkAvailability listItem = (NetworkAvailability) listView.getItemAtPosition(position);
        final String mac = listItem.getName();
        Log.v("WifiSecurity", "Asking for confirmation to remove mac " + mac + " for network " + SSID);
        // Ask for confirmation first
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format(getResources().getString(R.string.dialog_removetrustedmac), mac));
        builder.setPositiveButton(R.string.dialog_remove, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Actually remove the BSSID from the 'trusted' list
                PreferencesStorage prefs = new PreferencesStorage(MACManagerActivity.this);
                if (listItem.getAccessPointSafety() == ScanResultsChecker.AccessPointSafety.TRUSTED)
                    prefs.removeAllowedBSSID(SSID, mac);
                else
                    prefs.removeBlockedBSSID(SSID, mac);
                MACManagerActivity.this.refresh();
            }
        });
        builder.setNegativeButton(R.string.dialog_clearhotspots_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User canceled
            }
        });
        builder.show();
    }

    /**
     * Get the SSID which is managed by this MAC manager
     * @return the network name for this network
     */
    public String getSSID() {
        return SSID;
    }

    /**
     * Asks the user for confirmation, and then removes all trusted MAC addresses for the current
     * SSID.
     */
    @Override
    public void confirmClearAll() {
        // Ask for confirmation first
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format(getResources().getString(R.string.dialog_clearhotspotsformac), SSID));
        builder.setPositiveButton(R.string.dialog_clearhotspots_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Actually clear the list
                PreferencesStorage prefs = new PreferencesStorage(MACManagerActivity.this);
                prefs.clearBSSIDsForNetwork(MACManagerActivity.this.getSSID());
                MACManagerActivity.this.refresh();
            }
        });
        builder.setNegativeButton(R.string.dialog_clearhotspots_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User canceled
            }
        });
        builder.show();
    }

    protected class MACManagerAdapter extends NetworkManagerAdapter {
        public void refresh() {
            Log.v("WifiSecurity", "Refreshing the SSID list adapter");
            // Use an ArrayMap so we can put available access points at the top
            networkList = new ArrayList<>();

            // Combine the access points that we know of with the access points that are available.
            List<ScanResult> scanResults = wifiManager.getScanResults();

            Set<String> trustedMACs = prefs.getAllowedBSSIDs(getSSID());
            // Add currently available access points that we trust to the list
            for (ScanResult scanResult : scanResults) {
                if (trustedMACs.contains(scanResult.BSSID)) {
                    networkList.add(new NetworkAvailability(scanResult.BSSID, scanResult.level, ScanResultsChecker.AccessPointSafety.TRUSTED));
                    trustedMACs.remove(scanResult.BSSID);
                }
            }
            Set<String> blockedMACs = prefs.getBlockedBSSIDs(getSSID());
            // Add currently available access points that we block to the list
            for (ScanResult scanResult : scanResults) {
                if (blockedMACs.contains(scanResult.BSSID)) {
                    networkList.add(new NetworkAvailability(scanResult.BSSID, scanResult.level, ScanResultsChecker.AccessPointSafety.UNTRUSTED));
                    blockedMACs.remove(scanResult.BSSID);
                }
            }

            // Add all other (non-available) saved SSIDs to the list
            for (String MAC : trustedMACs) {
                networkList.add(new NetworkAvailability(MAC, -99999, ScanResultsChecker.AccessPointSafety.TRUSTED));
            }
            for (String MAC : blockedMACs) {
                networkList.add(new NetworkAvailability(MAC, -99999, ScanResultsChecker.AccessPointSafety.UNTRUSTED));
            }

            notifyDataSetChanged();
        }
    }
}
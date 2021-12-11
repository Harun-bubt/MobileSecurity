package com.mobilesecurity.mobileapp.AntiVirus;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.TextView;

import com.mobilesecurity.mobileapp.R;


public class DetailScanReport extends Activity{


    TextView txtScanItem;
    TextView txtScanStrt;
    TextView txtScanend;
    TextView txtTotalFilesScanned;
    TextView txtstatus;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_scan_report);
        Intent i = getIntent();
        String metadata = i.getStringExtra("metadata");

        String [] textParts=metadata.split(";;");
        txtScanItem = (TextView) findViewById(R.id.txtScanItem);
        txtScanItem.setText("Report For: " + textParts[0]);

        txtTotalFilesScanned = (TextView) findViewById(R.id.txtTotalFilesScanned);
        txtTotalFilesScanned.setText("Files Scanned: " + textParts[3]);

        txtScanStrt = (TextView) findViewById(R.id.txtScanStrt);
        txtScanStrt.setText("Scan started at: " + textParts[1]);

        txtScanend = (TextView) findViewById(R.id.txtScanend);
        txtScanend.setText("Scan finished at: " + textParts[2]);

        txtstatus = (TextView) findViewById(R.id.txtstatus);
        txtstatus.setText(textParts[4]);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {

        }
        return false;
    }
}

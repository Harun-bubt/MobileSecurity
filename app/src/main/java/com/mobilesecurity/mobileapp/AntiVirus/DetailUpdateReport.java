package com.mobilesecurity.mobileapp.AntiVirus;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.TextView;

import com.mobilesecurity.mobileapp.R;

public class DetailUpdateReport extends Activity {

    TextView txtScanItem;
    TextView txtUpdateTime;
    TextView txtFromDB;
    TextView txtToDB;
    TextView txtstatus;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_update_report);
        Intent i = getIntent();
        String metadata = i.getStringExtra("metadata");

        String [] textParts=metadata.split(";;");
        txtScanItem = (TextView) findViewById(R.id.txtScanItem);
        txtScanItem.setText("Report For: " + textParts[0]);

        txtUpdateTime = (TextView) findViewById(R.id.txtUpdateTime);
        txtUpdateTime.setText(textParts[1]);

        txtFromDB = (TextView) findViewById(R.id.txtFromDB);
        txtFromDB.setText(textParts[2]);

        txtToDB = (TextView) findViewById(R.id.txtToDB);
        txtToDB.setText(textParts[3]);

        txtstatus = (TextView) findViewById(R.id.txtstatus);
        txtstatus.setText("Status: " + textParts[4]);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {

        }
        return false;
    }

}

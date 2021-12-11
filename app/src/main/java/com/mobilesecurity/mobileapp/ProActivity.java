package com.mobilesecurity.mobileapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import static com.mobilesecurity.mobileapp.Constant.LICENSE_KEY;
import static com.mobilesecurity.mobileapp.Constant.PRODUCT_ID;
import static com.mobilesecurity.mobileapp.Constant.UpgradePro;

/**
 * Created by dell on 11/21/2017.
 */

public class ProActivity extends Activity implements BillingProcessor.IBillingHandler, View.OnClickListener {

    private SharedPreferences saveVerified;
    private BillingProcessor bp;
    private boolean readyToPurchase = false;

    private Button btnPurchase;
    private final String PURCHASE_ID = PRODUCT_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_pro);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        bp = BillingProcessor.newBillingProcessor(this, LICENSE_KEY, this); // doesn't bind
        bp.initialize(); // binds

        initView();
    }

    private void initView() {
        btnPurchase = (Button) findViewById(R.id.subscribeButton);

        btnPurchase.setEnabled(false);

        btnPurchase.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.subscribeButton:
                if (readyToPurchase) {
                    bp.purchase(this, PURCHASE_ID);
                } else {
                    Toast.makeText(this, "Unable to initiate purchase", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePurchase();
    }

    private void updatePurchase() {
        if (bp.isPurchased(PURCHASE_ID)) {
            saveVerified = getSharedPreferences("config", MODE_PRIVATE);

            saveVerified.edit().putBoolean(UpgradePro, true).apply();

            startproActivity();
            //tvResult.setText("THANK YOU!");
        }
    }

    @Override
    public void onBillingInitialized() {
        readyToPurchase = true;
        btnPurchase.setEnabled(true);
        updatePurchase();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Toast.makeText(this, "Thanks for your Purchased!", Toast.LENGTH_SHORT).show();
        updatePurchase();
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Toast.makeText(this, "Unable to process billing", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPurchaseHistoryRestored() {
        updatePurchase();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();

        super.onDestroy();
    }


    private void startproActivity() {
        startActivity(new Intent(this, ProActivitySuccess.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

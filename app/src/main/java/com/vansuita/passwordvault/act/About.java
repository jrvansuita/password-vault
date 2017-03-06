package com.vansuita.passwordvault.act;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.pref.Billing;
import com.vansuita.passwordvault.view.Snack;

/**
 * Created by jrvansuita on 20/02/17.
 */

public class About extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    private BillingProcessor billingProcessor;
    private static final String REMOVE_ADS_ID = "remove_ads";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);
        setup();

        //Just gor test, remove it.
        Billing.with(this).setRemoveAdsPurchased(false);

        AboutBuilder builder = AboutBuilder.with(this)
                .setPhoto(R.mipmap.jr)
                .setCover(R.mipmap.cover)
                .setName(R.string.developer_name)
                .setSubTitle(R.string.developer_subtitle)
                .setBrief(R.string.developer_brief)
                .setAppIcon(R.mipmap.green_vault)
                .setAppName(R.string.app_name)
                .addGooglePlayStoreLink(R.string.developer_play_store)
                .addGitHubLink(R.string.developer_github)
                .addInstagramLink(R.string.developer_instagram)
                .addLinkedInLink(R.string.developer_linkedin)
                .addEmailLink(R.string.developer_email)
                .addGooglePlusLink(R.string.developer_gplus)
                .addFiveStarsAction()
                .setVersionNameAsAppSubTitle()
                .addShareAction(R.string.app_name)
                .addMoreFromMeAction(R.string.developer_play_store_user_name)
                .addUpdateAction()
                .addFeedbackAction(getString(R.string.developer_email), getString(R.string.feedback, getString(R.string.app_name)))
                .setWrapScrollView(true);

        if (!Billing.with(this).isRemoveAdsPurchased())
            if (billingProcessor != null)
                builder.addRemoveAdsAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Lock.isIgnoreAction(true);

                        if (billingProcessor != null && billingProcessor.isOneTimePurchaseSupported()) {
                            billingProcessor.purchase(About.this, REMOVE_ADS_ID);
                        } else {
                            Snack.show(getWindow().getDecorView().getRootView(), R.string.purchase_n_supported);
                        }
                    }
                });

        ((ViewGroup) findViewById(R.id.holder)).addView(builder.build());

    }

    private void setup() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.about);

        if (BillingProcessor.isIabServiceAvailable(this))
            billingProcessor = new BillingProcessor(this, getString(R.string.app_key), this);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return true;
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Log.i("BillingProcessor", "Purchased SKU: " + productId);

        if (details.purchaseInfo.purchaseData.productId.equals(REMOVE_ADS_ID))
            Billing.with(this).setRemoveAdsPurchased(true);

    }

    @Override
    public void onPurchaseHistoryRestored() {
        //nothing
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        //nothing
        Log.i("BillingProcessor", "onBillingError: " + errorCode);

        if (error != null)
            error.printStackTrace();
    }

    @Override
    public void onBillingInitialized() {
        //nothing
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!billingProcessor.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        if (billingProcessor != null)
            billingProcessor.release();

        super.onDestroy();
    }
}


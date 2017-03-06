package com.vansuita.passwordvault.ads;

import android.content.Context;
import android.view.View;
import android.view.Window;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.vansuita.passwordvault.BuildConfig;
import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.dev.Device;

/**
 * Created by jrvansuita on 02/03/17.
 */

public class Ads extends AdListener {

    private View holder;
    private Context context;
    private int adUnitId;

    Ads(Context context) {
        this.context = context;
    }

    Ads(Window window) {
        this(window.getDecorView().getRootView());
    }

    Ads(View holder) {
        this(holder.getContext());
        this.holder = holder;
    }

    public static Ads with(Context context) {
        return new Ads(context);
    }

    public static Ads with(Window window) {
        return new Ads(window);
    }

    public Ads setAdUnitId(int adUnitId) {
        this.adUnitId = adUnitId;
        return this;
    }

    private AdRequest getAdRequest() {
        AdRequest adRequest;

        if (BuildConfig.DEBUG) {
            adRequest = new AdRequest.Builder()
                    .addTestDevice(Device.getId(context))
                    .build();
        } else {
            adRequest = new AdRequest.Builder().build();
        }

        return adRequest;
    }


    public void showBannerAd() {
        if (holder != null) {
            AdView adView = ((AdView) holder.findViewById(R.id.ad_view));
            if (adView != null) {
                adView.loadAd(getAdRequest());
                adView.setAdListener(this);
            }
        }
    }


    private InterstitialAd fullScreen;

    public void loadFullScreenAd() {
        if (context != null && adUnitId != 0) {
            if (fullScreen == null) {
                fullScreen = new InterstitialAd(context);
                fullScreen.setAdUnitId(context.getString(adUnitId));
                fullScreen.setAdListener(this);
            }

            fullScreen.loadAd(getAdRequest());
        }
    }

    public void showFullScreenAd() {
        if (fullScreen != null && fullScreen.isLoaded()) {
            fullScreen.show();
        }
    }

    @Override
    public void onAdClosed() {
        super.onAdClosed();

        if (fullScreen != null)
            loadFullScreenAd();
    }

    @Override
    public void onAdFailedToLoad(int i) {
        super.onAdFailedToLoad(i);
    }

    @Override
    public void onAdLeftApplication() {
        super.onAdLeftApplication();
    }

    @Override
    public void onAdOpened() {
        super.onAdOpened();
    }

    @Override
    public void onAdLoaded() {
        super.onAdLoaded();
    }
}

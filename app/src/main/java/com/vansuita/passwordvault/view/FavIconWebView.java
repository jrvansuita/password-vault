package com.vansuita.passwordvault.view;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.vansuita.passwordvault.util.Util;
import com.vansuita.passwordvault.util.Validation;

/**
 * Created by jrvansuita on 20/01/17.
 */

public class FavIconWebView extends WebView {

    public FavIconWebView(Context context) {
        this(context, null);
    }

    public FavIconWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FavIconWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setup();
    }

    private void setup() {
        setBackgroundColor(Color.TRANSPARENT);
        setInitialScale(1);
        getSettings().setLoadWithOverviewMode(true);
        getSettings().setUseWideViewPort(true);
        getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                clear();
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                clear();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                clear();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                clear();
            }
        });
    }


    @Override
    public void loadUrl(String url) {
        if (Validation.isWebsite(url) && Util.internet(getContext())) {
            Uri u = Uri.parse(url);

            url = "http://" + (u.getHost() == null ? u.getPath() : u.getHost()) + "/favicon.ico";

            stopLoading();
            clear();
            super.loadUrl(url);
        }else{
            clear();
        }
    }

    public void clear() {
        super.loadUrl("about:blank");
    }


}

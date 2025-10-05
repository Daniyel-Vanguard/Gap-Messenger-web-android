package com.vddaniyel.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.vddaniyel.R;
import com.vddaniyel.common.activity.BaseActivity;

public class MainActivity extends BaseActivity {

    private WebView mainWebView;
    private WebView adWebView;
    private ValueCallback<Uri[]> uploadMessage;
    private final static int FILE_CHOOSER_RESULT_CODE = 1;
    private static final String ALLOWED_DOMAIN = "gap.im";
    private static final String AD_URL = "https://vddaniyel.top/app/index.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupWebViews();
        showWelcomeToast();
    }

    private void initializeViews() {
        mainWebView = findViewById(R.id.main_webview);
        adWebView = findViewById(R.id.ad_webview);
    }

    private void setupWebViews() {
        // تنظیمات اصلی WebView
        setupMainWebView();
        setupAdWebView();
    }

    private void setupMainWebView() {
        mainWebView.setWebViewClient(new CustomWebViewClient());
        configureWebViewSettings(mainWebView);
        mainWebView.setWebChromeClient(new CustomWebChromeClient());
        mainWebView.loadUrl("https://web.gap.im/");
    }

    private void setupAdWebView() {
        adWebView.setWebViewClient(new WebViewClient());
        configureWebViewSettings(adWebView);
        adWebView.loadUrl(AD_URL);
    }

    private void configureWebViewSettings(WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
    }

    private void showWelcomeToast() {
        Toast.makeText(this, "ساخته شده توسط GHOST\nوبسایت: vddaniyel.top", Toast.LENGTH_LONG).show();
    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url != null && !url.contains(ALLOWED_DOMAIN)) {
                openExternalBrowser(url);
                return true;
            }
            return false;
        }

        private void openExternalBrowser(String url) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "خطا در باز کردن لینک", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class CustomWebChromeClient extends WebChromeClient {
        @Override
        public boolean onShowFileChooser(WebView webView, 
                                         ValueCallback<Uri[]> filePathCallback,
                                         FileChooserParams fileChooserParams) {
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
            }
            uploadMessage = filePathCallback;

            try {
                startActivityForResult(fileChooserParams.createIntent(), FILE_CHOOSER_RESULT_CODE);
                return true;
            } catch (Exception e) {
                uploadMessage = null;
                return false;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE && uploadMessage != null) {
            handleFileChooserResult(resultCode, data);
        }
    }

    private void handleFileChooserResult(int resultCode, Intent data) {
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK && data != null && data.getDataString() != null) {
            results = new Uri[]{Uri.parse(data.getDataString())};
        }
        uploadMessage.onReceiveValue(results);
        uploadMessage = null;
    }

    @Override
    public void onBackPressed() {
        if (mainWebView.canGoBack()) {
            mainWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}

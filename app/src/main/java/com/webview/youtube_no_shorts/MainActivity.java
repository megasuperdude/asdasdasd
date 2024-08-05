package com.webview.youtube_no_shorts;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private static final String TAG = "MainActivity";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebChromeClient(new MyChrome());

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                injectJavaScript();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                injectJavaScript();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                injectJavaScript();
            }

            private void injectJavaScript() {
                try {
                    webView.evaluateJavascript(
                            "(function() { " +
                                    "    var element = document.getElementsByClassName('pivot-bar-item-tab pivot-shorts'); " +

                                    "    if (element.length > 0) { " +
                                    "       document.getElementsByTagName('ytm-pivot-bar-item-renderer')[1].remove(); " +
                                    "    } " +

                                    "    var element_lento = document.getElementsByClassName('modern-typography reel-shelf-responsive-layout'); " +
                                    "    if (element_lento.length > 0 && element_lento[0].innerText.substring(0, 10).includes('Shorts')) { " +
                                    "       document.getElementsByClassName('modern-typography reel-shelf-responsive-layout')[0].remove(); " +
                                    "    } " +

                                    "    var element_lento = document.getElementsByClassName('item modern-typography'); " +
                                    "    if (element_lento.length > 0 && element_lento[0].innerText.substring(0, 10).includes('Shorts')) { " +
                                    "       document.getElementsByClassName('item modern-typography')[0].remove(); " +
                                    "    } " +

                                    "    var element = document.getElementById('player-container-id'); " +
                                    "    if (element) { " +
                                    "        element.style.top = '0px'; " +
                                    "        var currentUrl = window.location.href; " +
                                    "        if (currentUrl.includes('watch')) { " +
                                    "           var element = document.getElementsByClassName('watch-content full-bleed-wn-thumbs fresh-feeds-dismissals'); " +
                                    "           element[0].style.marginTop = '-50px'; " +
                                    "           var element = document.getElementsByClassName('chip-bar chips-fixed-positioning'); " +
                                    "           if (element.length > 0) { " +
                                    "           element[0].style.marginTop = '-50px'; " +
                                    "           } " +
                                    "           document.getElementById('header-bar').remove(); " +
                                    "    } " +
                                    "    } " +
                                    "})()",
                            null
                    );
                } catch (Exception e) {
                    Log.e(TAG, "Error injecting JavaScript: ", e);
                }
            }

        });

        webView.loadUrl("https://www.youtube.com/");
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private class MyChrome extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyChrome() {}

        public Bitmap getDefaultVideoPoster()
        {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView()
        {

            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
        {
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }


}
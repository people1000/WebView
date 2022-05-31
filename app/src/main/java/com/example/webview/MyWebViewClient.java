package com.example.webview;

import android.annotation.TargetApi;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

//  чтобы открывать ссылки в своей программе, нужно переопределить класс WebViewClient и позволить нашему приложению обрабатывать ссылки

class MyWebViewClient extends WebViewClient {

    @TargetApi(Build.VERSION_CODES.N)

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url2) {
        view.loadUrl(url2);
        return true;
    }
}
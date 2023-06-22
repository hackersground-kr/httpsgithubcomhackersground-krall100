package com.example.all100;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class WebViewDemo extends AppCompatActivity {

    private WebView mWebView;
    private WebSettings mWebSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.do_activity_web_view_demo);

        mWebView = findViewById(R.id.wvLayout);
        mWebView.loadUrl("https://appsvr-hg1017.azurewebsites.net/"); //연결할 url
    }
}
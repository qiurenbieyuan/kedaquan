package com.yangs.just.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.yangs.just.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yangs on 2017/3/1.
 */

public class Browser extends AppCompatActivity implements View.OnClickListener, Toolbar.OnMenuItemClickListener {
    private WebView webview;
    private ProgressBar bar;
    private Toolbar toolbar;
    private String url;
    private String cookie;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.browser_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        url = bundle.getString("url");
        cookie = bundle.getString("cookie");
        setContentView(R.layout.findweb_layout);
        toolbar = (Toolbar) findViewById(R.id.browser_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arraw_back_white);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(this);
        bar = (ProgressBar) this.findViewById(R.id.findweb_myProgressBar);
        webview = (WebView) this.findViewById(R.id.tencet_webview_findweb);
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                toolbar.setTitle(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                bar.getProgressDrawable().setColorFilter(Color.rgb(51, 122, 183),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                if (newProgress == 100) {
                    bar.setVisibility(View.GONE);
                    if (view.getUrl().contains("http://202.195.195.198")) {
                        view.loadUrl("javascript:{var id=document.getElementById('iframe2');id.style.height='388px';id.style.height=(id.contentWindow.document.body.scrollHeight+12).toString()+'px';console.log('已优化');}");
                    }
                } else {
                    if (View.GONE == bar.getVisibility()) {
                        bar.setVisibility(View.VISIBLE);
                    }
                    bar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(view.getContext()).setTitle("提示").setMessage(message)
                        .setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        result.confirm();
                    }
                }).create().show();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message,
                                       final JsResult result) {
                new AlertDialog.Builder(view.getContext()).setCancelable(false)
                        .setMessage(message).setTitle("提示").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                }).create().show();
                return true;
            }

        });
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setAppCacheMaxSize(1024 * 1024 * 5);
        webSettings.setUserAgentString("myangs Mozilla/5.0 (Linux; Android; kedaquan) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.125 Mobile Safari/537.36");
        if (cookie != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                CookieSyncManager.createInstance(this);
            }
            CookieManager cookieManager = CookieManager.getInstance();
            for (String t : cookie.split(";")) {
                cookieManager.setCookie(url, t);
            }
        }
        webview.setInitialScale(100);
        webview.addJavascriptInterface(new JavaJS(this), "JavaJS");
        webview.loadUrl(url);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.titlebar_iv_left) {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webview.destroy();
        webview = null;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.browser_menu_refresh:
                webview.reload();
                break;
            case R.id.browser_menu_goto:
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(url);
                intent.setData(content_url);
                startActivity(intent);
                break;
        }
        return true;
    }
}

class JavaJS {

    private Activity activity;

    public JavaJS(Activity activity) {
        this.activity = activity;
    }

    @JavascriptInterface            //5.0以上必须加注解才允许被执行
    public void fun(final String user, final String pwd) {
        Intent data = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("user", user);
        bundle.putString("pwd", pwd);
        bundle.putString("term", APPAplication.term);
        data.putExtra("data", bundle);
        activity.setResult(2, data);
        activity.finish();
    }

    @JavascriptInterface
    public String postPwd() {
        return APPAplication.save.getString("pwd", "");
    }
}

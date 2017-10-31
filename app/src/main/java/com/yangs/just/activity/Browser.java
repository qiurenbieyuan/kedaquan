package com.yangs.just.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.yangs.just.R;
import com.yangs.just.score.VpnSource;

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
        setContentView(R.layout.browser_layout);
        toolbar = (Toolbar) findViewById(R.id.browser_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arraw_back_white);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(this);
        bar = (ProgressBar) findViewById(R.id.findweb_myProgressBar);
        webview = (WebView) findViewById(R.id.browser_webview);
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                toolbar.setTitle(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    if (bar != null)
                        bar.setVisibility(View.GONE);
                    if (view != null & view.getUrl().contains("http://202.195.195.198")) {
                        view.loadUrl("javascript:{var id=document.getElementById('iframe2');" +
                                "id.style.height='388px';" +
                                "id.style.height=(id.contentWindow.document.body.scrollHeight+12)" +
                                ".toString()+'px';}");
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
                AlertDialog.Builder b = new AlertDialog.Builder(Browser.this
                        , R.style.AlertDialogCustom);
                b.setTitle("提示");
                b.setMessage(message);
                b.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(Browser.this
                        , R.style.AlertDialogCustom);
                b.setTitle("确定");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                });
                b.create().show();
                return true;
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
                                      final JsPromptResult result) {
                final View v = View.inflate(view.getContext(), R.layout.browser_prompt_dialog, null);
                AlertDialog.Builder b = new AlertDialog.Builder(Browser.this
                        , R.style.AlertDialogCustom);
                b.setTitle("提示");
                b.setView(v);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String value = ((EditText) v.findViewById(R.id.browser_prompt_dialog_et))
                                .getText().toString();
                        result.confirm(value);
                    }
                });
                b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                });
                b.create().show();
                return true;
            }
        });
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (url.startsWith("http") || url.startsWith("https") || url.startsWith("about")) {
                    return super.shouldInterceptRequest(view, url);
                } else if (url.startsWith("tel") || url.startsWith("mqq") || url.startsWith("tencent")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return super.shouldInterceptRequest(view, view.getUrl());
                } else {
                    return super.shouldInterceptRequest(view, view.getUrl());
                }
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
    protected void onDestroy() {
        super.onDestroy();
        webview.removeAllViews();
        webview.setVisibility(View.GONE);
        webview.destroy();
        if (url.contains("vpn.just.edu.cn")) {
            APPAplication.showToast("正在退出vpn系统", 0);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    VpnSource.exitVpn2();
                }
            }).start();
        }
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

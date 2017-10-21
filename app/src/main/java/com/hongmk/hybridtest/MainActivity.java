package com.hongmk.hybridtest;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {
    private static final String HOME_URL = "http://172.16.2.8/#!/";
    private static final String SIGNUP_URL = "http://172.16.2.8/#!/signup";
    private static final String USERLIST_URL ="http://172.16.2.8/#!/user/list";
    private WebView webview;

    final class WebBrowserClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            final JsResult fResult = result;
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("Javascript Alert");
            dialog.setMessage(message);
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    fResult.confirm();
                }
            });
            dialog.show();
            return super.onJsAlert(view, url, message, result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webview = (WebView)findViewById(R.id.webview);

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.setWebChromeClient(new WebBrowserClient());
        webview.loadUrl(HOME_URL);

    }

    public void goHome(View view) {
        webview.loadUrl(HOME_URL);
    }

    public void goSignup(View view) {
        webview.loadUrl(SIGNUP_URL);
    }

    public void goUserList(View view) {
        webview.loadUrl(USERLIST_URL);
    }
}

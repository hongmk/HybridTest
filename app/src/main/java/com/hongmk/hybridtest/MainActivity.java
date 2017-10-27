package com.hongmk.hybridtest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class MainActivity extends AppCompatActivity {
    private static final String HOME_URL = "http://172.16.2.8:9000/#!/";
    private static final String SIGNUP_URL = "http://172.16.2.8:9000/#!/signup";
    private static final String USERLIST_URL = "http://172.16.2.8:9000/#!/user/list";
    private static final String LOGIN_URL = "http://172.16.2.8:9000/#!/user/login";
    private WebView webview;

    //dialog 용으로만 주로사용
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

    //webview 페이지의 동작을 후킹하여 앱에서 동작을 시키고 싶을 때 사용
    class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            Log.i("URL=", url);

            if (url.equals("login:")) {
                LayoutInflater layoutInflater =
                        (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View loginView = layoutInflater.inflate(R.layout.login, null); //값을 꺼내려면 final을 붙여야함


                AlertDialog.Builder loginDialog = new AlertDialog.Builder(MainActivity.this);
                loginDialog.setTitle("Login");
                loginDialog.setMessage("아이디와 비밀번호 확인하세요");
                loginDialog.setView(loginView);
                loginDialog.setPositiveButton("로그인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView idText = (TextView) loginView.findViewById(R.id.user_id);
                        TextView passwordText = (TextView) loginView.findViewById(R.id.user_password);
                        Toast.makeText(MainActivity.this, idText.getText() + "/" +
                                passwordText.getText(), Toast.LENGTH_LONG).show();

                        webview.loadUrl(USERLIST_URL);
                    }
                });
                loginDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        webview.loadUrl(LOGIN_URL);
                    }
                });
                loginDialog.show();
                return true;
            } else {
                String urls[] = url.split(":");

                if (urls.length > 2 && urls[1].equals("detail")) {
                    String params[] = urls[2].split("&");
                    Log.i("urls[2]", urls[2]);
                    try {
                        params[1] = URLDecoder.decode(params[1], "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.i("params[0]", params[0]);
                    Log.i("params[1]", params[1]);
                    Log.i("params[2]", params[2]);
                    LinearLayout popup = (LinearLayout) findViewById(R.id.popup);
                    TextView popupText = (TextView) findViewById(R.id.popup_text);
                    popup.setVisibility(View.VISIBLE);
                    popupText.setText(params[1]);

                    return true;
                }
            }

            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webview = (WebView) findViewById(R.id.webview);

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webview.setWebChromeClient(new WebBrowserClient());
        webview.setWebViewClient(new MyWebViewClient());
        webview.loadUrl(HOME_URL);


        //토큰값이 없으면 로그인화면으로 이동
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        if (pref.getString("token", "").equals("")) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

    }


    public void goHome(View view) {
        webview.loadUrl(HOME_URL+"?os=android"); //안드로이드에서 접속을 웹페이지쪽에 보내줄때 사용
    }

    public void goSignup(View view) {
        webview.loadUrl(SIGNUP_URL);
    }

    public void goUserList(View view) {
        webview.loadUrl(USERLIST_URL);
    }

    public void goLogin(View view) {
        webview.loadUrl(LOGIN_URL);
    }

    public void closePopup(View view) {
        LinearLayout popup = (LinearLayout)findViewById(R.id.popup);
        popup.setVisibility(View.GONE);
    }

    //로그아웃
    public void logout(View view) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("token");
        editor.commit();
        finish();
    }
}

package com.bookkos.bircle;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EncodingUtils;

import java.util.ArrayList;

public class EquipmentListWebViewActivity extends Activity {

    public static final String PREFERENCES_FILE_NAME = "user_preference";

    private Activity _activity;
    private Context _context;

    private String loginId;
    private int groupId;
    private String secret;
    public int flag=0;

    private ActionBar actionBar;
    private WebView webView;

    public boolean deleteUserFlag = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_list_web_view);

        _context = getApplicationContext();
        _activity = this;
        actionBar = getActionBar();
        actionBar.setTitle("SnapBorrow-Equipment(Web)");

        loginId = "";
        secret = "";


        getUserData();

        HttpClient httpClient = new DefaultHttpClient();

        HttpPost post = new HttpPost(BircleTools.BircleHome+"/equipment");

        ArrayList<NameValuePair> value = new ArrayList<NameValuePair>();

        String postData = "login_id=" + loginId + "&password=" + secret + "&group_id=" + groupId+"&list_flag="+flag;
//		String postData = "login_id=" + loginId + "&password=" + secret;
        webView = (WebView)findViewById(R.id.equipment_web_view);
        if (webView==null)
            Log.v("EquipmentWebView", "findViewbyID Error!");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Toast.makeText(_context, message, Toast.LENGTH_SHORT).show();
                return super.onJsAlert(view, url, message, result);
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
       // webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //  webView.addJavascriptInterface(new JSInterface(this,_context,webView), "Android");

//		webView.loadDataWithBaseURL(null, body, "text/html", "UTF-8", null);
//		webView.loadUrl("https://bms-dev.herokuapp.com");
        webView.postUrl(BircleTools.BircleHome+"/equipment", EncodingUtils.getBytes(postData, "UTF-8"));
    }
    @Override
    public void onPause() {
        super.onPause();

        webView.clearCache(true);
        webView.clearHistory();
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.web_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_back_camera:
                Intent intent = new Intent(_context, EquipmentMana.class);
                startActivity(intent);
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        else if(deleteUserFlag == true) {
            return true;
        }
        else {
            Intent intent = new Intent(_context,EquipmentMana.class);
            startActivity(intent);
            finish();
            return false;
        }

//	    return super.onKeyDown(keyCode, event);
    }

    private void getUserData() {
        // preferenceフォルダにあるxmlファイルから値を取得する
        SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
        //
        if(settings == null) {
            return ;
        }

        loginId = settings.getString("login_id", "");
        secret = settings.getString("password", "");
        groupId = (int) settings.getLong("group_id", 0);
    }
}

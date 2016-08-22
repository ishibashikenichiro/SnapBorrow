package com.bookkos.bircle;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.apache.http.util.EncodingUtils;

public class ManualActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

      WebView  webview = (WebView) findViewById(R.id.webView2);
        webview.loadUrl("http://www.baidu.com/");
        if (webview==null)
            Log.v("EquipmentWebView", "findViewbyID Error!");

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
            }
        });
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                return super.onJsAlert(view, url, message, result);
            }
        });

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setBuiltInZoomControls(true);
      //  webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //  webView.addJavascriptInterface(new JSInterface(this,_context,webView), "Android");

//		webView.loadDataWithBaseURL(null, body, "text/html", "UTF-8", null);
//		webView.loadUrl("https://bms-dev.herokuapp.com");
        webview.loadUrl(BircleTools.BircleHome + "/manual.htm");
    }

}

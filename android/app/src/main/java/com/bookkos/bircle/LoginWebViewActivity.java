package com.bookkos.bircle;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;

import com.bookkos.bircle.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.JsResult;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;



public class LoginWebViewActivity extends Activity {
	
	public static final String PREFERENCES_FILE_NAME = "user_preference";
	
	private Activity _activity;
	private Context _context;
	
	private String loginId;
	private int groupId;
	private String secret;
	
	private ActionBar actionBar;
	private WebView webView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		_context = getApplicationContext();
		_activity = this;
		actionBar = getActionBar();
		actionBar.setTitle("Snap Borrow(Web)");
		
		loginId = "";
		secret = "";
		
		setContentView(R.layout.web_view);
				
		HttpClient httpClient = new DefaultHttpClient();
		
		HttpPost post = new HttpPost(BircleTools.BircleHome);
		
		ArrayList<NameValuePair> value = new ArrayList<NameValuePair>();
		
		webView = (WebView)findViewById(R.id.webView);
		
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}

			@Override
			public void onPageFinished(WebView view, String url){
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
		//webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		
//		webView.loadDataWithBaseURL(null, body, "text/html", "UTF-8", null);
		webView.loadUrl(BircleTools.BircleHome);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		webView.clearCache(true);
		webView.clearHistory();
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.login_web_view, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_back_login:
			Intent intent = new Intent(_context, LoginActivity.class);
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
		else {
			Intent intent = new Intent(_context, LoginActivity.class);
			startActivity(intent);
			finish();
			return false;
		}

	}
}
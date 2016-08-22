package com.bookkos.bircle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class JSInterface {
	private WebViewActivity _activity;
	private Context _context;
	private WebView _webView;
	
	private int userId;
	private int groupId;
	private String regId;
	private String groupName;
	
	public static final String PREFERENCES_FILE_NAME = "user_preference";
	
	public JSInterface(WebViewActivity activity, Context context, WebView web_view) {
		_activity = activity;
		_context = context;
		_webView = web_view;
	}
	
	@JavascriptInterface
	public void closeWebView() {
		getUserData();
		SharedPreferences settings = _activity.getSharedPreferences(PREFERENCES_FILE_NAME, 0); // 0 -> MODE_PRIVATE
		// SharedPreferencesであるsettingsを編集する
		SharedPreferences.Editor editor = settings.edit();
		
		String userId_str = String.valueOf(userId);
		
		String logout_request_url = "https://bms-dev.herokuapp.com/android_logout?user_id=" + userId + "&group_id=" + groupId;

		HttpConnectRegistOrUnregistDevice httpConnectRegistOrUnregistDevice = new HttpConnectRegistOrUnregistDevice (_context, regId, userId_str, false);
		httpConnectRegistOrUnregistDevice.execute();
		
		HttpConnectLogout httpConnectLogout = new HttpConnectLogout(_context, logout_request_url);
		httpConnectLogout.execute();
		
		editor.remove("user_id");
		editor.remove("group_id");
		editor.remove("logged_in");
		editor.remove("group_name");
		
		editor.commit();
		
		_activity.deleteUserFlag = true;
		
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Intent intent = new Intent(_context, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		_context.startActivity(intent);
		_activity.finish();
	}
	
	private void getUserData() {
		// preferenceフォルダにあるxmlファイルから値を取得する
		SharedPreferences settings = _activity.getSharedPreferences(PREFERENCES_FILE_NAME, 0);
		// 
		if(settings == null) {
			return ;
		}
		
		userId = (int) settings.getLong("user_id", 0);
		groupId = (int) settings.getLong("group_id", 0);
		regId = settings.getString("reg_id", "");
		groupName = settings.getString("group_name", "");
		
	}
}
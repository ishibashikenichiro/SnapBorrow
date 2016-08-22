package com.bookkos.bircle;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class HttpConnectLogin extends AsyncTask<Integer, Integer, LoginData> {
	private Context _context;
	private Activity activity;
	private HttpClient http_client;
	private String request_url;
	private String text;
	private LoginAsyncTaskListener loginAsyncTaskListener;

	private JSONArray itemArray;
	private String json;
	private JSONObject Object;
	private JSONObject flagResultObject;
	private JSONObject userIdResultObject;
	private JSONObject groupIdResultObject;
	private JSONObject groupNameResultObject;
	private int result_flag;
	private int result_user_id;

	private ProgressDialog dialog = null;

	private LoginData loginData;

	public HttpConnectLogin(Activity activity, Context context, String url, LoginAsyncTaskListener login_async_task_listener) {
		this.activity = activity;
		_context = context;
		request_url = url;
		loginAsyncTaskListener = login_async_task_listener;
	}

	@Override
	protected void onPreExecute() {
		// Log.d("GetLocation", "onPreExecute().");
		// ダイアログを表示
		dialog = new ProgressDialog(this.activity);
		dialog.setMessage("認証中...");
		dialog.show();
	}

	protected LoginData doInBackground(Integer... params) {

		try {
			String sUrl = request_url;
			// -1はログイン失敗の判定なので, ネットワークエラーは-2にしておく
			loginData = new LoginData(-2, -2, -2, "");

			HttpParams params1 = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params1, 5000);
			HttpConnectionParams.setSoTimeout(params1, 5000);
			HttpClient httpClient = new DefaultHttpClient(params1);

			// HttpUriRequest httpRequest = new HttpGet(sUrl);
			HttpUriRequest httpRequest = new HttpPost(sUrl);
			HttpResponse httpResponse = null;

			try {
				httpResponse = httpClient.execute(httpRequest);
			} catch (ClientProtocolException e) {
				// 例外処理
			} catch (IOException e) {
				// 例外処理
			}

			if (httpResponse != null
					&& httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				try {
					json = EntityUtils.toString(httpEntity);
				} catch (ParseException e) {
					// 例外処理
				} finally {
					try {
						httpEntity.consumeContent();
					} catch (IOException e) {
						// 例外処理
					}
				}
			}

			httpClient.getConnectionManager().shutdown();

			// TextView 表示用のテキストバッファ
			StringBuffer stringBuffer = new StringBuffer();

			Object = new JSONObject(json);
			itemArray = Object.getJSONArray("result");

			flagResultObject = itemArray.getJSONObject(0);
			userIdResultObject = itemArray.getJSONObject(1);
			groupIdResultObject = itemArray.getJSONObject(2);
			groupNameResultObject = itemArray.getJSONObject(3);
			
			loginData.flag = Integer.parseInt(flagResultObject.get("flag")
					.toString());
			loginData.userId = Integer.parseInt(userIdResultObject.get(
					"user_id").toString());
			loginData.groupId = Integer.parseInt(groupIdResultObject.get("group_id").toString());
			loginData.groupName = groupNameResultObject.get("group_name").toString();
			

		} catch (Exception e) {
			e.printStackTrace();
		}

		return loginData;
	}

	@Override
	protected void onPostExecute(LoginData result) {
		super.onPostExecute(result);
		loginAsyncTaskListener.loginAsyncTaskCallback(result, request_url);
		dialog.dismiss();
	}

}
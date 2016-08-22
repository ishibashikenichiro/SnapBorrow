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



public class HttpConnectPost extends AsyncTask<JSONArray, String, JSONArray>
{
	private Context _context;
	private Activity activity;
	private HttpClient http_client;
	private String requestUrl;
	private String text;

	private JSONArray itemArray;
	private JSONObject Object;
	private JSONObject dataObject;
	private JSONObject statusObject;
	private JSONObject isbnObject;
	private JSONObject titleObject;
	private JSONObject qrObject;
	private JSONObject book_numObject;

	private String status_result;
	private String isbn_result;
	private String qr_result;
	private String title_result;
	private String book_num_result;

	private int module;

	private String json;

	private ProgressDialog dialog = null;
	
	private AsyncTaskListener _asyncTaskListener = null;

	public HttpConnectPost(Activity activity, String url, AsyncTaskListener asyncTaskListener) {
		this.activity = activity;
		//_context = context;
		_asyncTaskListener = asyncTaskListener;
		requestUrl = url;
	}

	@Override
	protected void onPreExecute() {
		if(_asyncTaskListener != null && !this.activity.getClass().getName().equals("com.bookkos.bircle.AlertDialogActivity")) {
			dialog = new ProgressDialog(this.activity);
			dialog.setMessage("通信中...");
			dialog.show();
		}
	}


	protected JSONArray doInBackground(JSONArray... params) {
		String toast_text = null;

		// TODO Auto-generated method stub
		try {
			String sUrl = requestUrl;

			HttpParams params1 = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params1, 3000);
			HttpConnectionParams.setSoTimeout(params1, 3000);
			HttpClient httpClient = new DefaultHttpClient(params1);

			//HttpUriRequest httpRequest = new HttpGet(sUrl);
			HttpUriRequest httpRequest = new HttpPost(sUrl);

			HttpResponse httpResponse = null;

			try {
				httpResponse = httpClient.execute(httpRequest);
			}
			catch (ClientProtocolException e) {
				//例外処理
			}
			catch (IOException e){
				//例外処理
			}
			json = null;
			if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity httpEntity = httpResponse.getEntity();
				Log.d("HttpConnectPost", "statsu code = " + httpResponse.getStatusLine().getStatusCode());
				try {
					json = EntityUtils.toString(httpEntity);
					if(json == null) {
//						Toast.makeText(_context, "レスポンスがありませんでした.", Toast.LENGTH_SHORT);
					}
				}
				catch (ParseException e) {
					//例外処理
				}
				catch (IOException e) {
					//例外処理
				}
				finally {
					try {
						httpEntity.consumeContent();
					}
					catch (IOException e) {
						//例外処理
					}
				}
			}

			httpClient.getConnectionManager().shutdown();

			// TextView 表示用のテキストバッファ
			StringBuffer stringBuffer = new StringBuffer();

			Object = new JSONObject(json);
			itemArray = Object.getJSONArray("result");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return itemArray;

	}

	@Override
	protected void onPostExecute(JSONArray result) {
		super.onPostExecute(result);
		if(_asyncTaskListener != null && !this.activity.getClass().getName().equals("com.bookkos.bircle.AlertDialogActivity")) {
			dialog.dismiss();
		}
		if(_asyncTaskListener != null) {
			_asyncTaskListener.asyncTaskCallback(result, requestUrl);
		}
	}

}

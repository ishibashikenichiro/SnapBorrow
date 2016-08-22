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



public class HttpConnectPostReturnFlag extends AsyncTask<Integer, Integer, Integer>
{
	private Context _context;
	private Activity activity;
	private HttpClient http_client;
	private String request_url;
	private String text;
	private int count;

	private ProgressDialog dialog = null;

	public HttpConnectPostReturnFlag(Activity activity, String url) {
		this.activity = activity;
		request_url = url;
	}

	@Override
	protected void onPreExecute() {
//		dialog = new ProgressDialog(this.activity);
//		dialog.setMessage("通信中...");
//		dialog.show();
	}


	protected Integer doInBackground(Integer... params) {
		String toast_text = null;
		Integer response_flag = 0;

		// TODO Auto-generated method stub
		try {
			String sUrl = request_url;
			HttpClient httpClient = new DefaultHttpClient();
			HttpParams params1 = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params1, 5000);
			HttpConnectionParams.setSoTimeout(params1, 5000);

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
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity httpEntity = httpResponse.getEntity();
				try {
					response_flag = 1;
				}
				catch (ParseException e) {
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

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response_flag;
	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
//		dialog.dismiss();
	}

}

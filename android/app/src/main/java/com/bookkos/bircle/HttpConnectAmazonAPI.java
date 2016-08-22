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



public class HttpConnectAmazonAPI extends AsyncTask<String, String, String>
{
	private Context _context;
	private Activity activity;
	private HttpClient http_client;
	private String requestUrl;
	private String text;
	private int count;

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
	private String result;

	private ProgressDialog dialog = null;
	
	private AsyncTaskListener _asyncTaskListener = null;

	public HttpConnectAmazonAPI(Activity activity, Context context, String url, AsyncTaskListener asyncTaskListener) {
		this.activity = activity;
		_context = context;
		requestUrl = url;
		_asyncTaskListener = asyncTaskListener;
	}

	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(this.activity);
		dialog.setMessage("通信中...");
		dialog.show();
	}


	protected String doInBackground(String... params) {
		String toast_text = null;

		// TODO Auto-generated method stub
		try {
			String sUrl = requestUrl;

//			HttpClient httpClient = new DefaultHttpClient();
//			HttpParams params1 = httpClient.getParams();
			HttpParams params1 = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params1, 10000);
			HttpConnectionParams.setSoTimeout(params1, 10000);
			HttpClient httpClient = new DefaultHttpClient(params1);

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
				try {
					json = EntityUtils.toString(httpEntity);
					Log.d("HttpConnectAmazonAPI_Getjson", json);
					if(json == null) {
//						Toast.makeText(_context, "レスポンスがありませんでした.", Toast.LENGTH_SHORT);
						Log.d("HttpConnectPost", "レスポンスがありませんでした.");
					}
				}
				catch (ParseException e) {
					//例外処理
					Log.d("HttpConnectAmazonAPI", "ParseException");
				}
				catch (IOException e) {
					//例外処理
					Log.d("HttpConnectAmazonAPI", "IOException1");
				}
				finally {
					try {
						httpEntity.consumeContent();
					}
					catch (IOException e) {
						//例外処理
						Log.d("HttpConnectAmazonAPI", "IOException2");
					}
				}
			}
			Log.d("HttpConnectAmazonAPI", "status code = " + httpResponse.getStatusLine().getStatusCode());

			httpClient.getConnectionManager().shutdown();

			Object = new JSONObject(json);
//			itemArray = Object.getJSONArray("result");
			result = Object.getString("result");
//			Log.d("dataObject", itemArray.toString());
			Log.d("dataObject", "result = " + result);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		return json;
//		return itemArray;
		return result;

	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if(_asyncTaskListener != null) {
			_asyncTaskListener.amazonAPIAsyncTaskCallback(result, requestUrl);
			dialog.dismiss();
		}
		dialog.dismiss();
	}

}

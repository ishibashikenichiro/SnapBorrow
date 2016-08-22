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

public class HttpConnectGCMServer extends AsyncTask<Void, Void, Void>
{
	private Context _context;
	private Activity activity;
	private HttpClient http_client;
	private String requestUrl;
	
	public HttpConnectGCMServer(Activity activity, Context context, String url) {
		this.activity = activity;
		_context = context;
		requestUrl = url;
	}
	
	@Override
	protected void onPreExecute() {

	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO 自動生成されたメソッド・スタブ
					
		HttpClient httpClient = new DefaultHttpClient();
        HttpParams params1 = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params1, 5000);
        HttpConnectionParams.setSoTimeout(params1, 5000);
        
        //HttpUriRequest httpRequest = new HttpGet(sUrl);
        HttpUriRequest httpRequest = new HttpPost(requestUrl);
        try {
			httpClient.execute(httpRequest);
		} catch (ClientProtocolException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return null;
	}

}
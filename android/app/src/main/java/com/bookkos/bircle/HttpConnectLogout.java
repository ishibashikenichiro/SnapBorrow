package com.bookkos.bircle;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


public class HttpConnectLogout extends AsyncTask<Void, Void, Void>
{
	private Context _context;

	private String requestUrl;

	public HttpConnectLogout(Context context, String request_url) {
		_context = context;
		requestUrl = request_url;
	}

	@Override
	protected void onPreExecute() {

	}

	@Override
	protected Void doInBackground(Void... params) {
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
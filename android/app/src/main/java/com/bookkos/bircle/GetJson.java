package com.bookkos.bircle;

import java.io.ByteArrayOutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.util.Log;

public class GetJson {
	private JSONObject rootObject = null;
	
	public GetJson(){
	}
	
	protected JSONObject getJson(String uri){ //uri����JSON�I�u�W�F�N�g���擾����
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(uri);
		HttpResponse response = null;

		try{
			response = httpClient.execute(request);
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        response.getEntity().writeTo(outputStream);
	        String data;
	        data = outputStream.toString();
	        int statusCode = 0;
	        statusCode = response.getStatusLine().getStatusCode();
	        switch(statusCode){
	        case HttpStatus.SC_OK:
	        	Log.d("StatusCode", "200 OK");
		        rootObject = new JSONObject(data);
		        break;
	        case HttpStatus.SC_NOT_FOUND: 
	        	Log.d("StatusCode", "404 NOT FOUND");
		        break;
	        case HttpStatus.SC_FORBIDDEN:
	        	Log.d("StatusCode", "403 FORBIDDEN");
		        break;
	        case HttpStatus.SC_INTERNAL_SERVER_ERROR:
	        	Log.d("StatusCode", "500 INTERNAL SERVER ERROR");
		        break;
	        case HttpStatus.SC_SERVICE_UNAVAILABLE:
	        	Log.d("StatusCode", "503 SERVICE UNAVAILABLE");
		        break;
	        case HttpStatus.SC_BAD_GATEWAY:
	        	Log.d("StatusCode", "502 BAD GATEWAY");
		        break;
	        }	        
		} catch(Exception e){
			e.printStackTrace();
		}finally{
			httpClient.getConnectionManager().shutdown();			
		}
		
		return rootObject;
	}
}

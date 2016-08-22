package com.bookkos.bircle;

import org.json.JSONArray;
import org.json.JSONObject;

public interface AsyncTaskListener {
	public void asyncTaskCallback(JSONArray jsonArray, String request_url);
	public void bookRegisterAsyncTaskCallback(JSONArray jsonArray, String request_url);
	public void amazonAPIAsyncTaskCallback(String result, String request_url);
	public void getStatusAsyncTaskCallback(JSONObject jsonObject, String request_url);
}
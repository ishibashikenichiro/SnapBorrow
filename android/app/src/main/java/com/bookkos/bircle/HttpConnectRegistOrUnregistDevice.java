package com.bookkos.bircle;

import android.content.Context;

import android.os.AsyncTask;


public class HttpConnectRegistOrUnregistDevice extends AsyncTask<Void, Void, Void>
{
	private Context _context;
	
	private String regId;
	private String userId;
	private Boolean regOrUnRegFlag;
	
	public HttpConnectRegistOrUnregistDevice(Context context, String registrationId, String userIdStr, Boolean bool) {
		_context = context;
		regId = registrationId;
		userId = userIdStr;
		regOrUnRegFlag = bool;
	}
	
	@Override
	protected void onPreExecute() {

	}

	@Override
	protected Void doInBackground(Void... params) {
		if(regOrUnRegFlag) {
			ServerUtilities.register(_context, regId, userId);
		}
		else {
			ServerUtilities.unregister(_context, regId, userId);
		}
		return null;
	}

}
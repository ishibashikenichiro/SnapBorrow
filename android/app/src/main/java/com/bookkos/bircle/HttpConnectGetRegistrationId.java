package com.bookkos.bircle;

import static com.bookkos.bircle.CommonUtilities.SENDER_ID;
import static com.bookkos.bircle.LoginActivity.regId;

import java.io.IOException;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


public class HttpConnectGetRegistrationId extends AsyncTask<Void, Void, Void>
{
	private Context _context;
	
	public HttpConnectGetRegistrationId(Context context) {
		_context = context;
	}
	
	@Override
	protected void onPreExecute() {

	}

	@Override
	protected Void doInBackground(Void... params) {
		// registration_idの確認, なかったら発行
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(_context);
		try {
			regId = gcm.register(SENDER_ID);
		} catch (IOException e1) {
			Log.d("onCreate() in LoginActivity", "Colud not get regId");
			e1.printStackTrace();
		}
		return null;
	}

}
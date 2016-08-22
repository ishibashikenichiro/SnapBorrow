package com.bookkos.bircle;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;


public class NotificationIntentService extends IntentService {

	public NotificationIntentService(String name) {
		super(name);
	}
	
	public NotificationIntentService() {
		super("NotificationIntentService");
	}

	@Override
	protected void onHandleIntent(Intent data) {
		Intent intent = new Intent();
		intent.setAction("TIMER_FINISHED");
		sendBroadcast(intent);
	}
}
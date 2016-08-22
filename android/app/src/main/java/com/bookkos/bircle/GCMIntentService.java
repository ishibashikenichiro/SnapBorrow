package com.bookkos.bircle;

import static com.bookkos.bircle.CommonUtilities.EXTRA_MESSAGE;
import static com.bookkos.bircle.CommonUtilities.SENDER_ID;
import static com.bookkos.bircle.CommonUtilities.displayMessage;
import static com.bookkos.bircle.GCMIntentService.notificationId;
import static com.bookkos.bircle.LoginActivity.PREFERENCES_FILE_NAME;
import static com.bookkos.bircle.LoginActivity.regId;

import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.bookkos.bircle.CaptureActivity;
import com.bookkos.bircle.R;
import com.bookkos.bircle.ServerUtilities;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

public class GCMIntentService extends GCMBaseIntentService {
	
	public static String messageFromServer;
	public static int notificationId;
	
	private String userId;

	@Override
	protected void onError(Context context, String registrationId) {
		
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		notificationId = 0;
		String message = intent.getExtras().getString(EXTRA_MESSAGE);
		messageFromServer = message;
//        Log.d("onMessage", "message = " + message);
        if(!message.equals("")) {
        	// notifies user
        	generateNotification(context, "お知らせです.");
        }
//        else {
//        	cancelNotification();
//        }
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        regId = registrationId;
        getUserData();
        saveRegId();
        //ServerUtilities.register(context, registrationId, userId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            //ServerUtilities.unregister(context, registrationId);
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.i(TAG, "Ignoring unregister callback");
        }
	}
	
	private static void generateNotification(Context context, String message) {
        int icon = R.drawable.rameicon;
        
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, AlertDialogActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        //notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notification.defaults = Notification.DEFAULT_SOUND;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        Random r = new Random();
        int n = r.nextInt(50);
        notificationManager.notify(notificationId, notification);
    }
	
	private void cancelNotification() {
		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(notificationId);
	}
	
	// アプリに保存されているデータの取り出し
	public void getUserData() {
		// preferenceフォルダにあるxmlファイルから値を取得する
		SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
		// 
		if(settings == null) {
			return ;
		}
		
		userId = String.valueOf((int) settings.getLong("user_id", 0));
	}
	
	// sharedPreferencesにregIdを保存する
	public void saveRegId() {
		SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0); // 0 -> MODE_PRIVATE
		// SharedPreferencesであるsettingsを編集する
        SharedPreferences.Editor editor = settings.edit();
        
        editor.putString("reg_id", regId);
        editor.commit();
	}
	
}
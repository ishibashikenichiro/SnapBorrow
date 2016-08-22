package com.bookkos.bircle;
 
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bookkos.bircle.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import static com.bookkos.bircle.CaptureActivity._context;
import static com.bookkos.bircle.CaptureActivity.groupId;
import static com.bookkos.bircle.CaptureActivity.userId;
import static com.bookkos.bircle.GCMIntentService.messageFromServer;
import static com.bookkos.bircle.GCMIntentService.notificationId;
 
public class AlertDialogFragment extends DialogFragment implements AsyncTaskListener{
	
	private Activity _activity;
		
	// ログイン用preferenceのファイル名
	public static final String PREFERENCES_FILE_NAME = "user_preference";
	
	private BircleBeepManager bircleBeepManager;
	
	private String toastText;
	
	private static String lend_register_url;
	
	private static String bookTitle = null;
	private static String isbn = null;
	private static String groupIdString;
	private static String groupName = null;
	private static String shelfId;
	private static String shelfName = null;
	private static String borrowDate = null;
	
	private int requestCount = 0;
	private Time currentTime;
	private String now;
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	_activity = getActivity();
    	
    	bircleBeepManager = new BircleBeepManager(getActivity());
    	bircleBeepManager.updatePrefs();
    	
    	currentTime = new Time("Asia/Tokyo");
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        this.setCancelable(false);
    	
        String message = messageFromServer;
		JSONObject json_object = null;
		String title = null;
		String book_code = null;
		String group_id = null;
		String group_name = null;
		String shelf_id = null;
		String shelf_name = null;
		String borrow_date = null;
		String auto_return = null;
		
		try {
			json_object = new JSONObject(message);
			title =json_object.get("title").toString();
			book_code =json_object.get("book_code").toString();
			group_id =json_object.get("group_id").toString();
			group_name =json_object.get("group_name").toString();
			shelf_id =json_object.get("shelf_id").toString();
			shelf_name =json_object.get("shelf_name").toString();
			borrow_date =json_object.get("borrow_date").toString();
			auto_return =json_object.get("auto_return").toString();
		} catch (JSONException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
//		Log.d("AlertDialogFragment", "book_code = " + book_code + ", shelf_name = " + shelf_name + ", borrow_date = " + borrow_date);
	
		
		if(auto_return.equals("true")) {
			cancelNotification();
			getCurrentTime();
			bookTitle = title;
			shelfName = shelf_name;
			
			setReturnHistory("本のタイトル :<br /><font color=#3498db>" + bookTitle + "</font><br />返却する場所(本棚) :<br /><font color=#1abc9c>" + shelfName + "</font><br />返却日時 :<br /><font color=#e67e22>" + now + "</font><br /><br />");

			builder.setTitle("お知らせ");
			builder.setMessage("「"+group_name + "」の「" + shelf_name + "」から借りていた「" + title + "」を返却し忘れていた可能性があるので, 自動返却しました. その図書をまだ手元に持っていた場合は, 登録を行いましょう.");
			builder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}
			);
    	}
    	else {
	    	
	    	/*
	    	  messageFromServerは「図書タイトル_ISBN_グループID_グループ名_本棚ID_本棚名_貸出日」
	    	  というアンダーバーで各種情報が繋がった形式になっている
	    	*/
//	    	Log.d("AlertDialogFragment", "messageFromServer = " + messageFromServer);
//	    	bookTitle = messageFromServer.split("_", 0)[0];
//	    	isbn = messageFromServer.split("_", 0)[1];
//	    	groupIdString = messageFromServer.split("_", 0)[2];
//	    	groupName = messageFromServer.split("_", 0)[3];
//	    	borrowDate = messageFromServer.split("_", 0)[4];
	    	
	    	bookTitle = title;
	    	isbn = book_code;
	    	groupIdString = group_id;
	    	groupName = group_name;
	    	borrowDate = borrow_date;
	    	shelfName = shelf_name;
//	    	Log.d("AlertDialogFragment", "book_title = " + bookTitle + "isbn = " + isbn);
	    	lend_register_url = "https://bms-dev.herokuapp.com/lend_register?user_id=" + userId + "&group_id=" + groupIdString + "&qr=1&book_code=" + isbn;
	        
	        builder.setTitle("Bircleからの質問");
			builder.setMessage("あなたは 「" + groupName + "」グループにある, \n「"
					+ bookTitle + "」を \n「" + borrowDate + "」 に借りたことになっています.\nもう本棚に返却しましたか?");
			builder.setPositiveButton("まだ手元に持っている",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final String lend_possess_url = "https://bms-dev.herokuapp.com/lend_possess?book_code=" + isbn + "&group_id=" + groupId;
						lendPossess(lend_possess_url);
					}
				}
			);
			builder.setNeutralButton("もう本棚に返却しておきました",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final String lend_not_possess_url = "https://bms-dev.herokuapp.com/lend_not_possess?book_code=" + isbn + "&group_id=" + groupId;
						lendNotPossess(lend_not_possess_url);
					}
				}
			);
    	}
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }
    
    @Override
    public void onStop() {
        super.onStop();
        getActivity().finish();
    }
    
    private void lendNotPossess(String request_url) {
    	int response_flag = -1;
    	
    	int icon = 0;
		boolean bool = false;
    	
    	HttpConnectLendPossess httpConnectLendPossess = new HttpConnectLendPossess(_activity, request_url);
		httpConnectLendPossess.execute();
		
		try {
			response_flag = httpConnectLendPossess.get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		
		if(response_flag == 1) {
			HttpConnectPost http_connect_return = new HttpConnectPost(_activity, lend_register_url, this);
			http_connect_return.execute();
			Toast.makeText(_context, "通信しています. 少々お待ちください...", Toast.LENGTH_SHORT).show();

//			bircleBeepManager.playBeepSoundAndVibrate(true);
//			toastText = "「" + bookTtle + "」" + "を返しました.";
//			icon = R.drawable.toast_icon3;
//			bool = true;
//			customToast(toastText, icon, bool);
		}
		else {
			Toast.makeText(_context, "データの通信に失敗したようなので再送信しています...", Toast.LENGTH_SHORT).show();
			lendNotPossess(request_url);
		}
    }
    
    private void lendPossess(String request_url) {
    	int response_flag = -1;
    	
    	/*
    		HttpConnectLendPossessはlendNotPosses(図書を返していなかった場合の処理)と
    		lendPossess(図書をまだ借りている場合の処理), 2つのリクエスト用AsyncTaskです
    	*/
    	HttpConnectLendPossess httpConnectLendPossess = new HttpConnectLendPossess(_activity, request_url);
		httpConnectLendPossess.execute();
		
		try {
			response_flag = httpConnectLendPossess.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
		if(response_flag != 1) {
			Toast.makeText(_context, "データの通信に失敗したようなので再送信しています...", Toast.LENGTH_SHORT).show();
			lendPossess(request_url);
		}
    }
    
    private void customToast(String toast_text, int icon, boolean bool) {
//		getActivity().getLayoutInflater();
//		LayoutInflater inflater = getActivity().getLayoutInflater();
		LayoutInflater inflater = _activity.getLayoutInflater();

		int resource = 0;

		if(bool == true) {
			resource = R.layout.success_toast;
		}
		else {
			resource = R.layout.failure_toast;
		}

		View layout = inflater.inflate(resource, null);
		ImageView image = (ImageView) layout.findViewById(R.id.image);
		image.setImageResource(icon);

		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setText(Html.fromHtml(toast_text));

		Toast toast = new Toast(_activity);
		toast.setView(layout);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.show();

		//		Toast.makeText(context, toast_text, Toast.LENGTH_SHORT).show();
	}
    
	// 返却の履歴を作成する
	private void setReturnHistory(String return_history) {
		SharedPreferences settings = _activity.getSharedPreferences(PREFERENCES_FILE_NAME, 0);
		if(settings == null) {
			Toast.makeText(_context, "履歴情報を取得できませんでした. もう一度お試しください.", Toast.LENGTH_LONG).show();
			return ;
		}
		SharedPreferences.Editor editor = settings.edit();
		
		String return_history1 = settings.getString("return_history1_" + userId + "_" + groupId, "");
		String return_history2 = settings.getString("return_history2_" + userId + "_" + groupId, "");
		String return_history3 = settings.getString("return_history3_" + userId + "_" + groupId, "");
		
		if(return_history1 == "") {
			editor.putString("return_history1_" + userId + "_" + groupId, return_history);
		}
		else {
			if(return_history2 == "") {
				editor.putString("return_history2_" + userId + "_" + groupId, return_history1);
				editor.putString("return_history1_" + userId + "_" + groupId, return_history);
			}
			else {
				editor.putString("return_history3_" + userId + "_" + groupId, return_history2);
				editor.putString("return_history2_" + userId + "_" + groupId, return_history1);
				editor.putString("return_history1_" + userId + "_" + groupId, return_history);
			}
		}
				
		editor.commit();
	}
	
	private void getCurrentTime() {
		currentTime.setToNow();
		now = currentTime.year + "年" + (currentTime.month+1) + "月" + currentTime.monthDay + "日 " + 
						currentTime.hour + "時" + currentTime.minute + "分" + currentTime.second + "秒";
	}
	
	private void cancelNotification() {
		NotificationManager notificationManager = (NotificationManager)_activity.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
	}

	@Override
	public void asyncTaskCallback(JSONArray jsonArray, String request_url) {
		getCurrentTime();
		
		JSONArray borrowReturnArray = null;
		JSONObject statusObject = null;
		JSONObject titleObject = null;
		JSONObject shelfIdObject = null;
		JSONObject shelfNameObject = null;
		JSONArray shelfIdArray = null;
		JSONArray shelfNameArray = null;
		String status_result = null;
		String title_result = null;
		String shelf_name_result = null;
		
		borrowReturnArray = jsonArray;
		
		if(borrowReturnArray == null) {
			requestCount++;
			if(requestCount < 5) {
				HttpConnectPost http_connect_return = new HttpConnectPost(_activity, lend_register_url, this);
				http_connect_return.execute();
			}
			else {
				Toast.makeText(_context, "通信できませんでした. インターネット環境の良い場所で再度お試しください.", Toast.LENGTH_LONG).show();
				requestCount = 0;
			}
		}
		else {
			try {
				statusObject = borrowReturnArray.getJSONObject(0);
				status_result = statusObject.get("status").toString();
				
				// JSONから取得しているのはbook_codeという名前だが, 中身は図書のタイトルになっている(要修正)
				titleObject = borrowReturnArray.getJSONObject(1);
				title_result = titleObject.get("book_code").toString();
				
				shelfNameObject = borrowReturnArray.getJSONObject(2);
				shelf_name_result = shelfNameObject.get("shelf_name").toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			bircleBeepManager.playBeepSoundAndVibrate(4);
			toastText = "「" + title_result + "」" + "を返しました.";
			toastText += "<font color=#1abc9c>「" + shelf_name_result + "」</font>に返却してください.";
			int icon = R.drawable.toast_icon3;
			boolean bool = true;
			customToast(toastText, icon, bool);
			setReturnHistory("本のタイトル :<br /><font color=#3498db>" + bookTitle + "</font><br />返却する場所(本棚) :<br /><font color=#1abc9c>" + shelfName + "</font><br />返却日時 :<br /><font color=#e67e22>" + now + "</font><br /><br />");
		}
	}

	@Override
	public void bookRegisterAsyncTaskCallback(JSONArray jsonArray,
			String request_url) {		
	}

	@Override
	public void amazonAPIAsyncTaskCallback(String result, String request_url) {		
	}

	@Override
	public void getStatusAsyncTaskCallback(JSONObject jsonObject,
			String request_url) {		
	}
}
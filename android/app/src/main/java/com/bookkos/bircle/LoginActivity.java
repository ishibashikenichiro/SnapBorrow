package com.bookkos.bircle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;  
import android.content.SharedPreferences;  
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class LoginActivity extends Activity implements LoginAsyncTaskListener {
	
	private String model;
	private String version;
	
	private int displayWidth;
	private int displayHeight;
	private float displayInch;
	private float textSize;
		
	private EditText login_id;
	private EditText password;
	private Button loginButton;
	private Button webViewButton;
	private String resultLoginId;
	private String resultPassword;
	
	private Context _context;
	private Activity activity;
	private LoginActivity _this;
	
	private LoginData loginData;
	
	private int requestCount = 0;
	
	public static String regId;
	
	private int result;
	private final int HOST_SETTING_REQUEST_CODE = 110;
	
	// アプリのpreferenceフォルダを指定 
	public static final String PREFERENCES_FILE_NAME = "user_preference";
	//private static String url = "https://bms-dev.herokuapp.com/~bookkos/android_login.rb?";
//	private static String login_url = BircleTools.BircleHome+"/android_login?";
	private static String login_url;
	private static String regist_device_url;
//	private static String regist_device_url = BircleTools.BircleHome+"/gcm/register?";
	private String login_request_url;
	private String regist_device_request_url;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		_context = getApplicationContext();
		activity = this;
		_this = this;
		
		displayInch = getInch();
		// 基本のテキストのサイズを4インチのスマホに合わせてリサイズ
		textSize = 12 * (displayInch / 4);

		BircleTools.initServerHost(LoginActivity.this);
		if("".equals(BircleTools.BircleHome)){
			Intent intent = new Intent(LoginActivity.this, ServerHostConfigActivity.class);
			startActivityForResult(intent, HOST_SETTING_REQUEST_CODE);
		}
		else{
			exec();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.loginmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.loginmenu:
				Intent intent = new Intent(LoginActivity.this, ServerHostConfigActivity.class);
				startActivityForResult(intent, HOST_SETTING_REQUEST_CODE);
				break;
			case R.id.manual:
				intent = new Intent(this,ManualActivity.class);
				startActivity(intent);
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void exec(){
		login_url = BircleTools.BircleHome + "/android_login?";
		regist_device_url = BircleTools.BircleHome + "/gcm/register?";

		// gcmサーバからregistration_idを取得し, regIdに格納する非同期処理
		HttpConnectGetRegistrationId httpConnectGetRegistrationId = new HttpConnectGetRegistrationId(_context);
		httpConnectGetRegistrationId.execute();

		// ログインしているかを判定する
		// ログインしている場合
		if(loginCheck()) {
			Intent intent = new Intent(_context,EquipmentMana.class);
			startActivity(intent);
			finish();
		}
		//ログインしていない場合
		else {
			// Viewの設置
			setContentView(R.layout.login);

			login_id = (EditText)findViewById(R.id.loginIdText);
			password = (EditText)findViewById(R.id.passwordText);
			loginButton = (Button)findViewById(R.id.loginButton);
			webViewButton = (Button)findViewById(R.id.webViewButton);

			// 画面サイズを取得
			WindowManager window_manager = getWindowManager();
			Display display = window_manager.getDefaultDisplay();
			Point point = new Point();
			display.getSize(point);
			displayWidth = point.x;
			displayHeight = point.y;

			int loginButton_width = displayWidth / 5 * 2;
			int loginButton_height = displayHeight / 12;
			int loginButton_x = (displayWidth - loginButton_width) / 2;
			loginButton.setTranslationX(loginButton_x);
			loginButton.setLayoutParams(new RelativeLayout.LayoutParams(loginButton_width, loginButton_height));
			loginButton.setBackgroundColor(Color.rgb(230, 126, 34));
			RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams)loginButton.getLayoutParams();
			params1.addRule(RelativeLayout.BELOW, R.id.passwordText);
			params1.setMargins(0, 30, 0, 0);
			loginButton.setLayoutParams(params1);

			(loginButton).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					resultLoginId = login_id.getText().toString();
					resultPassword = password.getText().toString();

					if(!CommonUtilities.netWorkCheck(_context)) {
						Toast.makeText(_context, "ネットワークに接続して下さい", Toast.LENGTH_LONG).show();
						return ;
					}
					else {
					}

					login_request_url = login_url + "login_id=" + resultLoginId + "&password=" + resultPassword;

					HttpConnectLogin connect_login = new HttpConnectLogin(activity, _context, login_request_url, _this);
					connect_login.execute();
				}
			});

			int webViewButton_width = displayWidth / 5 * 3;
			int webViewButton_height = displayHeight / 10;
			int webViewButton_x = (displayWidth - webViewButton_width) / 2;
			webViewButton.setTranslationX(webViewButton_x);
			webViewButton.setLayoutParams(new RelativeLayout.LayoutParams(webViewButton_width, webViewButton_height));
			webViewButton.setTextSize(textSize);
			webViewButton.setText("ユーザ登録・グループの作成\nなどはこちらから");
			RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)webViewButton.getLayoutParams();
			params2.addRule(RelativeLayout.BELOW, R.id.loginButton);
			params2.setMargins(0, 30, 0, 0);
			webViewButton.setLayoutParams(params2);

			webViewButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
					intent.setClassName(_context, LoginWebViewActivity.class.getName());
					startActivity(intent);
					finish();
				}
			});
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(HOST_SETTING_REQUEST_CODE == requestCode){
			if(RESULT_OK == resultCode){
				String serverHost = data.getStringExtra("ServerHost");

				String ipInfo = "Server: " + serverHost;
				Toast toast = Toast.makeText(getApplicationContext(), ipInfo, Toast.LENGTH_SHORT);
				toast.show();

				exec();
			}

		}
	}

	@Override
	public void loginAsyncTaskCallback(LoginData login_data, String request_url) {
		loginData = login_data;
		
		// loginData.flagが-2だったらネットワークエラー
		if(loginData.flag == -2) {
			requestCount++;
			if(requestCount < 5) {
				HttpConnectLogin connect_login = new HttpConnectLogin(activity, _context, request_url, _this);
				connect_login.execute();
			}
			else {
				Toast.makeText(_context, "通信できませんでした. インターネット環境の良い場所で再度お試しください.", Toast.LENGTH_LONG).show();
				requestCount = 0;
			}
		}
		else {
			if(loginData.flag == 1) {
				if(loginData.groupId == 0) {
					Toast.makeText(getApplicationContext(), "グループに所属していないので, グループに招待してもらうか, グループを作成しましょう.", Toast.LENGTH_LONG).show();
				}
				else {
					login_request_url = login_url;
					login();
				}
			}
			else {
				login_request_url = login_url;
				Toast.makeText(getApplicationContext(), "ログインIDとパスワードが一致しません!", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	// ログインのチェック
	public boolean loginCheck() {
		// preferenceフォルダにあるxmlファイルから値を取得する
		SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
		// 
		if(settings == null) {
			return false;
		}
		
		int login = (int) settings.getLong("logged_in", 0);
		if(login == 1) {
			int user_id = (int) settings.getLong("user_id", 0);
			int group_id = (int) settings.getLong("group_id", 0);
			
//			String request_url = "https://bms-dev.herokuapp.com/android_action_log?user_id="
//					+ user_id + "&group_id=" + group_id + "&book_code=empty&tag=login&detail=" + model + "_" + version + "&shelf_id=empty";

//			HttpConnectPostReturnFlag send_action_log = new HttpConnectPostReturnFlag(this.activity, request_url);
//			send_action_log.execute();
			return true;
		}
		else {
			return false;
		}
	}
	
	// ログイン処理(SharedPreferencesにログイン状態を保存する)
	public void login() {
		SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0); // 0 -> MODE_PRIVATE
		// SharedPreferencesであるsettingsを編集する
        SharedPreferences.Editor editor = settings.edit();

        // logged-inの値を1に設定する(ログイン状態)
        editor.putLong("logged_in", 1);
        editor.putLong("user_id", loginData.userId);
        editor.putLong("group_id", loginData.groupId);
        editor.putString("group_name", loginData.groupName);
        editor.putString("login_id", resultLoginId);
        editor.putString("password", resultPassword);
        
        // 値を反映する
        editor.commit();
        
        String user_id_str = String.valueOf(loginData.userId);
        HttpConnectRegistOrUnregistDevice httpConnectRegistOrUnregistDevice = new HttpConnectRegistOrUnregistDevice(getApplicationContext(), regId, user_id_str, true);
        httpConnectRegistOrUnregistDevice.execute();
		
		Intent intent = new Intent(_context,EquipmentMana.class);
		intent.putExtra("regId", regId);
		startActivity(intent);
		finish();
	}
	
	private float getInch() {
		// ディスプレイ情報を取得する
		final DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics); 

		// ピクセル数（width, height）を取得する
		final int widthPx = metrics.widthPixels;
		final int heightPx = metrics.heightPixels;

		// dpi (xdpi, ydpi) を取得する
		final float xdpi = metrics.xdpi;
		final float ydpi = metrics.ydpi;

		// インチ（width, height) を計算する
		final float widthInch = widthPx / xdpi;
		final float heightInch = heightPx / ydpi;

		// 画面サイズ（インチ）を計算する
		final double inch = Math.sqrt(widthInch * widthInch + heightInch * heightInch);
		
		return (float)inch;

	}

}
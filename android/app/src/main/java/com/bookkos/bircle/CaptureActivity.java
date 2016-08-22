package com.bookkos.bircle;

import com.bookkos.bircle.GroupSelectActivity;
import com.bookkos.bircle.HttpConnectBookRegister;
import com.bookkos.bircle.HttpConnectGCMServer;
import com.bookkos.bircle.HttpConnectLogout;
import com.bookkos.bircle.HttpConnectRegistOrUnregistDevice;
import com.bookkos.bircle.R;
import com.bookkos.bircle.camera.CameraManager;
import com.bookkos.bircle.result.ResultHandler;
import com.bookkos.bircle.result.ResultHandlerFactory;
// import com.bookkos.bircle.result.supplement.SupplementalInfoRetriever;
// import com.bookkos.bircle.share.ShareActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;

import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.bookkos.bircle.CommonUtilities.SERVER_URL;
import static com.bookkos.bircle.GCMIntentService.notificationId;

public final class CaptureActivity extends Activity implements SurfaceHolder.Callback, DialogListener, AsyncTaskListener, RemoveIsbnListener {

	private static final String TAG = CaptureActivity.class.getSimpleName();

	private static final long DEFAULT_INTENT_RESULT_DURATION_MS = 1500L;
	private static final long BULK_MODE_SCAN_DELAY_MS = 3000L;
	
	// ログイン用preferenceのファイル名
	public static final String PREFERENCES_FILE_NAME = "user_preference";

	private static final String[] ZXING_URLS = { "http://zxing.appspot.com/scan", "zxing://scan/" };
	//private static String url = "http://www31092u.sakura.ne.jp/~g031i043/inrode/connect_mysql_api.rb";
	private static String book_register_url = BircleTools.BircleHome+"/book_register";
	private static String lend_register_url = BircleTools.BircleHome+"/lend_register";
	private static String temporary_lend_register_url =  BircleTools.BircleHome+"/temporary_lend_register";
	private static String catalog_register_url =  BircleTools.BircleHome+"/catalog_register";
	private static String manually_catalog_register_url =  BircleTools.BircleHome+"/manually_catalog_register";
	private static String get_status_url =  BircleTools.BircleHome+"/get_status";
	private static String logout_url = BircleTools.BircleHome+ "/android_logout?";
	private static String gcm_notify_url = SERVER_URL + "/notify?";
	// 電波状態が悪い時などに再リクエストを自動で5回まで行うようにする. その時のカウント用変数
	private int requestCount = 0;

	private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT; 
	private final int MP = ViewGroup.LayoutParams.MATCH_PARENT; 
	
	private float displayInch;
	private int displayWidth;
	private int displayHeight;
	private int titleBarHeight;

	public static final int HISTORY_REQUEST_CODE = 0x0000bacc;

	private static final Collection<ResultMetadataType> DISPLAYABLE_METADATA_TYPES =
			EnumSet.of(ResultMetadataType.ISSUE_NUMBER,
					ResultMetadataType.SUGGESTED_PRICE,
					ResultMetadataType.ERROR_CORRECTION_LEVEL,
					ResultMetadataType.POSSIBLE_COUNTRY);
	
	public static Context _context;
	public static Activity _activity;
	
	private Time currentTime;
	private String now;
	
	// viewの変数
	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private Result savedResultToShow;
	private ActionBar actionBar;
	private ViewfinderView viewfinderView;
	private TextView statusView;
	private View resultView;
	private DrawerLayout leftDrawerLayout;
	private ListView leftDrawer;
	public static Rect scanFrame;
	
	private Button borrowReturnButton;
	private Button registButton;
	private Button returnHistoryButton;
	
	// ヘルプのView専用変数
	private Button helpViewButton;
	private ImageView registHelpView;
	private ImageView returnBorrowHelpView;
	
	// 未登録図書読み取りのView専用変数
	private RelativeLayout registSelectShelfRelativeLayout;
	private LinearLayout textViewLinearLayout;
	private LinearLayout buttonLinearLayout;
	private LinearLayout listViewLinearLayout;
	private ListView shelfListView;
	private TextView tempTextView;
	private Button cancelButton;
	private Button decisionButton;

	// 図書一括登録モードのView専用変数
	private RelativeLayout bookRegistRelativeLayout;
	private LinearLayout bookRegistLinearLayout;
	private LinearLayout bookRegistListViewLinearLayout;
	private ListView bookRegistListView;
	private TextView bookRegistTextView;
	private Button bookRegistCancelButton;
	private BookListViewAdapter bookListViewAdapter;
	
	// 未登録図書読み取り時に使用する変数
	private String registSelectShelfName;
	private String registSelectShelfId;
	private String registSelectShelfIsbn;

	// ZXingが用意した変数
	private Result lastResult;
	private boolean hasSurface;
	private boolean copyToClipboard;
	private IntentSource source;
	private String sourceUrl;
	
	// 図書の一括登録・借用・返却処理をさせるサーバのURL変数
	// (initはパラメータの無いURL用, その他はパラメータのついたリクエストURL用)
	private String initBookRegistUrl;
	private String initLendRegistUrl;
	private String initTemporaryLendRegistUrl;
	public static String initCatalogRegistUrl;
	public static String initManuallyCatalogRegistUrl;
	private String BookRegistUrl;
	private String lendRegistUrl;
	private String temporaryLendRegistUrl;
	private String catalogRegistUrl;
	private String manuallyCatalogRegistUrl;
	public static String getStatusUrl;
	//  private ScanFromWebPageManager scanFromWebPageManager;
	
	private Collection<BarcodeFormat> decodeFormats;
	private Map<DecodeHintType,?> decodeHints;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private BircleBeepManager bircleBeepManager;
	private AmbientLightManager ambientLightManager;
	private TextView textView;
	private TextView modeText;
	private String groupText;
	private String subGroupText;
	public static ArrayList<String> arrayList;
	public String tempRegistIsbn;
	private BarcodeFormat barcode_type;
	// modeFlag = trueの時, 借用・返却モードからの登録処理 | modeFlag = falseの時, 一括登録モードからの登録処理
	private boolean modeFlag;
	// registFlag = 1の時, 登録処理 | registFlag = 0の時, 借用・返却処理
	private int registFlag;
	private boolean helpViewFlag;
	/*
	 * 借用・返却モードから未登録図書を登録する際に, 本棚一覧から選択して登録した後にすぐISBNなどを読むと,
	 * 間髪無く読み取ってしまうため, 一旦待ちの時間を設けるためのフラグ
	*/
	private boolean waitingFlag = false;
	
	// 借用・返却時の図書タイトル&ISBN用変数
	private String resultTitle;
	private String resultIsbn;
	
	// カスタムtoastに表示するテキスト用の変数
	private String toastText;
	private float textSize;

	// 各処理のレスポンス用変数
	private JSONArray amazonAPIResultJSONArray;

	// user_preferenceに保存されているデータの取得用変数
	// useridがpublic static なのはAlertDialogFragmentで使用するため(後ほど対策)
	public static int userId;
	public static int groupId;
	private String regId;
	private String groupName;
	
	// モードを変更した時の枠の色を格納する変数
	public static int strokeColor;
	
	// 強制終了用exception handlerの変数
	private ExceptionHandler exceptionHandler;
	
	ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	CameraManager getCameraManager() {
		return cameraManager;
	}
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		_context = getApplicationContext();
		_activity = this;
		
		currentTime = new Time("Asia/Tokyo");
		
//		exceptionHandler = new ExceptionHandler(_context);	
//		Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
		
		// sharedPreferenceに入ってる, user_idとgroup_idとregistration_idを取得する
		getUserData();

		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		// 画面サイズを取得
		WindowManager window_manager = getWindowManager();
		Display display = window_manager.getDefaultDisplay();
		Point point = new Point();
		display.getSize(point);
		displayWidth = point.x;
		displayHeight = point.y;
		
		displayInch = getInch();
		// 基本のテキストのサイズを4インチのスマホに合わせてリサイズ
		textSize = 17 * (displayInch / 4);
		
		actionBar = getActionBar(); 
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE, ActionBar.DISPLAY_USE_LOGO); 
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(false);
		String title_text = "";
		subGroupText = "";
		groupText = groupName;
		
		if(displayInch < 4.7) {
			title_text = "<small><small><small>現在のグループ: </small></small></small>";
			resizeTitleSizeTooSmall();
		}
		else if(displayInch >= 4.7 && displayInch < 5.5) {
			title_text = "<small><small>現在のグループ: </small></small>";
			resizeTitleSizeSmall();
		}
		else if(displayInch >= 5.5 && displayInch < 6.5) {
			title_text = "<small>現在のグループ: </small>";
			resizeTitleSizeMiddle();
		}
		else if(displayInch >= 6.5 && displayInch < 8) {
			title_text = "<small>現在のグループ: </small>";
			resizeTitleSizeLarge();
		}
		else {
			title_text = "現在のグループ: ";
		}
		String modify_group_text = title_text + "<font color=#FF0000>" + groupName + "</font>";
		actionBar.setTitle(Html.fromHtml(modify_group_text));
		
		Resources resources = _context.getResources();
		int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
		titleBarHeight = resources.getDimensionPixelSize(resourceId);
		
		setContentView(R.layout.capture);
		
		returnBorrowHelpView = (ImageView)findViewById(R.id.return_borrow_help_view);
		returnBorrowHelpView.setImageResource(R.drawable.return_borrow_help);
		returnBorrowHelpView.setTranslationY(displayHeight / 5 + titleBarHeight);
		returnBorrowHelpView.setLayoutParams(new FrameLayout.LayoutParams(displayWidth, displayHeight / 5 + titleBarHeight, Gravity.BOTTOM | Gravity.CENTER));

		registHelpView = (ImageView)findViewById(R.id.regist_help_view);
		registHelpView.setImageResource(R.drawable.regist_help);
		registHelpView.setTranslationY(displayHeight / 5 + titleBarHeight);
		registHelpView.setLayoutParams(new FrameLayout.LayoutParams(displayWidth, displayHeight / 5 + titleBarHeight, Gravity.BOTTOM | Gravity.CENTER));
		registHelpView.setVisibility(View.GONE);
		
		leftDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		leftDrawer = (ListView)findViewById(R.id.left_drawer);
		textView = (TextView)findViewById(R.id.textView);
				
		modeText = (TextView)findViewById(R.id.mode_text);
		modeText.setTextColor(Color.rgb(56, 234, 123));
		modeText.setTextSize(textSize);
		modeText.setTypeface(Typeface.SERIF.MONOSPACE, Typeface.BOLD);
		strokeColor = Color.rgb(56, 234, 123);
		
		borrowReturnButton = (Button)findViewById(R.id.borrowReturnButton);
		registButton = (Button)findViewById(R.id.registButton);
		returnHistoryButton = (Button)findViewById(R.id.return_history_button);
		helpViewButton = (Button)findViewById(R.id.help_view_button);
		
		registSelectShelfRelativeLayout = (RelativeLayout)findViewById(R.id.regist_select_shelf_relative_layout);
		textViewLinearLayout = (LinearLayout)findViewById(R.id.text_view_linear_layout);
		buttonLinearLayout = (LinearLayout)findViewById(R.id.button_linear_layout);
		listViewLinearLayout = (LinearLayout)findViewById(R.id.list_view_linear_layout);
		decisionButton = (Button)findViewById(R.id.decision_button);
		cancelButton = (Button)findViewById(R.id.cancel_button);
		shelfListView = (ListView)findViewById(R.id.shelf_list_view);
		tempTextView = (TextView)findViewById(R.id.temp_text_view);
//		bookListViewAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		bookListViewAdapter = new BookListViewAdapter(_context, R.layout.book_list_row, this);
		
		bookRegistRelativeLayout = (RelativeLayout)findViewById(R.id.book_regist_relative_layout);
		bookRegistLinearLayout = (LinearLayout)findViewById(R.id.book_regist_linear_layout);
		bookRegistListViewLinearLayout = (LinearLayout)findViewById(R.id.book_regist_list_view_linear_layout);
		bookRegistListView = (ListView)findViewById(R.id.book_regist_list_view);
		bookRegistTextView = (TextView)findViewById(R.id.book_regist_text_view);
		bookRegistCancelButton = (Button)findViewById(R.id.book_regist_cancel_button);
		
		registFlag = 0;
		
		int borrowReturnButton_width = displayWidth / 5 * 2;
		int borrowReturnButton_height = displayHeight / 10;
		int borrowReturnButton_x = ((displayWidth / 2) - borrowReturnButton_width) / 2;
		int borrowReturnButton_y = displayHeight / 2 + titleBarHeight;
		borrowReturnButton.setTranslationX(borrowReturnButton_x);
		borrowReturnButton.setTranslationY(borrowReturnButton_y);
		borrowReturnButton.setLayoutParams(new FrameLayout.LayoutParams(borrowReturnButton_width, borrowReturnButton_height));
		borrowReturnButton.setBackgroundColor(Color.rgb(56, 234, 123));
		borrowReturnButton.setText("図書の借用・返却\nモード");
		borrowReturnButton.setTextSize(textSize * 7 / 10);
		borrowReturnButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	arrayList.clear();
            	registFlag = 0;
            	
            	borrowReturnButton.setText("図書の借用・返却\nモード");
            	borrowReturnButton.setEnabled(false);
            	borrowReturnButton.setTextColor(Color.WHITE);
            	borrowReturnButton.setBackgroundColor(Color.rgb(56, 234, 123));

            	registButton.setText("図書の一括登録\nモードに切り替える");
            	registButton.setEnabled(true);
            	registButton.setTextColor(Color.GRAY);
            	registButton.setBackgroundColor(Color.argb(170, 21, 38, 45));
            	
            	modeText.setText("図書の借用・返却モード");
    			modeText.setTextColor(Color.rgb(56, 234, 123));
    			returnBorrowHelpView.setVisibility(View.VISIBLE);
    			registHelpView.setVisibility(View.GONE);
    			strokeColor = Color.rgb(56, 234, 123);
            }
        });
		
		int registButton_width = displayWidth / 5 * 2;
		int registButton_height = displayHeight / 10;
		int registButton_x = (displayWidth / 2) + ((displayWidth / 2) - registButton_width) / 2;
		int registButton_y = displayHeight / 2 + titleBarHeight;
		registButton.setTranslationX(registButton_x);
		registButton.setTranslationY(registButton_y);
		registButton.setLayoutParams(new FrameLayout.LayoutParams(registButton_width, registButton_height));
		registButton.setBackgroundColor(Color.argb(170, 21, 38, 45));
		registButton.setTextColor(Color.GRAY);
		registButton.setTextSize(textSize * 7 / 10);
		registButton.setText("図書の一括登録\nモードに切り替える");
		registButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	arrayList.clear();
            	registFlag = 1;
            	
            	borrowReturnButton.setText("図書の借用・返却\nモードに切り替える");
            	borrowReturnButton.setEnabled(true);
            	borrowReturnButton.setTextColor(Color.GRAY);
            	borrowReturnButton.setBackgroundColor(Color.argb(170, 9, 54, 16));

            	registButton.setText("図書の一括登録\nモード");
            	registButton.setEnabled(false);
            	registButton.setTextColor(Color.WHITE);
            	registButton.setBackgroundColor(Color.rgb(62, 162, 229));

            	modeText.setText("図書の一括登録モード");
    			modeText.setTextColor(Color.rgb(62, 162, 229));
    			returnBorrowHelpView.setVisibility(View.GONE);
    			registHelpView.setVisibility(View.VISIBLE);
    			strokeColor = Color.rgb(62, 162, 229);
            }
        });
				
		returnHistoryButton.setText("返却履歴はこちら");
		returnHistoryButton.setTextSize(textSize * 7 / 10);
		returnHistoryButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				leftDrawerLayout.openDrawer(Gravity.RIGHT);
//				animateTranslationY(bookRegistRelativeLayout, displayHeight, displayHeight - displayHeight / 4 - titleBarHeight);
			}
		});
		getReturnHistory();
		getCurrentTime();
		
		setHelpView();
		setScanUnregisterBookView();
		setBookRegistView();

		arrayList = new ArrayList<String>();
		tempRegistIsbn = "";

		initBookRegistUrl = book_register_url + "?user_id=" + userId + "&group_id=" + groupId;
		initLendRegistUrl = lend_register_url + "?user_id=" + userId + "&group_id=" + groupId;
		initTemporaryLendRegistUrl = temporary_lend_register_url + "?user_id=" + userId + "&group_id=" + groupId;
		initCatalogRegistUrl = catalog_register_url + "?group_id=" + groupId + "&book_code=";
		initManuallyCatalogRegistUrl = manually_catalog_register_url + "?group_id=" + groupId + "&book_code=";
		getStatusUrl = get_status_url + "?group_id=" + groupId + "&user_id=" + userId;

		hasSurface = false;
		
		inactivityTimer = new InactivityTimer(this);
		bircleBeepManager = new BircleBeepManager(this);
		ambientLightManager = new AmbientLightManager(this);

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		toastText = "";
	}
	

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// cameraManagerはここで初期化しないといけない(onCreateだと各種サイズの取得ができない?)
		cameraManager = new CameraManager(getApplication());

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);

//		resultView = findViewById(R.id.result_view);

		handler = null;
		lastResult = null;
		registFlag = 0;
		resetStatusView();

		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
		}

		bircleBeepManager.updatePrefs();
		ambientLightManager.start(cameraManager);

		inactivityTimer.onResume();

		Intent intent = getIntent();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		copyToClipboard = prefs.getBoolean(PreferencesActivity.KEY_COPY_TO_CLIPBOARD, true)
				&& (intent == null || intent.getBooleanExtra(Intents.Scan.SAVE_HISTORY, true));

		source = IntentSource.NONE;
		decodeFormats = null;
		characterSet = null;

		if (intent != null) {

			String action = intent.getAction();
			String dataString = intent.getDataString();

			if (Intents.Scan.ACTION.equals(action)) {

				source = IntentSource.NATIVE_APP_INTENT;
				decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
				decodeHints = DecodeHintManager.parseDecodeHints(intent);

				if (intent.hasExtra(Intents.Scan.WIDTH) && intent.hasExtra(Intents.Scan.HEIGHT)) {
					int width = intent.getIntExtra(Intents.Scan.WIDTH, 0);
					int height = intent.getIntExtra(Intents.Scan.HEIGHT, 0);
					if (width > 0 && height > 0) {
						cameraManager.setManualFramingRect(width, height);
					}
				}

				String customPromptMessage = intent.getStringExtra(Intents.Scan.PROMPT_MESSAGE);
				if (customPromptMessage != null) {
				}

			} else if (dataString != null &&
					dataString.contains("http://www.google") &&
					dataString.contains("/m/products/scan")) {

				source = IntentSource.PRODUCT_SEARCH_LINK;
				sourceUrl = dataString;
				decodeFormats = DecodeFormatManager.PRODUCT_FORMATS;

			} else if (isZXingURL(dataString)) {

				source = IntentSource.ZXING_LINK;
				sourceUrl = dataString;
				Uri inputUri = Uri.parse(dataString);
				decodeFormats = DecodeFormatManager.parseDecodeFormats(inputUri);
				decodeHints = DecodeHintManager.parseDecodeHints(inputUri);
			}

			characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);

		}
	}

	private static boolean isZXingURL(String dataString) {
		if (dataString == null) {
			return false;
		}
		for (String url : ZXING_URLS) {
			if (dataString.startsWith(url)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		ambientLightManager.stop();
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}
	
	@Override
	protected void onStop() {
//		sendActionLog("empty", "END", "ホームキーまたはバックキーが押された", "empty");
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (source == IntentSource.NATIVE_APP_INTENT) {
				setResult(RESULT_CANCELED);
				finish();
				return true;
			}
			if ((source == IntentSource.NONE || source == IntentSource.ZXING_LINK) && lastResult != null) {
				restartPreviewAfterDelay(0L);
				return true;
			}
			break;
		case KeyEvent.KEYCODE_FOCUS:
		case KeyEvent.KEYCODE_CAMERA:
			return true;
			// Use volume up/down to turn on light
//		case KeyEvent.KEYCODE_VOLUME_DOWN:
//			cameraManager.setTorch(false);
//			return true;
//		case KeyEvent.KEYCODE_VOLUME_UP:
//			cameraManager.setTorch(true);
//			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.capture, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		switch (item.getItemId()) {
		case R.id.menu_group_select:
			intent.setClassName(this, GroupSelectActivity.class.getName());
			startActivity(intent);
			break;
		case R.id.menu_web_view:
			intent.setClassName(this, WebViewActivity.class.getName());
			startActivity(intent);
			break;
		case R.id.menu_logout:
			logout();
			break;
		case R.id.menu_open_source_license:
			openSourceLicenseDialog();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}



	private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
		if (handler == null) {
			savedResultToShow = result;
		} else {
			if (result != null) {
				savedResultToShow = result;
			}
			if (savedResultToShow != null) {
				Message message = Message.obtain(handler, R.id.decode_succeeded, savedResultToShow);
				handler.sendMessage(message);
			}
			savedResultToShow = null;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	/**
	 * A valid barcode has been found, so give an indication of success and show the results.
	 *
	 * @param rawResult The contents of the barcode.
	 * @param scaleFactor amount by which thumbnail was scaled
	 * @param barcode   A greyscale bitmap of the camera data which was decoded.
	 */
	//読み取った結果はこのメソッド内で見ることができる
	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
		inactivityTimer.onActivity();
		
		
		lastResult = rawResult;
		barcode_type = rawResult.getBarcodeFormat();
		String text = "";
		ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);

		boolean fromLiveScan = barcode != null;
		if (fromLiveScan) {
			drawResultPoints(barcode, scaleFactor, rawResult);
		}

		switch (source) {
		case NATIVE_APP_INTENT:
		case PRODUCT_SEARCH_LINK:
			handleDecodeExternally(rawResult, resultHandler, barcode);
			break;
		case NONE: //バーコードの読み取り時はこのケースに入る
			// waitingFlagがtrueの時は一旦失敗させて, 間髪無く読み取る行為をやめさせる
			if(waitingFlag == true) {
				restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
				break;
			}
			
			// 読み取り結果がnullになってしまった場合, 処理を抜ける
			if(rawResult == null) {
				restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
				bircleBeepManager.playBeepSoundAndVibrate(0);
				break;
			}
			
			// 読み取れないコードが読まれた場合, 処理を抜ける
			if(barcode_type.toString() != "QR_CODE" && (!lastResult.toString().startsWith("978") && !lastResult.toString().startsWith("979"))) {
				Toast.makeText(_context, "読み取れないコードです!", Toast.LENGTH_SHORT).show();
				restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
				bircleBeepManager.playBeepSoundAndVibrate(0);
				break;
			}

			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

			// customToast用(アイコンと[成功or失敗]が引数)
//			int icon = 0;
//			boolean bool = false;

			//本の登録処理の場合(借用・返却モードで未登録図書を読み取った時に「QRコードを読み取る」を選んだ時もここに入る)
			if(registFlag == 1) {
				String groupIdInQR = "";
				String shelfIdInQR = "";
				//本を何冊か読み込んでいて, 本棚のQRコードを読み取った場合
				if(barcode_type.toString() == "QR_CODE") {
//					exceptionHandler.setShelfId(lastResult.toString());
					// QRコードが[num, num]形式だった場合
//					if(lastResult.toString().indexOf(",") != -1 && !lastResult.toString().matches(".*[a-zA-Z].*+")) {
					if(lastResult.toString().matches("^[1-9][0-9]*,[1-9][0-9]*$")) {
						// split0 はグループID, split1は本棚ID
						String split0 = lastResult.toString().split(",")[0];
						String split1 = lastResult.toString().split(",")[1];
						if(!split0.matches(".*[0-9].*+") || !split1.matches(".*[0-9].*+")) {
							Toast.makeText(_context, "本棚のQRコードを読み取りましょう!", Toast.LENGTH_LONG).show();
							restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
							bircleBeepManager.playBeepSoundAndVibrate(0);
							break;
						}
						
//						groupIdInQR = Integer.parseInt(split0);
//						shelfIdInQR = Integer.parseInt(split1);
						groupIdInQR = split0;
						shelfIdInQR = split1;

						// 違うグループの本棚を読んだ場合
						String group_id = String.valueOf(groupId);
						if(!groupIdInQR.equals(group_id)) {
							Toast.makeText(_context, "選択されているグループの本棚のQRコードを読み取りましょう!", Toast.LENGTH_SHORT).show();
							restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
							bircleBeepManager.playBeepSoundAndVibrate(0);
							break;
						}
					}
					else {
						Toast.makeText(_context, "本棚のQRコードを読み取りましょう!", Toast.LENGTH_LONG).show();
						restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
						bircleBeepManager.playBeepSoundAndVibrate(0);
						break;
					}
					
					// 通常の登録処理
					if(arrayList.size() != 0 && tempRegistIsbn == "") {
//						BookRegistUrl = initBookRegistUrl + "&qr=" + lastResult;
						BookRegistUrl = initBookRegistUrl + "&qr=" + shelfIdInQR;
						for(int i=0; i < arrayList.size(); i++){
							text += arrayList.get(i) + "\n";
							BookRegistUrl = BookRegistUrl + arrayList.get(i);
						}
						
						bookRegister(BookRegistUrl, false, "");
						
						BookRegistUrl = initBookRegistUrl;
//						borrowReturnButton.performClick();
					}
					// 借用・返却モード → 登録だった時(tempRegistIsbnが存在する時)の登録処理
					else if(arrayList.size() == 0 && tempRegistIsbn != "") {
						BookRegistUrl = initBookRegistUrl + "&qr=" + shelfIdInQR + "&book_code[]=" + tempRegistIsbn;
						
						bookRegister(BookRegistUrl, true, tempRegistIsbn);
						
						BookRegistUrl = initBookRegistUrl;
						borrowReturnButton.performClick();
					}
					//ISBNコードを読み込んでいない状態でQRコードを読み取った場合
					else if(arrayList.size() == 0 && tempRegistIsbn == ""){
						Toast.makeText(_context, "本のISBNコードから読み込みましょう!", Toast.LENGTH_SHORT).show();
						restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
						bircleBeepManager.playBeepSoundAndVibrate(0);
						break;
					}

				}
				//ISBNを読み込んだ場合
				else if(barcode_type.toString() != "QR_CODE"){
					// 借用・返却モード → 登録だった時(tempRegistIsbnが存在する時)は, ISBNコードを読み込まないようにする
					if(tempRegistIsbn != "") {
						Toast.makeText(_context, "QRコードを読み込みましょう!", Toast.LENGTH_SHORT).show();
						restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
						bircleBeepManager.playBeepSoundAndVibrate(0);
						break;
					}
					else {
//						exceptionHandler.setBookCode(lastResult.toString());
						catalogRegistUrl =  initCatalogRegistUrl + lastResult;
						
						getBookDataFromAmazon(catalogRegistUrl, lastResult.toString(), null);
					}
				}
			}
			//借用・返却処理の場合
			else if(registFlag == 0){
				if(barcode_type.toString() == "QR_CODE") {
					Toast.makeText(_context, "本のISBNコードを読み込みましょう!", Toast.LENGTH_SHORT).show();
					restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
					bircleBeepManager.playBeepSoundAndVibrate(0);
					break;
				}
//				exceptionHandler.setBookCode(lastResult.toString());
				lendRegistUrl = initLendRegistUrl + "&book_code=" + lastResult;

				bookBorrowReturn(lendRegistUrl, lastResult.toString());

				toastText = "";
				lendRegistUrl = initLendRegistUrl;
			}
//			exceptionHandler.setBookCode("empty");
//			exceptionHandler.setShelfId("empty");

			//連続処理の場合, どのISBNコードを読み込んだが分かるようにする
			if (fromLiveScan && prefs.getBoolean(PreferencesActivity.KEY_BULK_MODE, false)) {
//				if(registFlag == 1 && barcode_type.toString() != "QR_CODE") {
//					Toast.makeText(_context,
//							getResources().getString(R.string.msg_bulk_mode_scanned) + " (" + rawResult.getText() + ')',
//							Toast.LENGTH_SHORT).show();
//				}
//				restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
			} else {
				//handleDecodeInternally(rawResult, resultHandler, barcode);
			}
			break;
		}
	}
	
	@Override
	public void getStatusAsyncTaskCallback(JSONObject jsonObject, String request_url) {
		
	}
	

	/**
	 * Superimpose a line for 1D or dots for 2D to highlight the key features of the barcode.
	 *
	 * @param barcode   A bitmap of the captured image.
	 * @param scaleFactor amount by which thumbnail was scaled
	 * @param rawResult The decoded results which contains the points to draw.
	 */
	private void drawResultPoints(Bitmap barcode, float scaleFactor, Result rawResult) {
		ResultPoint[] points = rawResult.getResultPoints();
		if (points != null && points.length > 0) {
			Canvas canvas = new Canvas(barcode);
			Paint paint = new Paint();
			paint.setColor(getResources().getColor(R.color.result_points));
			if (points.length == 2) {
				paint.setStrokeWidth(4.0f);
				drawLine(canvas, paint, points[0], points[1], scaleFactor);
			} else if (points.length == 4 &&
					(rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A ||
					rawResult.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
				// Hacky special case -- draw two lines, for the barcode and metadata
				drawLine(canvas, paint, points[0], points[1], scaleFactor);
				drawLine(canvas, paint, points[2], points[3], scaleFactor);
			} else {
				paint.setStrokeWidth(10.0f);
				for (ResultPoint point : points) {
					if (point != null) {
						canvas.drawPoint(scaleFactor * point.getX(), scaleFactor * point.getY(), paint);
					}
				}
			}
		}
	}

	private static void drawLine(Canvas canvas, Paint paint, ResultPoint a, ResultPoint b, float scaleFactor) {
		if (a != null && b != null) {
			canvas.drawLine(scaleFactor * a.getX(), 
					scaleFactor * a.getY(), 
					scaleFactor * b.getX(), 
					scaleFactor * b.getY(), 
					paint);
		}
	}


	private void handleDecodeExternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {

		if (barcode != null) {
			viewfinderView.drawResultBitmap(barcode);
		}

		long resultDurationMS;
		if (getIntent() == null) {
			resultDurationMS = DEFAULT_INTENT_RESULT_DURATION_MS;
		} else {
			resultDurationMS = getIntent().getLongExtra(Intents.Scan.RESULT_DISPLAY_DURATION_MS,
					DEFAULT_INTENT_RESULT_DURATION_MS);
		}

		if (resultDurationMS > 0) {
			String rawResultString = String.valueOf(rawResult);
			if (rawResultString.length() > 32) {
				rawResultString = rawResultString.substring(0, 32) + " ...";
			}
		}


		if (source == IntentSource.NATIVE_APP_INTENT) {

			// Hand back whatever action they requested - this can be changed to Intents.Scan.ACTION when
			// the deprecated intent is retired.
			Intent intent = new Intent(getIntent().getAction());
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			intent.putExtra(Intents.Scan.RESULT, rawResult.toString());
			intent.putExtra(Intents.Scan.RESULT_FORMAT, rawResult.getBarcodeFormat().toString());
			byte[] rawBytes = rawResult.getRawBytes();
			if (rawBytes != null && rawBytes.length > 0) {
				intent.putExtra(Intents.Scan.RESULT_BYTES, rawBytes);
			}
			Map<ResultMetadataType,?> metadata = rawResult.getResultMetadata();
			if (metadata != null) {
				if (metadata.containsKey(ResultMetadataType.UPC_EAN_EXTENSION)) {
					intent.putExtra(Intents.Scan.RESULT_UPC_EAN_EXTENSION,
							metadata.get(ResultMetadataType.UPC_EAN_EXTENSION).toString());
				}
				Number orientation = (Number) metadata.get(ResultMetadataType.ORIENTATION);
				if (orientation != null) {
					intent.putExtra(Intents.Scan.RESULT_ORIENTATION, orientation.intValue());
				}
				String ecLevel = (String) metadata.get(ResultMetadataType.ERROR_CORRECTION_LEVEL);
				if (ecLevel != null) {
					intent.putExtra(Intents.Scan.RESULT_ERROR_CORRECTION_LEVEL, ecLevel);
				}
				@SuppressWarnings("unchecked")
				Iterable<byte[]> byteSegments = (Iterable<byte[]>) metadata.get(ResultMetadataType.BYTE_SEGMENTS);
				if (byteSegments != null) {
					int i = 0;
					for (byte[] byteSegment : byteSegments) {
						intent.putExtra(Intents.Scan.RESULT_BYTE_SEGMENTS_PREFIX + i, byteSegment);
						i++;
					}
				}
			}
			sendReplyMessage(R.id.return_scan_result, intent, resultDurationMS);

		} else if (source == IntentSource.PRODUCT_SEARCH_LINK) {

			// Reformulate the URL which triggered us into a query, so that the request goes to the same
			// TLD as the scan URL.
			int end = sourceUrl.lastIndexOf("/scan");
			String replyURL = sourceUrl.substring(0, end) + "?q=" + resultHandler.getDisplayContents() + "&source=zxing";      
			sendReplyMessage(R.id.launch_product_query, replyURL, resultDurationMS);

		} else if (source == IntentSource.ZXING_LINK) {

		}
	}

	private void sendReplyMessage(int id, Object arg, long delayMS) {
		if (handler != null) {
			Message message = Message.obtain(handler, id, arg);
			if (delayMS > 0L) {
				handler.sendMessageDelayed(message, delayMS);
			} else {
				handler.sendMessage(message);
			}
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a RuntimeException.
			if (handler == null) {
				handler = new CaptureActivityHandler(this, _context, decodeFormats, decodeHints, characterSet, cameraManager);
			}
			decodeOrStoreSavedBitmap(null, null);
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		}
	}

	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
		resetStatusView();
//		if(registFlag == 1) {
//			registHelpView.setVisibility(View.GONE);
//			returnBorrowHelpView.setVisibility(View.VISIBLE);
//		}
	}

	private void resetStatusView() {
		viewfinderView.setVisibility(View.VISIBLE);
		lastResult = null;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	private void setMode(TextView textview) {
		arrayList.clear();
		if(textview.getText().toString().equals("図書の借用・返却モード")) {
			textview.setText("図書の一括登録モード");
			textview.setTextColor(Color.rgb(62, 162, 229));
			strokeColor = Color.rgb(62, 162, 229);
			registFlag = 1;
		}
		else {
			textview.setText("図書の借用・返却モード");
			textview.setTextColor(Color.rgb(56, 234, 123));
			strokeColor = Color.rgb(56, 234, 123);
			registFlag = 0;
		}
	}

	// 成功と失敗の表示を分けるためのメソッド
	private void customToast(String toast_text, int icon, boolean bool) {
		getLayoutInflater();
		LayoutInflater inflater = getLayoutInflater();

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
		
		final Toast toast = new Toast(this);
		
		layout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				toast.cancel();
			}
		});
		
		toast.setView(layout);
		toast.setGravity(Gravity.CENTER, 0, 0);
		if(modeFlag == true) {
			toast.setDuration(Toast.LENGTH_SHORT);
		}
		else {
			toast.setDuration(Toast.LENGTH_LONG);
		}
		toast.show();

		//		Toast.makeText(context, toast_text, Toast.LENGTH_SHORT).show();
	}
	
	private void getBookDataFromAmazon(final String request_url, final String isbn, final JSONArray json_array) {
		amazonAPIResultJSONArray = new JSONArray();
		amazonAPIResultJSONArray = json_array;
		String result = "";
		HttpConnectAmazonAPI connect_amazon_api = new HttpConnectAmazonAPI(_activity, _context, request_url, this);
		connect_amazon_api.execute();
	}
	
	@Override
	public void amazonAPIAsyncTaskCallback(String result, String request_url) {
		String response = result;
		JSONArray json_array = amazonAPIResultJSONArray;
		String isbn = "";
		if(lastResult != null) {
			isbn = lastResult.toString();
		}
		// ネットワークエラーだった場合
		if(response == null) {
			requestCount++;
			if(requestCount < 5) {
				HttpConnectAmazonAPI connect_amazon_api = new HttpConnectAmazonAPI(_activity, _context, request_url, this);
				connect_amazon_api.execute();
			}
			else {
				Toast.makeText(_context, "通信できませんでした. インターネット環境の良い場所で再度お試しください.", Toast.LENGTH_LONG).show();
				restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
				requestCount = 0;
			}
		}
		// 通信が成功した場合
		else if(response != null) {
			requestCount = 0;
			// AmazonAPIから書籍情報を取得できなかった場合, 図書のタイトルを手動で入力するダイアログを表示
			if(response.equals("0")) {
				titleInputDialog(isbn, json_array);
			}
			// 取得できた場合
			else if(response.equals("1")) {
				// 借用・返却モードの時に未登録図書を読み取った場合, json_arrayには本棚一覧の情報が入っているのでパースする
				if(json_array != null) {
					parseShelfData(isbn, json_array);
				}
				// 登録モードの時にISBNコードを読み取って書籍情報が取得できた場合にelseのケースに入る(読み取り処理を続ける)
				else {
					// 図書を連続登録するために図書コードのパラメータを配列に格納しておく
					arrayList.add("&book_code[]=" + lastResult);
					if(bookListViewAdapter.isEmpty()) {
						animateTranslationY(bookRegistRelativeLayout, displayHeight, displayHeight - displayHeight / 4 - titleBarHeight);
						borrowReturnButton.setEnabled(false);
						registButton.setEnabled(false);
						borrowReturnButton.setTextColor(Color.GRAY);
						registButton.setTextColor(Color.GRAY);
					}
					// 読み込んだ図書一覧のリストビューに追加する
					bookListViewAdapter.add(new BookListViewItem(lastResult.toString()));
//					for(int i=0; i < arrayList.size(); i++){
//						text += arrayList.get(i) + "\n";
//					}
					bircleBeepManager.playBeepSoundAndVibrate(2);
					Toast.makeText(_context, "ISBN = " + lastResult + " を読み込みました.", Toast.LENGTH_SHORT).show();
					restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
				}
			}
			else {
			}
		}
	}

	public void tempRegister(String isbn) {
//		setMode(modeText);
//		tempRegistIsbn = "&book_code[]=" + isbn;
		tempRegistIsbn = isbn;
		//arrayList.add("&book_code[]=" + isbn);
	}
	
	private void bookBorrowReturn(final String url, final String isbn) {
		// 現在時刻の取得
		getCurrentTime();
		
		// 借用・返却のリクエストを送信する
		HttpConnectPost connect_borrow_return = new HttpConnectPost(_activity, url, this);
		connect_borrow_return.execute();
	}
	
	@Override
	public void asyncTaskCallback(JSONArray jsonArray, String request_url) {
		int icon = 0;
		boolean bool = false;
		String isbn = "";
		if(lastResult != null) {
			isbn = lastResult.toString();
		}
		
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
				HttpConnectPost connect_borrow_return = new HttpConnectPost(_activity, request_url, this);
				connect_borrow_return.execute();
			}
			else {
				if(modeFlag == true) {
					icon = R.drawable.toast_icon6;
					bool = false;
					customToast("通信エラーで借用処理のみ失敗しました... 再度借用してください.", icon, bool);
					bircleBeepManager.playBeepSoundAndVibrate(0);
					restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
					requestCount = 0;
					modeFlag = false;
				}
				else {
					Toast.makeText(_context, "通信できませんでした. インターネット環境の良い場所で再度お試しください.", Toast.LENGTH_LONG).show();
					restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
					requestCount = 0;
				}
			}
		}
		else if(borrowReturnArray != null) {
			requestCount = 0;
			
			try {
				statusObject = borrowReturnArray.getJSONObject(0);
				status_result = statusObject.get("status").toString();
				// JSONから取得しているのはbook_codeという名前だが, 中身は図書のタイトルになっている(要修正)
				titleObject = borrowReturnArray.getJSONObject(1);
				title_result = titleObject.get("book_code").toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			switch(status_result) {
			// 通常の返却の場合
			case "0":
				try {
					shelfNameObject = borrowReturnArray.getJSONObject(2);
					shelf_name_result = shelfNameObject.get("shelf_name").toString();
				}
				catch (JSONException e) {
					e.printStackTrace();
				}
				bircleBeepManager.playBeepSoundAndVibrate(4);
				toastText = "「" + title_result + "」" + "を返しました.";
				toastText += "<font color=#1abc9c>「" + shelf_name_result + "」</font>に返却してください.";
				icon = R.drawable.toast_icon3;
				bool = true;
				customToast(toastText, icon, bool);
//				sendActionLog(isbn, "return", "通常の返却", "0");
				cancelNotification();
				setReturnHistory("本のタイトル :<br /><font color=#3498db>" + title_result + "</font><br />返却する場所(本棚) :<br /><font color=#1abc9c>" + shelf_name_result + "</font><br />返却日時 :<br /><font color=#e67e22>" + now + "</font><br />");
				getReturnHistory();
				shelfNameObject = null;
				shelfNameArray = null;
				restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
				break;
			// 通常の借用の場合
			case "1":
				// modeFlag == true, つまり未登録図書の登録&借用の流れであれば登録で1回音が鳴っているため, この時の借用は音を鳴らさないようにする
				if(modeFlag != true) {
					bircleBeepManager.playBeepSoundAndVibrate(3);
				}
				toastText = "「" + title_result + "」" + "を借りました.";
				icon = R.drawable.toast_icon1;
				bool = true;
				customToast(toastText, icon, bool);
//				sendActionLog(isbn, "borrow", "通常の借用", "0");
				// 未登録図書の登録&借用の時は, ここでmodeFlagを初期化(false)する
				modeFlag = false;
				restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
				break;
			// 同一図書を2人のユーザが借用した場合
			case "-1":
//				toastText = "「" + title_result + "」" + "は全て借りられている本です!";
//				icon = R.drawable.toast_icon2;
//				bool = false;
//				customToast(toastText, icon, bool);
//				bircleBeepManager.playBeepSoundAndVibrate(false);
				setTitleAndIsbn(title_result, isbn);
				alertMessageDialog(1, title_result, isbn);
				restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
				break;
			// 未登録図書を借用した場合
			case "-2":
				resultIsbn = isbn;
//				bircleBeepManager.playBeepSoundAndVibrate(false);
//				setTitleAndIsbn(title_result, isbn);
//				alertMessageDialog(2, title_result, isbn);
				getBookDataFromAmazon(initCatalogRegistUrl + isbn, isbn, borrowReturnArray);
				break;
			// 複数の本棚に同一の図書があった場合
			case "-4":
				try {
					// サーバからのレスポンスとして本棚IDと本棚名が複数返ってくるのでパースする
					shelfIdObject = borrowReturnArray.getJSONObject(2);
					shelfNameObject = borrowReturnArray.getJSONObject(3);
					shelfIdArray = shelfIdObject.getJSONArray("shelves");
					shelfNameArray = shelfNameObject.getJSONArray("shelf_name");
					List<String> shelf_id_arrayList = new ArrayList<String>();
					List<String> shelf_name_arrayList = new ArrayList<String>();
					
					for(int i = 0; i < shelfIdArray.length(); i++) {
						shelf_id_arrayList.add(shelfIdArray.get(i).toString());
						shelf_name_arrayList.add(shelfNameArray.get(i).toString());
					}
					final CharSequence[] shelf_id_array = shelf_id_arrayList.toArray(new CharSequence[shelf_id_arrayList.size()]);
					final CharSequence[] shelf_name_array = shelf_name_arrayList.toArray(new CharSequence[shelf_name_arrayList.size()]);
					
					// 本棚IDの選択ダイアログ呼び出し
					selectShelfDialog(title_result, isbn, shelf_id_array, shelf_name_array, 0);
					shelfIdObject = null;
					shelfNameObject = null;
					shelfIdArray = null;
					shelfNameArray = null;
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			// 未登録本棚に登録してある図書を返却した時
			case "-5":
				try {
					// サーバからのレスポンスとして本棚IDと本棚名が複数返ってくるのでパースする
					shelfIdObject = borrowReturnArray.getJSONObject(2);
					shelfNameObject = borrowReturnArray.getJSONObject(3);
					shelfIdArray = shelfIdObject.getJSONArray("shelves");
					shelfNameArray = shelfNameObject.getJSONArray("shelf_name");
					List<String> shelf_id_arrayList = new ArrayList<String>();
					List<String> shelf_name_arrayList = new ArrayList<String>();
					
					for(int i = 0; i < shelfIdArray.length(); i++) {
						shelf_id_arrayList.add(shelfIdArray.get(i).toString());
						shelf_name_arrayList.add(shelfNameArray.get(i).toString());
					}
					final CharSequence[] shelf_id_array = shelf_id_arrayList.toArray(new CharSequence[shelf_id_arrayList.size()]);
					final CharSequence[] shelf_name_array = shelf_name_arrayList.toArray(new CharSequence[shelf_name_arrayList.size()]);
					
					// 本棚IDの選択ダイアログ呼び出し
					selectShelfDialog(title_result, isbn, shelf_id_array, shelf_name_array, 1);
					cancelNotification();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			default: 
				break;
			}
		}

	}
	
	// 図書の一括登録を行うメソッド(引数urlはリクエストURL, 引数mode_flagはどちらのモードからの登録処理なのかを判断するフラグ, 
	// 引数temp_regist_isbnは図書の借用・返却モードから登録処理をした時のISBNの値)
	private void bookRegister(final String url, final boolean mode_flag, final String temp_regist_isbn) {
		modeFlag = mode_flag;
		tempRegistIsbn = temp_regist_isbn;
		HttpConnectBookRegister connect_book_register = new HttpConnectBookRegister(_activity, _context, url, this);
		connect_book_register.execute();
	}

	@Override
	public void bookRegisterAsyncTaskCallback(JSONArray jsonArray, String request_url) {
//		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		
		int icon = 0;
		boolean bool = false;

		JSONArray bookRegistResultArray = null;
		JSONObject qrObject = null;
		JSONObject book_numObject = null;

		String book_num_result = null;
		String qr_result = null;
		
		bookRegistResultArray = jsonArray;
		
		if(bookRegistResultArray == null) {
			requestCount++;
			if(requestCount < 5) {
				HttpConnectBookRegister connect_book_register = new HttpConnectBookRegister(_activity, _context, request_url, this);
				connect_book_register.execute();
			}
			else {
				Toast.makeText(_context, "通信できませんでした. インターネット環境の良い場所で再度お試しください.", Toast.LENGTH_LONG).show();
				restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
				requestCount = 0;
			}
		}
		else if(bookRegistResultArray != null) {
			requestCount = 0;
			try {
				qrObject = bookRegistResultArray.getJSONObject(0);
				qr_result = qrObject.get("qr").toString();
				book_numObject = bookRegistResultArray.getJSONObject(1);
				book_num_result = book_numObject.get("book_num").toString();
			} catch (JSONException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			if(!book_num_result.equals("0")) {
				switch(qr_result) {
				case "0":
					toastText = book_num_result + "冊の本を未登録本棚に登録しました.";
					icon = R.drawable.toast_icon5;
					bool = true;
					customToast(toastText, icon, bool);
//					sendActionLog("empty", "regist", "empty", qr_result);
					arrayList.clear();
					bookListViewAdapter.clear();
					animateTranslationY(bookRegistRelativeLayout, displayHeight - displayHeight / 4 - titleBarHeight, displayHeight);
					bircleBeepManager.playBeepSoundAndVibrate(1);
					modeFlag = false;
					break;
				default:
					// 図書の借用・返却モードからの登録処理だった場合, 借用も同時に行う
					if(modeFlag == true) {
						toastText = book_num_result + "冊の本を本棚「" + qr_result + "」に登録しました.";
						icon = R.drawable.toast_icon5;
						bool = true;
						customToast(toastText, icon, bool);
//						sendActionLog("empty", "regist", "empty", qr_result);
						bircleBeepManager.playBeepSoundAndVibrate(1);
						
//						String request_url = initLendRegistUrl + "&book_code=" + temp_regist_isbn;
						String borrow_return_url = initLendRegistUrl + "&book_code=" + tempRegistIsbn;
						HttpConnectPost connect_borrow_return = new HttpConnectPost(_activity, borrow_return_url, this);
						connect_borrow_return.execute();
						
						// 未登録図書読み取り時のビューを下にしまって, 関連する変数を初期化する
						resetRegistSelectShelfData();
						animateTranslationY(registSelectShelfRelativeLayout, displayHeight - displayHeight / 3 - titleBarHeight, displayHeight);
						registFlag = 0;
					}
					else {
						toastText = book_num_result + "冊の本を本棚「" + qr_result + "」に登録しました.";
						icon = R.drawable.toast_icon5;
						bool = true;
						customToast(toastText, icon, bool);
//						sendActionLog("empty", "regist", "empty", qr_result);
						arrayList.clear();
						bookListViewAdapter.clear();
						animateTranslationY(bookRegistRelativeLayout, displayHeight - displayHeight / 4 - titleBarHeight, displayHeight);
						bircleBeepManager.playBeepSoundAndVibrate(1);
//						registButton.setEnabled(true);
//						registButton.setTextColor(Color.WHITE);
						registButton.performClick();
						modeFlag = false;
					}
					break;
				}
			}
			else {
				// connect_book_register.execute();
				Toast.makeText(_context, "図書が登録できませんでした...", Toast.LENGTH_SHORT).show();
			}
			restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
		}
//		else {
//			alertDialogBuilder.setTitle("送信できませんでした.");
//			alertDialogBuilder.setMessage(
//				"ネットワークの状態を確認してもう一度送信しましょう."
//			);
//			alertDialogBuilder.setPositiveButton("再送信",
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						bookRegister(url, mode_flag, temp_regist_isbn);
//					}
//				}
//			);
//			alertDialogBuilder.setNeutralButton("キャンセル",
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
//					}
//				}
//			);
//			
//			alertDialogBuilder.setCancelable(true);
//			AlertDialog alertDialog = alertDialogBuilder.create();
//			alertDialogBuilder.show();
//		}
	}
	
	private void alertMessageDialog(int flag, final String result, final String isbn) {
		if(flag == 1) {
			// String request_url = initTemporaryLendRegistUrl + "&qr=1&book_code=" + isbn;
			String request_url = initTemporaryLendRegistUrl + "&book_code=" + isbn;
			bookBorrowReturn(request_url, isbn);
			String request_gcm_url = gcm_notify_url + "user_id=" + userId + "&group_id=" + groupId + "&book_code=" + isbn;
			HttpConnectGCMServer httpConnectGCMServer = new HttpConnectGCMServer(CaptureActivity.this, _context, request_gcm_url);
			httpConnectGCMServer.execute();
		}
		else if(flag == 2) {
			resultIsbn = isbn;
			ExceptionDialogFragment exceptionDialogFragment = new ExceptionDialogFragment(_activity, _context, result, isbn);
			exceptionDialogFragment.setDialogListener(this);
			exceptionDialogFragment.show(getFragmentManager(), "exception");	
		}
	}
	
	
	// 本棚選択のダイアログ(未登録図書の登録時)
	private void registSelectShelfDialog(String isbn, CharSequence[] shelf_id_array, CharSequence[] shelf_name_array, int borrow_return_flag) {
		RegistSelectShelfDialogFragment registSelectShelfDialogFragment = new RegistSelectShelfDialogFragment(_activity, _context, isbn, shelf_id_array, shelf_name_array, borrow_return_flag);
		registSelectShelfDialogFragment.setDialogListener(this);
		registSelectShelfDialogFragment.show(getFragmentManager(), "registSelectShelf");
	}
	
	/*
	 * 本棚選択のダイアログ(借用・返却時共通)
	 * borrow_return_flagで判別する(0: 借用時, 1: 返却時)
	 */
	private void selectShelfDialog(String book_title, String isbn, CharSequence[] shelf_id_array, CharSequence[] shelf_name_array, int borrow_return_flag) {
		SelectShelfDialogFragment selectShelfDialogFragment = new SelectShelfDialogFragment(_activity, _context, book_title, isbn, shelf_id_array, shelf_name_array, borrow_return_flag);
		selectShelfDialogFragment.setDialogListener(this);
		selectShelfDialogFragment.show(getFragmentManager(), "selectShelf");
	}
	
	private void titleInputDialog(final String isbn, final JSONArray json_array) {
		// 図書タイトルを入力するためのテキストフォームを用意
		final EditText titleEditText = new EditText(_activity);
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("書籍情報が取得できませんでした");
		alertDialogBuilder.setMessage(
			"手動で図書のタイトルを入力しておきましょう!"
		);
		alertDialogBuilder.setView(titleEditText);
		alertDialogBuilder.setPositiveButton("このタイトルで登録する",
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String title = titleEditText.getText().toString();
					// 図書タイトルが空白でなければmanually_catalog_registerを叩く
					if(!title.isEmpty() || title != " " || title != "　") {
						title = title.replaceAll(" ", "_");
						title = title.replaceAll("　", "_");
						String request_url = initManuallyCatalogRegistUrl + isbn + "&title=" + title;
						HttpConnectPostReturnFlag manuallyCatalogRegist = new HttpConnectPostReturnFlag(_activity, request_url);
						manuallyCatalogRegist.execute();
						
						// 未登録図書を読み取った時に本棚一覧の選択で図書を登録出来るように, このjson_arrayには本棚の情報が入っているため, それをパースする
						if(json_array != null) {
							parseShelfData(isbn, json_array);
						}
						else {
							// 「図書の一括登録モード」だった時は, 一時的にISBNコードを保存しておく
							if(registFlag == 1) {
								// 図書を連続登録するために図書コードのパラメータを配列に格納しておく
								arrayList.add("&book_code[]=" + lastResult);
								if(bookListViewAdapter.isEmpty()) {
									animateTranslationY(bookRegistRelativeLayout, displayHeight, displayHeight - displayHeight / 4 - titleBarHeight);
									borrowReturnButton.setEnabled(false);
									registButton.setEnabled(false);
								}
								bookListViewAdapter.add(new BookListViewItem(lastResult.toString()));
//								for(int i=0; i < arrayList.size(); i++){
//									text += arrayList.get(i) + "\n";
//								}
								Toast.makeText(_context, "ISBN = " + lastResult + " を読み込みました.", Toast.LENGTH_SHORT).show();
								bircleBeepManager.playBeepSoundAndVibrate(2);
								restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
							}
						}
					}
					else {
						Toast.makeText(_context, "タイトルを入力してください!", Toast.LENGTH_LONG).show();
					}
				}
			}
		);
		alertDialogBuilder.setNeutralButton("キャンセル",
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
				}
			}
		);
		
		alertDialogBuilder.setCancelable(true);
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialogBuilder.show();
	}

	public void logout() {
		SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0); // 0 -> MODE_PRIVATE
		// SharedPreferencesであるsettingsを編集する
		SharedPreferences.Editor editor = settings.edit();
		
		String userId_str = String.valueOf(userId);
		
		String logout_request_url = logout_url + "user_id=" + userId + "&group_id=" + groupId;

		HttpConnectRegistOrUnregistDevice httpConnectRegistOrUnregistDevice = new HttpConnectRegistOrUnregistDevice (_context, regId, userId_str, false);
		httpConnectRegistOrUnregistDevice.execute();
		
		HttpConnectLogout httpConnectLogout = new HttpConnectLogout(_context, logout_request_url);
		httpConnectLogout.execute();
		
		editor.remove("user_id");
		editor.remove("group_id");
		editor.remove("logged_in");
		editor.remove("group_name");
		
		editor.commit();
		
//		sendActionLog("empty", "Logout", "ログアウトが押された", "0");
		
		Intent intent = new Intent(_context, LoginActivity.class);
		startActivity(intent);
		finish();
	}
	
	private void setTitleAndIsbn(String title, String isbn) {
		resultTitle = title;
		resultIsbn = isbn;
	}
	
	private void getUserData() {
		// preferenceフォルダにあるxmlファイルから値を取得する
		SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
		// 
		if(settings == null) {
			return ;
		}
		
		userId = (int) settings.getLong("user_id", 0);
		groupId = (int) settings.getLong("group_id", 0);
		regId = settings.getString("reg_id", "");
		groupName = settings.getString("group_name", "");
		
	}
	
	// 返却の履歴を取得する(3件のみ)
	private void getReturnHistory() {
		SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
		if(settings == null) {
			return ;
		}
		
		// それぞれの返却履歴にuserIdを付けることによって複数のユーザが同端末で利用しても各人の履歴が表示される
		String return_history1 = settings.getString("return_history1_" + userId + "_" + groupId, "");
		String return_history2 = settings.getString("return_history2_" + userId + "_" + groupId, "");
		String return_history3 = settings.getString("return_history3_" + userId + "_" + groupId, "");
		CharSequence[] return_history_array = {Html.fromHtml(return_history1), Html.fromHtml(return_history2), Html.fromHtml(return_history3)};
				
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, R.layout.row, return_history_array);
		leftDrawer.setAdapter(adapter);
		
	}
	
	// 返却の履歴を作成する
	private void setReturnHistory(String return_history) {
		SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
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
		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(notificationId);
	}
	
	// 評価実験用 Androidの操作ログ送信関数
//	private void sendActionLog(String book_code, String action_tag, String detail, String shelf_id) {
//		String request_url = "https://bms-dev.herokuapp.com/android_action_log?user_id="
//								+ userId + "&group_id=" + groupId + "&book_code=" + book_code + "&tag="
//								+ action_tag + "&detail=" + detail + "&shelf_id=" + shelf_id;
//		
//		HttpConnectPostReturnFlag send_action_log = new HttpConnectPostReturnFlag(_activity, request_url);
//		send_action_log.execute();
//	}
	
	private void parseShelfData(String isbn, JSONArray json_array) {
		try {
			// サーバからのレスポンスとして本棚IDと本棚名が複数返ってくるのでパースする
			JSONObject shelfIdObject = json_array.getJSONObject(2);
			JSONObject shelfNameObject = json_array.getJSONObject(3);
			JSONArray shelfIdArray = shelfIdObject.getJSONArray("shelves");
			JSONArray shelfNameArray = shelfNameObject.getJSONArray("shelf_name");
			List<String> shelf_id_arrayList = new ArrayList<String>();
			List<String> shelf_name_arrayList = new ArrayList<String>();
			
			if(shelfIdArray.length() != 0) {
				for(int i = 0; i < shelfIdArray.length(); i++) {
					shelf_id_arrayList.add(shelfIdArray.get(i).toString());
					shelf_name_arrayList.add(shelfNameArray.get(i).toString());
				}
				final CharSequence[] shelf_id_array = shelf_id_arrayList.toArray(new CharSequence[shelf_id_arrayList.size()]);
				final CharSequence[] shelf_name_array = shelf_name_arrayList.toArray(new CharSequence[shelf_name_arrayList.size()]);
			
				// 本棚IDの選択ダイアログ呼び出し
//				registSelectShelfDialog(isbn, shelf_id_array, shelf_name_array, 2);
				setListView(isbn, shelf_name_array, shelf_id_array);
				animateTranslationY(registSelectShelfRelativeLayout, displayHeight, displayHeight - displayHeight / 3 - titleBarHeight);
				borrowReturnButton.setEnabled(false);
				registButton.setEnabled(false);
			}
			else {
				Toast.makeText(_context, "本棚が登録されていないので, 本棚を登録してから借用しましょう", Toast.LENGTH_LONG).show();
			}
			
			shelfIdObject = null;
			shelfNameObject = null;
			shelfIdArray = null;
			shelfNameArray = null;
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
/////////////////////////////////// DialogLisntenerのメソッドの実装 ここから ///////////////////////////////////
	// 未登録図書の読み取り処理において, ダイアログの「登録する」ボタンを押した時の処理
	@Override
	public void doPositiveClick() {
		tempRegister(resultIsbn);
//		exceptionHandler.setBookCode(resultIsbn);
		HttpConnectAmazonAPI connect_amazon_api = new HttpConnectAmazonAPI(_activity, _context, initCatalogRegistUrl + resultIsbn, this);
		connect_amazon_api.execute();
		resultIsbn = null;
		restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
	}

	// 未登録図書の読み取り処理において, ダイアログの「登録しない」ボタンを押した時などの処理(キャンセルの役割)
	@Override
	public void doNegativeClick() {
		restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
	}
	
	@Override
	public void doUnregisterClick(String isbn, String book_title) {
		getCurrentTime();
		bircleBeepManager.playBeepSoundAndVibrate(4);
		toastText = "「" + book_title + "」" + "を返却しました.";
		int icon = R.drawable.toast_icon3;
		boolean bool = true;
		customToast(toastText, icon, bool);
		setReturnHistory("本のタイトル :<br /><font color=#3498db>" + book_title + "</font><br />返却する場所(本棚) :<br /><font color=#1abc9c>未登録本棚</font><br />返却日時 :<br /><font color=#e67e22>" + now + "</font><br />");
		getReturnHistory();
		restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
	}
	
	@Override
	public void doOKClick(int regist_borrow_return_flag, String isbn, String book_title, String shelf_name) {
		getCurrentTime();
		if(regist_borrow_return_flag == 0) {
			bircleBeepManager.playBeepSoundAndVibrate(3);
			toastText = "「" + book_title + "」" + "を借りました.";
			int icon = R.drawable.toast_icon1;
			boolean bool = true;
			customToast(toastText, icon, bool);
//			sendActionLog(isbn, "borrow", "複数の本棚に登録されている図書の借用", shelf_name);
		}
		else if(regist_borrow_return_flag == 1) {
			bircleBeepManager.playBeepSoundAndVibrate(4);
			toastText = "「" + book_title + "」を本棚「" + shelf_name + "」に登録して, 返却しました.";
			int icon = R.drawable.toast_icon5;
			boolean bool = true;
			customToast(toastText, icon, bool);
			setReturnHistory("本のタイトル :<br /><font color=#3498db>" + book_title + "</font><br />返却する場所(本棚) :<br /><font color=#1abc9c>" + shelf_name + "</font><br />返却日時 :<br /><font color=#e67e22>" + now + "</font><br />");
			getReturnHistory();
//			sendActionLog(isbn, "regist", "未登録図書の返却時登録", shelf_name);
		}
		else if(regist_borrow_return_flag == 2) {
			bircleBeepManager.playBeepSoundAndVibrate(1);
			toastText = "1冊の図書を本棚「" + shelf_name + "」に登録しました.";
			int icon = R.drawable.toast_icon5;
			boolean bool = true;
			customToast(toastText, icon, bool);
			waitingFlag = true;
			
//			sendActionLog(isbn, "regist", "未登録図書の登録", shelf_name);
		}
		restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
	}
	
	// 未登録図書の読み取り処理において, 本棚を選択してから「決定」ボタンを押した時の処理
	@Override
	public void doDecideClick(String isbn, String shelf_id, String shelf_name) {
//		getBookDataFromAmazon(initCatalogRegistUrl + isbn, isbn);
		
		String request_url = "https://bms-dev.herokuapp.com/book_register?qr=" + shelf_id + "&book_code[]=" + isbn + "&group_id=" + groupId + "&user_id=" + userId;
		bookRegister(request_url, true, isbn);
		restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
	}

	// 未登録図書の読み取り処理において, ダイアログの「QRコードを読み取って登録する」ボタンを押した時の処理
	@Override
	public void doNeutralClick() {
		tempRegister(resultIsbn);
//		exceptionHandler.setBookCode(resultIsbn);
//		HttpConnectAmazonAPI connect_amazon_api = new HttpConnectAmazonAPI(CaptureActivity.this, _context, initCatalogRegistUrl + resultIsbn);
//		connect_amazon_api.execute();
		resultIsbn = null;
		registFlag = 1;
		restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
	}
/////////////////////////////////// DialogLisntenerのメソッドの実装 ここまで ///////////////////////////////////
	
	private void animateTranslationX(View target) {
	// translationXプロパティを0fから200fに変化させます
		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(target,"translationX",0f,200f);
		// 1秒かけて実行
		objectAnimator.setDuration(1000);
		// アニメーションを開始します 
		objectAnimator.start();
	}
	
	private void animateTranslationY(View target, float start_height, float end_height) {
		
		// translationYプロパティを変化させます(表示ON)
		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(target, "translationY", start_height, end_height);
		// 1秒かけて実行
		objectAnimator.setDuration(300);
		// アニメーションを開始します 
		objectAnimator.start();
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
	
	private void setListView(String isbn, final CharSequence[] string_array, final CharSequence[] id_array) {
		// この doNeutralClick() は未登録図書読み取り時のダイアログで「本棚のQRコードを読み取って登録する」を選択した時に呼ばれるもので, そのまま流用した
		doNeutralClick();
		
		registSelectShelfName = string_array[0].toString();
		registSelectShelfId = id_array[0].toString();
		registSelectShelfIsbn = isbn;
		
		shelfListView.setAdapter(new ArrayAdapter<CharSequence> (
			this, android.R.layout.simple_list_item_single_choice, string_array) {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					TextView view = (TextView)super.getView(position, convertView, parent);
					view.setTextSize(textSize * 2 / 3);
					return view;
				}
			}
		);
		
		// 選択の方式の設定  
		shelfListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		shelfListView.setItemChecked(0, true);
		shelfListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				registSelectShelfName = string_array[((ListView) parent).getCheckedItemPosition()].toString();
				registSelectShelfId = id_array[((ListView) parent).getCheckedItemPosition()].toString();
			}
		});
		
	}
	
	// 使い方が記載された画像を表示するボタンの設定
	private void setHelpView() {
		int helpViewButton_width = displayWidth / 5;
		int helpViewButton_height = displayHeight / 16;
		helpViewButton.setText("ヘルプ");
		helpViewButton.setTextSize(textSize * 7 / 10);
		helpViewButton.setBackgroundColor(Color.rgb(230, 126, 34));
		helpViewButton.setLayoutParams(new FrameLayout.LayoutParams(helpViewButton_width, helpViewButton_height, Gravity.BOTTOM));
		helpViewButton.setTranslationX(displayWidth - helpViewButton_width * 1.3f);

		helpViewFlag = false;
		/*
		 *  helpViewButtonを押すと, 下から使い方説明のViewが出てくる
		 *  animateTranslationYでViewを移動させる距離は, ちょうど良い感じに調整した
		 *  そのため, titleBarHeight / 4 などは微調整するために設定した値である
		 */
		helpViewButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(helpViewFlag == true) {
					animateTranslationY(registHelpView, titleBarHeight / 4, displayHeight / 5 + titleBarHeight);
					animateTranslationY(returnBorrowHelpView, titleBarHeight / 4, displayHeight / 5 + titleBarHeight);
					animateTranslationY(helpViewButton, - displayHeight / 5 + (titleBarHeight / 3) , 0);
					helpViewButton.setText("ヘルプ");
					helpViewFlag = false;
				}
				else if(helpViewFlag == false) {
					animateTranslationY(registHelpView, displayHeight / 5 + titleBarHeight, titleBarHeight / 4);
					animateTranslationY(returnBorrowHelpView, displayHeight / 5 + titleBarHeight, titleBarHeight / 4);
					animateTranslationY(helpViewButton, 0, - displayHeight / 5 + (titleBarHeight / 3));
					helpViewButton.setText("非表示");
					helpViewFlag = true;
				}
			}
		});
		
	}
	
	// 未登録図書を読み取った時に下から本棚選択ビューを出すための設定
	private void setScanUnregisterBookView() {
		
		int registSelectShelfRelativeLayout_width = displayWidth;
		int registSelectShelfRelativeLayout_height = displayHeight / 3;
		int registSelectShelfRelativeLayout_x = 0;
		int registSelectShelfRelativeLayout_y = displayHeight;
		registSelectShelfRelativeLayout.setTranslationX(registSelectShelfRelativeLayout_x);
		registSelectShelfRelativeLayout.setTranslationY(registSelectShelfRelativeLayout_y);
		registSelectShelfRelativeLayout.setLayoutParams(new FrameLayout.LayoutParams(registSelectShelfRelativeLayout_width, registSelectShelfRelativeLayout_height));
		//registSelectShelfRelativeLayout.setVisibility(View.GONE);
		
		int textViewLinearLayout_width = displayWidth;
		int textViewLinearLayout_height = registSelectShelfRelativeLayout_height / 4;
		textViewLinearLayout.setLayoutParams(new RelativeLayout.LayoutParams(textViewLinearLayout_width, textViewLinearLayout_height));
		RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams)textViewLinearLayout.getLayoutParams();
//		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		textViewLinearLayout.setLayoutParams(params1);
		
		String tempText = "<font color=#3498db>※未登録図書を読み取りました!</font><br />図書を登録するにはそのまま本棚のQRコードを読み取るか, 以下の本棚一覧から選択して\"決定\"ボタンを押して下さい";
		tempTextView.setText(Html.fromHtml(tempText));
		tempTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize - displayInch * 1.5f); 
		
		int buttonLinearLayout_width = displayWidth;
		int buttonLinearLayout_height = registSelectShelfRelativeLayout_height / 4;
		int buttonLinearLayout_x = 0;
		int buttonLinearLayout_y = displayHeight;
//		buttonLinearLayout.setTranslationX(buttonLinearLayout_x);
//		buttonLinearLayout.setTranslationY(buttonLinearLayout_y);
		buttonLinearLayout.setLayoutParams(new RelativeLayout.LayoutParams(buttonLinearLayout_width, buttonLinearLayout_height));
		RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)buttonLinearLayout.getLayoutParams();
		params2.addRule(RelativeLayout.BELOW, R.id.text_view_linear_layout);
		buttonLinearLayout.setLayoutParams(params2);
		
		int listViewLinearLayout_width = displayWidth;
		int listViewLinearLayout_height = registSelectShelfRelativeLayout_height / 2;
		int listViewLinearLayout_x = 0;
		int listViewLinearLayout_y = displayHeight;
//		listViewLinearLayout.setTranslationX(listViewLinearLayout_x);
//		listViewLinearLayout.setTranslationY(listViewLinearLayout_y);
		listViewLinearLayout.setLayoutParams(new RelativeLayout.LayoutParams(listViewLinearLayout_width, listViewLinearLayout_height));
		RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams)listViewLinearLayout.getLayoutParams();
		params3.addRule(RelativeLayout.BELOW, R.id.button_linear_layout);
		listViewLinearLayout.setLayoutParams(params3);
//		listViewLinearLayout.setVisibility(View.GONE);
		
		int decisionButton_width = displayWidth / 5 * 2;
//		int decisionButton_height = displayHeight / 10;
		int decisionButton_height = registSelectShelfRelativeLayout_height / 4;
		int decisionButton_x = ((displayWidth / 2) - decisionButton_width) / 2;
		int decisionButton_y = displayHeight - (titleBarHeight * 4);
		decisionButton.setTranslationX(decisionButton_x);
//		decisionButton.setTranslationY(decisionButton_y);
		decisionButton.setLayoutParams(new LinearLayout.LayoutParams(decisionButton_width, decisionButton_height));
		decisionButton.setBackgroundColor(Color.argb(255, 230, 126, 34));
		decisionButton.setTextColor(Color.WHITE);
		decisionButton.setText("決定");
		decisionButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doDecideClick(registSelectShelfIsbn, registSelectShelfId, registSelectShelfName);
				registButton.setEnabled(true);
				borrowReturnButton.setEnabled(true);
			}
		});
		
		int cancelButton_width = displayWidth / 5 * 2;
//		int cancelButton_height = displayHeight / 10;
		int cancelButton_height = registSelectShelfRelativeLayout_height / 4;
		int cancelButton_x = ((displayWidth / 2) + decisionButton_x) - decisionButton_width;
		int cancelButton_y = displayHeight - (titleBarHeight * 4);
		cancelButton.setTranslationX(cancelButton_x);
//		cancelButton.setTranslationY(cancelButton_y);
		cancelButton.setLayoutParams(new LinearLayout.LayoutParams(cancelButton_width, cancelButton_height));
		cancelButton.setBackgroundColor(Color.argb(255, 127, 140, 141));
		cancelButton.setText("キャンセル");
		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				resetRegistSelectShelfData();
				animateTranslationY(registSelectShelfRelativeLayout, displayHeight - displayHeight / 3 - titleBarHeight, displayHeight);
				registFlag = 0;
				registButton.setEnabled(true);
				borrowReturnButton.setEnabled(true);
			}
		});
		
	}
	
	private void resetRegistSelectShelfData() {
		CharSequence[] reset_array = new CharSequence[0];
		registSelectShelfIsbn = "";
		registSelectShelfName = "";
		registSelectShelfId = "";
		tempRegistIsbn = "";
		shelfListView.setAdapter(new ArrayAdapter<CharSequence> (
			this, android.R.layout.simple_list_item_single_choice, reset_array)
		);
	}
	
	private void setBookRegistView() {
		int bookRegistRelativeLayout_width = displayWidth;
		int bookRegistRelativeLayout_height = displayHeight / 4;
		int bookRegistRelativeLayout_x = 0;
		int bookRegistRelativeLayout_y = displayHeight;
		bookRegistRelativeLayout.setTranslationX(bookRegistRelativeLayout_x);
		bookRegistRelativeLayout.setTranslationY(bookRegistRelativeLayout_y);
		bookRegistRelativeLayout.setLayoutParams(new FrameLayout.LayoutParams(bookRegistRelativeLayout_width, bookRegistRelativeLayout_height));
		
		int bookRegistLinearLayout_width = displayWidth;
		int bookRegistLinearLayout_height = bookRegistRelativeLayout_height / 3;
		bookRegistLinearLayout.setLayoutParams(new RelativeLayout.LayoutParams(bookRegistLinearLayout_width, bookRegistLinearLayout_height));
		RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams)bookRegistLinearLayout.getLayoutParams();
		params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		bookRegistLinearLayout.setLayoutParams(params1);
	
		int bookRegistListViewLinearLayout_width = displayWidth;
		int bookRegistListViewLinearLayout_height = bookRegistRelativeLayout_height * 2 / 3;
		bookRegistListViewLinearLayout.setLayoutParams(new RelativeLayout.LayoutParams(bookRegistListViewLinearLayout_width, bookRegistListViewLinearLayout_height));
		RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)bookRegistListViewLinearLayout.getLayoutParams();
		params2.addRule(RelativeLayout.BELOW, R.id.book_regist_linear_layout);
		bookRegistListViewLinearLayout.setLayoutParams(params2);
		
		int bookRegistListView_width = bookRegistListViewLinearLayout_width;
		int bookRegistListView_height = bookRegistListViewLinearLayout_height;
		bookRegistListView.setLayoutParams(new LinearLayout.LayoutParams(bookRegistListView_width, bookRegistListView_height));
		bookRegistListView.setAdapter(bookListViewAdapter);
		
		int bookRegistTextView_width = displayWidth * 4 / 5;
		int bookRegistTextView_height = bookRegistLinearLayout_height;
		int bookRegistTextView_x = 0;
		int bookRegistTextView_y = displayHeight;
		bookRegistTextView.setLayoutParams(new LinearLayout.LayoutParams(bookRegistTextView_width, bookRegistTextView_height, Gravity.LEFT));
		bookRegistTextView.setText("読み込んだ図書一覧");
		bookRegistTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize - displayInch * 1.5f);
		
		int bookRegistCancelButton_width = displayWidth / 5;
		int bookRegistCancelButton_height = bookRegistLinearLayout_height;
		int bookRegistCancelButton_x = 0;
		int bookRegistCancelButton_y = displayHeight;
		bookRegistCancelButton.setLayoutParams(new LinearLayout.LayoutParams(bookRegistCancelButton_width, bookRegistCancelButton_height, Gravity.RIGHT));
		bookRegistCancelButton.setText("キャンセル");
		bookRegistCancelButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize - displayInch * 1.5f);
		bookRegistCancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				animateTranslationY(bookRegistRelativeLayout, displayHeight - displayHeight / 4 - titleBarHeight, displayHeight);
				arrayList.clear();
				bookListViewAdapter.clear();
				registButton.setEnabled(true);
				borrowReturnButton.setEnabled(true);
				registButton.setTextColor(Color.WHITE);
			}
		});
	}
	
/////////////////////////////////// 現在のグループの文字の大きさを画面のインチ+文字の数によって変更する処理 ここから ///////////////////////////////////
	
	// 画面のインチが4.5より小さい端末における「現在のグループ」(タイトル部分)の表示の大きさを変更する関数
		private void resizeTitleSizeTooSmall() {
			int group_text_bytes_length = 0;

			try {
				group_text_bytes_length = groupName.getBytes("SHIFT-JIS").length;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			// タイトルの中に入る文字列の長さ(バイト数換算)によってサイズを縮めていく
			if(group_text_bytes_length > 10 && group_text_bytes_length <= 14) {
				groupName = "<small>" + groupName + "</small>";
			}
			else if(group_text_bytes_length > 14 && group_text_bytes_length <= 18) {
				groupName = "<small><small>" + groupName + "</small></small>";
			}
			else if(group_text_bytes_length > 18 && group_text_bytes_length <= 24) {
				groupName = "<small><small><small>" + groupName + "</small></small></small>";
			}
			else if(group_text_bytes_length > 24) {
				
				int split_point = 12;
				String split_text1 = "";
				int split_text1_length = 0;
				
				while(split_point < 24) {
					split_text1 = groupName.substring(0, split_point);
					try {
						split_text1_length = split_text1.getBytes("SHIFT-JIS").length;
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					if(split_text1_length <= 24) {
						split_point++;
					}
					else {
						break;
					}
					
					if(split_point >= groupName.length()) {
						split_point--;
						break;
					}
				}

				split_text1 = groupName.substring(0, split_point);
				String split_text2 = groupName.substring(split_point, groupName.length());
				groupName = "<small><small><small>" + split_text1 + "</small></small></small>";
				subGroupText = "<small><small>" + split_text2 + "</small></small>";
				actionBar.setSubtitle(Html.fromHtml("<font color=#FF0000>" + subGroupText + "</font>"));
			}
		}
	
	// 画面のインチが5.5より小さい端末における「現在のグループ」(タイトル部分)の表示の大きさを変更する関数
	private void resizeTitleSizeSmall() {
		int group_text_bytes_length = 0;

		try {
			group_text_bytes_length = groupName.getBytes("SHIFT-JIS").length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		// タイトルの中に入る文字列の長さ(バイト数換算)によってサイズを縮めていく
		if(group_text_bytes_length > 14 && group_text_bytes_length <= 18) {
			groupName = "<small>" + groupName + "</small>";
		}
		else if(group_text_bytes_length > 18 && group_text_bytes_length <= 22) {
			groupName = "<small><small>" + groupName + "</small></small>";
		}
		else if(group_text_bytes_length > 22 && group_text_bytes_length <= 30) {
			groupName = "<small><small><small>" + groupName + "</small></small></small>";
		}
		else if(group_text_bytes_length > 30) {
			/* 
			 * グループ名を1行で表示できる限界が 30byte
			 * グループ名が 30byte よりも長い文字だった時, 最初に 30byte 分の文字列を変数に格納しておく
			 * (split_pointは1行目と2行目の文字列の分割点)
			 * グループ名に英数字と日本語が混ざった時に, ちゃんと 30byte 分の文字列が1行目に表示できるように検査する
			 */
			int split_point = 15;
			String split_text1 = "";
			int split_text1_length = 0;
			
			while(split_point < 30) {
				split_text1 = groupName.substring(0, split_point);
				try {
					split_text1_length = split_text1.getBytes("SHIFT-JIS").length;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if(split_text1_length <= 30) {
					split_point++;
				}
				else {
					break;
				}
				
				if(split_point >= groupName.length()) {
					split_point--;
					break;
				}
			}

			split_text1 = groupName.substring(0, split_point);
			String split_text2 = groupName.substring(split_point, groupName.length());
			groupName = "<small><small><small>" + split_text1 + "</small></small></small>";
			subGroupText = "<small><small>" + split_text2 + "</small></small>";
			actionBar.setSubtitle(Html.fromHtml("<font color=#FF0000>" + subGroupText + "</font>"));
		}
	}
	
	// 画面のインチが5.5以上で6.5より小さい端末における「現在のグループ」(タイトル部分)の表示の大きさを変更する関数
	private void resizeTitleSizeMiddle() {
		int group_text_bytes_length = 0;

		try {
			group_text_bytes_length = groupName.getBytes("SHIFT-JIS").length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		// タイトルの中に入る文字列の長さ(バイト数換算)によってサイズを縮めていく
		if(group_text_bytes_length > 22 && group_text_bytes_length <= 28) {
			groupName = "<small>" + groupName + "</small>";
		}
		else if(group_text_bytes_length > 28 && group_text_bytes_length <= 36) {
			groupName = "<small><small>" + groupName + "</small></small>";
		}
		else if(group_text_bytes_length > 36 && group_text_bytes_length <= 50) {
			groupName = "<small><small><small>" + groupName + "</small></small></small>";
		}
		else if(group_text_bytes_length > 50) {
			int split_point = 18;
			String split_text1 = "";
			int split_text1_length = 0;
			
			while(split_point < 36) {
				split_text1 = groupName.substring(0, split_point);
				try {
					split_text1_length = split_text1.getBytes("SHIFT-JIS").length;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if(split_text1_length < 36) {
					split_point++;
				}
				else {
					break;
				}
				
				if(split_point >= groupName.length()) {
					split_point--;
					break;
				}
			}
			
			split_text1 = groupName.substring(0, split_point);
			String split_text2 = groupName.substring(split_point, groupName.length());
			groupName = "<small><small>" + split_text1 + "</small></small>";
			subGroupText = "<small>" + split_text2 + "</small>";
			actionBar.setSubtitle(Html.fromHtml("<font color=#FF0000>" + subGroupText + "</font>"));
		}
	}
	
	// 画面のインチが6.5以上で8より小さい端末における「現在のグループ」(タイトル部分)の表示の大きさを変更する関数
	private void resizeTitleSizeLarge() {
		int group_text_bytes_length = 0;

		try {
			group_text_bytes_length = groupName.getBytes("SHIFT-JIS").length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		// タイトルの中に入る文字列の長さ(バイト数換算)によってサイズを縮めていく
		if(group_text_bytes_length > 34 && group_text_bytes_length <= 42) {
			groupName = "<small>" + groupName + "</small>";
		}
		else if(group_text_bytes_length > 42 && group_text_bytes_length <= 52) {
			groupName = "<small><small>" + groupName + "</small></small>";
		}
		else if(group_text_bytes_length > 52 && group_text_bytes_length <= 68) {
			groupName = "<small><small><small>" + groupName + "</small></small></small>";
		}
		else if(group_text_bytes_length > 68) {
			int split_point = 25;
			String split_text1 = "";
			int split_text1_length = 0;
			
			while(split_point < 52) {
				split_text1 = groupName.substring(0, split_point);
				try {
					split_text1_length = split_text1.getBytes("SHIFT-JIS").length;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if(split_text1_length < 52) {
					split_point++;
				}
				else {
					break;
				}
				
				if(split_point >= groupName.length()) {
					split_point--;
					break;
				}
			}
			
			split_text1 = groupName.substring(0, split_point);
			String split_text2 = groupName.substring(split_point, groupName.length());
			groupName = "<small><small>" + split_text1 + "</small></small>";
			subGroupText = "<small>" + split_text2 + "</small>";
			actionBar.setSubtitle(Html.fromHtml("<font color=#FF0000>" + subGroupText + "</font>"));
		}
	}
	
/////////////////////////////////// 現在のグループの文字の大きさを画面のインチ+文字の数によって変更する処理 ここまで ///////////////////////////////////
	
	@Override
	public void removeIsbn(int position, String isbn) {
		Toast.makeText(_context, "ISBN: " + isbn + " の図書を削除しました.", Toast.LENGTH_SHORT).show();
		arrayList.remove(position);
		if(arrayList.isEmpty()) {
//			animateTranslationY(bookRegistRelativeLayout, displayHeight - displayHeight / 4 - titleBarHeight, displayHeight);
			bookRegistCancelButton.performClick();
		}
	}
	
	// オープンソースライセンスのダイアログ
		private void openSourceLicenseDialog() {
			OpenSourceLicenseDialogFragment open_source_license_dialog_fragment = new OpenSourceLicenseDialogFragment();
			open_source_license_dialog_fragment.show(getFragmentManager(), "openSourceLicenseDialog");
		}
}
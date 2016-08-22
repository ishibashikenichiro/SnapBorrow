/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bookkos.bircle;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.provider.Browser;

import com.bookkos.bircle.R;
import com.bookkos.bircle.camera.CameraManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.Collection;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.bookkos.bircle.CaptureActivity.getStatusUrl;

/**
 * This class handles all the messaging which comprises the state machine for capture.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CaptureActivityHandler extends Handler implements AsyncTaskListener {

	private static final String TAG = CaptureActivityHandler.class.getSimpleName();
	
	private static final long BULK_MODE_SCAN_DELAY_MS = 3000L;

	private final CaptureActivity activity;
	private Context _context;
	private final DecodeThread decodeThread;
	private State state;
	private final CameraManager cameraManager;
	
	private Object tempMessageObj;
	private Bitmap tempBarcode;
	private float tempScaleFactor;
	
	private int requestCount = 0;

	private enum State {
		PREVIEW,
		SUCCESS,
		DONE
	}

	CaptureActivityHandler(CaptureActivity activity, Context context,
			Collection<BarcodeFormat> decodeFormats,
			Map<DecodeHintType,?> baseHints,
			String characterSet,
			CameraManager cameraManager) {
		this.activity = activity;
		_context = context;
		decodeThread = new DecodeThread(activity, decodeFormats, baseHints, characterSet,
				new ViewfinderResultPointCallback(activity.getViewfinderView()));
		decodeThread.start();
		state = State.SUCCESS;

		// Start ourselves capturing previews and decoding.
		this.cameraManager = cameraManager;
		cameraManager.startPreview();
		restartPreviewAndDecode();
	}

	@Override
	public void handleMessage(Message message) {
		switch (message.what) {
		case R.id.restart_preview:
			restartPreviewAndDecode();
			break;
		case R.id.decode_succeeded:
			// ネットワークに繋がっていない場合, 処理を抜ける
			if(!CommonUtilities.netWorkCheck(_context)) {
				Toast.makeText(_context, "ネットワークに接続して下さい", Toast.LENGTH_LONG).show();
				break;
			}
			state = State.SUCCESS;
			Bundle bundle = message.getData();
			Bitmap barcode = null;
			float scaleFactor = 1.0f;
			if (bundle != null) {
				byte[] compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
				if (compressedBitmap != null) {
					barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
					// Mutable copy:
					barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
				}
				scaleFactor = bundle.getFloat(DecodeThread.BARCODE_SCALED_FACTOR);
			}
			
			HttpConnectGetStatus httpConnectGetStatus = new HttpConnectGetStatus(activity, getStatusUrl, this);
			httpConnectGetStatus.execute();
			
			tempMessageObj = message.obj;
			tempBarcode = barcode;
			tempScaleFactor = scaleFactor;
			
			break;
		case R.id.decode_failed:
			// We're decoding as fast as possible, so when one decode fails, start another.
			state = State.PREVIEW;
			cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
			break;
		case R.id.return_scan_result:
			activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
			activity.finish();
			break;
		case R.id.launch_product_query:
			String url = (String) message.obj;

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			intent.setData(Uri.parse(url));

			ResolveInfo resolveInfo =
					activity.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
			String browserPackageName = null;
			if (resolveInfo != null && resolveInfo.activityInfo != null) {
				browserPackageName = resolveInfo.activityInfo.packageName;
			}

			// Needed for default Android browser / Chrome only apparently
			if ("com.android.browser".equals(browserPackageName) || "com.android.chrome".equals(browserPackageName)) {
				intent.setPackage(browserPackageName);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(Browser.EXTRA_APPLICATION_ID, browserPackageName);
			}

			try {
				activity.startActivity(intent);
			} catch (ActivityNotFoundException ignored) {
				Log.w(TAG, "Can't find anything to handle VIEW of URI " + url);
			}
			break;
		}
	}

	public void quitSynchronously() {
		state = State.DONE;
		cameraManager.stopPreview();
		Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
		quit.sendToTarget();
		try {
			// Wait at most half a second; should be enough time, and onPause() will timeout quickly
			decodeThread.join(500L);
		} catch (InterruptedException e) {
			// continue
		}

		// Be absolutely sure we don't send any queued up messages
		removeMessages(R.id.decode_succeeded);
		removeMessages(R.id.decode_failed);
	}

	private void restartPreviewAndDecode() {
		if (state == State.SUCCESS) {
			state = State.PREVIEW;
			cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
			activity.drawViewfinder();
		}
	}
	
	@Override
	public void getStatusAsyncTaskCallback(JSONObject jsonObject, String request_url) {
		// ネットワークエラーだった場合
		if(jsonObject == null) {
			requestCount++;
			if(requestCount < 5) {
				HttpConnectGetStatus httpConnectGetStatus = new HttpConnectGetStatus(activity, getStatusUrl, this);
				httpConnectGetStatus.execute();
			}
			else {
				Toast.makeText(_context, "通信できませんでした. インターネット環境の良い場所で再度お試しください.", Toast.LENGTH_LONG).show();
				requestCount = 0;
			}
		}
		// 通信が成功した場合
		else if(jsonObject != null) {
			requestCount = 0;
			
			String has_group_result = null;
			String has_shelf_result = null;
			String has_user_result = null;
			
			try {
				has_group_result = jsonObject.get("has_group").toString();
				has_shelf_result = jsonObject.get("has_shelf").toString();
				has_user_result = jsonObject.get("has_user").toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if(!has_user_result.equals("1")) {
				Toast.makeText(_context, "ユーザ情報が削除されています. ログアウトしてユーザの登録を行って下さい.", Toast.LENGTH_LONG).show();
				activity.restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
			}
			else {
				if(!has_group_result.equals("1") && !has_shelf_result.equals("1")){
					Toast.makeText(_context, "グループから脱退されています. 招待してもらうか, 他のグループに切り替えるか, グループを作成しましょう.", Toast.LENGTH_LONG).show();
					activity.restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
				}
				else if(!has_group_result.equals("1")) {
					Toast.makeText(_context, "グループから脱退されています. 招待してもらうか, 他のグループに切り替えるか, グループを作成しましょう.", Toast.LENGTH_LONG).show();
					activity.restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
				}
				else if(!has_shelf_result.equals("1")) {
					Toast.makeText(_context, "本棚を作成しましょう.", Toast.LENGTH_LONG).show();
					activity.restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
				}
				else {
					activity.handleDecode((Result) tempMessageObj, tempBarcode, tempScaleFactor);
				}
			}
		}
	}

	// これらは使用しない
	@Override
	public void asyncTaskCallback(JSONArray jsonArray, String request_url) {		
	}
	@Override
	public void bookRegisterAsyncTaskCallback(JSONArray jsonArray,
			String request_url) {		
	}
	@Override
	public void amazonAPIAsyncTaskCallback(String result, String request_url) {		
	}

}

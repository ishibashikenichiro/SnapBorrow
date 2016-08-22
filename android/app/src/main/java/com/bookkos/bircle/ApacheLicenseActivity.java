package com.bookkos.bircle;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

public class ApacheLicenseActivity extends Activity {
	
	private Activity _activity;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.apache_license);
		
		_activity = this;
		
		InputStream input_stream = null;
		BufferedReader buffered_reader = null;
		String text = "";

		try {
			try {
				// assetsフォルダ内の sample.txt をオープンする
				input_stream = this.getAssets().open("LICENSE-2.0.txt");
				buffered_reader = new BufferedReader(new InputStreamReader(input_stream));

				String string;
				while ((string = buffered_reader.readLine()) != null) {
					text += string + "\n";
				}
			} finally {
				if (input_stream != null) input_stream.close();
				if (buffered_reader != null) buffered_reader.close();
			}
		} catch (Exception e){
			// エラー発生時の処理
		}
		Log.d("ApacheLicenseActivity", "text = " + text);

		TextView explain_text_view = (TextView) findViewById(R.id.explain_text_view);
		explain_text_view.setText(text);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(getApplicationContext(), CaptureActivity.class);
			startActivity(intent);
			finish();
			return false;
		} 
		return super.onKeyDown(keyCode, event);
	}
}
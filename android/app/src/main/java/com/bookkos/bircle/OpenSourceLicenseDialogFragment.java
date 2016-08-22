package com.bookkos.bircle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class OpenSourceLicenseDialogFragment extends DialogFragment {
	
	private Activity _activity;
	
	public Dialog onCreateDialog(Bundle savedInstance) {
		_activity = getActivity();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		Button link_button = new Button(_activity);
		link_button.setText("Apache License 2.0の詳細");
		link_button.setBackgroundColor(Color.rgb(127, 140, 141));
		link_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				intent.setClassName(_activity, ApacheLicenseActivity.class.getName());
				startActivity(intent);
			}
		});
		
		ScrollView scroll_view = new ScrollView(_activity);
        scroll_view.addView(link_button);
        
		String apache_license_link = "http://www.apache.org/licenses/LICENSE-2.0";
		String explain = "このアプリケーションはApache License 2.0で配布されている作品が含まれています.\n\n" + apache_license_link + "\nの詳細は以下のボタンから\n";
		
		builder.setTitle("オープンソースライセンスについて");
		builder.setMessage(explain);
		builder.setPositiveButton("OK",
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}
		);
		builder.setView(scroll_view);
		
		Dialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}
}
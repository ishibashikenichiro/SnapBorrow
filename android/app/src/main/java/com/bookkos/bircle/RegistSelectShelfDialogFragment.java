package com.bookkos.bircle;
 
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.DialogFragment;
import android.util.Log;
import android.widget.Toast;

import com.bookkos.bircle.CaptureActivity;

import static com.bookkos.bircle.CaptureActivity._activity;
import static com.bookkos.bircle.CaptureActivity._context;
import static com.bookkos.bircle.CaptureActivity.groupId;
import static com.bookkos.bircle.CaptureActivity.initCatalogRegistUrl;
import static com.bookkos.bircle.CaptureActivity.userId;
import static com.bookkos.bircle.GCMIntentService.messageFromServer;
 
public class RegistSelectShelfDialogFragment extends DialogFragment {
	
	private DialogListener listener = null;
		
	// ログイン用preferenceのファイル名
	public static final String PREFERENCES_FILE_NAME = "user_preference";
	
	private Activity activity;
	private Context context;
	
	private String toastText;
	private String isbn;
	private CharSequence[] shelfIdArray;
	private CharSequence[] shelfNameArray;
	private int registFlag;
	
	private int selected;
	private String selectedShelfName;
	
	public RegistSelectShelfDialogFragment(Activity activity, Context context, String isbn, CharSequence[] shelf_id_array, CharSequence[] shelf_name_array, int regist_flag) {
		this.activity = activity;
		this.context = context;
		this.isbn = isbn;
		this.shelfNameArray = shelf_name_array;
		this.shelfIdArray = shelf_id_array;
		this.registFlag = regist_flag;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		for(int i = 0; i < shelfNameArray.length; i++) {
		}
		// 本棚の名前を初期選択されている本棚の名前にしておく
		selectedShelfName = shelfNameArray[0].toString();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		// アラートダイアログに本棚選択のラジオボタンを設置
		builder.setSingleChoiceItems(shelfNameArray, 0, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				//⇒アイテムを選択した時のイベント処理
				selectedShelfName = shelfNameArray[whichButton].toString();
				selected = whichButton;
			}
		});
		

		// builder.setTitle("「" + bookTitle + "」 が置いてある本棚が複数見つかりました.");
		builder.setTitle("未登録図書を読み取りました. QRコードを読み取って登録するか, 以下から登録する本棚を選択してください.");
		// builder.setMessage("どの本棚から借用したか, 以下から選択しましょう.");
		
		builder.setPositiveButton("決定",
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// 読み込んだ図書が複数の本棚にあった場合に, 本棚を選択した旨を通知するリクエスト
					CharSequence qr = shelfIdArray[selected];
					listener.doDecideClick(isbn, qr.toString(), selectedShelfName);
					dismiss();
				}
			}
		);
		builder.setNeutralButton("本棚のQRコードを読み取って登録する",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						listener.doNeutralClick();
						dismiss();
					}
				}
			);
		builder.setNegativeButton("登録しない",
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					listener.doNegativeClick();
					dismiss();
				}
			}
		);
		
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }
    
//    @Override
//    public void onStop() {
//        super.onStop();
//        getActivity().finish();
//    }
	
	public void setDialogListener(DialogListener listener){
		this.listener = listener;
	}
}
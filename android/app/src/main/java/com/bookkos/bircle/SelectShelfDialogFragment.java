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
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.bookkos.bircle.CaptureActivity;

import static com.bookkos.bircle.CaptureActivity._activity;
import static com.bookkos.bircle.CaptureActivity._context;
import static com.bookkos.bircle.CaptureActivity.groupId;
import static com.bookkos.bircle.CaptureActivity.initCatalogRegistUrl;
import static com.bookkos.bircle.CaptureActivity.userId;
import static com.bookkos.bircle.GCMIntentService.messageFromServer;
 
public class SelectShelfDialogFragment extends DialogFragment {
	
	private DialogListener listener = null;
		
	// ログイン用preferenceのファイル名
	public static final String PREFERENCES_FILE_NAME = "user_preference";
	
	private Activity activity;
	private Context context;
	
	private String toastText;
	private String bookTitle;
	private String isbn;
	private CharSequence[] shelfIdArray;
	private CharSequence[] shelfNameArray;
	private int borrowReturnFlag;
	
	private int selected;
	private String selectedShelfName;
	
	public SelectShelfDialogFragment(Activity activity, Context context, String book_title, String isbn, CharSequence[] shelf_id_array, CharSequence[] shelf_name_array, int borrow_return_flag) {
		this.activity = activity;
		this.context = context;
		this.bookTitle = book_title;
		this.isbn = isbn;
		this.shelfNameArray = shelf_name_array;
		this.shelfIdArray = shelf_id_array;
		this.borrowReturnFlag = borrow_return_flag;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		for(int i = 0; i < shelfNameArray.length; i++) {
		}
		// 本棚の名前を初期選択されている本棚の名前にしておく
		selectedShelfName = shelfNameArray[0].toString();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		this.setCancelable(false);
		
		// アラートダイアログに本棚選択のラジオボタンを設置
		builder.setSingleChoiceItems(shelfNameArray, 0, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				//⇒アイテムを選択した時のイベント処理
				selectedShelfName = shelfNameArray[whichButton].toString();
				selected = whichButton;
			}
		});
		
		// 借用時の本棚選択
		if(borrowReturnFlag == 0) {
			// builder.setTitle("「" + bookTitle + "」 が置いてある本棚が複数見つかりました.");
			builder.setTitle("この図書が置いてある本棚が複数見つかりましたので, 以下から選択してください.");
			// builder.setMessage("どの本棚から借用したか, 以下から選択しましょう.");
			
			builder.setPositiveButton("決定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 読み込んだ図書が複数の本棚にあった場合に, 本棚を選択した旨を通知するリクエスト
							CharSequence qr = shelfIdArray[selected];
							String request_url = "https://bms-dev.herokuapp.com/select_shelf_lend?qr=" + qr + "&book_code=" + isbn + "&group_id=" + groupId + "&user_id=" + userId;
							HttpConnectPostReturnFlag selected_shelf = new HttpConnectPostReturnFlag(_activity, request_url);
							selected_shelf.execute();
							
							int result_flag = 0;
							
							try {
								result_flag = selected_shelf.get();
							} catch (InterruptedException | ExecutionException e) {
								// TODO 自動生成された catch ブロック
								e.printStackTrace();
							}
//							if(result_flag == 1) {
								listener.doOKClick(borrowReturnFlag, isbn, bookTitle, selectedShelfName);
								dismiss();
//							}
//							else {
//								Toast.makeText(_context, "データの通信に失敗したようなので, 再度「決定」ボタンを押してください.", Toast.LENGTH_SHORT).show();
//							}
						}
					}
				);
			builder.setNegativeButton("やっぱり借りない",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						listener.doNegativeClick();
						dismiss();
					}
				}
			);
		}
		// 返却時の本棚選択
		else if(borrowReturnFlag == 1) {
			TextView title_text_view = new TextView(context);
			title_text_view.setText(Html.fromHtml("<big><font color=#3498db>この図書はどの本棚にも登録されていません. \n以下から本棚を選択して登録してください.</font></big>"));
			builder.setCustomTitle(title_text_view);
//			builder.setTitle(Html.fromHtml("<small><small>この図書はどの本棚にも登録されていません. <br />以下から本棚を選択して登録してください.</small></small>"));
			
			builder.setPositiveButton("決定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							CharSequence qr = shelfIdArray[selected];
							String request_url = "https://bms-dev.herokuapp.com/update_shelf_id?book_code=" + isbn + "&group_id=" + groupId + "&shelf_id=" + qr;
							HttpConnectPostReturnFlag selected_shelf = new HttpConnectPostReturnFlag(_activity, request_url);
							selected_shelf.execute();
							
							int result_flag = 0;
							
							try {
								result_flag = selected_shelf.get();
							} catch (InterruptedException | ExecutionException e) {
								// TODO 自動生成された catch ブロック
								e.printStackTrace();
							}
							
//							if(result_flag == 1) {
								listener.doOKClick(borrowReturnFlag, isbn, bookTitle, selectedShelfName);
								dismiss();
//							}
//							else {
//								Toast.makeText(_context, "データの通信に失敗したようなので, 再度「決定」ボタンを押してください.", Toast.LENGTH_SHORT).show();
//							}
						}
					}
				);
			builder.setNegativeButton("未登録本棚のままにする",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						listener.doUnregisterClick(isbn, bookTitle);
						dismiss();
					}
				}
			);
		}
		
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
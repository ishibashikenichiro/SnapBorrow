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
 
public class ExceptionDialogFragment extends DialogFragment {
	
	private DialogListener listener = null;
		
	// ログイン用preferenceのファイル名
	public static final String PREFERENCES_FILE_NAME = "user_preference";
	
	private Activity activity;
	private Context context;
	
	private String toastText;
	private String bookTitle;
	private String isbn;
	
	public ExceptionDialogFragment(Activity activity, Context context, String book_title, String isbn) {
		this.activity = activity;
		this.context = context;
		this.bookTitle = book_title;
		this.isbn = isbn;
	}
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("未登録図書を読み取りました");
		builder.setMessage("ISBNコード : " + isbn + " は登録されていません.\n登録しますか?\n\n※「登録する」を押した後は, この図書を置いておく本棚のQRコードを読み取るだけで登録できます.");
		builder.setPositiveButton("QRコードを読み取って登録する",
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					listener.doPositiveClick();
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
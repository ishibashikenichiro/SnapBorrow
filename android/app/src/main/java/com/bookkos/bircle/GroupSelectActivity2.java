package com.bookkos.bircle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.bookkos.bircle.LoginActivity.PREFERENCES_FILE_NAME;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup;

import com.bookkos.bircle.HttpConnectPost;
import com.bookkos.bircle.R;

import static com.bookkos.bircle.LoginActivity.PREFERENCES_FILE_NAME;
import static com.bookkos.bircle.LoginActivity.regId;

public class GroupSelectActivity2 extends Activity implements AsyncTaskListener {

    private Activity _activity;
    private Context _context;

    private String apiUrl = BircleTools.BircleHome+"/group/get_groups?";

    private int userId;
    private int groupId;
    private String groupName;

    private ListView listView;

    private JSONArray returnArray = null;
    private JSONObject groupIdObject = null;
    private JSONObject groupNameObject = null;
    private JSONArray groupIdArray = null;
    private JSONArray groupNameArray = null;

    private float displayInch;
    private float textSize;

    private String requestUrl;
    private HttpConnectPost httpConnectBorrowReturn;
    private int requestCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.group_select);

        _activity = this;
        _context = getApplicationContext();
        requestCount = 0;

        getUserData();

        displayInch = getInch();
        textSize = 11 * (displayInch / 4);

//		String request_url = "https://bms-dev.herokuapp.com/android_action_log?user_id="
//				+ userId + "&group_id=" + groupId + "&book_code=empty&tag=group_select&detail=グループ選択画面を開いた&shelf_id=empty";
//
//		HttpConnectPostReturnFlag send_action_log = new HttpConnectPostReturnFlag(_activity, request_url);
//		send_action_log.execute();

        // レイアウトの初期設定
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.groupSelectRelativeLayout);
        listView = (ListView)findViewById(R.id.groupListView);
        Button decisionButton = (Button)findViewById(R.id.decisionButton);
        decisionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                saveGroupData();
//				String request_url = "https://bms-dev.herokuapp.com/android_action_log?user_id="
//						+ userId + "&group_id=" + groupId + "&book_code=empty&tag=group_select&detail=グループ選択画面から戻った&shelf_id=empty";
//
//				HttpConnectPostReturnFlag send_action_log = new HttpConnectPostReturnFlag(_activity, request_url);
//				send_action_log.execute();
                Intent intent = new Intent(_context, EquipmentMana.class);
                startActivity(intent);
                finish();
            }

        });

        getGroup();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            // 戻るボタンの処理
//			Log.d("onKeyDown() in GroupSelectActivity", "バックキーが押されました");
//			String request_url = "https://bms-dev.herokuapp.com/android_action_log?user_id="
//					+ userId + "&group_id=" + groupId + "&book_code=empty&tag=group_select&detail=グループ選択画面から戻った&shelf_id=empty";
//
//			HttpConnectPostReturnFlag send_action_log = new HttpConnectPostReturnFlag(_activity, request_url);
//			send_action_log.execute();
            Intent intent = new Intent(_context,EquipmentMana.class);
            startActivity(intent);
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
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
        groupName = settings.getString("group_name", "");
    }

    private void getGroup() {
        requestUrl = apiUrl + "user_id=" + userId;

        httpConnectBorrowReturn = new HttpConnectPost(_activity, requestUrl, this);
        httpConnectBorrowReturn.execute();
    }

    private void saveGroupData() {
        SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0); // 0 -> MODE_PRIVATE
        // SharedPreferencesであるsettingsを編集する
        SharedPreferences.Editor editor = settings.edit();

        editor.putLong("group_id", groupId);
        editor.putString("group_name", groupName);

        editor.commit();
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

    private void setListView() {
        // サーバからデータが取得できたらリストビューのアイテムを設置する
        if(returnArray != null) {
            final String[] group_name_string = new String[groupIdArray.length()];
            final Integer[] group_id_int = new Integer[groupIdArray.length()];

            for(int i = 0; i < groupIdArray.length(); i++) {
                try {
                    group_name_string[i] = groupNameArray.get(i).toString();
                    group_id_int[i] = groupIdArray.getInt(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            listView.setAdapter(new ArrayAdapter<String>(
                                        this, android.R.layout.simple_list_item_single_choice, group_name_string) {
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        TextView view = (TextView)super.getView(position, convertView, parent);
                                        view.setTextSize(textSize);
                                        return view;
                                    }
                                }
            );

            // 選択の方式の設定
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

            // 指定したアイテムがチェックされているかを設定
            if(groupIdArray.length() == 1) {
                listView.setItemChecked(0, true);
                groupId = group_id_int[0];
                groupName = group_name_string[0];
            }
            else {
                for(int i = 0; i < groupIdArray.length(); i++) {
                    if(group_id_int[i] == groupId) {
                        listView.setItemChecked(i, true);
                        break;
                    }
                    else {
                        listView.setItemChecked(0, true);
                    }
                }
            }

            // アイテムがクリックされた時に呼び出されるコールバック
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    groupId = group_id_int[position];
                    groupName = group_name_string[position];
                }
            });

            listView.getCheckedItemPosition();
        }
    }

    public void asyncTaskCallback(JSONArray jsonArray, String request_url) {
        returnArray = jsonArray;

        if(returnArray == null) {
            requestCount++;
            if(requestCount < 5) {
                httpConnectBorrowReturn = new HttpConnectPost(_activity, request_url, this);
                httpConnectBorrowReturn.execute();
            }
            else {
                Toast.makeText(_context, "通信できませんでした. インターネット環境の良い場所で再度お試しください.", Toast.LENGTH_LONG).show();
            }
        }
        else {
            try {
                groupIdObject = returnArray.getJSONObject(0);
                groupNameObject = returnArray.getJSONObject(1);

                groupIdArray = groupIdObject.getJSONArray("group_ids");
                groupNameArray = groupNameObject.getJSONArray("group_names");

                setListView();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    // AsyncTaskListenerを使い回ししため, このメソッドを実装しなければならないが, 特に使用することはない
    @Override
    public void bookRegisterAsyncTaskCallback(JSONArray jsonArray, String request_url) {
    }
    @Override
    public void amazonAPIAsyncTaskCallback(String result, String request_url) {
    }
    @Override
    public void getStatusAsyncTaskCallback(JSONObject jsonObject, String request_url) {
    }
}
package com.bookkos.bircle;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.UnsupportedEncodingException;

public class EquipmentMana extends Activity {

    public static final String PREFERENCES_FILE_NAME = "user_preference";
    private ActionBar actionBar;
    private static String logout_url = BircleTools.BircleHome+ "/android_logout?";
    public static int userId;
    public static int groupId;
    private String regId;
    private String groupName;
    public static Context _context;
    private String subGroupText;
    private String groupText;
    private float displayInch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_mana);

        actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE, ActionBar.DISPLAY_USE_LOGO);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);

        //by yk.Leonier
        _context = getApplicationContext();
        // sharedPreferenceに入ってる, user_idとgroup_idとregistration_idを取得する
        getUserData();

        String title_text = "";
        title_text = "<small>現在のグループ: </small>";

        String modify_group_text = title_text + "<font color=#FF0000>" + groupName + "</font>";
        actionBar.setTitle(Html.fromHtml(modify_group_text));

        ImageButton orientationActivitySwitchButton=(ImageButton)findViewById(R.id.imageButton);
        orientationActivitySwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(),RecognitionActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.equipment, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        Intent intent;

        switch (item.getItemId()) {
            case R.id.menu_group_select:
                intent = new Intent(this, GroupSelectActivity2.class);
                startActivity(intent);
                break;
            case R.id.menu_equipment_register:
                intent = new Intent(this,EquipmentRegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_equipment_list:
                intent = new Intent(this,EquipmentListWebViewActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_book:
                intent = new Intent(this, CaptureActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_logout:
                logout();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
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

}
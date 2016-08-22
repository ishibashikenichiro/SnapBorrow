package com.bookkos.bircle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EquipmentRegisterActivity extends Activity {
    public static Activity activity;
    public static final String PREFERENCES_FILE_NAME = "user_preference";
    public static int userId;
    public static int groupId;
    public int dialogflag;
    private String regId;
    private String groupName;
    private JSONArray mFormJArr;
    private JSONArray mFormJArrCheck;
    NumberPicker numberpicker;
    public int registerNumber;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    static  final int register_row_id_start = 7000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String createmessage="既に登録済みの備品ではないかを確認してください";
        dialogflag=1;
        showDialog(createmessage);
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_equipment_register);
        getUserData();
        activity=this;

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

        final TableLayout registerTable = (TableLayout)findViewById(R.id.register_table);
        View rowView = inflater.inflate(R.layout.test, null);
        rowView.setId(register_row_id_start + 0);
        registerTable.addView(rowView);

        numberpicker=(NumberPicker)findViewById(R.id.numberPicker);
        numberpicker.setMinValue(1);
        numberpicker.setMaxValue(20);
        numberpicker.setValue(1);
        numberpicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {


            @Override
            public void onValueChange(NumberPicker picker, int oldVal,
                                      int newVal) {
                // TODO Auto-generated method stub
                registerNumber = numberpicker.getValue();

                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

                final TableLayout registerTable = (TableLayout) findViewById(R.id.register_table);
                registerTable.removeAllViews();
                for (int i = 0; i < numberpicker.getValue(); i++) {
                    View rowView = inflater.inflate(R.layout.test, null);
                    rowView.setId(register_row_id_start + i);
                    registerTable.addView(rowView);
                }
            }

        });
        Button checkbutton=(Button)findViewById(R.id.CheckButton);
        checkbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                EditText editText=(EditText)findViewById(R.id.Text_name);
                String strname=editText.getText().toString();
                final JSONArray jarr = new JSONArray();
                JSONObject jobj = new JSONObject();
            try{
                JSONObject jobj2 = new JSONObject();
                jobj2.put("userid",userId);
                jobj2.put("groupid",groupId);
                jobj2.put("equipmentname", strname);
                jarr.put(jobj2);
            }catch (Exception e){
                e.printStackTrace();
            }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mFormJArrCheck = jarr;

                        try {
                            String myresponse=post(BircleTools.BircleHome+"/check/equipmentcheck", mFormJArrCheck.toString());
                            String flag=myresponse.substring(0,1);
                            Log.v("TEST",flag);
                            if(flag.equals("1")) {
                                dialogflag=2;
                                activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        showDialog("この備品名はすでに登録されています");
                                    }
                                });
                            }
                            else {
                                dialogflag=3;
                                activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        showDialog("登録可能です");
                                    }
                                });
                                // finish();
                            }
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        Button locationActivitySwitchButton=(Button)findViewById(R.id.button);
        locationActivitySwitchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                TextView tr = (TextView) findViewById(R.id.texterror);
                EditText editText = (EditText) findViewById(R.id.Text_name);
                String strname = editText.getText().toString();
                Pattern pattern = Pattern.compile("[0-9a-zA-Z\u4E00-\u9FA5]+");
                Matcher matcher = pattern.matcher(strname);
                if (!matcher.matches()) {
                    tr.setError("備品名error");
                    Toast.makeText(activity,"備品名に空白を入れないでください", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    final TableLayout registerTable = (TableLayout) findViewById(R.id.register_table);

                    int rowCount = numberpicker.getValue();
                    JSONArray jarr = new JSONArray();
                    JSONObject jobj = new JSONObject();

                    for (int i = 0; i < rowCount; i++) {
                        final TableRow row = (TableRow) registerTable.findViewById(register_row_id_start + i);
                        final EditText cellID = (EditText) row.findViewById(R.id.cellID);
                        final EditText cellLoc = (EditText) row.findViewById(R.id.cellLoc);
                        final EditText cellDesc = (EditText) row.findViewById(R.id.cellDesc);

                        try {
                            JSONObject jobj1 = new JSONObject();
                            jobj1.put("id", cellID.getText());
                            jobj1.put("loc", cellLoc.getText());
                            jobj1.put("desc", cellDesc.getText());
                            jobj1.put("userid", userId);
                            jobj1.put("groupid", groupId);
                            jobj1.put("equipmentname", strname);
                            jobj1.put("registernumber", registerNumber);
                            jarr.put(jobj1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Log.v("TEST", jarr.toString());
                    }

                    Log.v("TEST", jarr.toString());

                    mFormJArr = jarr;

                    Intent intent = new Intent(getApplicationContext(), ContinuosShooting.class);
                    intent.putExtra("strname", strname);
                    startActivityForResult(intent, 25);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(25 == requestCode){
            if(RESULT_OK == resultCode){
                final ProgressDialog dialog = ProgressDialog.show(EquipmentRegisterActivity.this, "備品登録 ...", "登録中 ...", true);
                dialog.setCancelable(false);
                dialog.show();

                new Thread() {
                    public void run() {
                        try{
                            post(BircleTools.BircleHome + "/equipmentregistration/getjson", mFormJArr.toString());
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    finish();
                                }
                            }, 500);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }.start();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
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
    private void showDialog(String mess)
    {  //final Activity mActivity = (Activity)this.getContext();
        new AlertDialog.Builder(this).setTitle("Message")
                .setMessage(mess)
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (dialogflag == 1) {
                                    Intent it = new Intent(getApplicationContext(), RecognitionActivity.class);
                                    startActivity(it);
                                } else if (dialogflag == 2) {
                                    Intent it = new Intent(getApplicationContext(), EquipmentMana.class);
                                    startActivity(it);
                                }
                            }
                        })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialogflag == 2) {
                            Intent it = new Intent(getApplicationContext(), EquipmentMana.class);
                            startActivity(it);
                        }
                    }
                })
                .show();
    }

}

package com.bookkos.bircle;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ServerHostConfigActivity extends Activity {
    private Button mButtonSetIp;
    private EditText mFieldServerHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_host_config);

        mFieldServerHost = (EditText)findViewById(R.id.field_server_host);

        if(!BircleTools.BircleHome.isEmpty()){
            mFieldServerHost.setText(BircleTools.BircleHome);
        }

        mButtonSetIp = (Button)findViewById(R.id.button_saveIp);
        mButtonSetIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mFieldServerHost.getText().toString().trim().isEmpty()) {

                    Intent intent = new Intent();
                    String serverHost = mFieldServerHost.getText().toString().trim();

                    intent.putExtra("ServerHost", serverHost);
                    BircleTools.setServerHost(ServerHostConfigActivity.this, serverHost);

                    setResult(RESULT_OK, intent);

                    finish();
                }
            }
        });
    }
}

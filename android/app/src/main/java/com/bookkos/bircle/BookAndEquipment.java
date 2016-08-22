package com.bookkos.bircle;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BookAndEquipment extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_and_equipment);

        Button orientationActivitySwitchButton=(Button)findViewById(R.id.book_manage_button);
        orientationActivitySwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), CaptureActivity.class);
                startActivity(intent);
            }
        });

        Button locationActivitySwitchButton=(Button)findViewById(R.id.equipment_manage_button);
        locationActivitySwitchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(),EquipmentMana.class);
                startActivity(intent);
            }
        });
    }
}

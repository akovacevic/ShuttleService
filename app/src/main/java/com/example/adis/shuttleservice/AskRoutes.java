package com.example.adis.shuttleservice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;


public class AskRoutes extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_routes);
        final CheckBox bluecheckBox = (CheckBox) findViewById(R.id.blueCheckBox);
        final CheckBox orangecheckBox = (CheckBox) findViewById(R.id.orangeCheckBox);
        final CheckBox yellowcheckBox = (CheckBox) findViewById(R.id.yellowCheckBox);
        final CheckBox greencheckBox = (CheckBox) findViewById(R.id.greenCheckBox);
        final CheckBox redcheckBox = (CheckBox) findViewById(R.id.redCheckBox);

        final Button button = (Button) findViewById(R.id.viewRoutes);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean isBlue = false, isOrange = false, isYellow = false, isGreen=false, isRed = false;
                isBlue = bluecheckBox.isChecked();
                isOrange = orangecheckBox.isChecked();
                isYellow = yellowcheckBox.isChecked();
                isGreen = greencheckBox.isChecked();
                isRed = redcheckBox.isChecked();
                Intent intent = new Intent(v.getContext(),GoogleMaps.class);
                intent.putExtra("isBlueSelected",isBlue);
                intent.putExtra("isOrangeSelected",isOrange);
                intent.putExtra("isYellowSelected",isYellow);
                intent.putExtra("isGreenSelected",isGreen);
                intent.putExtra("isRedSelected",isRed);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ask_routes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

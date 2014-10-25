package com.example.adis.shuttleservice;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zsoft.signala.SendCallback;
import com.zsoft.signala.transport.StateBase;
import com.zsoft.signala.transport.longpolling.LongPollingTransport;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.content.Intent;


public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        final Button button = (Button) findViewById(R.id.passanger);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startPassanger();
            }
        });

        final Button button2 = (Button) findViewById(R.id.driver);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startDriver();
            }
        });

    }

    private void startPassanger()
    {
        Intent intent = new Intent(this,GoogleMaps.class);
        intent.putExtra("test","test");
        startActivity(intent);
    }

    private void startDriver()
    {
        Intent intent = new Intent(this,Driver.class);
        intent.putExtra("test","test");
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
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


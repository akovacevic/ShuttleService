package com.example.adis.shuttleservice;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class Driver extends Activity {
    private SignalrManager signalrManager;
    private LocationListener locationListener;

    private EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        signalrManager = new SignalrManager(this, null);

        final Button button = (Button) findViewById(R.id.start);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                start();
            }
        });

        final Button button2 = (Button) findViewById(R.id.stop);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stop();
            }
        });

        final Button button3 = (Button) findViewById(R.id.SendMessage);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                send();
            }
        });

        text = (EditText) findViewById(R.id.NameBox);

    }

    private void start()
    {
        Button button = (Button) findViewById(R.id.stop);
        button.setEnabled(true);
        Button button2 = (Button) findViewById(R.id.start);
        button2.setEnabled(false);
        signalrManager.start();
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                signalrManager.sendMessage(text.getText().toString());

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
    }

    private void stop()
    {
        Button button = (Button) findViewById(R.id.stop);
        button.setEnabled(false);
        Button button2 = (Button) findViewById(R.id.start);
        button2.setEnabled(true);
        signalrManager.stop();
        locationListener = null;
    }

    private void send()
    {
        EditText text = (EditText) findViewById(R.id.NameBox);

        signalrManager.sendMessage(text.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.driver, menu);
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

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


public class MyActivity extends Activity {

    String url = "http://signalr.adiskovacevic.com/echo";
    com.zsoft.signala.Connection con = null;

    GpsTracker gps = null;

    TextView NameView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        gps = new GpsTracker(MyActivity.this);

        // check if GPS enabled
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
        }

        con = new com.zsoft.signala.Connection(url, this, new LongPollingTransport()) {

            @Override
            public void OnError(Exception exception) {
                Toast.makeText(MyActivity.this, "On error: " + exception.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void OnMessage(String message) {
                Toast.makeText(MyActivity.this, "Message: " + message, Toast.LENGTH_LONG).show();

                try {
                    JSONObject reader = new JSONObject(message);

                    GpsCoordinates coordinates = new GpsCoordinates();

                    coordinates.Location = reader.getString("Location");
                    coordinates.Name = reader.getString("Name");
                    coordinates.Latitude = reader.getDouble("Latitude");
                    coordinates.Longitude = reader.getDouble("Longitude");

                    TextView t = (TextView) findViewById(R.id.textView);

                    String val = t.getText().toString();

                    t.setText(val + "\nName: " + coordinates.Name + " Location: (" + coordinates.Latitude + ")-(" + coordinates.Longitude + ")");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnStateChanged(StateBase oldState, StateBase newState) {

                // Toast.makeText(MyActivity.this, "oldState: " + oldState.getState() + " newState: " + newState.getState(), Toast.LENGTH_LONG).show();
            }
        };

        final Button start = (Button) findViewById(R.id.Start);
        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startSignalRConnection();

            }
        });

        final Button send = (Button) findViewById(R.id.SendMessage);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                sendMessage();

            }
        });

        final Button stop = (Button) findViewById(R.id.Stop);
        stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                stopSignalRConnection();

            }
        });

        NameView = (TextView) findViewById(R.id.editText);
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

    private void startSignalRConnection() {
        try {
            //con.addHeader("Accept", "application/json");
            con.Start();
        } catch (Exception ex) {
            Toast.makeText(MyActivity.this, "Exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void stopSignalRConnection() {
        try {
            //con.addHeader("Accept", "application/json");
            con.Stop();
        } catch (Exception ex) {
            Toast.makeText(MyActivity.this, "Exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void sendMessage() {
        try {

            GpsCoordinates test = new GpsCoordinates();
            test.Location = "0.0.0.0.1";
            test.Name = NameView.getText().toString();
            test.Latitude = gps.getLatitude();
            test.Longitude = gps.getLongitude();

            JSONStringer json = new JSONStringer().object()
                    .key("Location").value(test.Location)
                    .key("Name").value(test.Name)
                    .key("Latitude").value(test.Latitude)
                    .key("Longitude").value(test.Longitude).endObject();


            con.Send(json.toString(), new SendCallback() {
                public void OnError(Exception ex) {
                    Toast.makeText(MyActivity.this, "Error when sending: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                }

                public void OnSent(CharSequence message) {
                    Toast.makeText(MyActivity.this, "Sent: " + message, Toast.LENGTH_SHORT).show();
                }

            });
        } catch (Exception ex) {
            Toast.makeText(MyActivity.this, "Exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

}


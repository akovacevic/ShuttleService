package com.example.adis.shuttleservice;

import android.content.Context;
import android.widget.Toast;
import com.zsoft.signala.SendCallback;
import com.zsoft.signala.transport.longpolling.LongPollingTransport;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.Observable;


/**
 * Created by Adis on 10/20/2014.
 */
public class SignalrManager extends SendCallback
{
    private String url = "http://signalr.adiskovacevic.com/echo";
    private SignalrConnection con = null;
    private GpsTracker gps = null;
    private Context context;
    private LongPollingTransport transport;

    private ArrayList<Observer> observers;

    public SignalrManager(Context contextChoice, Observer o)
    {
        this.context = contextChoice;

        transport = new LongPollingTransport();

        gps = new GpsTracker(context);

        // check if GPS enabled
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
        }

        con = new SignalrConnection(url, context, transport);

        if( o != null)
            con.registerObserver(o);
    }

    public void start() {

        Toast.makeText(context, "testing the context", Toast.LENGTH_LONG).show();
        con.Start();
    }

    public void stop()
    {
        con.Stop();
    }

    public void sendMessage()
    {
        try {

            GpsCoordinates test = new GpsCoordinates();
            test.Location = "0.0.0.0.1";
            test.Name = "Adis";
            test.Latitude = gps.getLatitude();
            test.Longitude = gps.getLongitude();

            JSONStringer json = new JSONStringer().object()
                    .key("Location").value(test.Location)
                    .key("Name").value(test.Name)
                    .key("Latitude").value(test.Latitude)
                    .key("Longitude").value(test.Longitude).endObject();


            con.Send(json.toString(),this);

        } catch (Exception ex) {
            Toast.makeText(context, "Exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void OnSent(CharSequence charSequence) {
        Toast.makeText(context, "Sent", Toast.LENGTH_LONG).show();
    }

    @Override
    public void OnError(Exception e) {
        Toast.makeText(context, "Error when sending: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

}

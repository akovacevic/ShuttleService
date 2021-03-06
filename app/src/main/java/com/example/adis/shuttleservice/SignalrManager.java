package com.example.adis.shuttleservice;

import android.content.Context;
import android.content.OperationApplicationException;
import android.location.Location;
import android.widget.Toast;
import com.zsoft.signala.SendCallback;
import com.zsoft.signala.hubs.HubInvokeCallback;
import com.zsoft.signala.hubs.HubOnDataCallback;
import com.zsoft.signala.hubs.IHubProxy;
import com.zsoft.signala.transport.longpolling.LongPollingTransport;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.UUID;


/**
 * Created by Adis on 10/20/2014.
 */
public class SignalrManager
{
    //private String url = "http://signalr.adiskovacevic.com/echo";
    private String url = "http://signalr2.adiskovacevic.com/signalr";
    private SignalrConnection con = null;
    private IHubProxy hub = null;
    private Context context;
    private UUID guid;

    public SignalrManager(Context contextChoice, Observer o)
    {
        this.context = contextChoice;
        con = new SignalrConnection(url, context, new LongPollingTransport());

        if( o != null)
        {
            con.registerObserver(o);
        }

        guid = UUID.randomUUID();
    }

    public void start()
    {
        Toast.makeText(context, "testing the context", Toast.LENGTH_LONG).show();

        try
        {
            hub = con.CreateHubProxy("mainHub");
        }
        catch (OperationApplicationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        con.Start();
    }

    public void stop()
    {
        if(con != null)
        {
            con.Stop();
        }
    }

    public void sendMessage(String name, int capacity, Location location)
    {
        Toast.makeText(context,"sendingMessage",Toast.LENGTH_LONG);
        try
        {
            GpsCoordinates test = new GpsCoordinates();
            test.Name = name;
            test.Latitude = location.getLatitude();
            test.Longitude = location.getLongitude();
            test.Capacity = capacity;
            test.Guid = guid;

            JSONStringer json = new JSONStringer().object()
                    .key("Name").value(test.Name)
                    .key("Latitude").value(test.Latitude)
                    .key("Longitude").value(test.Longitude)
                    .key("Guid").value(test.Guid)
                    .key("Capacity").value(test.Capacity).endObject();


            List<String> args = new ArrayList<String>(1);
            args.add(json.toString());

            hub.Invoke("Send", args, null);

        }
        catch (Exception ex)
        {
            Toast.makeText(context, "Exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

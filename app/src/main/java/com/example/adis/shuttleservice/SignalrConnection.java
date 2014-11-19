package com.example.adis.shuttleservice;

import android.content.Context;
import android.widget.Toast;

import com.zsoft.signala.Connection;
import com.zsoft.signala.hubs.HubConnection;
import com.zsoft.signala.transport.ITransport;
import com.zsoft.signala.transport.StateBase;
import com.zsoft.signala.transport.longpolling.LongPollingTransport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Adis on 10/20/2014.
 */
public class SignalrConnection extends HubConnection implements Subject{

    private Context context;

    private ArrayList<Observer> observers;

    public SignalrConnection(String url, Context context, ITransport transport) {
        super(url, context, transport);
        observers = new ArrayList<Observer>();
        this.context = context;
    }

    @Override
    public void OnError(Exception exception) {
        Toast.makeText(context, "On error: " + exception.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void OnMessage(String message) {
        Toast.makeText(context, "Message: " + message, Toast.LENGTH_LONG).show();

        try
        {
            JSONObject reader = new JSONObject(message);
            JSONArray array = reader.getJSONArray("A");

            String t = (String) array.get(0);
            JSONObject reader2 = new JSONObject(t);

            GpsCoordinates coordinates = new GpsCoordinates();
            coordinates.Name = reader2.getString("Name");
            coordinates.Latitude = reader2.getDouble("Latitude");
            coordinates.Longitude = reader2.getDouble("Longitude");
            coordinates.Capacity = reader2.getInt("Capacity");
            coordinates.Guid = UUID.fromString(reader2.getString("Guid"));

            for(Observer observer: observers)
            {
                observer.update(coordinates);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }
    @Override
    public void OnStateChanged(StateBase oldState, StateBase newState) {

        Toast.makeText(context, "oldState: " + oldState.getState() + " newState: " + newState.getState(), Toast.LENGTH_LONG).show();
    }

    public void registerObserver(Observer o)
    {
        observers.add(o);
    }

    public void removeObserver(Observer o)
    {
        int i = observers.indexOf(o);

        if(i >= 0)
        {
            observers.remove(i);
        }
    }
}

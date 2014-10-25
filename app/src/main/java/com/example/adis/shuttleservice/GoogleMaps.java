package com.example.adis.shuttleservice;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


public class GoogleMaps extends Activity implements Observer {
    static final LatLng KIEL = new LatLng(53.551, 9.993);
    private GoogleMap map;

    private HashMap<String,GpsCoordinates> coordinates;

    private GpsTracker gps = null;
    private SignalrManager signalrManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        coordinates= new HashMap<String, GpsCoordinates>();

        signalrManager = new SignalrManager(this,this);
        signalrManager.start();

        gps = new GpsTracker(this);

        LatLng current = new LatLng(gps.getLatitude(),gps.getLongitude());

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        Marker You = map.addMarker(new MarkerOptions().position(current)
                .title("You"));

        Marker kiel = map.addMarker(new MarkerOptions()
                .position(KIEL)
                .title("Kiel")
                .snippet("Kiel is cool")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_launcher)));

        // Move the camera instantly to hamburg with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 200));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(300), 1000, null);

    }

    @Override
    public void update(GpsCoordinates gpsCoordinates)
    {
        if(gpsCoordinates.Latitude == 0.0 && gpsCoordinates.Longitude == 0.0)
            return;

        coordinates.put(gpsCoordinates.Name, gpsCoordinates);

        map.clear();

        for(Map.Entry<String,GpsCoordinates> entry : coordinates.entrySet())
        {
            String key = entry.getKey();
            GpsCoordinates gpsCoordinate = entry.getValue();

            LatLng coor = new LatLng(gpsCoordinate.Latitude,gpsCoordinate.Longitude);

            map.addMarker(new MarkerOptions().position(coor)
                    .title(key)
                    .icon(BitmapDescriptorFactory
                    .fromResource(R.drawable.busicon)));
        }

        LatLng current = new LatLng(gps.getLatitude(),gps.getLongitude());

        map.addMarker(new MarkerOptions().position(current)
                .title("You"));
    }
}

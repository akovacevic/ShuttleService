package com.example.adis.shuttleservice;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.WindowManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        coordinates= new HashMap<String, GpsCoordinates>();

        signalrManager = new SignalrManager(this,this);

        gps = new GpsTracker(this);
        Location location = gps.getLocation();
        LatLng current = new LatLng(location.getLatitude(),location.getLongitude());

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        Marker You = map.addMarker(new MarkerOptions().position(current)
                .title("You"));

        Marker kiel = map.addMarker(new MarkerOptions()
                .position(KIEL)
                .title("Kiel")
                .snippet("Kiel is cool")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.busicon)));

        Polyline orangeLine = map.addPolyline(new PolylineOptions()
                .add(new LatLng(32.734316, -97.121659), new LatLng(32.734302, -97.119075),new LatLng(32.733384, -97.119092),new LatLng(32.733368, -97.117962) )
                .width(25)
                .color(Color.argb(255,237,145,33))
                .geodesic(true));


        // Move the camera instantly to hamburg with a zoom of 15
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 100));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(300), 1000, null);

        signalrManager.start();
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
        Location location = gps.getLocation();
        LatLng current = new LatLng(location.getLatitude(),location.getLongitude());
        map.addMarker(new MarkerOptions().position(current)
                .title("You"));
    }
}

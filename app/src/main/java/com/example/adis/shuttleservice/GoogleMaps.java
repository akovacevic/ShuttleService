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

    private Polyline orangeLine;
    private Polyline greenLine;
    private Polyline redLine;
    private Polyline blueLine;

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

        addConstants();

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 5));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(15), 1000, null);

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
                    .title(key + ", Capacity: " + gpsCoordinate.Capacity)
                    .icon(BitmapDescriptorFactory
                    .fromResource(R.drawable.busicon)));
        }
        Location location = gps.getLocation();
        LatLng current = new LatLng(location.getLatitude(),location.getLongitude());
        map.addMarker(new MarkerOptions().position(current)
                .title("You"));

        addConstants();

    }

    private void addConstants()
    {
        orangeLine = map.addPolyline(new PolylineOptions()
                .add(new LatLng(32.734316, -97.121659),
                        new LatLng(32.734302, -97.119075),
                        new LatLng(32.733384, -97.119092),
                        new LatLng(32.733368, -97.117962),
                        new LatLng(32.733321, -97.117966),
                        new LatLng(32.733315, -97.117700),
                        new LatLng(32.732781, -97.117692),
                        new LatLng(32.732711, -97.117619),
                        new LatLng(32.732691, -97.116391),
                        new LatLng(32.733892, -97.116353),
                        new LatLng(32.734120, -97.116284),
                        new LatLng(32.733820, -97.115197),
                        new LatLng(32.733707, -97.112123),
                        new LatLng(32.732096, -97.112145),
                        new LatLng(32.732055, -97.108728),
                        new LatLng(32.730133, -97.108765),
                        new LatLng(32.730144, -97.109892),
                        new LatLng(32.727184, -97.109951),
                        new LatLng(32.727012, -97.111083),
                        new LatLng(32.726897, -97.112156),
                        new LatLng(32.727057, -97.113207),
                        new LatLng(32.727206, -97.113953),
                        new LatLng(32.727224, -97.114586),
                        new LatLng(32.724388, -97.114628),
                        new LatLng(32.724451, -97.123689),
                        new LatLng(32.726067, -97.123710),
                        new LatLng(32.726103, -97.125647),
                        new LatLng(32.726071, -97.126585),
                        new LatLng(32.725945, -97.127074),
                        new LatLng(32.725877, -97.127648),
                        new LatLng(32.727881, -97.127573),
                        new LatLng(32.727889, -97.126285),
                        new LatLng(32.726109, -97.126282),
                        new LatLng(32.726114, -97.125062),
                        new LatLng(32.726130, -97.123716),
                        new LatLng(32.729377, -97.123670),
                        new LatLng(32.729397, -97.121687),
                        new LatLng(32.734334, -97.121628)
                )
                .width(15)
                .color(Color.argb(100,237,145,33))
                .geodesic(true));

        greenLine = map.addPolyline(new PolylineOptions()
                .add(new LatLng(32.730146, -97.109900),
                        new LatLng(32.730128, -97.108774),
                        new LatLng(32.722458, -97.108907),
                        new LatLng(32.722490, -97.111162),
                        new LatLng(32.727016, -97.111085),
                        new LatLng(32.727190, -97.109934),
                        new LatLng(32.730146, -97.109900)
                )
                .width(15)
                .color(Color.GREEN)
                .geodesic(true));

        redLine = map.addPolyline(new PolylineOptions()
                .add(new LatLng(32.732081, -97.111593),
                        new LatLng(32.732054, -97.108622)
                )
                .width(15)
                .color(Color.RED)
                .geodesic(true));

        blueLine = map.addPolyline(new PolylineOptions()
                .add(new LatLng(32.734316, -97.121659),
                        new LatLng(32.734302, -97.119075),
                        new LatLng(32.733384, -97.119092),
                        new LatLng(32.733368, -97.117962),
                        new LatLng(32.733321, -97.117966),
                        new LatLng(32.733315, -97.117700),
                        new LatLng(32.732781, -97.117692),
                        new LatLng(32.732711, -97.117619),
                        new LatLng(32.732691, -97.116391),
                        new LatLng(32.733892, -97.116353),
                        new LatLng(32.734120, -97.116284),
                        new LatLng(32.733820, -97.115197),
                        new LatLng(32.733752, -97.114564),
                        new LatLng(32.734962, -97.114537),
                        new LatLng(32.734971, -97.112882),
                        new LatLng(32.733705, -97.112895),
                        new LatLng(32.733707, -97.112123),
                        new LatLng(32.732096, -97.112145),
                        new LatLng(32.732055, -97.108728),
                        new LatLng(32.730133, -97.108765),
                        new LatLng(32.730144, -97.109892),
                        new LatLng(32.727184, -97.109951),
                        new LatLng(32.727012, -97.111083),
                        new LatLng(32.727012, -97.111083),
                        new LatLng(32.727000, -97.111083),
                        new LatLng(32.728391, -97.112211),
                        new LatLng(32.728415, -97.116251),
                        new LatLng(32.728588, -97.116407),
                        new LatLng(32.730643, -97.116413),
                        new LatLng(32.730656, -97.117948),
                        new LatLng(32.730780, -97.118755),
                        new LatLng(32.730816, -97.118956),
                        new LatLng(32.730803, -97.125146),
                        new LatLng(32.729591, -97.125154),
                        new LatLng(32.729587, -97.124645),
                        new LatLng(32.729447, -97.124651),
                        new LatLng(32.729426, -97.125146),
                        new LatLng(32.728038, -97.125156),
                        new LatLng(32.727871, -97.125285),
                        new LatLng(32.727878, -97.127560),
                        new LatLng(32.725897, -97.127619),
                        new LatLng(32.726009, -97.126851),
                        new LatLng(32.726092, -97.126298),
                        new LatLng(32.726102, -97.123724),
                        new LatLng(32.729394, -97.123670),
                        new LatLng(32.729399, -97.121702),
                        new LatLng(32.734316, -97.121659)
                )
                .width(15)
                .color(Color.BLUE)
                .geodesic(true));
    }
}

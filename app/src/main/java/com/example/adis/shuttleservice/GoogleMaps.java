package com.example.adis.shuttleservice;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class GoogleMaps extends Activity implements Observer {
    private GoogleMap map;
    private HashMap<UUID,Pair<GpsCoordinates,Marker>> coordinates;
    private SignalrManager signalrManager;
    private Button centerButton;
    private Marker you;
    private LocationManager manager;
    private LocationListener locationListener;

    private ArrayList<Marker> orangeStops;
    private ArrayList<Marker> greenStops;
    private ArrayList<Marker> redStops;
    private ArrayList<Marker> blueStops;
    private ArrayList<Marker> yellowStops;

    private Polyline orangeLine;
    private Polyline greenLine;
    private Polyline redLine;
    private Polyline blueLine;
    private Polyline yellowLine;
    boolean isBlueSelected = false, isOrangeSelected = false, isYellowSelected = false, isGreenSelected = false, isRedSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent AskRoutesIntent = getIntent();
        isBlueSelected = AskRoutesIntent.getBooleanExtra("isBlueSelected",false);
        isOrangeSelected = AskRoutesIntent.getBooleanExtra("isOrangeSelected",false);
        isYellowSelected = AskRoutesIntent.getBooleanExtra("isYellowSelected",false);
        isGreenSelected = AskRoutesIntent.getBooleanExtra("isGreenSelected",false);
        isRedSelected = AskRoutesIntent.getBooleanExtra("isRedSelected",false);

        coordinates= new HashMap<UUID, Pair<GpsCoordinates,Marker>>();

        orangeStops = new ArrayList<Marker>();
        greenStops = new ArrayList<Marker>();
        redStops = new ArrayList<Marker>();
        blueStops = new ArrayList<Marker>();
        yellowStops = new ArrayList<Marker>();


        signalrManager = new SignalrManager(this,this);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                animateMarker(you, new LatLng(location.getLatitude(),location.getLongitude()),false);
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}
            @Override
            public void onProviderEnabled(String s) {}
            @Override
            public void onProviderDisabled(String s) {}
        };

        manager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,10,locationListener);

        Location lastKnown = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        LatLng current = new LatLng(lastKnown.getLatitude(),lastKnown.getLongitude());

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        map.clear();
        you = map.addMarker(new MarkerOptions().position(current)
                .title("You"));

        addConstants();

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 5));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(15), 1000, null);

        signalrManager.start();

        centerButton = (Button)findViewById(R.id.GoToLocationButton);

        centerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                centerToMarker();
            }
        });
    }

    @Override
    public void update(GpsCoordinates gpsCoordinates)
    {
        try
        {
            LatLng newCoor = new LatLng(gpsCoordinates.Latitude, gpsCoordinates.Longitude);

            if (coordinates.containsKey(gpsCoordinates.Guid)) {
                Pair<GpsCoordinates, Marker> old = coordinates.get(gpsCoordinates.Guid);
                old.second.setTitle(gpsCoordinates.Name + ", Capacity: " + gpsCoordinates.Capacity);

                animateMarker(old.second, newCoor, false);
            }
            else {
                Marker marker = map.addMarker(new MarkerOptions().position(newCoor)
                        .title(gpsCoordinates.Name + ", Capacity: " + gpsCoordinates.Capacity)
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.busicon)));
                coordinates.put(gpsCoordinates.Guid, new Pair<GpsCoordinates, Marker>(gpsCoordinates, marker));
            }
        }
        catch(Exception ex)
        {
            ex.getStackTrace();
        }
    }

    private void centerToMarker()
    {
        Location newPosition = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        LatLng newLatLng = new LatLng(newPosition.getLatitude(),newPosition.getLongitude());

        animateMarker(you,newLatLng,false);

        map.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));

    }

    private void addStops(ArrayList<Marker> stops, LatLng latlng)
    {
        stops.add(map.addMarker(new MarkerOptions().position(latlng)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.busstopicon))));
    }

    private void addConstants()
    {
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(orangeStops.contains(marker) || blueStops.contains(marker) || yellowStops.contains(marker) || greenStops.contains(marker) || redStops.contains(marker))
                {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr=" + you.getPosition().latitude + "," + you.getPosition().longitude +
                                    "&daddr=" + marker.getPosition().latitude + "," + marker.getPosition().longitude));
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });


        //Add all orangeStops
        if(isOrangeSelected) {

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


            addStops(orangeStops, new LatLng(32.734214, -97.121572));//Swift Center/Midtown Blue, orange routes stop here
            addStops(orangeStops, new LatLng(32.733708, -97.119082));//Timberbrook/Campus Edge Blue, orange routes stop here
            addStops(orangeStops, new LatLng(32.732679, -97.116507)); //MAC Building Blue, orange routes stop here
            addStops(orangeStops, new LatLng(32.732048, -97.111819)); //University Center Blue, red, orange routes stop here
            addStops(orangeStops, new LatLng(32.729466, -97.109963)); //Business Building Blue, green, orange routes stop here
            addStops(orangeStops, new LatLng(32.724466, -97.119737)); //Maverick Place Orange route stops here
            addStops(orangeStops, new LatLng(32.727896, -97.126281)); //Stadium Lot 26 Blue, yellow, orange routes stop here
            addStops(orangeStops, new LatLng(32.730631, -97.121657)); //Arbor Oaks Blue, orange routes stop here
            addStops(orangeStops, new LatLng(32.731416, -97.121657));//Meadow Run Blue, orange routes stop here
        }
        //Add all Bluestops
        if(isBlueSelected) {

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
                    .color(Color.argb(100,0,0,255))
                    .geodesic(true));

            addStops(blueStops, new LatLng(32.734214, -97.121572));//Swift Center/Midtown Blue, orange routes stop here
            addStops(blueStops, new LatLng(32.733708, -97.119082));//Timberbrook/Campus Edge Blue, orange routes stop here
            addStops(blueStops, new LatLng(32.732679, -97.116507)); //MAC Building Blue, orange routes stop here
            addStops(blueStops, new LatLng(32.734931, -97.114109)); //Social Work Complex Blue route stops here
            addStops(blueStops, new LatLng(32.732048, -97.111819)); //University Center Blue, red, orange routes stop here
            addStops(blueStops, new LatLng(32.729466, -97.109963)); //Business Building Blue, green, orange routes stop here
            addStops(blueStops, new LatLng(32.728436, -97.113738)); //University Hall Blue, yellow routes stop here
            addStops(blueStops, new LatLng(32.730579, -97.116393)); //Smart Hospital Blue, yellow routes stop here
            addStops(blueStops, new LatLng(32.730818, -97.120465)); //Greek Row Blue, yellow routes stop here
            addStops(blueStops, new LatLng(32.730631, -97.121657)); //Arbor Oaks Blue, orange routes stop here
            addStops(blueStops, new LatLng(32.727896, -97.126281)); //Stadium Lot 26 Blue, yellow, orange routes stop here
            addStops(blueStops, new LatLng(32.729731, -97.124955)); //Studio Arts Blue route stops here
            addStops(blueStops, new LatLng(32.731416, -97.121657));//Meadow Run Blue, orange routes stop here
        }

        if(isYellowSelected) {

            yellowLine = map.addPolyline(new PolylineOptions()
                    .add(new LatLng(32.727858, -97.127580),
                            new LatLng(32.726098, -97.127612),
                            new LatLng(32.725882, -97.127623),
                            new LatLng(32.726001, -97.126899),
                            new LatLng(32.726087, -97.126287),
                            new LatLng(32.726128, -97.124292),
                            new LatLng(32.726033, -97.123326),
                            new LatLng(32.726010, -97.122613),
                            new LatLng(32.725920, -97.118230),
                            new LatLng(32.726010, -97.117549),
                            new LatLng(32.726416, -97.116744),
                            new LatLng(32.726971, -97.115945),
                            new LatLng(32.727188, -97.115248),
                            new LatLng(32.727244, -97.114000),
                            new LatLng(32.726973, -97.112887),
                            new LatLng(32.726912, -97.111771),
                            new LatLng(32.727026, -97.111065),
                            new LatLng(32.728371, -97.111076),
                            new LatLng(32.728443, -97.116280),
                            new LatLng(32.728615, -97.116419),
                            new LatLng(32.733917, -97.116387),
                            new LatLng(32.734134, -97.116258),
                            new LatLng(32.734278, -97.116773),
                            new LatLng(32.734332, -97.120442),
                            new LatLng(32.733231, -97.120635),
                            new LatLng(32.730767, -97.120635),
                            new LatLng(32.730758, -97.123532),
                            new LatLng(32.730623, -97.123736),
                            new LatLng(32.730677, -97.124176),
                            new LatLng(32.730677, -97.125152),
                            new LatLng(32.727978, -97.125185),
                            new LatLng(32.727888, -97.125303),
                            new LatLng(32.727906, -97.127588)
                    )
                    .width(15)
                    .color(Color.YELLOW)
                    .geodesic(true));

            addStops(yellowStops, new LatLng(32.727896, -97.126281)); //Stadium Lot 26 Blue, yellow, orange routes stop here
            addStops(yellowStops, new LatLng(32.730818, -97.120465)); //Greek Row Blue, yellow routes stop here
            addStops(yellowStops, new LatLng(32.733873, -97.120642)); //Swift Center Yellow route stops here
            addStops(yellowStops, new LatLng(32.730579, -97.116393)); //Smart Hospital Blue, yellow routes stop here
            addStops(yellowStops, new LatLng(32.728436, -97.113738)); //University Hall Blue, yellow routes stop here
        }

        if(isGreenSelected) {

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

            addStops(greenStops, new LatLng(32.729466, -97.109963)); //Business Building Blue, green, orange routes stop here
            addStops(greenStops, new LatLng(32.725164, -97.108829)); //Pecan Street Green route stops here
            addStops(greenStops, new LatLng(32.723720, -97.111265)); //West Street (south stand) Green route stops here
        }

        if(isRedSelected) {

            redLine = map.addPolyline(new PolylineOptions()
                    .add(new LatLng(32.732081, -97.111593),
                            new LatLng(32.732080, -97.111670),
                            new LatLng(32.732076, -97.111347),
                            new LatLng(32.732068, -97.111025),
                            new LatLng(32.732069, -97.110702),
                            new LatLng(32.732072, -97.110347),
                            new LatLng(32.732073, -97.110059),
                            new LatLng(32.732072, -97.109819),
                            new LatLng(32.732050, -97.108642)
                    )
                    .width(15)
                    .color(Color.RED)
                    .geodesic(true));

            addStops(redStops, new LatLng(32.732048, -97.111819)); //University Center Blue, red, orange routes stop here
            addStops(redStops, new LatLng(32.732050, -97.108642)); //College Park Garage Red route stops here
        }
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = map.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }
}

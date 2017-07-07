package com.example.hp.TranSaver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.kml.KmlLayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.hp.TranSaver.FetchNearestStation.getNearest_station_long;
import static com.example.hp.TranSaver.InsertLocation.destination;
import static com.example.hp.TranSaver.InsertLocation.iden;
import static com.example.hp.TranSaver.InsertLocation.latitude;
import static com.example.hp.TranSaver.InsertLocation.longitude;
import static com.example.hp.TranSaver.LoginActivity.id;
import static com.example.hp.TranSaver.TramLocationRetrieve.locationList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    double longitude_loc;
    double latitude_loc;
    double lat = 31.2198358;
    double longi = 29.9415861;
    LatLng location, Loc_distination;
    private BroadcastReceiver broadcastReceiver;
    public static Marker marker, marker_Dest;
    LocationObj myLoc = new LocationObj();
    LocationObj DestLoc = new LocationObj();
    static int joined, request;
    double currentDistance;
    List<MarkerOptions> markers = new ArrayList<>();
    List<Marker> Trammarkers = new ArrayList<>();
    Boolean canJoin = false;
    Button Join;
    int stopFlag = 0;
    int my_tram_index;
    double distance, time, distTodest, timetodest;

    @Override
    protected void onStart() {
        super.onStart();
        Intent i = new Intent(getApplicationContext(), GPS_Service.class);
        startService(i);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        Intent i = new Intent(getApplicationContext(), GPS_Service.class);
        stopService(i);
        joined = 0;
        request = 0;
        destination = "0";
        UpdateLocation updateLocation = new UpdateLocation(this);
        updateLocation.execute(latitude, longitude, iden, Integer.toString(request), destination, Integer.toString(joined));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        request = 1;
        Join = (Button) findViewById(R.id.buttonJoin);
        if (savedInstanceState == null || !savedInstanceState.containsKey("MY_LOC")) {
            iden = id;
        } else {
            myLoc = savedInstanceState.getParcelable("MY_LOC");
            location = new LatLng(myLoc.getLatitude(), myLoc.getLongitude());
            DestLoc = savedInstanceState.getParcelable("Destination");
            Loc_distination = new LatLng(DestLoc.getLatitude(), DestLoc.getLongitude());
            locationList = savedInstanceState.getParcelableArrayList("TRAMS");
            iden = savedInstanceState.getString("iden");
            joined = savedInstanceState.getInt("join");
            distance = savedInstanceState.getDouble("distance");
            time = savedInstanceState.getDouble("Time");
            distTodest = savedInstanceState.getDouble("distance_to_destination");
            timetodest = savedInstanceState.getDouble("Time_to_destination");
            // new getNearestStationID(this).execute(id);
            request = 1;
            destination = savedInstanceState.getString("destin");
            UpdateLocation updateLocation = new UpdateLocation(this);
            updateLocation.execute(latitude, longitude, iden, Integer.toString(request), destination, Integer.toString(joined));
        }
        if (joined == 1) {
            distance = 0;
            time = 0;
            Join.setBackgroundResource(R.drawable.circle_button);

            StateListDrawable drawable = (StateListDrawable) Join.getBackground();
            drawable.setColorFilter(Color.rgb(100, 100, 100), PorterDuff.Mode.MULTIPLY);//MULTIPLY
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                SharedPreferences preferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                stopService(i);
                new DeleteLocation(this).execute(id);
                Intent intent = new Intent(this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intent);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    longitude_loc = (double) intent.getExtras().get("Longitude");
                    latitude_loc = (double) intent.getExtras().get("Latitude");
                    location = new LatLng(latitude_loc, longitude_loc);
                    myLoc.setIden(id);
                    myLoc.setLatitude(latitude_loc);
                    myLoc.setLongitude(longitude_loc);
                    Log.e("Longitude :" + longitude_loc, "latitude :" + latitude_loc);
                    Loc_distination = new LatLng(RetrieveStationId.getStation_lat(), RetrieveStationId.getStation_lon());
                    Log.e("Dest Lat :" + RetrieveStationId.getStation_lat(), "Des lon :" + RetrieveStationId.getStation_lon());
                    DestLoc.setIden(RetrieveStationId.station_id);
                    DestLoc.setLatitude(RetrieveStationId.getStation_lat());
                    DestLoc.setLongitude(RetrieveStationId.getStation_lon());
                    updateTramMarkers(locationList);
                    Join.setBackgroundResource(R.drawable.circle_button);
                    if (joined == 1) {
                        distance = 0;
                        time = 0;
                        double min;
                        if (!locationList.isEmpty()) {
                            min = measureDistance(locationList.get(0).getLatitude(), locationList.get(0).getLongitude(), myLoc.getLatitude(), myLoc.getLongitude());
                            my_tram_index = 0;
                            for (int j = 1; j < locationList.size(); j++) {
                                double distance2 = measureDistance(locationList.get(j).getLatitude(), locationList.get(j).getLongitude(), myLoc.getLatitude(), myLoc.getLongitude());
                                if (distance2 < min) {
                                    min = distance2;
                                    my_tram_index = j;
                                }
                            }
                            // checking that the joined user is not away from the centroid
//                            if(measureDistance(locationList.get(my_tram_index).getLatitude(),locationList.get(my_tram_index).getLongitude(),myLoc.getLatitude(),myLoc.getLongitude())> 50)
//                            {
//                                joined=0;
//                                stopFlag=1;
//                                UpdateLocation updateLocation = new UpdateLocation(getApplicationContext());
//                                updateLocation.execute(latitude, longitude,iden,Integer.toString(MapsActivity.request),destination,Integer.toString(MapsActivity.joined));
//                            }
                            if (joined == 1) {
                                distTodest = measureDistance(locationList.get(my_tram_index).getLatitude(), locationList.get(my_tram_index).getLongitude(), DestLoc.getLatitude(), DestLoc.getLongitude());
                                timetodest = distTodest / (20 * 10 / 36.0);
                            }
                            StateListDrawable drawable = (StateListDrawable) Join.getBackground();
                            drawable.setColorFilter(Color.rgb(100, 100, 100), PorterDuff.Mode.MULTIPLY);
                        }
                    }
                    if (joined == 0) {
                        if (!locationList.isEmpty()) {
                            distance = measureDistance(locationList.get(0).getLatitude(), locationList.get(0).getLongitude(), FetchNearestStation.getNearest_station_lat(), getNearest_station_long());
                            for (int j = 1; j < locationList.size(); j++) {
                                double distance2 = measureDistance(locationList.get(j).getLatitude(), locationList.get(j).getLongitude(), FetchNearestStation.getNearest_station_lat(), FetchNearestStation.getNearest_station_long());
                                if (distance2 < distance)
                                    distance = distance2;
                            }
                            time = distance / (20 * 10 / 36.0);
                        }
                        StateListDrawable drawable = (StateListDrawable) Join.getBackground();
                        drawable.setColorFilter(Color.rgb(238, 238, 238), PorterDuff.Mode.MULTIPLY);
                    }
                    updateCamera();
                    updateMarker();
                    Log.e("id is", iden);
                    Log.e("join id", Integer.toString(joined));
                }
            };
        }

        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }

    private void updateTramMarkers(ArrayList<LocationObj> locationObjs) {
        for (int i = 0; i < Trammarkers.size(); i++) {
            Trammarkers.get(i).remove();
        }

        markers.clear();
        Trammarkers.clear();

        for (int y = 0; y < locationObjs.size(); y++) {
            LatLng tramLoc = new LatLng(locationList.get(y).getLatitude(), locationList.get(y).getLongitude());
            Log.e("LOC LIST", Double.toString(locationList.get(y).getLatitude()) + " " + Double.toString(locationList.get(y).getLongitude()));
            MarkerOptions markerOptions = new MarkerOptions().position(tramLoc).title("Tram").icon(BitmapDescriptorFactory.fromResource(R.drawable.tram));
            Trammarkers.add(mMap.addMarker(markerOptions));
            markers.add(markerOptions);//arrayList of markerOptions
        }

    }

    private void updateMarker() {
        if (marker != null) {
            marker.remove();
            Log.i("update Marker", "marker removed");
        }
        if (marker_Dest != null) {
            marker_Dest.remove();
        }

        marker = mMap.addMarker(new MarkerOptions().position(location).title("current location").icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location)));
        marker.showInfoWindow();
        marker_Dest = mMap.addMarker(new MarkerOptions().position(Loc_distination).title("destination: " + Home.station_destination).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

    }

    private void updateCamera() {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        joined = 0;
        request = 0;
        destination = "0";
        UpdateLocation updateLocation = new UpdateLocation(this);
        updateLocation.execute(latitude, longitude, iden, Integer.toString(request), destination, Integer.toString(joined));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        // Position the map's camera near alex,egypt
        // googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        try {
            KmlLayer layer = new KmlLayer(googleMap, R.raw.tramstations, getApplicationContext());
            layer.addLayerToMap();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("error", "KML");
            e.printStackTrace();
        }
        LatLng latLng = new LatLng(lat, longi);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(10).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap = googleMap;

        MapStateManager mgr = new MapStateManager(this);
        CameraPosition position = mgr.getSavedCameraPosition();
        if (location != null && Loc_distination != null) {
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
            // Toast.makeText(this, "entering Resume State", Toast.LENGTH_SHORT).show();
            mMap.moveCamera(update);
            updateMarker();
            mMap.setMapType(mgr.getSavedMapType());
        }
        if (mMap != null) {
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker mark) {
                    View v = getLayoutInflater().inflate(R.layout.info_window, null);

                    RelativeLayout header = (RelativeLayout) v.findViewById(R.id.header);
                    TextView MYLocation = (TextView) header.findViewById(R.id.myLocation);
                    LinearLayout l0 = (LinearLayout) v.findViewById(R.id.content);
                    LinearLayout l1 = (LinearLayout) l0.findViewById(R.id.layout1);
                    TextView timetxt = (TextView) l1.findViewById(R.id.time_tv);
                    TextView disttxt = (TextView) l1.findViewById(R.id.dist_tv);

                    LinearLayout l2 = (LinearLayout) l0.findViewById(R.id.layout2);
                    TextView time_value = (TextView) l2.findViewById(R.id.TVal);
                    TextView dist_value = (TextView) l2.findViewById(R.id.DVal);


                    if (joined == 0) {
                        timetxt.setText("Time to nearest station  ");
                        disttxt.setText("Distance from nearest tram");
                        if (time > 60) {
                            double time_in_min = time / 60;

                            time_value.setText(new DecimalFormat("#.##").format(time_in_min) + " min");
                        } else {
                            time_value.setText(new DecimalFormat("#.##").format(time) + " s");
                        }
                        dist_value.setText(new DecimalFormat("#.##").format(distance) + " m");
                    } else if (joined == 1) {
                        timetxt.setText("Time to destination  ");
                        disttxt.setText("Distance from destination");
                        if (timetodest > 60) {
                            double timetodest_in_min = timetodest / 60;
                            time_value.setText(new DecimalFormat("#.##").format(timetodest_in_min) + " min");
                        } else {
                            time_value.setText(new DecimalFormat("#.##").format(timetodest) + " s");
                        }
                        dist_value.setText(new DecimalFormat("#.##").format(distTodest) + " m");
                    }
                    if (mark.equals(marker))
                        return v;
                    else
                        return null;
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putParcelable("MY_LOC", myLoc);
        outState.putParcelable("Destination", DestLoc);
        outState.putParcelableArrayList("TRAMS", locationList);
        outState.putInt("join", joined);
        outState.putString("destin", destination);
        outState.putString("iden", iden);
        outState.putDouble("distance", distance);
        outState.putDouble("Time", time);
        outState.putDouble("distance_to_destination", distTodest);
        outState.putDouble("Time_to_destination", timetodest);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MapStateManager mgr = new MapStateManager(this);
        mgr.saveMapState(mMap);
    }

    public void joinBtn(View view) {
        new checkForJoin(this).execute(id);
        while (checkForJoin.JoinjsonStr == null) {
            SystemClock.sleep(1000);
        }

        if (checkForJoin.JoinjsonStr.compareTo("1") == 1) {
            canJoin = true;
        }

        if (canJoin == true) {
            currentDistance = measureDistance(myLoc.getLatitude(), myLoc.getLongitude(), RetrieveStationId.getStation_lat(), RetrieveStationId.getStation_lon());
            callAsynchronousTask();
        }

    }

    public void check(double oldDistance) {
        currentDistance = measureDistance(myLoc.getLatitude(), myLoc.getLongitude(), RetrieveStationId.getStation_lat(), RetrieveStationId.getStation_lon());
        if ((oldDistance - currentDistance < -30) || currentDistance == 0) {
            Log.e("Diff", Double.toString(oldDistance - currentDistance));
            joined = 0;
            Join.setBackgroundResource(R.drawable.circle_button);

            StateListDrawable drawable = (StateListDrawable) Join.getBackground();
            drawable.setColorFilter(Color.rgb(238, 238, 238), PorterDuff.Mode.MULTIPLY);
            updateMarker();
            updateCamera();
            stopFlag = 1;
        } else if (oldDistance - currentDistance > 0) {
            Log.e("Diff", Double.toString(oldDistance - currentDistance));
            joined = 1;
            Join.setBackgroundResource(R.drawable.circle_button);

            StateListDrawable drawable = (StateListDrawable) Join.getBackground();
            drawable.setColorFilter(Color.rgb(100, 100, 100), PorterDuff.Mode.MULTIPLY);
        }
        UpdateLocation updateLocation = new UpdateLocation(this);
        updateLocation.execute(latitude, longitude, iden, Integer.toString(request), destination, Integer.toString(joined));
        updateMarker();
        updateCamera();

    }

    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {

                        try {
                            check(currentDistance);
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                });
            }
        };
        if (stopFlag == 1) {
            timer.cancel();
            return;
        } else
            timer.schedule(doAsynchronousTask, 0, 30000); //execute in every 30000 ms
    }

    public double measureDistance(double Requested_latitude, double Requested_longitude, double joined_latitude, double joined_longitude) {
        double earthRadius = 6371000;
        double Requested_lat = Math.toRadians(Requested_latitude);
        double Requested_long = Math.toRadians(Requested_longitude);
        double joined_lat = Math.toRadians(joined_latitude);
        double joined_long = Math.toRadians(joined_longitude);

        double dlon = joined_long - Requested_long;
        double dlat = joined_lat - Requested_lat;
        double a = (Math.pow(Math.sin(dlat / 2), 2)) + Math.cos(Requested_lat) * Math.cos(joined_lat) * (Math.pow(Math.sin(dlon / 2), 2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = earthRadius * c;
        return d;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}

package com.example.hp.TranSaver;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.example.hp.TranSaver.LoginActivity.id;
import static com.example.hp.TranSaver.RetrieveStationId.station_id;


public class GPS_Service extends Service {
    private LocationListener listener;
    protected LocationManager locationManager;
    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    Location oldLocation=null;
    public  static float mCurrentSpeed;
    double latitude=0.0; // latitude

    double longitude=0.0; // longitude
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        listener = new LocationListener() {

            public void onLocationChanged(Location location) {

                Intent i = new Intent("location_update");

                i.putExtra("Longitude",location.getLongitude());
                i.putExtra("Latitude",location.getLatitude());
                if(isBetterLocation(location,oldLocation)){
                    Log.d("IS","BETTER");
                    sendBroadcast(i);
                    oldLocation=location;
                    InsertLocation insertLocation = new InsertLocation(getApplicationContext());
                    insertLocation.execute(Double.toString(location.getLatitude()),
                            Double.toString(location.getLongitude()),id,
                            Double.toString(MapsActivity.request),
                            station_id);
                }

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {
                location = getLocation();
                Log.i("onProviderEnabled",location.toString());
            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        // locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        location = getLocation();
        //noinspection MissingPermission
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, listener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
    }



    public Location getLocation() {
        try {
            locationManager = (LocationManager)getApplicationContext()
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(! isGPSEnabled)
            {
                // showSettingsAlert();
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                SystemClock.sleep(10000);
                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);
            }

            while (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled

            }// else {
            this.canGetLocation = true;

            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {

                if (location == null) {
                    //noinspection MissingPermission
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,5000,0,listener);
                    Log.d("GPS Enabled", "GPS Enabled");
                    if (locationManager != null) {
                        //noinspection MissingPermission
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                        else if (isNetworkEnabled) {
                            //noinspection MissingPermission
                            locationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    10000,
                                    0,listener);
                            Log.d("Network", "but gps is enabled ");
                            if (locationManager != null) {
                                //noinspection MissingPermission
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }

                            }
                        }
                    }
                }
            }
            else if (isNetworkEnabled ) {
                //noinspection MissingPermission
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                       5000,
                        0,listener);
                Log.d("Network", "Network");
                if (locationManager != null) {
                    //noinspection MissingPermission
                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }



    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }







}
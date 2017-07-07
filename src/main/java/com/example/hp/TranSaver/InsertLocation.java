package com.example.hp.TranSaver;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class InsertLocation extends AsyncTask<String, Void, String> {
    private Context mContext;
    String jsonStr;
    static String latitude;
    static String longitude;
    static String iden;
    String request;
    static String destination;
    private final String TAG = InsertLocation.class.getSimpleName();

    //constructor
    public InsertLocation(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(String... params) {
        latitude = params[0];
        longitude = params[1];
        iden = params[2];
        request = params[3];
        destination = params[4];
        String link2 = "http://trackingsystem.000webhostapp.com/insert_location.php?iden=" + iden + "&latitude=" + latitude +
                "&longitude=" + longitude + "&request=" + request + "&destination=" + destination;
        HttpHandler sh = new HttpHandler();

        jsonStr = sh.makeServiceCall(link2);
        //Log.e("insert url",link2);
        Log.e(TAG, "Response from url: " + jsonStr);

        if (jsonStr == null) {
            try {
                String data = URLEncoder.encode("latitude", "UTF-8") + "=" + URLEncoder.encode(latitude, "UTF-8");
                data += "&" + URLEncoder.encode("longitude", "UTF-8") + "=" + URLEncoder.encode(longitude, "UTF-8");
                data += "&" + URLEncoder.encode("iden", "UTF-8") + "=" + URLEncoder.encode(iden, "UTF-8");
                data += "&" + URLEncoder.encode("request", "UTF-8") + "=" + URLEncoder.encode(request, "UTF-8");
                data += "&" + URLEncoder.encode("destination", "UTF-8") + "=" + URLEncoder.encode(destination, "UTF-8");

                URL url2 = new URL(link2);
                URLConnection conn = url2.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(data);
                wr.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sbin = new StringBuilder();
                String line2 = null;

                // Read Server Response
                while ((line2 = reader.readLine()) != null) {
                    sbin.append(line2);
                    break;
                }

                return sbin.toString();
            } catch (Exception e) {
                Log.e("unable to connect", "");
                return new String("Exception: " + e);

            }
        } else {

            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s == null) {
            UpdateLocation updateLocation = new UpdateLocation(mContext);
            updateLocation.execute(latitude, longitude, iden, Integer.toString(MapsActivity.request), destination, Integer.toString(MapsActivity.joined));
        } else
            new TramLocationRetrieve(mContext).execute();
        new getNearestStationID(mContext).execute(iden);
    }
}
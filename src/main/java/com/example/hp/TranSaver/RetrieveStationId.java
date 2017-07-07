package com.example.hp.TranSaver;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.net.URLEncoder;


public class RetrieveStationId extends AsyncTask<String, Void, String> {

    static String station_id;
    static private String station_lat;
    static private String station_long;
    private Context mContext;
    String jsonStr;
    private final String TAG = RetrieveStationId.class.getName();

    public static Double getStation_lat() {
        return Double.parseDouble(station_lat);
    }

    public static Double getStation_lon() {
        return Double.parseDouble(station_long);
    }

    //constructor
    public RetrieveStationId(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            String name = params[0];
            String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");

            String link = "https://trackingsystem.000webhostapp.com/selectstation.php?" + data;
            HttpHandler sh = new HttpHandler();

            jsonStr = sh.makeServiceCall(link);
            Log.e("url", "https://trackingsystem.000webhostapp.com/selectstation.php?" + data);

            Log.e(TAG, "Response from url: " + jsonStr);
            while (jsonStr == null || jsonStr.equals("Connection failed: MySQL server has gone away")) {
                jsonStr = sh.makeServiceCall(link);
                Log.e("url", "https://trackingsystem.000webhostapp.com/selectstation.php?" + data);
            }
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    if (jsonObj.length() > 0) {
                        station_id = jsonObj.getString("id");
                        String station_name = jsonObj.getString("name");
                        station_lat = jsonObj.getString("latitude");
                        station_long = jsonObj.getString("longitude");
                        Log.e("TAG", "station_id:" + station_id);
                        Log.e("TAG", "station_name:" + station_name);
                        Log.e("TAG", "Dest Lat:" + station_lat);
                        Log.e("TAG", "Dest Lon:" + station_long);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            Log.e("connection", "error");
            Log.e(TAG, "Exception,retrieveId: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Intent intent4 = new Intent(mContext, MapsActivity.class);
        mContext.startActivity(intent4);

    }

}


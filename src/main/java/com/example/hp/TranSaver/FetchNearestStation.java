package com.example.hp.TranSaver;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.net.URLEncoder;

import static com.google.android.gms.wearable.DataMap.TAG;


public class FetchNearestStation  extends AsyncTask<String,Void,String> {
    static private String nearest_station_lat  ;
    static private String nearest_station_long;
    static private String nearest_stat_name;

    public static String getNearest_stat_name() {
        return nearest_stat_name;
    }

    public static void setNearest_stat_name(String nearest_stat_name) {
        FetchNearestStation.nearest_stat_name = nearest_stat_name;
    }

    private Context mContext;
    String jsonStr;

    public static double getNearest_station_lat() {
        return Double.parseDouble(nearest_station_lat);
    }

    public static void setNearest_station_lat(String nearest_station_lat) {
        FetchNearestStation.nearest_station_lat = nearest_station_lat;
    }

    public static double getNearest_station_long() {
        return Double.parseDouble(nearest_station_long);
    }

    public static void setNearest_station_long(String nearest_station_long) {
        FetchNearestStation.nearest_station_long = nearest_station_long;
    }

    public FetchNearestStation(Context mContext) {
        this.mContext = mContext;
    }
    @Override
    protected String doInBackground(String... params) {
        try{
            String id = params[0];
            String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");

            String link = "https://trackingsystem.000webhostapp.com/nearest_stat_info.php?id="+id;
            HttpHandler sh = new HttpHandler();

            jsonStr = sh.makeServiceCall(link);

            Log.e(TAG, "Response from url: " + jsonStr);
            while (jsonStr==null || jsonStr.equals("Connection failed: MySQL server has gone away"))
            {
                jsonStr = sh.makeServiceCall(link);
                Log.e("url","https://trackingsystem.000webhostapp.com/selectstation.php?"+data);
            }
            if(jsonStr!=null){
                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);
                    if (jsonObj.length() > 0)
                    {
                        nearest_stat_name=jsonObj.getString("name");
                        nearest_station_lat  = jsonObj.getString("latitude");
                        nearest_station_long = jsonObj.getString("longitude");
                        Log.e("TAG", "near Lat:"+nearest_station_lat);
                        Log.e("TAG", "near Lon:"+nearest_station_long);
                    }
                }catch (Exception e){
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            }
        }catch (Exception e){
            Log.e("connection","error");
            Log.e(TAG,"Exception " + e.getMessage());
        }
        return null;
    }

}

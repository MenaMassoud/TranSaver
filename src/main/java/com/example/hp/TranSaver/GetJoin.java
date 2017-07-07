package com.example.hp.TranSaver;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Hp on 24/04/2017.
 */

public class GetJoin extends AsyncTask<String,Void,String> {
    private Context mContext;
    String id;
    static String json_val;
    static String nearest_station_id;
    private final String TAG = GetJoin.class.getSimpleName();
    //constructor
    public GetJoin(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(String... params) {
        id = params[0];
        String link = "https://trackingsystem.000webhostapp.com/getJoin.php?iden="+id;
        HttpHandler sh = new HttpHandler();

        json_val = sh.makeServiceCall(link);
        while (json_val==null)
        {
            json_val = sh.makeServiceCall(link);
        }
        Log.e(ContentValues.TAG, "Response from url: " +json_val);
        if(json_val!=null){
            try {

                JSONObject jsonObj = new JSONObject(json_val);
                if (jsonObj.length() > 0)
                {
                    String strJoin  = jsonObj.getString("join");
                    MapsActivity.joined=Integer.parseInt(strJoin);
                    nearest_station_id=jsonObj.getString("nearest_station");
                }
            }catch (Exception e){
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        }

        return json_val;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(json_val!=null)
            new FetchNearestStation(mContext).execute(nearest_station_id);
    }
}
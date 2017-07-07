package com.example.hp.TranSaver;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class TramLocationRetrieve extends AsyncTask<String, Void, ArrayList<LocationObj>> {
    static public ArrayList<LocationObj> locationList = new ArrayList<LocationObj>();
    private final Context mContext;
    private final String TAG = TramLocationRetrieve.class.getSimpleName();

    public TramLocationRetrieve(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected void onPostExecute(ArrayList<LocationObj> locationObjs) {
        super.onPostExecute(locationObjs);
    }

    @Override
    protected ArrayList<LocationObj> doInBackground(String... params) {
        String url = "https://trackingsystem.000webhostapp.com/Centralization.php?iden=" + LoginActivity.id;
        HttpHandler sh = new HttpHandler();


        String jsonStr = sh.makeServiceCall(url);
        Log.v(TAG, "Response from url: " + jsonStr);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray jsonArray = jsonObj.getJSONArray("Location");
                String[] resultStrs = new String[jsonArray.length()];
                locationList.clear();
                //looping through all locations
                for (int i = 0; i < jsonArray.length(); i++) {
                    //object from class LocationObj
                    LocationObj loc = new LocationObj();
                    JSONObject jObject = jsonArray.getJSONObject(i);//ygeeb curly bracket rkm i
                    String Slat = jObject.getString("latitude");
                    double lat = Double.parseDouble(Slat);
                    loc.setLatitude(lat);
                    String Slon = jObject.getString("longitude");
                    double lon = Double.parseDouble(Slon);
                    loc.setLongitude(lon);
                    //add loc to arrayList locationList
                    locationList.add(loc);
                    resultStrs[i] = " lat=" + lat + " lon=" + lon + "\n";
                }

                return locationList;

            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }

        }//end if

        else {
            Log.e(TAG, "Couldn't get json from server.");

        }

        return locationList;
    }

}

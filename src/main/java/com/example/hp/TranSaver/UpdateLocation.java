package com.example.hp.TranSaver;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class UpdateLocation extends AsyncTask<String, Void, Void> {
    private Context mContext;
    String iden;

    public UpdateLocation(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            String latitude = params[0];
            String longitude = params[1];
            iden = params[2];
            String request = params[3];
            String destination = params[4];
            String joined = params[5];
            String data = URLEncoder.encode("latitude", "UTF-8") + "=" + URLEncoder.encode(latitude, "UTF-8");
            data += "&" + URLEncoder.encode("longitude", "UTF-8") + "=" + URLEncoder.encode(longitude, "UTF-8");
            data += "&" + URLEncoder.encode("iden", "UTF-8") + "=" + URLEncoder.encode(iden, "UTF-8");
            data += "&" + URLEncoder.encode("request", "UTF-8") + "=" + URLEncoder.encode(request, "UTF-8");
            data += "&" + URLEncoder.encode("destination", "UTF-8") + "=" + URLEncoder.encode(destination, "UTF-8");
            data += "&" + URLEncoder.encode("joined", "UTF-8") + "=" + URLEncoder.encode(joined, "UTF-8");

            String link2 = "https://trackingsystem.000webhostapp.com/update.php?iden=" + iden + "&latitude=" + latitude + "&longitude=" +
                    longitude + "&request=" + request + "&destination=" + destination + "&joined=" + joined;
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
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        new TramLocationRetrieve(mContext).execute();
        new getNearestStationID(mContext).execute(iden);
    }
}

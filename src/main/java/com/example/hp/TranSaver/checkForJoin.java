package com.example.hp.TranSaver;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Hp on 15/03/2017.
 */
public class checkForJoin extends AsyncTask<String,Void,String> {
    private Context mContext;
    String id;
    static String JoinjsonStr;

    //constructor
    public checkForJoin(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(String... params) {
        id = params[0];
        String link = "https://trackingsystem.000webhostapp.com/check_join.php?iden="+id;
        HttpHandler sh = new HttpHandler();

        JoinjsonStr = sh.makeServiceCall(link);
        while (JoinjsonStr==null || JoinjsonStr.equals("Connection failed: MySQL server has gone away"))
        {
            JoinjsonStr = sh.makeServiceCall(link);
        }
        Log.e(ContentValues.TAG, "Response from url: " + JoinjsonStr);

        return JoinjsonStr;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.i("can Join:",s);
    }
}



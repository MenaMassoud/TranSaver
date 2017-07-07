package com.example.hp.TranSaver;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Hp on 13/03/2017.
 */
public class DeleteLocation extends AsyncTask<String,Void,String> {
    private Context mContext;
    String id;
    String jsonStr;

    //constructor
    public DeleteLocation(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(String... params) {
        id = params[0];
        String link = "https://trackingsystem.000webhostapp.com/delete.php?iden="+id;
        HttpHandler sh = new HttpHandler();

        jsonStr = sh.makeServiceCall(link);
        while (jsonStr==null || jsonStr.equals("Connection failed: MySQL server has gone away"))
        {
            jsonStr = sh.makeServiceCall(link);
        }
        Log.e(ContentValues.TAG, "Response from url: " + jsonStr);
        if(jsonStr!=null){
            Log.e("Record","deleted successfully");
        }

        return null;
    }
}


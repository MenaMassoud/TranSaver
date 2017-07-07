package com.example.hp.TranSaver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import static com.example.hp.TranSaver.LoginActivity.id;

public class Home extends AppCompatActivity {
    String[]stationList;
    static String station_destination="";
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);//Menu Resource, Menu
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        stationList = getResources().getStringArray(R.array.stations);
        Log.v("stations", String.valueOf(stationList.length));
        MyArrayAdapter<String> adapter = new MyArrayAdapter<String>(this, R.layout.list_item, R.id.label, stationList);
        final ListView listView = (ListView) findViewById(R.id.listview_station);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                station_destination = (String) adapterView.getItemAtPosition(i);
                Log.v("destination", station_destination);
                Toast.makeText(Home.this, station_destination + " requested", Toast.LENGTH_LONG).show();
               // new TramLocationRetrieve(Home.this).execute();
                new RetrieveStationId(Home.this).execute(station_destination);

            }
        });

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}

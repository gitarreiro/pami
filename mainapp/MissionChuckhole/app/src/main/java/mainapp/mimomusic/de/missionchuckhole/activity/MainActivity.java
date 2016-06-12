package mainapp.mimomusic.de.missionchuckhole.activity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import mainapp.mimomusic.de.missionchuckhole.R;
import mainapp.mimomusic.de.missionchuckhole.data.AccFix;
import mainapp.mimomusic.de.missionchuckhole.data.DataStore;
import mainapp.mimomusic.de.missionchuckhole.listener.RecordButtonListener;
import mainapp.mimomusic.de.missionchuckhole.listener.SettingsButtonListener;
import mainapp.mimomusic.de.missionchuckhole.listener.ShowMapButtonListener;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int updateInterval = 500; //0,5 Sekunden
    private static final int retryInterval = 1000;
    private GoogleMap map;
    private HeatmapTileProvider mProvider;
    private HeatmapTileProvider mProvider1;
    private TileOverlay mOverlay;
    private boolean isRecording;
    private boolean isUpdateMapPossible;
    private Handler updateHandler;
    private Handler tryUpdateHandler = new Handler();
    Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            //TODO update map here
            System.out.println("doing map update");
            if (updateHandler != null) {
                updateHandler.postDelayed(updateRunnable, updateInterval);
            }
        }
    };

    private Runnable tryUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if(isUpdateMapPossible) {
                updateRunnable.run();
            } else {
                tryUpdateHandler.postDelayed(tryUpdateRunnable, retryInterval);
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataStore.getInstance();

        System.out.println("onCreate() called");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        init();
        // test

    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("onPause() called");
        DataStore.getInstance().persistFixes();
        if (updateHandler != null) {
            stopUpdatingMap();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume() called");
        if (updateRunnable != null && isUpdateMapPossible && isRecording) {
            updateRunnable.run();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy() called");
    }

    private void init() {
        Button btnRecord = (Button) findViewById(R.id.button_record);
        btnRecord.setOnClickListener(new RecordButtonListener(this, btnRecord));
        Button btnShowMap = (Button) findViewById(R.id.button_showmap);
        btnShowMap.setOnClickListener(new ShowMapButtonListener(this));
        Button btnSettings = (Button) findViewById(R.id.button_settings);
        btnSettings.setOnClickListener(new SettingsButtonListener(this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, permissions, 0);

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        Location location = null;
        try{
            System.out.println("best provider: " + manager.getBestProvider(criteria, false));
            location = manager.getLastKnownLocation(manager.getBestProvider(criteria, false));
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        if(location == null) {

            //create dummy location: Uni Passau3
            location = new Location("dummy");
            location.setLatitude(48.566827);
            location.setLongitude(13.451358);
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 13));

        SharedPreferences prefs = getSharedPreferences("CHUCK_PREFS", Context.MODE_PRIVATE);

        Gson gson = new Gson();

        String json = prefs.getString("acclist", "");

        List<AccFix> fixes = DataStore.getInstance().getFixes();
        System.out.println("Fixes loaded from DataStore: "+fixes.size());

        //Type type = new TypeToken<List<AccFix>>(){}.getType();
        //final List<String> items= gson.fromJson(json, List.class);
/*
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                for (String item: items) {
                    if(i>10) {
                        break;
                    }
                    AccFix fix = AccFix.fromSaveString(item);
                    //System.out.println(fix);
                }
            }
        });
        thread.start();

*/
/*
        for (int i = 0; i<fixes.size();i++) {
            Object fix = fixes.get(i);
            System.out.println(fix);
        }
*/


        //List<AccFix> fixes = gson.fromJson(json, List.class);
        /*if (fixes == null) {
            fixes = new ArrayList<>();
        }*/


        //List<WeightedLatLng> overlayPoints = new ArrayList<>();
/*
        for (AccFix fix : fixes) {
            System.out.println(fix);
            if(fix.getLocation() == null) {
                continue;
            }
            double latitude = fix.getLocation().getLatitude();
            double longitude = fix.getLocation().getLongitude();
            WeightedLatLng point = new WeightedLatLng(new LatLng(latitude, longitude), fix.getgForce() / 6.0);
            System.out.println("weight: " + fix.getgForce() / 6.0);
        }
*/


        setUpdateMapPossible();


    }

    private void setUpdateMapPossible() {
        this.isUpdateMapPossible = true;
        updateHandler = new Handler();
    }

    public void startUpdatingMap() {
        tryUpdateRunnable.run();
        this.isRecording = true;
    }

    public void stopUpdatingMap() {
        updateHandler.removeCallbacks(updateRunnable);
        this.isRecording = false;
    }

}

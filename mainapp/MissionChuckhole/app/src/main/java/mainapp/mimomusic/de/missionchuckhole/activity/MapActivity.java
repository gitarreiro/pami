package mainapp.mimomusic.de.missionchuckhole.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.androidplot.ui.widget.Widget;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;


import java.util.ArrayList;
import java.util.List;

import mainapp.mimomusic.de.missionchuckhole.R;
import mainapp.mimomusic.de.missionchuckhole.data.AccFix;
import mainapp.mimomusic.de.missionchuckhole.data.DataStore;
import mainapp.mimomusic.de.missionchuckhole.data.MyItem;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private HeatmapTileProvider mProvider1;
    private TileOverlay mOverlay;
    private List<AccFix> records = new ArrayList<>();
    private ClusterManager<MyItem> mClusterManager;
    double lat, lng, intensity;
    private Location L;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        final CheckBox heatMaps = (CheckBox) findViewById(R.id.checkbox1);
        final CheckBox markers = (CheckBox) findViewById(R.id.checkbox2);


        heatMaps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (heatMaps.isChecked())
                    /* markers.setChecked(false);
                    if (verif1)
                    mClusterManager.clearItems(); */

                    dynamic_heatmap();
                else
                    mOverlay.remove();


            }
        });

        markers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (markers.isChecked())

                    /* heatMaps.setChecked(false);
                    if (verif2)
                    mOverlay.remove(); */

                    markers_clustering();


                else
                    mClusterManager.clearItems();
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        //mMap.getUiSettings().setMyLocationButtonEnabled(true);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location myLocation = locationManager.getLastKnownLocation(provider);
            double lat = myLocation.getLatitude();
            double lng = myLocation.getLongitude();
            LatLng latLng = new LatLng(lat, lng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

        }
        else
        {
            // request permission
            String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 0);
        }


    }

    private void markers_clustering() {
        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)

        mClusterManager = new ClusterManager<MyItem>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster manager.
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        // Add cluster items (markers) to the cluster manager.

        records = DataStore.getInstance(this).getFixes();

        for (AccFix record : records)
        {

            L = record.getLocation();

            if (record.isBigChuckhole()) {
                lat = L.getLatitude();
                lng = L.getLongitude();
                MyItem offsetItem = new MyItem(lat, lng);
                mClusterManager.addItem(offsetItem);

            }

        }
       if (mClusterManager.getMarkerCollection().getMarkers().size() == 0)
           Toast.makeText(this, "no chuckholes were recorded", Toast.LENGTH_SHORT).show();
    }

    private void dynamic_heatmap()

    {


        records = DataStore.getInstance(getApplicationContext()).getFixes();
        overlay(records);

    }

    private void overlay(List<AccFix> r)

    {

        List<WeightedLatLng> list = new ArrayList<>();


        for (AccFix record : r) {
            Location L = record.getLocation();

            lat = L.getLatitude();
            lng = L.getLongitude();

            intensity = record.getgForce() / 5.0;
            if (intensity >= 1)
                intensity = 1;

            WeightedLatLng detection = new WeightedLatLng(new LatLng(lat, lng), intensity);

            list.add(detection);
        }

        int[] Colors1 =
                {
                        //Color.rgb(102, 225, 0) // green
                        Color.rgb(0, 255, 0),  //light green
                        //Color.rgb(255, 255, 0), // yellow
                        Color.rgb(255, 0, 0)  // red
                };

        float[] StartPoints =
                {
                        0.2f, 1f
                };

        Gradient gradient1 = new Gradient(Colors1, StartPoints);

        if (list.size() == 0)
            Toast.makeText(this, "No dataset is available to overlay, please do your recording", Toast.LENGTH_LONG).show();
        else
        {
            //if (mProvider1 == null)
            //{
                mProvider1 = new HeatmapTileProvider.Builder()
                        .weightedData(list)
                        .radius(10)
                        .gradient(gradient1)
                        .build();

                //mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider1));


            //mProvider1.setWeightedData(list);
            //mOverlay.clearTileCache();

            //if (mOverlay == null) {
                mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider1));
           // }

        }
    }
/*
    private void static_heatmap ()
    {


        List<LatLng> list1 = new ArrayList<>();
        List<LatLng> list2 = new ArrayList<>();
        List<LatLng> list3 = new ArrayList<>();

        List<AccFix> records = DataStore.getInstance(this).getFixes();

        for (AccFix record : records)
        {
            Location L = record.getLocation();

            double lat = L.getLatitude();
            double lng = L.getLongitude();
            double intensity = record.getgForce() / 6.0;

            LatLng detection = new LatLng(lat, lng);

            if (intensity <=1 && intensity >=0.8)
            list3.add(detection);
            else if (intensity>=0.4 && intensity <0.8)
                list2.add(detection);
            else
                list1.add(detection);
        }

        int[] Colors1 = {

                Color.rgb(0, 255, 0),  //light green

        };

        int[] Colors2 = {
                Color.rgb(255, 255, 0) // yellow
        };

        int[] Colors3 = {
                Color.rgb(255, 0, 0)  // red
        };

        float[] StartPoints = {
                1f

        };

        Gradient gradient1 = new Gradient(Colors1, StartPoints);
        Gradient gradient2 = new Gradient(Colors2, StartPoints);
        Gradient gradient3 = new Gradient(Colors3, StartPoints);


        if (list1.size()==0 || list2.size()==0 || list3.size()==0)
            Toast.makeText(this, "inexistent dataset", Toast.LENGTH_SHORT).show();
        else
        {
            mProvider1 = new HeatmapTileProvider.Builder()
                    .data(list1)
                    .radius(10)
                    .gradient(gradient1)
                    .build();


            mProvider2 = new HeatmapTileProvider.Builder()
                    .data(list2)
                    .radius(10)
                    .gradient(gradient2)
                    .build();

            mProvider3 = new HeatmapTileProvider.Builder()
                    .data(list3)
                    .radius(10)
                    .gradient(gradient3)
                    .build();


            mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider1));
            mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider2));
            mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider3));

        }

    }



   */

}
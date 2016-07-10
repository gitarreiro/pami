package mainapp.mimomusic.de.missionchuckhole.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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


public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener {

    private GoogleMap mMap;
    private HeatmapTileProvider mProvider1;
    private TileOverlay mOverlay;
    private List<AccFix> records = new ArrayList<>();
    private ClusterManager<MyItem> mClusterManager;
    private int i;
    private double lastZoom;
    boolean test;
    private CheckBox heatMaps, markers;



    //GoogleMap.OnCameraChangeListener


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);




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

        LatLng Location;
        double lat, lng;



        mMap = googleMap;
        mMap.setPadding(0,0,0,55);


        try
        {
            mMap.setMyLocationEnabled(true);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);



        mMap.getUiSettings().setCompassEnabled(true);

        //mMap.getUiSettings().setMyLocationButtonEnabled(true);



        heatMaps = (CheckBox) findViewById(R.id.checkbox1);
        markers = (CheckBox) findViewById(R.id.checkbox2);



        heatMaps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (heatMaps.isChecked())
                    dynamic_heatmap();

                else
                if (mOverlay!=null)
                    mOverlay.remove();


            }
        });

        markers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (markers.isChecked())
                {
                    markers_clustering();

                }

                else
                {
                    if (i != 0) {
                        mClusterManager.clearItems();
                        mClusterManager.cluster();
                    }

                }
            }});







            String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 0);


            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // If GPS is not enabled, take user to screen to enable it
            boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!enabled) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_LOW);

            Location myLocation = null;
            try
            {

            String provider = locationManager.getBestProvider(criteria, true);
            if (provider != null)
                myLocation = locationManager.getLastKnownLocation(provider);


            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

            if (myLocation !=null)
            {
            lat = myLocation.getLatitude();
            lng = myLocation.getLongitude();
            Location = new LatLng(lat, lng);
            }
            else
                Location = new LatLng(48.571947, 13.449502);






           mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Location, 13));
           heatMaps.setChecked(true);


           mMap.setOnCameraChangeListener(this);


    }




    private void markers_clustering() {
        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        i=0;
        double lat, lng;
        Location L;


        mClusterManager = new ClusterManager<MyItem>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster manager.
        mMap.setOnCameraChangeListener(mClusterManager);
        //mMap.setOnMarkerClickListener(mClusterManager);


        // Add cluster items (markers) to the cluster manager.
        records = DataStore.getInstance(this).getFixes();

        for (AccFix record : records)
        {

            L = record.getLocation();

            if (record.isBigChuckhole())
            {
                lat = L.getLatitude();
                lng = L.getLongitude();
                MyItem offsetItem = new MyItem(lat, lng);
                mClusterManager.addItem(offsetItem);
                mClusterManager.cluster();
                i++;
            }

        }
        //System.out.println("Number or markers of clustering  "+i);
        if (i==0)
            Toast.makeText(this, "No chuckholes were found", Toast.LENGTH_SHORT).show();
    }

    public void onCameraChange(CameraPosition position) {


        lastZoom = position.zoom;
        if (heatMaps.isChecked())
            dynamic_heatmap();



    }


    private void dynamic_heatmap()

    {
        double lat, lng, intensity;

        System.out.println("last zoom before overlay" + lastZoom);

        records = DataStore.getInstance(this).getFixes(lastZoom);

                List<WeightedLatLng> list = new ArrayList<>();
                WeightedLatLng point;


                for (AccFix record : records)
                {
                    Location L = record.getLocation();

                    lat = L.getLatitude();
                    lng = L.getLongitude();

                    intensity = record.getgForce() / 6.0;
                    //System.out.println(intensity);
                    if (intensity > 1)
                        intensity = 1;

                    point = new WeightedLatLng(new LatLng(lat, lng), intensity);

                    list.add(point);
                }
                if (list.size() > 0)
                {
/*
                    int[] Colors1 =
                            {
                                    Color.rgb(0, 255, 0),  //light green
                                    Color.rgb(255, 0, 0)  // red
                            };

                    float[] StartPoints =
                            {
                                    0.2f, 1f
                            };

                    Gradient gradient1 = new Gradient(Colors1, StartPoints);*/

                    mProvider1 = new HeatmapTileProvider.Builder()
                            .weightedData(list)
                            .radius(10)
                            .build();




                    //mProvider1.setWeightedData(list);
                    //mOverlay.clearTileCache();

                    if (mOverlay == null)
                    mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider1));
                    else
                    {
                        mProvider1.setWeightedData(list);
                        mOverlay.clearTileCache();
                    }

                }

                else
                   Toast.makeText(MapActivity.this, "No dataset is available to overlay, please do your recording", Toast.LENGTH_SHORT).show();



    }





}
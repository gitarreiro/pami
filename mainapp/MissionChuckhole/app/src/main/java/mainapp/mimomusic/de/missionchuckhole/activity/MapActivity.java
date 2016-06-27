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
import android.widget.Toast;

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
    double lastzoom;
    private CheckBox heatMaps, markers;
    // The minimum distance to change Updates in meters
    private static final double MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final double MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute


    //,GoogleMap.OnCameraChangeListener


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


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

                    /*
                    else
                    {
                        System.out.println("no clustering");
                        Toast.makeText(MapActivity.this,"no chuckholes were detected",Toast.LENGTH_SHORT).show();

                    }*/
                }

                else
                {
                    if (i != 0) {
                        mClusterManager.clearItems();
                        mClusterManager.cluster();
                    }

                }
        }});


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
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        mMap.setOnCameraChangeListener(this);



        //mMap.getUiSettings().setMyLocationButtonEnabled(true);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {


            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            String provider = locationManager.getBestProvider(criteria, true);
            Location myLocation = locationManager.getLastKnownLocation(provider);

            lat = myLocation.getLatitude();
            lng = myLocation.getLongitude();
            Location = new LatLng(lat, lng);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Location, 12));



           //Location = new LatLng(48.719649, 13.384636);
           //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Location, 12));

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
        i=0;
        double lat, lng;
        Location L;
        mClusterManager = new ClusterManager<MyItem>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster manager.
        mMap.setOnCameraChangeListener(mClusterManager);
        //mMap.setOnMarkerClickListener(mClusterManager);


        // Add cluster items (markers) to the cluster manager.
        records = DataStore.getInstance(this).getFixes(lastzoom);
        //records = DataStore.getInstance(this).getFixes();
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
        mMap.setOnCameraChangeListener(this);
    }

    public void onCameraChange(CameraPosition position) {

        float maxZoom = 14;
        float minZoom = 10;

        if (heatMaps.isChecked())
            heatMaps.setChecked(false);

        if (position.zoom > maxZoom) {
            mMap.animateCamera(CameraUpdateFactory.zoomTo(maxZoom));

        }
        if (position.zoom < minZoom) {
            mMap.animateCamera(CameraUpdateFactory.zoomTo(minZoom));

        }


        lastzoom = position.zoom;

    }


    private void dynamic_heatmap()

    {
        double lat, lng, intensity;
        List<WeightedLatLng> list = new ArrayList<>();
        System.out.println("lastzoom in dynamic heatmap  " + lastzoom);
        records = DataStore.getInstance(this).getFixes(lastzoom);
        if (records.size()>0)
        {

          for (AccFix record : records) {
            Location L = record.getLocation();

            lat = L.getLatitude();
            lng = L.getLongitude();

            intensity = record.getgForce() / 6.0;
            System.out.println(record.getgForce());
            if (intensity > 1)
                intensity = 1;

            WeightedLatLng detection = new WeightedLatLng(new LatLng(lat, lng), intensity);

            list.add(detection);
          }

          int[] Colors1 =
                {
                        Color.rgb(0, 255, 0),  //light green
                        Color.rgb(255, 0, 0)  // red
                };

          float[] StartPoints =
                {
                        0.2f, 1f
                };

          Gradient gradient1 = new Gradient(Colors1, StartPoints);

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
        }
        else
            Toast.makeText(MapActivity.this, "No dataset is available to overlay, please do your recording", Toast.LENGTH_LONG).show();
    }


/*
    mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener(){
            public void onCameraChange(CameraPosition position) {

                float maxZoom = 14;
                float minZoom = 10;


                if (heatMaps.isChecked())
                    heatMaps.setChecked(false);

                if (position.zoom > maxZoom) {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(maxZoom));

                }
                if (position.zoom < minZoom) {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(minZoom));

                }



                lastzoom = position.zoom;


            }

        });

 -----------------------------------------------------
        ------------------------------------------------



    private Location getLocation()
    {
        boolean isGPSEnabled, isNetworkEnabled;
        double lat, lng;
        Location location;
        try {

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            //locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled && isNetworkEnabled)

            {
                //canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            lat = location.getLatitude();
                            lng = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                lat = location.getLatitude();
                                lng = location.getLongitude();
                            }
                        }
                    }
                }
            } else {
                String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions(this, permissions, 0);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }


*/



}
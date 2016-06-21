package mainapp.mimomusic.de.missionchuckhole.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

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


public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private HeatmapTileProvider mProvider1, mProvider2, mProvider3;
    private TileOverlay mOverlay;
    private List<AccFix> records = new ArrayList<>();
    ClusterManager<MyItem> mClusterManager;
    double lat, lng, intensity;
    Location L;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


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



    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            System.out.println("permission required");
        }


       //markers_clustering();
       dynamic_heatmap();
       //static_heatmap();


    }

     private void markers_clustering ()
    {

        // Position the map.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(48.719372, 13.383121), 14));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager <MyItem>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster manager.
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        // Add cluster items (markers) to the cluster manager.

       records = DataStore.getInstance(this).getFixes();

        for (AccFix record : records) {

            L = record.getLocation();

            intensity = record.getgForce() / 5.0;
            System.out.println(intensity);
            if (intensity>=0.233)
            {
                lat = L.getLatitude();
                lng = L.getLongitude();
                MyItem offsetItem = new MyItem(lat, lng);
                mClusterManager.addItem(offsetItem);

            }


        }

    }

    private void dynamic_heatmap ()

    {
        //float zoomLevel = mMap.getCameraPosition().zoom;

        LatLng Tittling = new LatLng(48.727804, 13.382363);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Tittling,12));

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            final int MaxZoom =13, MinZoom=9;
            double PreviousZoom= -1.0;

            @Override
            public void onCameraChange(CameraPosition position)
            {
                if (position.zoom<= MaxZoom && position.zoom>= MinZoom)
                {
                    System.out.println("clustering is used");
                    //mOverlay.remove();
                    records = DataStore.getInstance(getApplicationContext()).getFixes2();
                    overlay(records);
                }
                else
                {
                    System.out.println("no clustering is used");
                    //mOverlay.remove();
                    records = DataStore.getInstance(getApplicationContext()).getFixes();
                    overlay(records);

                }

            }
        });


    }

    private void overlay (List<AccFix> r)

    {

        List<WeightedLatLng> list = new ArrayList<>();


        for (AccFix record : r) {
            Location L = record.getLocation();

            lat = L.getLatitude();
            lng = L.getLongitude();
            //System.out.println(record.getgForce());
            intensity = record.getgForce() / 5.0;
            if (intensity >=1)
                intensity =1;

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

        if (list.size()==0)
            Toast.makeText(this, "inexistent dataset", Toast.LENGTH_SHORT).show();
        else
        {
            mProvider1 = new HeatmapTileProvider.Builder()
                    .weightedData(list)
                    .radius(10)
                    //.gradient(gradient1)
                    .build();


            mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider1));
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
/*

list.add(new WeightedLatLng(new LatLng(48.727660, 13.383032),0.5));

        list.add(new WeightedLatLng(new LatLng(48.727528, 13.383125),0.5));

        list.add(new WeightedLatLng(new LatLng(48.727793, 13.382612),0.5));

        list.add(new WeightedLatLng(new LatLng(48.727450, 13.383182),0.5));

        list.add(new WeightedLatLng(new LatLng(48.727365, 13.383254),0.5));

        list.add(new WeightedLatLng(new LatLng(48.727268, 13.383351),0.5));

        list.add(new WeightedLatLng(new LatLng(48.727170, 13.383473),0.5));

        list.add(new WeightedLatLng(new LatLng(48.727067, 13.383605),0.5));

        list.add(new WeightedLatLng(new LatLng(48.726934, 13.383785),0.5));

        list.add(new WeightedLatLng(new LatLng(48.726837, 13.383927),0.5));

        list.add(new WeightedLatLng(new LatLng(48.726728, 13.384087),0.5));

        list.add(new WeightedLatLng(new LatLng(48.726588, 13.384279),1));

        list.add(new WeightedLatLng(new LatLng(48.726456, 13.384388),1));

        list.add(new WeightedLatLng(new LatLng(48.726319, 13.384443),0.2));

        list.add(new WeightedLatLng(new LatLng(48.726219, 13.384484),0.2));

        list.add(new WeightedLatLng(new LatLng(48.726101, 13.384533),1));

        list.add(new WeightedLatLng(new LatLng(48.725958, 13.384572),1));

        list.add(new WeightedLatLng(new LatLng(48.725584, 13.384679),0.2));

        ------------------------------------------------------------------


        list1.add(new LatLng(48.727660, 13.383032));

        list1.add(new LatLng(48.727793, 13.382612));

        list1.add(new LatLng(48.727450, 13.383182));

        list1.add(new LatLng(48.727365, 13.383254));

        list2.add(new LatLng(48.727268, 13.383351));

        list2.add(new LatLng(48.727067, 13.383605));

        list2.add(new LatLng(48.726934, 13.383785));

        list2.add(new LatLng(48.726837, 13.383927));

        list2.add(new LatLng(48.726728, 13.384087));

        list3.add(new LatLng(48.726588, 13.384279));

        list3.add(new LatLng(48.726456, 13.384388));

        list3.add(new LatLng(48.726319, 13.384443));

        list3.add(new LatLng(48.726219, 13.384484));

        list2.add(new LatLng(48.726101, 13.384533));

        list1.add(new LatLng(48.725958, 13.384572));

        list1.add(new LatLng(48.725584, 13.384679));






 */
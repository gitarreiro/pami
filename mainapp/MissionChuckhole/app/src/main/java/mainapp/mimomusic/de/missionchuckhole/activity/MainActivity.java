package mainapp.mimomusic.de.missionchuckhole.activity;

import android.graphics.Color;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;

import mainapp.mimomusic.de.missionchuckhole.R;
import mainapp.mimomusic.de.missionchuckhole.listener.RecordButtonListener;
import mainapp.mimomusic.de.missionchuckhole.listener.SettingsButtonListener;
import mainapp.mimomusic.de.missionchuckhole.listener.ShowMapButtonListener;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private HeatmapTileProvider mProvider;
    private HeatmapTileProvider mProvider1;
    private TileOverlay mOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        init();
        // test

    }

    private void init() {
        Button btnRecord = (Button) findViewById(R.id.button_record);
        btnRecord.setOnClickListener(new RecordButtonListener(this));
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
        mMap = googleMap;


        //Add a marker in Passau and move the camera

        LatLng Passau = new LatLng(48.569303, 13.440118);



        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Passau, 13));

        List<LatLng> list = new ArrayList<>();

        list.add(new LatLng(48.569635,13.437468));
        list.add(new LatLng(48.570099,13.439429));
        list.add(new LatLng(48.569276,13.439373));
        list.add(new LatLng(48.568917,13.440532));
        list.add(new LatLng(48.568914,13.442915));
        list.add(new LatLng(48.569639,13.443935));
        list.add(new LatLng(48.569716,13.444685));
        list.add(new LatLng(48.571723,13.446555));
        list.add(new LatLng(48.571749,13.446817));
        list.add(new LatLng(48.571784,13.447093));
        list.add(new LatLng(48.571836,13.447316));
        list.add(new LatLng(48.571845,13.447684));
        list.add(new LatLng(48.571888,13.448183));





        List<LatLng> list1 = new ArrayList<>();


        list1.add(new LatLng(48.570219,13.445019));
        list1.add(new LatLng(48.571259,13.444718));
        list1.add(new LatLng(48.571635,13.445853));
        list1.add(new LatLng(48.571715,13.446384));
        list1.add(new LatLng(48.571880,13.448393));
        list1.add(new LatLng(48.571880,13.448656));
        list1.add(new LatLng(48.571880,13.449194));
        list1.add(new LatLng(48.571862,13.449785));
        list1.add(new LatLng(48.571854,13.449956));









        int[] colors = {
                //Color.rgb(102, 225, 0) // green
                Color.rgb(0, 255, 0)  //light green
                //Color.rgb(0, 153, 0),    // dark green

        };

        float[] startPoints = {
                //0.2f, 1f
                1
        };

        //double x = 0.2;

        int[] colors1 = {

                Color.rgb(255, 0, 0)    // light red
                //Color.rgb(153, 0, 0)     // dark red

        };

        float[] startPoints1 = {
                //0.2f, 1f
                1
        };
        Gradient gradient = new Gradient(colors, startPoints);
        Gradient gradient1 = new Gradient(colors1, startPoints1);

        // Create a heat map tile provider, passing it the latlngs of the police stations.

        mProvider = new HeatmapTileProvider.Builder()
                .data(list)
                .radius(10)
                .gradient(gradient)
                .build();



        mProvider1 = new HeatmapTileProvider.Builder()
                .data(list1)
                .radius(10)
                .gradient(gradient1)
                .build();

        //
     /*
        List<String> temp = Arrays.asList(coordinates.get(i).split(";"));
        LatLng coor = new LatLng(Double.parseDouble(temp.get(1)), Double.parseDouble(temp.get(2)));

        WeightedLatLng data = new WeightedLatLng(coor, Double.parseDouble(temp.get(0)) );
        ArrayList<WeightedLatLng> W = new ArrayList<>();
        W.add(list);

        mProvider = new HeatmapTileProvider.Builder().weightedData(W).build();

        mProvider = new HeatmapTileProvider.Builder().weightedData().build();

        */

        // Add a tile overlay to the map, using the heat map tile provider.
        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider1));

    }

}

package mainapp.mimomusic.de.missionchuckhole.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.androidplot.xy.XYPlot;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mainapp.mimomusic.de.missionchuckhole.R;
import mainapp.mimomusic.de.missionchuckhole.data.AccFix;
import mainapp.mimomusic.de.missionchuckhole.data.DataStore;
import mainapp.mimomusic.de.missionchuckhole.listener.ChuckLocationListener;
import mainapp.mimomusic.de.missionchuckhole.listener.RecordButtonListener;
import mainapp.mimomusic.de.missionchuckhole.listener.ShowMapButtonListener;
import mainapp.mimomusic.de.missionchuckhole.plot.DynamicLinePlot;
import mainapp.mimomusic.de.missionchuckhole.plot.PlotColor;

/**
 * Created by MiMo
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener, OnMapReadyCallback {

    private static final int updateInterval = 1000;
    private static final int retryInterval = 1000;
    // Plot keys for the acceleration plot
    private final static int PLOT_ACCEL_G_FORCE_KEY = 0;
    // Outputs for the acceleration and LPFs
    protected volatile float[] acceleration = new float[3];
    protected volatile double gForce;
    // Handler for the UI plots so everything plots smoothly
    protected Handler handler;
    protected Runnable runnable;
    // Sensor manager to access the accelerometer sensor
    protected SensorManager sensorManager;
    private GoogleMap map;
    private HeatmapTileProvider tileProvider;
    private TileOverlay overlay;
    private boolean isRecording;
    private LocationManager manager;
    private ChuckLocationListener listener;
    private boolean isUpdateMapPossible;
    private TileOverlayOptions options;
    private Handler updateHandler;
    Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            drawHeatmap();
            if (updateHandler != null) {
                updateHandler.postDelayed(updateRunnable, updateInterval);
            }
        }
    };
    private Handler tryUpdateHandler = new Handler();
    private Runnable tryUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if (isUpdateMapPossible) {
                updateRunnable.run();
            } else {
                tryUpdateHandler.postDelayed(tryUpdateRunnable, retryInterval);
            }
        }
    };
    // Color keys for the acceleration plot
    private int plotAccelGForceColor;

    // Graph plot for the UI outputs
    private DynamicLinePlot dynamicPlot;

    private ImageView ivCycling;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);


       /* Location l1 = new Location("gps");
        Location l2 = new Location("gps");

        l1.setLatitude(1);
        l1.setLongitude(1);
        l2.setLatitude(1);
        l2.setLongitude(1);

        AccFix fix1 = new AccFix(1,2,3,4,l1);
        AccFix fix2 = new AccFix(1,2,3,4,l2);

        List<AccFix> list = new ArrayList<>();
        list.add(fix1);

        System.out.println("contains: "+list.contains(fix2));
*/




        //TODO only first app start

        Intent intent = new Intent(this, OnboardingActivity.class); // war getBaseContext()
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);




        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sensorManager = (SensorManager) this
                .getSystemService(Context.SENSOR_SERVICE);

        handler = new Handler();

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern("###.####");

        init();
        initColor();
        initPlots();

        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 10);
                plotData();
            }
        };
    }


    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);

        handler.removeCallbacks(runnable);
        System.out.println("onPause() called");
        if (updateHandler != null) {
            stopUpdatingMap(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setSensorDelay();
        handler.post(runnable);
        System.out.println("onResume() called");
        if (updateRunnable != null && isUpdateMapPossible && isRecording) {
            updateRunnable.run();
        }
    }


    private void init() {
        this.manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.listener = new ChuckLocationListener();
        ImageButton btnShowMap = (ImageButton) findViewById(R.id.button_showmap);
        btnShowMap.setOnClickListener(new ShowMapButtonListener(this));

        ImageButton btnRecord = (ImageButton) findViewById(R.id.button_record);
        btnRecord.setOnClickListener(new RecordButtonListener(this, btnRecord, manager, listener));

        this.ivCycling = (ImageView) findViewById(R.id.iv_cycling);

        //Button btnSettings = (Button) findViewById(R.id.button_settings);
        //btnSettings.setOnClickListener(new SettingsButtonListener(this));
    }

    /**
     * Set the sensor delay based on user preferences. 0 = slow, 1 = medium, 2 =
     * fast.
     */
    private void setSensorDelay() {

        // Register for sensor updates.
        sensorManager.registerListener(this, sensorManager
                        .getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Event Handling for Individual menu item selected Identify single menu
     * item by it's id
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Log the data
            case R.id.action_settings:

                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public synchronized void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Get a local copy of the sensor values
            System.arraycopy(event.values, 0, acceleration, 0,
                    event.values.length);
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            this.gForce = Math.sqrt(x * x + y * y + z * z) / SensorManager.GRAVITY_EARTH;


        }
    }

    /**
     * Create the output graph line chart.
     */
    private void addAccelerationPlot() {
        addGraphPlot("G-Force", PLOT_ACCEL_G_FORCE_KEY, plotAccelGForceColor);
        //addGraphPlot("X-Axis", PLOT_ACCEL_X_AXIS_KEY,
        //        plotAccelXAxisColor);
        //addGraphPlot("Y-Axis", PLOT_ACCEL_Y_AXIS_KEY,
        //        plotAccelYAxisColor);
        //addGraphPlot("Z-Axis", PLOT_ACCEL_Z_AXIS_KEY,
        //        plotAccelZAxisColor);
    }

    /**
     * Add a plot to the graph.
     *
     * @param title The name of the plot.
     * @param key   The unique plot key
     * @param color The color of the plot
     */
    private void addGraphPlot(String title, int key, int color) {
        dynamicPlot.addSeriesPlot(title, key, color);
    }

    /**
     * Create the plot colors.
     */
    private void initColor() {
        PlotColor color = new PlotColor(this);

        plotAccelGForceColor = color.getDarkOrange();
    }

    /**
     * Initialize the plots.
     */
    private void initPlots() {
        // Create the graph plot
        XYPlot plot = (XYPlot) findViewById(R.id.plot_sensor);

        dynamicPlot = new DynamicLinePlot(plot, this);
        dynamicPlot.setMaxRange(7);
        dynamicPlot.setMinRange(0);

    }


    /**
     * Plot the output data in the UI.
     */
    private void plotData() {
        if (isRecording) {
            dynamicPlot.setData(gForce, PLOT_ACCEL_G_FORCE_KEY);
            dynamicPlot.draw();
        }

    }

    /**
     * Remove a plot from the graph.
     *
     * @param key a
     */
    private void removeGraphPlot(int key) {
        dynamicPlot.removeSeriesPlot(key);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, permissions, 0);

        this.manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (listener != null) {
            listener.setMap(map);
        }
        Criteria criteria = new Criteria();

        Location location = null;
        try {
            System.out.println("best provider: " + manager.getBestProvider(criteria, false));
            location = manager.getLastKnownLocation(manager.getBestProvider(criteria, false));
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        if (location == null) {

            //create dummy location: Uni Passau
            location = new Location("dummy");
            location.setLatitude(48.566827);
            location.setLongitude(13.451358);
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 14));


        drawHeatmap();


        setUpdateMapPossible();
    }

    private void drawHeatmap() {
        List<AccFix> fixes = DataStore.getInstance(this).getFixes();

        List<WeightedLatLng> points = new ArrayList<>();

        for (AccFix fix : fixes) {
            //create WeightedLatLng and add it to list
            Location fixLocation = fix.getLocation();
            if (fixLocation != null) {
                double latitude = fixLocation.getLatitude();
                double longitude = fixLocation.getLongitude();
                double intensity = fix.getgForce() / 5.0;
                WeightedLatLng wll = new WeightedLatLng(new LatLng(latitude, longitude), intensity);
                points.add(wll);
            }
        }

        if (points.size() > 0) {
            if(tileProvider == null ) {
                tileProvider = new HeatmapTileProvider.Builder().weightedData(points).build();
            }

            tileProvider.setWeightedData(points);

            if(overlay == null) {
                overlay = map.addTileOverlay(new TileOverlayOptions().fadeIn(false).tileProvider(tileProvider));
            }


            overlay.clearTileCache();

        }
    }
//TODO onBackPressed

    private void setUpdateMapPossible() {
        this.isUpdateMapPossible = true;
        updateHandler = new Handler();
    }

    public void startUpdatingMap(boolean fromButtonClick) {
        tryUpdateRunnable.run();
        this.isRecording = true;
        this.ivCycling.setVisibility(View.VISIBLE);
        initPlots();
        if (fromButtonClick) {
            addAccelerationPlot();
        }
    }

    public void stopUpdatingMap(boolean fromOnPause) {
        updateHandler.removeCallbacks(updateRunnable);
        this.isRecording = false;
        this.ivCycling.setVisibility(View.GONE);
        //removeGraphPlot(PLOT_ACCEL_G_FORCE_KEY);
        if (!fromOnPause) {
            removeGraphData();
        }
    }

    private void removeGraphData() {
        dynamicPlot.removeData(PLOT_ACCEL_G_FORCE_KEY);
        dynamicPlot.draw();
        dynamicPlot.removeSeriesPlot(PLOT_ACCEL_G_FORCE_KEY);
    }

}
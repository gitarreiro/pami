package mainapp.mimomusic.de.missionchuckhole.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
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

import com.androidplot.xy.XYPlot;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
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
import mainapp.mimomusic.de.missionchuckhole.util.Constants;

/**
 * Main Activity that is launched at program start. Displays a map that is updated during recording,
 * shows a plot with the current g force values in case of recording and handles input to the
 * Buttons in the lower bar.
 *
 * Created by MiMo
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener, OnMapReadyCallback, GoogleMap.OnCameraChangeListener {

    /**
     * time between heatmap updates
     */
    private static final int updateInterval = 10000;

    /**
     * time between attempts of starting map update
     */
    private static final int retryInterval = 1000;

    /**
     * Plot keys for the acceleration plot
     */
    private final static int PLOT_ACCEL_G_FORCE_KEY = 0;
    /**
     * the g force that is stored to display it in the acceleration plot
     */
    protected volatile double gForce;

    /**
     * the Handler that handles plotting the g force the the according Plot
     */
    protected Handler plotHandler;

    /**
     * the Runnable that does
     */
    protected Runnable plotRunnable;

    /**
     * Sensor manager to access the accelerometer sensor
     */
    protected SensorManager sensorManager;

    /**
     * the GoogleMap that is displayed on the start screen
     */
    private GoogleMap map;

    /**
     * the overlay used to display the heatmap
     */
    private TileOverlay overlay;

    /**
     * the last measured zoom that is stored to compare with the current zoom
     */
    private double lastZoom;

    /**
     * indicates if app is in recording status
     */
    private boolean isRecording;

    /**
     * the LocationManager that is used to get the current position
     */
    private LocationManager manager;

    /**
     * LocationListener used to zoom map to the current location
     */
    private ChuckLocationListener chuckLocationListener;

    /**
     * the button that shows MapActivity on click
     */
    private ImageButton btnShowMap;

    /**
     * indicates if the map is initialized completely
     */
    private boolean isMapUpdatePossible;

    /**
     * indicates if map is getting updates
     */
    private boolean isMapUpdateRunning;

    /**
     * the Handler that handles map updating
     */
    private Handler updateHandler;

    /**
     * the Runnable that contains the logic to update the map
     */
    Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            drawHeatmap();
            if (updateHandler != null) {

                // start the same Runnable after updateInterval
                updateHandler.postDelayed(updateRunnable, updateInterval);
            }
        }
    };

    /**
     * Handler that posts the Runnable to start map updating
     */
    private Handler tryUpdateHandler = new Handler();

    /**
     * Runnable that tries to start map updating
     */
    private Runnable tryUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if (isMapUpdatePossible) {

                // start updating map if possible
                updateRunnable.run();
            } else {

                // start the same Runnable after retryInterval
                tryUpdateHandler.postDelayed(tryUpdateRunnable, retryInterval);
            }
        }
    };

    /**
     * Color keys for the acceleration plot
     */
    private int plotAccelGForceColor;

    /**
     * Graph plot for the UI output
     */
    private DynamicLinePlot dynamicPlot;

    /**
     * ImageView that displays a picture while recording
     */
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
        if (toolbar != null) {
            toolbar.setTitle(R.string.app_name);
        }
        setSupportActionBar(toolbar);

        // show Onboarding on first app start
        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_FILE, MODE_PRIVATE);
        if (prefs.getBoolean(Constants.FIRST_APP_VISIT, true)) {//TODO remove
            Intent intent = new Intent(this, OnboardingActivity.class); // war getBaseContext()
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

            prefs.edit().putBoolean(Constants.FIRST_APP_VISIT, false).apply();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sensorManager = (SensorManager) this
                .getSystemService(Context.SENSOR_SERVICE);

        plotHandler = new Handler();

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern("###.####");

        init();
        initColor();
        initPlots();

        plotRunnable = new Runnable() {
            @Override
            public void run() {
                plotHandler.postDelayed(this, 10);
                plotData();
            }
        };
    }


    @Override
    public void onPause() {
        super.onPause();

        // remove listeners and callbacks
        sensorManager.unregisterListener(this);
        plotHandler.removeCallbacks(plotRunnable);

        // if map is updated, stop it
        if (updateHandler != null) {
            stopUpdatingMap(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setSensorDelay();
        plotHandler.post(plotRunnable);
        if (updateRunnable != null && isMapUpdatePossible && isRecording) {
            updateRunnable.run();
        }
    }

    /**
     * Inits the main components of MainActivity
     */
    private void init() {
        this.manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.chuckLocationListener = new ChuckLocationListener();

        btnShowMap = (ImageButton) findViewById(R.id.button_showmap);
        btnShowMap.setOnClickListener(new ShowMapButtonListener(this));

        ImageButton btnRecord = (ImageButton) findViewById(R.id.button_record);
        if (btnRecord != null) {
            btnRecord.setOnClickListener(new RecordButtonListener(this, btnRecord, manager, chuckLocationListener));
        }

        this.ivCycling = (ImageView) findViewById(R.id.iv_cycling);
    }

    /**
     * Set the sensor delay based on user preferences.
     */
    private void setSensorDelay() {

        // Register for sensor updates
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
     * Event handling for menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Log the data
            case R.id.action_settings:

                // start SettingsActivity
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }

    @Override
    public synchronized void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            // update g force value
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            this.gForce = Math.sqrt(x * x + y * y + z * z) / SensorManager.GRAVITY_EARTH;
        }
    }

    /**
     * Creates the output graph line chart.
     */
    private void addAccelerationPlot() {
        addGraphPlot("G-Force", PLOT_ACCEL_G_FORCE_KEY, plotAccelGForceColor);
    }

    /**
     * Adds a plot to the graph.
     *
     * @param title The name of the plot.
     * @param key   The unique plot key
     * @param color The color of the plot
     */
    private void addGraphPlot(String title, int key, int color) {
        dynamicPlot.addSeriesPlot(title, key, color);
    }

    /**
     * Creates the plot color.
     */
    private void initColor() {
        PlotColor color = new PlotColor(this);

        plotAccelGForceColor = color.getDarkOrange();
    }

    /**
     * Initializes the plot.
     */
    private void initPlots() {

        // Create the graph plot
        XYPlot plot = (XYPlot) findViewById(R.id.plot_sensor);
        dynamicPlot = new DynamicLinePlot(plot, this);
        dynamicPlot.setMaxRange(7);
        dynamicPlot.setMinRange(0);
    }

    /**
     * Plots the output data in the UI.
     */
    private void plotData() {

        // only plot data if app is in recording state
        if (isRecording) {
            dynamicPlot.setData(gForce, PLOT_ACCEL_G_FORCE_KEY);
            dynamicPlot.draw();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.setOnCameraChangeListener(this);

        // request permissions if not yet done
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, permissions, 0);

        this.manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (chuckLocationListener != null) {
            chuckLocationListener.setMap(map);
        }

        Criteria criteria = new Criteria();
        Location location = null;

        // try to get the current location
        try {
            String provider = manager.getBestProvider(criteria, false);
            if (provider != null) {
                location = manager.getLastKnownLocation(provider);
            }
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

        // animate to current location
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 14));

        // draw the initial heatmap
        drawHeatmap();

        // map init done: notify that updating map is possible from now
        setUpdateMapPossible();
    }

    @Override
    public void onCameraChange(CameraPosition position) {

        // maximum and minimum zoom values
        float maxZoom = 14.0f;
        float minZoom = 10.0f;

        boolean changedZoom = false;

        // limit zoom range
        if (position.zoom > maxZoom) {
            map.animateCamera(CameraUpdateFactory.zoomTo(maxZoom));
            changedZoom = true;
        } else if (position.zoom < minZoom) {
            map.animateCamera(CameraUpdateFactory.zoomTo(minZoom));
            changedZoom = true;
        }

        // if zoom has changed, update overlays
        if (changedZoom || lastZoom != position.zoom) {
            lastZoom = position.zoom;
            drawHeatmap();
        }
        lastZoom = position.zoom;
    }

    /**
     * Draws a heatmap based on recorded data
     */
    private void drawHeatmap() {

        System.out.println("drawing heatmap for zoom level "+lastZoom);

        // get the data to create the overlay from
        List<AccFix> fixes = DataStore.getInstance(this).getFixes(lastZoom);

        System.out.println("got "+fixes.size()+" fixes");

        List<WeightedLatLng> points = new ArrayList<>();

        // add a dummy point somewhere the user won't scroll to
        Location somewhereInNowhere = new Location("dummy"); //TODO replace by a point far away
        somewhereInNowhere.setLatitude(64.409029);
        somewhereInNowhere.setLongitude(-31.560097);

        AccFix somewhereInNowhereFix = new AccFix(7, 0, 0, 7, somewhereInNowhere);
        if (!fixes.contains(somewhereInNowhereFix))
            //fixes.add(somewhereInNowhereFix);

        for (AccFix fix : fixes) {

            //create WeightedLatLng and add it to list
            Location fixLocation = fix.getLocation();
            if (fixLocation != null) {
                double latitude = fixLocation.getLatitude();
                double longitude = fixLocation.getLongitude();

                double factor = 6.0;


                double intensity = fix.getgForce() / factor;

                WeightedLatLng wll = new WeightedLatLng(new LatLng(latitude, longitude), intensity);
                points.add(wll);
            }
        }

        // update the overlay
        if (points.size() > 0) {

            //removeHeatmap();
            addHeatmap(points);
/*
            if (tileProvider == null) {
                tileProvider = new HeatmapTileProvider.Builder().weightedData(points).build();
                tileProvider.setRadius(15);
            }

            tileProvider.setWeightedData(points);


            if (overlay == null) {
                overlay = map.addTileOverlay(new TileOverlayOptions().fadeIn(false).tileProvider(tileProvider));
            }

            overlay.clearTileCache();
            //   tileProvider.setWeightedData(points);
*/
        }
    }


    /**
     * removes an overlaid heatmap
     */
    private void removeHeatmap() {

        if (overlay != null) {
            overlay.remove();
        }
    }

    /**
     * adds an overlaid heatmap
     *
     * @param points the points the overlay is created from
     */
    private void addHeatmap(List<WeightedLatLng> points) {

        HeatmapTileProvider tileProvider = new HeatmapTileProvider.Builder().weightedData(points).build();
        tileProvider.setRadius(15);

        tileProvider.setWeightedData(points);
        overlay = map.addTileOverlay(new TileOverlayOptions().fadeIn(false).tileProvider(tileProvider));
    }


    @Override
    public void onBackPressed() {
        if (isMapUpdateRunning) {
            stopUpdatingMap(false);
        }

        super.onBackPressed();
    }


    /**
     * Sets indicators to enable map updating
     */
    private void setUpdateMapPossible() {
        this.isMapUpdatePossible = true;
        updateHandler = new Handler();
    }

    /**
     * Starts the map update
     *
     * @param fromButtonClick variable that indicates if method call has been triggered from a button click
     */
    public void startUpdatingMap(boolean fromButtonClick) {

        // disable clicks to the button that shows MapActivity
        btnShowMap.setClickable(false);

        isMapUpdateRunning = true;

        tryUpdateRunnable.run();
        this.isRecording = true;
        this.ivCycling.setVisibility(View.VISIBLE);
        initPlots();
        if (fromButtonClick) {
            addAccelerationPlot();
        }
    }

    /**
     * Stops the map update
     *
     * @param fromOnPause variable that indicates if method call has been triggered from Activity.onPause() method
     */
    public void stopUpdatingMap(boolean fromOnPause) {

        btnShowMap.setClickable(true);
        isMapUpdateRunning = false;
        updateHandler.removeCallbacks(updateRunnable);
        this.isRecording = false;
        this.ivCycling.setVisibility(View.GONE);

        if (!fromOnPause) {
            removeGraphData();
        }
    }

    /**
     * Removes any data from the acceleration plot
     */
    private void removeGraphData() {
        dynamicPlot.removeData(PLOT_ACCEL_G_FORCE_KEY);
        dynamicPlot.draw();
        dynamicPlot.removeSeriesPlot(PLOT_ACCEL_G_FORCE_KEY);
    }
}
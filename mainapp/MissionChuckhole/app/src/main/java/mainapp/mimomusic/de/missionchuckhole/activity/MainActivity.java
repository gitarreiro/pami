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
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

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

    private static final int updateInterval = 500; //0,5 Sekunden
    private static final int retryInterval = 1000;
    // Plot keys for the acceleration plot
    private final static int PLOT_ACCEL_X_AXIS_KEY = 0;
    private final static int PLOT_ACCEL_Y_AXIS_KEY = 1;
    private final static int PLOT_ACCEL_Z_AXIS_KEY = 2;
    // Outputs for the acceleration and LPFs
    protected volatile float[] acceleration = new float[3];
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
    private int plotAccelXAxisColor;
    private int plotAccelYAxisColor;
    private int plotAccelZAxisColor;

    // Graph plot for the UI outputs
    private DynamicLinePlot dynamicPlot;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

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
            stopUpdatingMap();
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
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.menu_logger, menu);
        return true;
    }

    /**
     * Event Handling for Individual menu item selected Identify single menu
     * item by it's id
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*switch (item.getItemId()) {
            // Log the data
            //case R.id.action_settings_sensor:
                return true;

            // Start the vector activity
            case R.id.action_help:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
        */
        return true;
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


        }
    }

    /**
     * Create the output graph line chart.
     */
    private void addAccelerationPlot() {
        addGraphPlot("X-Axis", PLOT_ACCEL_X_AXIS_KEY,
                plotAccelXAxisColor);
        addGraphPlot("Y-Axis", PLOT_ACCEL_Y_AXIS_KEY,
                plotAccelYAxisColor);
        addGraphPlot("Z-Axis", PLOT_ACCEL_Z_AXIS_KEY,
                plotAccelZAxisColor);
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

        plotAccelXAxisColor = color.getDarkBlue();
        plotAccelYAxisColor = color.getDarkGreen();
        plotAccelZAxisColor = color.getDarkRed();
    }

    /**
     * Initialize the plots.
     */
    private void initPlots() {
        // Create the graph plot
        XYPlot plot = (XYPlot) findViewById(R.id.plot_sensor);

        plot.setTitle("Acceleration");
        dynamicPlot = new DynamicLinePlot(plot, this);
        dynamicPlot.setMaxRange(20);
        dynamicPlot.setMinRange(-20);

        addAccelerationPlot();
    }


    /**
     * Plot the output data in the UI.
     */
    private void plotData() {
        dynamicPlot.setData(acceleration[0], PLOT_ACCEL_X_AXIS_KEY);
        dynamicPlot.setData(acceleration[1], PLOT_ACCEL_Y_AXIS_KEY);
        dynamicPlot.setData(acceleration[2], PLOT_ACCEL_Z_AXIS_KEY);

        dynamicPlot.draw();
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
                double intensity = fix.getgForce() / 6.0;
                WeightedLatLng wll = new WeightedLatLng(new LatLng(latitude, longitude), intensity);
                points.add(wll);
            }
        }

        if (points.size() > 0) {
            tileProvider = new HeatmapTileProvider.Builder().weightedData(points).build();

            if (overlay != null) {
                overlay.remove();
            }

            overlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
        }
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
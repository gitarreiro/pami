package datacollection.mimomusic.de.datacollection;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    // The minimum distance to change Updates in meters
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000; // 1 sekunde
    double maximumGForce = 0;
    private boolean shouldRecord = false;
    private float gravX;
    private float gravY;
    private float gravZ;

    private float gravXOld;
    private float gravYOld;
    private float gravZOld;

    private List<AccFix> results = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gravX = 0;
        gravY = 0;
        gravZ = 0;

        gravXOld = 0;
        gravYOld = 0;
        gravZOld = 0;


/*
        final EditText xField = (EditText) findViewById(R.id.x);
        final EditText yField = (EditText) findViewById(R.id.y);
        final EditText zField = (EditText) findViewById(R.id.z);

        final TextView tv1 = (TextView) findViewById(R.id.tv1);
        final TextView tv2 = (TextView) findViewById(R.id.tv2);
        final TextView tv3 = (TextView) findViewById(R.id.tv3);
*/

        final EditText etFilename = (EditText) findViewById(R.id.et_filename);

        final TextView tvGForce = (TextView) findViewById(R.id.tv_gforce);


        Button startStopButton = (Button) findViewById(R.id.button_start_stop);
        assert startStopButton != null;
        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shouldRecord = !shouldRecord;
                maximumGForce = 0;
                if (!shouldRecord) {
                    //TODO write to file and empty
                    String filename = etFilename.getText().toString() + ".txt";
                    File root = Environment.getExternalStorageDirectory();
                    File dir = new File(root.getAbsolutePath() + "/datacollection");
                    dir.mkdirs();

                    File file = new File(dir, filename);

                    try {
                        FileOutputStream f = new FileOutputStream(file);
                        PrintWriter pw = new PrintWriter(f);
                        for (AccFix fix : results) {
                            pw.println(fix.toString());
                        }
                        pw.flush();
                        pw.close();
                        f.close();
                        results.clear();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        SensorEventListener accelerationListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                if (!shouldRecord) {
                    return;
                }

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                gravX = 0.9f * gravXOld + 0.1f * x;
                gravY = 0.9f * gravYOld + 0.1f * y;
                gravZ = 0.9f * gravZOld + 0.1f * z;


                gravXOld = gravX;
                gravYOld = gravY;
                gravZOld = gravZ;


                double gravXDiv = gravX / 9.81;
                double gravYDiv = gravY / 9.81;
                double gravZDiv = gravZ / 9.81;


                x = x - gravX;
                y = y - gravY;
                z = z - gravZ;

                //double gForce = Math.sqrt(gravXDiv*gravXDiv + gravYDiv+gravYDiv + gravZDiv*gravZDiv);
                double gForce = Math.sqrt(x * x + y + y + z * z) / SensorManager.GRAVITY_EARTH;

                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                // getting GPS status
                boolean isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                // getting network status
                boolean isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                Location location = null;
                try {
                    if (!isGPSEnabled && !isNetworkEnabled) {
                        // no network provider is enabled
                    } else {
                        // First get location from Network Provider
                        if (isNetworkEnabled) {

                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, MainActivity.this);
                            Log.d("Network", "Network");
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }
                        // if GPS Enabled get lat/long using GPS Services
                        if (isGPSEnabled) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, MainActivity.this);
                            Log.d("GPS Enabled", "GPS Enabled");
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }

                } catch (SecurityException e) {
                    e.printStackTrace();
                }


                AccFix fix = new AccFix(x, y, z, gForce, location);
                results.add(fix);

                if (gForce > maximumGForce) {
                    maximumGForce = gForce;
                }

                tvGForce.setText(String.valueOf(maximumGForce));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        manager.registerListener(accelerationListener, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}
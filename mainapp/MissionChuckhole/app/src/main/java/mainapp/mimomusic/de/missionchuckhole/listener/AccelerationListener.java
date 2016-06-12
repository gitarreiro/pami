package mainapp.mimomusic.de.missionchuckhole.listener;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mainapp.mimomusic.de.missionchuckhole.data.AccFix;
import mainapp.mimomusic.de.missionchuckhole.thread.SaveThread;

/**
 * Created by MiMo
 */
public class AccelerationListener implements SensorEventListener {
    private static final long MIN_TIME_BW_UPDATES = 500; // 0,25 sekunden
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private Context context;
    private List<AccFix> tmpFixes;

    public AccelerationListener(Context context) {
        this.context = context;
        tmpFixes = new ArrayList<>();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        double gForce = Math.sqrt(x * x + y + y + z * z) / SensorManager.GRAVITY_EARTH;

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

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
                ChuckLocationListener locationListener = new ChuckLocationListener();


                // First get location from Network Provider
                if (isNetworkEnabled) {

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                    Log.d("Network", "Network");
                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                    Log.d("GPS Enabled", "GPS Enabled");
                    location = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }

        if(location != null) {
            AccFix fix = new AccFix(x, y, z, gForce, location);
            tmpFixes.add(fix);
        }

        if(tmpFixes.size()>50) {
            List<AccFix> fixesToSave = new ArrayList<>();
            fixesToSave.addAll(tmpFixes);


            /*
            for(AccFix tmpFix : tmpFixes) {
                try{
                    fixesToSave.add((AccFix) tmpFix.clone());
                } catch(CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }

            */
            SaveThread saver = new SaveThread(context);
            saver.setSaveData(fixesToSave);
            saver.start();
            System.out.println("SaverThread started");
            tmpFixes.clear();

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class ChuckLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }

}

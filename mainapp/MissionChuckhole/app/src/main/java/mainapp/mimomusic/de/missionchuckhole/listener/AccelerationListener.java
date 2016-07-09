package mainapp.mimomusic.de.missionchuckhole.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;

import mainapp.mimomusic.de.missionchuckhole.data.AccFix;
import mainapp.mimomusic.de.missionchuckhole.data.DataStore;

/**
 * AccelerationListener listens to sensor events of the acceleration sensor, creates AccFixes from
 * them and hands it over to the DataStore
 *
 * Created by MiMo
 */
public class AccelerationListener implements SensorEventListener {

    /**
     * the application's Context
     */
    private Context context;

    /**
     * the last saved location
     */
    private Location lastLocation;

    /**
     * Constructor for an AccelerationListener
     *
     * @param context the application's Context
     */
    public AccelerationListener(Context context) {
        this.context = context;
    }

    @Override
    public synchronized void onSensorChanged(SensorEvent event) {

        // get the sensor values
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // calculate the G force
        double gForce = Math.sqrt(x * x + y + y + z * z) / SensorManager.GRAVITY_EARTH;

        //get the last known Location
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // getting GPS status
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location location = null;
        try {
            if (isGPSEnabled || isNetworkEnabled) {

                // get location from Network Provider
                if (isNetworkEnabled) {

                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                // get location from GPS
                if (isGPSEnabled) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }

        // no location found: do not store an AccFix
        if (location == null) {
            return;
        }


        // location is equal to last location: do not store an AccFix
        if (lastLocation != null
                && lastLocation.getLatitude() == location.getLatitude()
                && lastLocation.getLongitude() == location.getLongitude()) {
            return;
        }

        AccFix fix = new AccFix(x, y, z, gForce, location);

        DataStore.getInstance(context).storeFix(fix);

        lastLocation = location;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }
}

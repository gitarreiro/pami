package mainapp.mimomusic.de.missionchuckhole.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import mainapp.mimomusic.de.missionchuckhole.listener.AccelerationListener;

/**
 * Service that does recording of acceleration sensor values and GPS fixes
 *
 * Created by MiMo
 */
public class RecordService extends Service {

    private AccelerationListener accelerationListener;
    private SensorManager manager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Service started!");

        init();
        startRecording();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        this.manager.unregisterListener(accelerationListener);
    }

    /**
     * Inits the main variables
     */
    private void init() {
        accelerationListener = new AccelerationListener(this);
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    }

    /**
     * Starts the recording process
     */
    private void startRecording() {
        manager.registerListener(accelerationListener, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

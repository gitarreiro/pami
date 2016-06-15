package mainapp.mimomusic.de.missionchuckhole.listener;

import android.content.Intent;
import android.location.LocationManager;
import android.view.View;
import android.widget.Button;

import mainapp.mimomusic.de.missionchuckhole.activity.MainActivity;
import mainapp.mimomusic.de.missionchuckhole.activity.MapActivity;
import mainapp.mimomusic.de.missionchuckhole.service.RecordService;

/**
 * Created by MiMo
 */
public class RecordButtonListener implements View.OnClickListener {

    private MainActivity activity;
    private Button btn;
    private LocationManager manager;
    private ChuckLocationListener listener;
    private boolean isRecording;

    public RecordButtonListener(MainActivity activity, Button btn, LocationManager manager, ChuckLocationListener listener) {
        this.activity = activity;
        this.btn = btn;
        this.manager = manager;
        this.listener = listener;
        this.isRecording = false;
    }

    @Override
    public void onClick(View v) {
        // start recording: service? just record and store data periodically
        this.isRecording = !this.isRecording;
        if (isRecording) {
            btn.setText("STOP RECORDING");
            if (manager != null && listener != null && listener.getMap() != null) {
                try {
                    manager.removeUpdates(listener);
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
            Intent intent = new Intent(this.activity, RecordService.class);
            activity.startService(intent);
            activity.startUpdatingMap();
        } else {
            btn.setText("START RECORDING");
            if (manager != null && listener != null && listener.getMap() != null) {
                try {
                    manager.removeUpdates(listener);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
            Intent intent = new Intent(this.activity, RecordService.class);
            activity.stopService(intent);
            activity.stopUpdatingMap();
            Intent showMapIntent = new Intent(this.activity, MapActivity.class);
            activity.startActivity(showMapIntent);
        }

    }
}

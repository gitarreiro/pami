package mainapp.mimomusic.de.missionchuckhole.listener;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.view.View;
import android.widget.ImageButton;

import mainapp.mimomusic.de.missionchuckhole.R;
import mainapp.mimomusic.de.missionchuckhole.activity.MainActivity;
import mainapp.mimomusic.de.missionchuckhole.activity.MapActivity;
import mainapp.mimomusic.de.missionchuckhole.service.RecordService;

/**
 * Created by MiMo
 */
public class RecordButtonListener implements View.OnClickListener {

    private MainActivity activity;
    private ImageButton btn;
    private LocationManager manager;
    private ChuckLocationListener listener;
    private boolean isRecording;
    private Drawable btnPressedDrawable;
    private Drawable btnReleasedDrawable;

    public RecordButtonListener(MainActivity activity, ImageButton btn, LocationManager manager, ChuckLocationListener listener) {
        this.activity = activity;
        this.btn = btn;
        this.manager = manager;
        this.listener = listener;
        this.isRecording = false;

        btnReleasedDrawable = activity.getResources().getDrawable( R.drawable.icon_record );
        btnPressedDrawable = activity.getResources().getDrawable(R.drawable.icon_record_pressed);

    }

    @Override
    public void onClick(View v) {
        // start recording: service? just record and store data periodically
        this.isRecording = !this.isRecording;
        if (isRecording) {
            //btn.setText("STOP RECORDING");
            //btn.setBackground(btnPressedDrawable);
            btn.setBackgroundDrawable(btnPressedDrawable);
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
            activity.startUpdatingMap(true);
        } else {
            //btn.setText("START RECORDING");
            //btn.setBackground(btnReleasedDrawable);
            btn.setBackgroundDrawable(btnPressedDrawable);
            if (manager != null && listener != null && listener.getMap() != null) {
                try {
                    manager.removeUpdates(listener);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
            Intent intent = new Intent(this.activity, RecordService.class);
            activity.stopService(intent);
            activity.stopUpdatingMap(false);
            Intent showMapIntent = new Intent(this.activity, MapActivity.class);
            activity.startActivity(showMapIntent);
        }

    }
}

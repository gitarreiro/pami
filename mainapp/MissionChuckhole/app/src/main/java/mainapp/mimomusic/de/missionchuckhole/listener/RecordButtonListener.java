package mainapp.mimomusic.de.missionchuckhole.listener;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import mainapp.mimomusic.de.missionchuckhole.activity.MainActivity;
import mainapp.mimomusic.de.missionchuckhole.service.RecordService;

/**
 * Created by MiMo
 */
public class RecordButtonListener implements View.OnClickListener{

    private MainActivity activity;
    private Button btn;
    private boolean isRecording;

    public RecordButtonListener(MainActivity activity, Button btn) {
        this.activity = activity;
        this.btn = btn;
        this.isRecording = false;
    }

    @Override
    public void onClick(View v) {
        // start recording: service? just record and store data periodically
        this.isRecording = !this.isRecording;
        if(isRecording) {
            btn.setText("STOP RECORDING");
            Intent intent = new Intent(this.activity, RecordService.class);
            activity.startService(intent);
            activity.startUpdatingMap();
        } else {
            btn.setText("START RECORDING");
            Intent intent = new Intent(this.activity, RecordService.class);
            activity.stopService(intent);
            activity.stopUpdatingMap();
        }

    }
}

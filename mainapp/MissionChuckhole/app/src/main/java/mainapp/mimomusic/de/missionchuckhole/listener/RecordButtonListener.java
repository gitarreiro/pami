package mainapp.mimomusic.de.missionchuckhole.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import mainapp.mimomusic.de.missionchuckhole.service.RecordService;

/**
 * Created by MiMo
 */
public class RecordButtonListener implements View.OnClickListener{

    private Context context;
    private Button btn;
    private boolean isRecording;

    public RecordButtonListener(Context context, Button btn) {
        this.context = context;
        this.btn = btn;
        this.isRecording = false;
    }

    @Override
    public void onClick(View v) {
        // start recording: service? just record and store data periodically
        this.isRecording = !this.isRecording;
        if(isRecording) {
            btn.setText("STOP RECORDING");
            Intent intent = new Intent(this.context, RecordService.class);
            context.startService(intent);
        } else {
            btn.setText("START RECORDING");
            Intent intent = new Intent(this.context, RecordService.class);
            context.stopService(intent);
        }

    }
}

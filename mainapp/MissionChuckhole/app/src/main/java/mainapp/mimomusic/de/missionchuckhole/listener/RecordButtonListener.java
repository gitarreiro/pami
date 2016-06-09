package mainapp.mimomusic.de.missionchuckhole.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import mainapp.mimomusic.de.missionchuckhole.service.RecordService;

/**
 * Created by MiMo
 */
public class RecordButtonListener implements View.OnClickListener{

    private Context context;

    public RecordButtonListener(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        // start recording: service? just record and store data periodically
        Intent intent = new Intent(this.context, RecordService.class);
        context.startService(intent);
    }
}

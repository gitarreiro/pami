package mainapp.mimomusic.de.missionchuckhole.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by MiMo on 09.06.2016.
 */
public class RecordService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Service started!");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("Service.onBind()");
        return null;
    }
}

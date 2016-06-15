package mainapp.mimomusic.de.missionchuckhole.thread;

import android.content.Context;

import mainapp.mimomusic.de.missionchuckhole.data.AccFix;
import mainapp.mimomusic.de.missionchuckhole.data.DataStore;

/**
 * Created by MiMo
 */
public class SaveThread extends Thread {

    Context context;
    private AccFix fix;

    public SaveThread(Context context) {
        this.context = context;
    }

    public void setSaveData(AccFix fix) {
        this.fix = fix;
    }

    @Override
    public void run() {
        DataStore.getInstance(context).storeFix(this.fix);
    }
}

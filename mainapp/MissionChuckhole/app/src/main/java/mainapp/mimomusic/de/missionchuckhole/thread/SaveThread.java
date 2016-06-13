package mainapp.mimomusic.de.missionchuckhole.thread;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import mainapp.mimomusic.de.missionchuckhole.data.AccFix;
import mainapp.mimomusic.de.missionchuckhole.data.DataStore;

/**
 * Created by MiMo
 */
public class SaveThread extends Thread {

    Context context;
    private List<AccFix> fixesToSave;

    public SaveThread(Context context) {
        fixesToSave = new ArrayList<>();
        this.context = context;
    }

    public void setSaveData(List<AccFix> newFixes) {
        fixesToSave = newFixes;
    }

    @Override
    public void run() {
        DataStore.getInstance(context).storeFixes(this.fixesToSave);
    }
}

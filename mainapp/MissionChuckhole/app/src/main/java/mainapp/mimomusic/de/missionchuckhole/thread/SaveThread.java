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

        DataStore dataStore = DataStore.getInstance();
        dataStore.storeFixes(this.fixesToSave);

 /*       SharedPreferences prefs = context.getSharedPreferences("CHUCK_PREFS", Context.MODE_PRIVATE);

        Gson gson = new Gson();

        String json = prefs.getString("acclist", "");
        System.out.println("Ausgangs-JSON: "+json);
        List<String> fixes = gson.fromJson(json, List.class);
        if (fixes == null) {
            fixes = new ArrayList<>();
            System.out.println("storedFixes was null out of Shared Preferences");
        }
        System.out.println("stored fixes: " + fixes.size());

        fixes.addAll(fixesToSave);

        System.out.println("new stored fixes: " + fixes.size());

        SharedPreferences.Editor prefsEditor = prefs.edit();
        json = gson.toJson(fixes);
        prefsEditor.putString("acclist", json);
        prefsEditor.apply();

*/
    }
}

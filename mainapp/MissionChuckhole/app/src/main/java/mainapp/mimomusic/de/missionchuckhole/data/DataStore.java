package mainapp.mimomusic.de.missionchuckhole.data;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MiMo
 */
public class DataStore {
    private static DataStore instance;
    private List<AccFix> fixes;

    private DataStore() {
        loadFixes();
    }

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public void storeFixes(List<AccFix> fixes) {
        this.fixes.addAll(fixes);
    }

    public List<AccFix> getFixes() {
        return this.fixes;
    }

    public void persistFixes() {
        //TODO implement
    }

    private void loadFixes() {
        fixes = new ArrayList<>();

        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/missionchuckhole");
        String filename = "fixes.mch";
        File file = new File(dir, filename);
        if(!file.exists()) {
            System.out.println("file does not exist");
            return;
        }else {
            System.out.println("file exists");
        }



    }
}

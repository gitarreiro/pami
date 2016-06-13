package mainapp.mimomusic.de.missionchuckhole.data;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MiMo
 */
public class DataStore {
    private static DataStore instance;
    private List<AccFix> fixes;

    private DataStore(Context context) {
        loadFixes(context);
    }

    public static DataStore getInstance(Context context) {
        if (instance == null) {
            instance = new DataStore(context);
        }
        return instance;
    }

    public void storeFixes(List<AccFix> fixes) {
        this.fixes.addAll(fixes);
    }

    public List<AccFix> getFixes() {
        return this.fixes;
    }

    public void persistFixes(Context context) {
        //File root = Environment.getExternalStorageDirectory();
        File root = context.getFilesDir();
        File dir = new File(root.getAbsolutePath() + "/missionchuckhole");
        dir.mkdir();
        String filename = "fixes.mch";
        File file = new File(dir, filename);
        System.out.println("DATEI: " + file.getAbsolutePath());

        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            for (AccFix fix : this.fixes) {
                pw.println(fix.toSaveString());
            }
            pw.flush();
            pw.close();
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO implement
    }

    private void loadFixes(Context context) {
        fixes = new ArrayList<>();

        File root = context.getFilesDir();
        File dir = new File(root.getAbsolutePath() + "/missionchuckhole");
        String filename = "fixes.mch";
        File file = new File(dir, filename);
        if (!file.exists()) {
            System.out.println("file does not exist");
            return;
        }
        System.out.println("file exists");

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            System.out.println("reading file");
            int i = 0;
            while ((line = br.readLine()) != null) {
                i++;
                AccFix fix = AccFix.fromSaveString(line);
                System.out.println("new fix: "+fix);
            }
            System.out.println("data found: "+i);
            br.close();
        }catch (IOException e) {
            e.printStackTrace();
        }


    }
}

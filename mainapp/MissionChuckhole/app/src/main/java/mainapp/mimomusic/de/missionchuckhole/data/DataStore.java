package mainapp.mimomusic.de.missionchuckhole.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MiMo
 */
public class DataStore {
    private static DataStore instance;
    private List<AccFix> fixes;

    // Database fields
    private SQLiteDatabase database;
    private ChuckSQLiteHelper dbHelper;
    private String[] allColumns = {ChuckSQLiteHelper.COLUMN_ID,
            ChuckSQLiteHelper.COLUMN_FIX};

    private ChuckDataSet dataSet;


    private DataStore(Context context) {
        dbHelper = new ChuckSQLiteHelper(context);
        dropDatabase();
        //dropTable(ChuckSQLiteHelper.TABLE_FIXES);
        dataSet = new ChuckDataSet();
        loadFixes(context);
    }

    public static DataStore getInstance(Context context) {
        if (instance == null) {
            instance = new DataStore(context);
        }
        return instance;
    }

    private void dropDatabase() {

    }

    public void closeDB() {
        dbHelper.close();
    }

    private void dropTable(String tableName) {
        try {
            database = dbHelper.getWritableDatabase();
            database.execSQL("DROP TABLE IF EXISTS " + tableName);
        } finally {
            if (database != null && database.isOpen()) {
                database.close();
            }
        }
    }

    public void storeFix(AccFix fix) {

        if (fixes.contains(fix)) {
            return;
        }

        dataSet.add(fix);

/*
        Location home = new Location("dummy");
        home.setLatitude(48.561960);
        home.setLongitude(13.578143);

        if(fix.getLocation().distanceTo(home) <50) {
            return;
        }
*/
        this.fixes.add(fix);
        try {
            database = dbHelper.getWritableDatabase();
            synchronized (database) {
                ContentValues values = new ContentValues();
                values.put(ChuckSQLiteHelper.COLUMN_FIX, fix.toString());
                database.insert(ChuckSQLiteHelper.TABLE_FIXES, null, values);
            }
        } finally {
            if (database != null && database.isOpen()) {
                database.close();
            }

        }
    }
/*
    public List<AccFix> getFixes(int zoomLevel) {
        Location home = new Location("dummy");
        home.setLatitude(48.561960);
        home.setLongitude(13.578143);


        List<AccFix> fixes = dataSet.getData(zoomLevel);
        fixes.add(new AccFix(1, 2, 3, 7, home));
        return fixes;

    }

*/
    public List<AccFix> getFixes() {
        List<AccFix> tmp = new ArrayList<>();

        /*
        Location home = new Location("dummy");

        home.setLatitude(48.561960);
        home.setLongitude(13.578143);

        List<AccFix> copy = new ArrayList<>();
        */


        for (AccFix fix : fixes) {
            //copy.add(new AccFix(fix));
/*
            for (AccFix snd:fixes) {
                if (!fix.equals(snd)) {
                    distances.add(fix.getLocation().distanceTo(snd.getLocation()));
                }
            }


            if (fix.getLocation().distanceTo(home) < 50) {
                continue;
            } */

            tmp.add(fix);
        }
        //tmp.add(new AccFix(1, 2, 3, 6, home));




        /*
        final List<AccFix> coopy = copy;

        Runnable r = new Runnable() {
            @Override
            public void run() {

                System.out.println("started Thread");

                List<Float> distances = new ArrayList<>();


                int counter = 0;
                int threshold = 500;
                for (AccFix fst : coopy) {
                    if (counter > threshold) {
                        break;
                    }
                    for (AccFix snd : coopy) {
                        if (counter > threshold) {
                            break;
                        }
                        if (!fst.equals(snd)) {
                            distances.add(fst.getLocation().distanceTo(snd.getLocation()));
                            counter++;
                        }
                    }
                }

                Collections.sort(distances);

                System.out.println("Distances: " + distances);

            }
        };


        new Thread(r).start();
        */

        return tmp;
        //return this.fixes;
    }

    public List<AccFix> getFixes(double zoomLevel) {
        /*Location home = new Location("dummy");
        home.setLatitude(48.561960);
        home.setLongitude(13.578143);


        List<AccFix> fixes = dataSet.getData(zoomLevel);
        fixes.add(new AccFix(1, 2, 3, 6, home));*/
        return dataSet.getData(zoomLevel);

    }


    private void loadFixes(Context context) {

        fixes = new ArrayList<>();


        try {
            try {
                database = dbHelper.getWritableDatabase();
                Cursor cursor = database.query(ChuckSQLiteHelper.TABLE_FIXES,
                        allColumns, null, null, null, null, null);

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    AccFix fix = cursorToAccFix(cursor);
                    fixes.add(fix);
                    dataSet.add(fix); //TODO time measurement
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
            } finally {
                if (database != null && database.isOpen()) {
                    database.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("DataStore: loaded " + fixes.size() + " AccFixes.");


    }

    private AccFix cursorToAccFix(Cursor cursor) {
        String saveString = cursor.getString(1);
        return AccFix.fromString(saveString);
    }


}
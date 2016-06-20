package mainapp.mimomusic.de.missionchuckhole.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

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



    private DataStore(Context context) {
        dbHelper = new ChuckSQLiteHelper(context);
        dropDatabase();
        //dropTable(ChuckSQLiteHelper.TABLE_FIXES);
        loadFixes(context);
    }

    private void dropDatabase(){

    }

    public static DataStore getInstance(Context context) {
        if (instance == null) {
            instance = new DataStore(context);
        }
        return instance;
    }


    public void closeDB() {
        dbHelper.close();
    }

    private void dropTable(String tableName) {
        try{
            database = dbHelper.getWritableDatabase();
            database.execSQL("DROP TABLE IF EXISTS " + tableName);
        }finally{
            if (database != null && database.isOpen()) {
                database.close();
            }
        }
    }

    public void storeFix(AccFix fix) {

        // TODO überprüfen, ob fix schon in den gespeicherten ist

        this.fixes.add(fix);
        try{
            database = dbHelper.getWritableDatabase();
            synchronized (database) {
                ContentValues values = new ContentValues();
                values.put(ChuckSQLiteHelper.COLUMN_FIX, fix.toString());
                database.insert(ChuckSQLiteHelper.TABLE_FIXES, null, values);
            }
        }finally{
            if (database != null && database.isOpen()) {
                database.close();
            }
        }
    }


    public List<AccFix> getFixes() {
        return this.fixes;
    }

    private void loadFixes(Context context) {

        fixes = new ArrayList<>();


        try {
            try{
                database = dbHelper.getWritableDatabase();
                Cursor cursor = database.query(ChuckSQLiteHelper.TABLE_FIXES,
                        allColumns, null, null, null, null, null);

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    AccFix fix = cursorToAccFix(cursor);
                    fixes.add(fix);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
            } finally {
                if (database != null && database.isOpen()) {
                    database.close();
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("DataStore: loaded "+fixes.size() + " AccFixes.");


    }

    private AccFix cursorToAccFix(Cursor cursor) {
        String saveString = cursor.getString(1);
        return AccFix.fromString(saveString);
    }


}
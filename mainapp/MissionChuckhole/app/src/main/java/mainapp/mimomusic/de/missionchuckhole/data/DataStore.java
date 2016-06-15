package mainapp.mimomusic.de.missionchuckhole.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

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
        openDB();

        dropTable(ChuckSQLiteHelper.TABLE_FIXES);
        loadFixes(context);
    }

    public static DataStore getInstance(Context context) {
        if (instance == null) {
            instance = new DataStore(context);
        }
        return instance;
    }

    private void openDB() throws SQLException {
        if(database==null) {
            database = dbHelper.getWritableDatabase();
        }
    }

    public void closeDB() {
        dbHelper.close();
    }

    private void dropTable(String tableName) {
        database.execSQL("DROP TABLE IF EXISTS " + tableName);
    }

    public void storeFix(AccFix fix) {
        openDB();

        this.fixes.add(fix);
        ContentValues values = new ContentValues();
        values.put(ChuckSQLiteHelper.COLUMN_FIX, fix.toString());
        database.insert(ChuckSQLiteHelper.TABLE_FIXES, null, values);
    }


    public List<AccFix> getFixes() {
        return this.fixes;
    }

    private void loadFixes(Context context) {
        openDB();

        fixes = new ArrayList<>();


        try {
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
        }catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private AccFix cursorToAccFix(Cursor cursor) {
        String saveString = cursor.getString(1);
        return AccFix.fromString(saveString);
    }
}

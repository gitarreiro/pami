package mainapp.mimomusic.de.missionchuckhole.data;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that handles data storing for the whole app
 * <p/>
 * Created by MiMo
 */
public class DataStore {
    private static DataStore instance;
    private List<AccFix> fixes;

    /**
     * the database used for this application
     */
    private SQLiteDatabase database;

    /**
     * helper class instance to access the database
     */
    private ChuckSQLiteHelper dbHelper;

    /**
     * the columns used for the database
     */
    private String[] allColumns = {ChuckSQLiteHelper.COLUMN_ID,
            ChuckSQLiteHelper.COLUMN_FIX};

    /**
     * the intelligent data set that stores the AccFixes
     */
    private ChuckDataSet dataSet;

    /**
     * Constructor for a DataStore that initializes the (hidden to fullfil the Singleton Pattern)
     *
     * @param context the application's Context
     */
    private DataStore(Context context) {
        dbHelper = new ChuckSQLiteHelper(context);
        //dropTable(ChuckSQLiteHelper.TABLE_FIXES);
        dataSet = new ChuckDataSet();
        ProgressDialog dialog = ProgressDialog.show(context, "Loading data, please wait...","");
        loadFixes(context);
        dialog.dismiss();
    }

    /**
     * Get the only instance available for this app
     *
     * @param context the application's Context
     * @return the DataStore instance
     */
    public static DataStore getInstance(Context context) {
        if (instance == null) {
            instance = new DataStore(context);
        }
        return instance;
    }

    /**
     * Closes the database
     */
    public void closeDB() {
        dbHelper.close();
    }

    /**
     * Drops the table with the specified name
     *
     * @param tableName the name of the table to drop
     */
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

    /**
     * Stores a single AccFix.
     *
     * @param fix the AccFix to store
     */
    public void storeFix(AccFix fix) {

        if (fixes.contains(fix)) {
            return;
        }

        dataSet.add(fix);

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

    /**
     * Gets all available AccFixes
     *
     * @return a List of AccFixes
     */
    public List<AccFix> getFixes() {
        return this.fixes;
    }

    /**
     * Gets the fixes suitable for a certain GoogleMap zoom level
     *
     * @param zoomLevel the zoom level to get the AccFixes for
     * @return a List of suitable AccFixes
     */
    public List<AccFix> getFixes(double zoomLevel) {
        return dataSet.getData(zoomLevel);
    }

    /**
     * Loads the AccFixes from the database
     *
     * @param context the application's Context
     */
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
                    dataSet.add(fix);
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
    }

    /**
     * Gets an AccFix from a database Cursor
     *
     * @param cursor the database cursor
     * @return the stored AccFix
     */
    private AccFix cursorToAccFix(Cursor cursor) {
        String saveString = cursor.getString(1);
        return AccFix.fromString(saveString);
    }
}
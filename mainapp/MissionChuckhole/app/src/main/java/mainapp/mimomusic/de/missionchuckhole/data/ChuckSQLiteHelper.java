package mainapp.mimomusic.de.missionchuckhole.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Class needed to handle storing data in the SQLite Database
 *
 * Created by MiMo
 */
public class ChuckSQLiteHelper extends SQLiteOpenHelper {

    /**
     * the table name where the AccFixes will be stored
     */
    public static final String TABLE_FIXES = "fixes";

    /**
     * the column name for the ID of the entry
     */
    public static final String COLUMN_ID = "_id";

    /**
     * the column name for the entry
     */
    public static final String COLUMN_FIX = "fix";

    /**
     * the database name
     */
    private static final String DATABASE_NAME = "fixes.db";

    /**
     * the database version
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE = "create table "
            + TABLE_FIXES + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_FIX
            + " text not null);";

    /**
     * Constructor for a ChuckSQLiteHelper
     *
     * @param context the application context
     */
    public ChuckSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ChuckSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FIXES);
        onCreate(db);
    }
}

package mainapp.mimomusic.de.missionchuckhole.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

import java.util.ArrayList;
import java.util.Iterator;
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


    public List<AccFix> getFixes2 ()
    {

        int i,j ;
        double Sum, Average;


        List<AccFix> Temp1 = this.getFixes();
        List<AccFix> Temp2 = new ArrayList<>();

        Temp1.subList(752, this.getFixes().size()).clear();
        i=0; Sum=0;

        for (AccFix record : Temp1)
            if (record.getgForce()>=0.9 && record.getgForce()<=1.1)
            {
                Sum = Sum + record.getgForce();
                i++;
            }

        Average = Sum/i;
        i=0;j=0;

            for (AccFix record : Temp1)
            if (record.getgForce()> Average || record.getgForce()<= 0.8 )
            {
                Temp2.add(j, record);
                j++;
            }



        //Iterator<AccFix> it = Temp1.iterator();



        return Temp2;
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


/*
public List<AccFix> getFixes(float zoom)
    {


        List<AccFix> Temp1 = this.getFixes();
        List<AccFix> Temp2 = new ArrayList<>();

        Temp1.subList(752, this.getFixes().size()).clear();

        //Iterator<AccFix> it = Temp1.iterator();

        int i,j,k,h, length;
        double diff,nb;
        boolean test;

        length = Temp1.size();
        i= 0;
        j=0;
        //Temp2.add(Temp1.get(0));

            do
            {
                k=1; test = false;
                do
                {
                    i++;
                    diff = Math.abs(Temp1.get(i).getgForce() - Temp1.get(i-1).getgForce());

                    if (diff > 0.2)
                    {
                        test = true;

                    }
                    else
                    {
                        k++;
                    }
                }while (test = true);

                nb = i-1;

                if (nb == 1)
                    Temp2.add(j,Temp1.get(i);
                    j++;
                 else if (nb))


                for (h=0;h<nb;h++)
                {
                    Temp2.add(j, Temp1.get(i));
                    j++;

                }


            }while (j>length);







        return Temp2;
    }
 */
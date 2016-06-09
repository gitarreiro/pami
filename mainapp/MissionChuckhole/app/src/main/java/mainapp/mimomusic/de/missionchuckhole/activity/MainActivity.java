package mainapp.mimomusic.de.missionchuckhole.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import mainapp.mimomusic.de.missionchuckhole.R;
import mainapp.mimomusic.de.missionchuckhole.listener.RecordButtonListener;
import mainapp.mimomusic.de.missionchuckhole.listener.SettingsButtonListener;
import mainapp.mimomusic.de.missionchuckhole.listener.ShowMapButtonListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        init();
        // test

    }

    private void init() {
        Button btnRecord = (Button) findViewById(R.id.button_record);
        btnRecord.setOnClickListener(new RecordButtonListener(this));
        Button btnShowMap = (Button) findViewById(R.id.button_showmap);
        btnShowMap.setOnClickListener(new ShowMapButtonListener(this));
        Button btnSettings = (Button) findViewById(R.id.button_settings);
        btnSettings.setOnClickListener(new SettingsButtonListener(this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

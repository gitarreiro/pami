package mainapp.mimomusic.de.missionchuckhole.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import mainapp.mimomusic.de.missionchuckhole.activity.MapActivity;

/**
 * Created by MiMo on 04.06.2016.
 */
public class ShowMapButtonListener implements View.OnClickListener {

    private Context context;

    public ShowMapButtonListener(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, MapActivity.class);
        context.startActivity(intent);
    }
}

package mainapp.mimomusic.de.missionchuckhole.util;

import android.graphics.Color;

import com.google.maps.android.heatmaps.Gradient;

/**
 * Created by MiMo
 */
public class Util {

    public static final Gradient getMapGradient(){


        int[] colors = {
                Color.rgb(0, 255, 0),
                Color.rgb(50, 255, 0),
                Color.rgb(100, 255, 0),
                Color.rgb(150, 255, 0),
                Color.rgb(200, 255, 0),
                Color.rgb(255, 255, 0),
                Color.rgb(255, 200, 0),
                Color.rgb(255, 150, 0),
                Color.rgb(255, 100, 0),
                Color.rgb(255, 50, 0),
                Color.rgb(255, 0, 0)
        };

        float[] startPoints = {
                0.0f,
                0.1f,
                0.2f,
                0.3f,
                0.4f,
                0.5f,
                0.6f,
                0.7f,
                0.8f,
                0.9f,
                1f
        };

        return new Gradient(colors, startPoints);
    }

}

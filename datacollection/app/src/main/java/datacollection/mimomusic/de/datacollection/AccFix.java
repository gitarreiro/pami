package datacollection.mimomusic.de.datacollection;

import android.location.Location;

/**
 * Created by MiMo on 27.05.2016.
 */
public class AccFix {

    private double x;
    private double y;
    private double z;
    private double gForce;
    private Location location;

    public AccFix(double x, double y, double z, double gForce, Location location) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.gForce = gForce;
        this.location = location;
    }

    @Override
    public String toString() {
        String result = String.valueOf(x) + ";"
                + String.valueOf(y) + ";"
                + String.valueOf(z) + ";"
                + String.valueOf(gForce);
        if(location != null) {
            result += ";" + location.getLatitude() + ";" + location.getLongitude();
        }
        return result;
    }
}

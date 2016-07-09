package mainapp.mimomusic.de.missionchuckhole.data;

import android.location.Location;
import android.support.annotation.NonNull;

/**
 * Class AccFix that stores a single fix created at recording state
 *
 * Created by MiMo
 */
public class AccFix implements Comparable {

    /**
     * the x value that came from the sensor
     */
    private double x;

    /**
     * the y value that came from the sensor
     */
    private double y;

    /**
     * the z value that came from the sensor
     */
    private double z;

    /**
     * the calculated g force oout of x, y and z
     */
    private double gForce;

    /**
     * the Location of this AccFix
     */
    private Location location;

    /**
     * Constructor for an AccFix that sets the initial variables
     *
     * @param x the x value
     * @param y the y value
     * @param z the z value
     * @param gForce the calculated g force
     * @param location the location of the fix
     */
    public AccFix(double x, double y, double z, double gForce, Location location) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.gForce = gForce;
        this.location = location;
    }

    /**
     * Creates an AccFix object from string
     *
     * @param saveString the string to create the AccFix from
     * @return the AccFix
     */
    public static AccFix fromString(String saveString) {
        String[] splitted = saveString.split(";");
        double x = Double.parseDouble(splitted[0]);
        double y = Double.parseDouble(splitted[1]);
        double z = Double.parseDouble(splitted[2]);
        double gForce = Double.parseDouble(splitted[3]);
        Location location = null;
        if (splitted.length == 7) {
            double latitude = Double.parseDouble(splitted[4]);
            double longitude = Double.parseDouble(splitted[5]);
            String provider = splitted[6];
            location = new Location(provider);
            location.setLatitude(latitude);
            location.setLongitude(longitude);
        }
        return new AccFix(x, y, z, gForce, location);
    }

    @Override
    public String toString() {
        String result = String.valueOf(x) + ";"
                + String.valueOf(y) + ";"
                + String.valueOf(z) + ";"
                + String.valueOf(gForce);
        if (location != null) {
            result += ";" + location.getLatitude() + ";" + location.getLongitude() + ";" + location.getProvider();
        }
        return result;
    }

    /**
     * Getter for the g force
     *
     * @return the g force
     */
    public double getgForce() {
        return gForce;
    }

    /**
     * Getter for the Location
     *
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Indicates if this fix has a big g force value
     *
     * @return true if a ig chuckhole has been detected, else false
     */
    public boolean isBigChuckhole() {
        return this.gForce >= 3.5;
    }

    @Override
    public int compareTo(@NonNull Object another) {

        // compare AccFix concerning Location
        if (!(another instanceof AccFix)) {
            return -1;
        }

        AccFix fix = (AccFix) another;
        if (this.location.getLatitude() < fix.location.getLatitude()) {
            return -1;
        } else if (this.location.getLatitude() == fix.location.getLatitude()) {
            if(this.location.getLongitude() < fix.location.getLongitude()) {
                return -1;
            } else if (this.location.getLongitude() == fix.location.getLongitude()) {
                return 0;
            }
        }

        return 1;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof AccFix && this.compareTo(o) == 0;
    }
}
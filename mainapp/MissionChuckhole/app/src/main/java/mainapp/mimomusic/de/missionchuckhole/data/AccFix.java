package mainapp.mimomusic.de.missionchuckhole.data;

import android.location.Location;

/**
 * Created by MiMo
 */
public class AccFix implements Cloneable {

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

    public static AccFix fromString(String saveString) {
        System.out.println("creating AccFix from: ");
        System.out.println(saveString);
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
        System.out.println("created AccFix: " + location);
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

    public double getgForce() {
        return gForce;
    }

    public Location getLocation() {
        return location;
    }
/*
    public String toSaveString() {
        String result = this.toString();
        if (location != null) {
            result += ";" + location.getProvider();
        } else {
            return null;
        }

        System.out.println("extracted savestring: ");
        System.out.println(result);

        return result;
    }
*/

}

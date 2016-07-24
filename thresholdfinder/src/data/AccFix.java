package data;

/**
 * AccFix class for pre-calculating suitable thresholds
 *
 * Created by MiMo
 */
public class AccFix {

    /**
     * the x-value of the acceleration sensor fix
     */
    private double x;

    /**
     * the y-value of the acceleration sensor fix
     */
    private double y;
    /**
     * the z-value of the acceleration sensor fix
     */
    private double z;

    /**
     * the xg force value of the acceleration sensor fix
     */
    private double gForce;

    /**
     * the AccFixes' Location
     */
    private Location location;

    /**
     * Constructor for an AccFix
     *
     * @param x the x value of the acceleration sensor
     * @param y the y value of the acceleration sensor
     * @param z the z value of the acceleration sensor
     * @param gForce the g force of the AccFix
     * @param location the Location of the AccFix
     */
    public AccFix(double x, double y, double z, double gForce, Location location) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.gForce = gForce;
        this.location = location;
    }

    /**
     * Get the x value of this AccFix
     *
     * @return the x value of this AccFix
     */
    public double getX() {
        return x;
    }

    /**
     * Get the y value of this AccFix
     *
     * @return the y value of this AccFix
     */
    public double getY() {
        return y;
    }

    /**
     * Get the z value of this AccFix
     *
     * @return the z value of this AccFix
     */
    public double getZ() {
        return z;
    }

    /**
     * Get the g force of this AccFix
     *
     * @return the g force value of this AccFix
     */
    public double getGForce() {
        return gForce;
    }

    @Override
    public String toString() {
        String result = String.valueOf(x) + ";"
                + String.valueOf(y) + ";"
                + String.valueOf(z) + ";"
                + String.valueOf(gForce);
        if (location != null) {
            result += ";" + location.getLatitude() + ";" + location.getLongitude();
        }
        return result;
    }
}


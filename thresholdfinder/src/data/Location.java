package data;

/**
 * Location Wrapper
 *
 * Created by MiMo
 */
public class Location {

    /**
     * the latitude of the Location
     */
    private double latitude;

    /**
     * the longitude of the Location
     */
    private double longitude;

    /**
     * Constructor for a Location
     *
     * @param latitude the latitude of the Location
     * @param longitude the longitude of the Location
     */
    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Gets the latitude
     *
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Gets the longitude
     *
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }
}

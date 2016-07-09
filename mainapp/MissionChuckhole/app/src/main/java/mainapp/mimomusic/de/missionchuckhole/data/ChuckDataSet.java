package mainapp.mimomusic.de.missionchuckhole.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for the intelligent data structure that sorts AccFixes in separate buckets
 * <p/>
 * Created by MiMo
 */
public class ChuckDataSet {

    /**
     * List that stores all AccFixes with a minimum distance of 15m to each other in the list
     */
    private List<AccFix> data15m;

    /**
     * List that stores all AccFixes with a minimum distance of 35m to each other in the list
     */
    private List<AccFix> data35m;

    /**
     * List that stores all AccFixes with a minimum distance of 60m to each other in the list
     */
    private List<AccFix> data60m;

    /**
     * List that stores all AccFixes with a minimum distance of 250 to each other in the list
     */
    private List<AccFix> data250m;

    /**
     * List that stores all AccFixes with a minimum distance of 500m to each other in the list
     */
    private List<AccFix> data500m;

    /**
     * Constructor for a ChuckDataSet set initializes the basic variables
     */
    public ChuckDataSet() {
        this.data15m = new ArrayList<>();
        this.data35m = new ArrayList<>();
        this.data60m = new ArrayList<>();
        this.data500m = new ArrayList<>();
        this.data250m = new ArrayList<>();
    }

    /**
     * Adds the fix to all suitable buckets.
     *
     * @param fix the AccFix to add
     * @return true if the AccFix was added a least once, else false
     */
    public boolean add(AccFix fix) {

        boolean addedOnce = add500m(fix);
        addedOnce = add250m(fix) || addedOnce;
        addedOnce = add60m(fix) || addedOnce;
        addedOnce = add35m(fix) || addedOnce;
        addedOnce = add15m(fix) || addedOnce;

        return addedOnce;
    }

    /**
     * Adds fix to List where minimum distance is 15m
     *
     * @param fix the AccFix to add
     * @return true if AccFix has been added, else false
     */
    private boolean add15m(AccFix fix) {
        for (AccFix fixInList : data15m) {
            if (fix.getLocation().distanceTo(fixInList.getLocation()) < 15) {
                return false;
            }
        }

        data15m.add(fix);
        return true;
    }

    /**
     * Adds fix to List where minimum distance is 35m
     *
     * @param fix the AccFix to add
     * @return true if AccFix has been added, else false
     */
    private boolean add35m(AccFix fix) {
        for (AccFix fixInList : data35m) {
            if (fix.getLocation().distanceTo(fixInList.getLocation()) < 35) {
                return false;
            }
        }

        data35m.add(fix);
        return true;
    }

    /**
     * Adds fix to List where minimum distance is 60m
     *
     * @param fix the AccFix to add
     * @return true if AccFix has been added, else false
     */
    private boolean add60m(AccFix fix) {
        for (AccFix fixInList : data60m) {
            if (fix.getLocation().distanceTo(fixInList.getLocation()) < 60) {
                return false;
            }
        }

        data60m.add(fix);
        return true;
    }

    /**
     * Adds fix to List where minimum distance is 250m
     *
     * @param fix the AccFix to add
     * @return true if AccFix has been added, else false
     */
    private boolean add250m(AccFix fix) {
        for (AccFix fixInList : data250m) {
            if (fix.getLocation().distanceTo(fixInList.getLocation()) < 250) {
                return false;
            }
        }

        data250m.add(fix);
        return true;
    }

    /**
     * Adds fix to List where minimum distance is 500m
     *
     * @param fix the AccFix to add
     * @return true if AccFix has been added, else false
     */
    private boolean add500m(AccFix fix) {
        for (AccFix fixInList : data500m) {
            if (fix.getLocation().distanceTo(fixInList.getLocation()) < 500) {
                return false;
            }
        }

        data500m.add(fix);
        return true;
    }

    /**
     * Retrieves the suitable List of AccFixes
     *
     * @param zoomLevel the zoom level for which the data has to be retrieved
     * @return the suitable List of AccFixes
     */
    public List<AccFix> getData(double zoomLevel) {

        if (zoomLevel >= 14) {
            return data15m;
        }

        if (zoomLevel >= 13) {
            return data35m;
        }

        if (zoomLevel >= 12) {
            return data60m;
        }

        if (zoomLevel >= 11) {
            return data250m;
        }

        if (zoomLevel >= 10) {
            return data500m;
        }

        return new ArrayList<>();
    }
}

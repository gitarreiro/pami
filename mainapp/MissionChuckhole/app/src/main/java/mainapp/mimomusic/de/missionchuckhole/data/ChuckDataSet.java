package mainapp.mimomusic.de.missionchuckhole.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MiMo
 */
public class ChuckDataSet {


    private List<AccFix> data20m;
    private List<AccFix> data35m;
    private List<AccFix> data50m;
    private List<AccFix> data100m;
    private List<AccFix> data250m;

    public ChuckDataSet() {
        this.data20m = new ArrayList<>();
        this.data35m = new ArrayList<>();
        this.data50m = new ArrayList<>();
        this.data100m = new ArrayList<>();
        this.data250m = new ArrayList<>();
    }

    public boolean add(AccFix fix) {
/*
        if(add250m(fix, true)){
            add100m(fix, false);
            add50m(fix, false);
            add40m(fix, false);
            add20m(fix, false);
            return true;
        }
        else if (add100m(fix, true)) {
            add50m(fix, false);
            add40m(fix, false);
            add20m(fix, false);
            return true;
        } else if (add50m(fix, true)) {
            add40m(fix, false);
            add20m(fix, false);
            return true;
        } else if (add40m(fix, true)) {
            add20m(fix, false);
            return true;
        } else if (add20m(fix, true)) {
            return true;
        } else {
            return false;
        }*/

        boolean addedOnce = false;
        addedOnce = add250m(fix, true)||addedOnce;
        addedOnce = add100m(fix, true)||addedOnce;
        addedOnce = add50m(fix, true)||addedOnce;
        addedOnce = add40m(fix, true)||addedOnce;
        addedOnce = add20m(fix, true)||addedOnce;


        return addedOnce;




    }

    private boolean add20m(AccFix fix, boolean hasToCheck) {
        if (hasToCheck) {
            for (AccFix fixInList : data20m) {
                if (fix.getLocation().distanceTo(fixInList.getLocation()) < 15) {
                    return false;
                }
            }
        }

        data20m.add(fix);
        return true;
    }

    private boolean add40m(AccFix fix, boolean hasToCheck) {
        if (hasToCheck) {
            for (AccFix fixInList : data35m) {
                if (fix.getLocation().distanceTo(fixInList.getLocation()) < 35) {
                    return false;
                }
            }
        }

        data35m.add(fix);
        return true;
    }

    private boolean add50m(AccFix fix, boolean hasToCheck) {
        if (hasToCheck) {
            for (AccFix fixInList : data50m) {
                if (fix.getLocation().distanceTo(fixInList.getLocation()) < 60) {
                    return false;
                }
            }
        }

        data50m.add(fix);
        return true;
    }

    private boolean add100m(AccFix fix, boolean hasToCheck) {
        if (hasToCheck) {
            for (AccFix fixInList : data100m) {
                if (fix.getLocation().distanceTo(fixInList.getLocation()) < 500) {
                    return false;
                }
            }
        }

        data100m.add(fix);
        return true;
    }



    private boolean add250m(AccFix fix, boolean hasToCheck) {
        if (hasToCheck) {
            for (AccFix fixInList : data250m) {
                if (fix.getLocation().distanceTo(fixInList.getLocation()) < 250) {
                    return false;
                }
            }
        }

        data250m.add(fix);
        return true;
    }




    public List<AccFix> getData(double zoomLevel) {

        if(zoomLevel>=14) {
            return data20m;
        }

        if(zoomLevel >= 13) {
            return data35m;
        }

        if(zoomLevel >= 12) {
            return data50m;
        }

        if(zoomLevel >= 11) {
            return data250m;
        }

        if(zoomLevel >= 10) {
            return data100m;
        }

        return new ArrayList<>();

    }


}

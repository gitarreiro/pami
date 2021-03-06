package main;

import data.AccFix;
import data.Location;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that handles reading AccFixes from a file
 *
 * Created by MiMo
 */
public class AccFixFileReader {

    /**
     * the resulting list of AccFixes
     */
    private List<AccFix> fixes;

    /**
     * Constructor for an AccFixFileReader
     */
    public AccFixFileReader() {
        fixes = new ArrayList<>();
    }

    /**
     * Method that handles all the reading
     *
     * @param filename the filename to read the AccFixes from
     * @return the retrieved list of AccFixes
     */
    public List<AccFix> readFromFile(String filename) {
        fixes.clear();

        try {
            FileReader fileReader = new FileReader(filename);
            BufferedReader reader = new BufferedReader(fileReader);
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(";");
                Location location = null;
                if (split.length > 4) {
                    double latitude = Double.parseDouble(split[4]);
                    double longitude = Double.parseDouble(split[5]);
                    location = new Location(latitude, longitude);
                }
                double x = Double.parseDouble(split[0]);
                double y = Double.parseDouble(split[1]);
                double z = Double.parseDouble(split[2]);
                double gForce = Double.parseDouble(split[3]);
                AccFix fix = new AccFix(x, y, z, gForce, location);
                fixes.add(fix);
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return fixes;
    }
}

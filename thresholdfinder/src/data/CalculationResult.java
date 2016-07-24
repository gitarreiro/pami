package data;

import java.util.List;

/**
 * Class that represents calculation results for a whole file
 *
 * Created by MiMo
 */
public class CalculationResult {

    /**
     * variable to calculate the min value in the file
     */
    private double minValue = Double.MAX_VALUE;

    /**
     * variable to calculate the max value in the file
     */
    private double maxValue = Double.MIN_VALUE;

    /**
     * variable to calculate the average value in the file
     */
    private double average;

    /**
     * list of AccFixes read from the file
     */
    private List<AccFix> fixes;

    /**
     * Constructor for a CalculationResult
     *
     * @param fixes the AccFixes to calculate the results from
     */
    public CalculationResult(List<AccFix> fixes) {
        this.fixes = fixes;
        calculate();
    }

    /**
     * Calculation method that does the whole calculation of max, min, average
     */
    private void calculate() {

        double accSum = 0;

        for (AccFix fix : fixes) {
            double x = fix.getX();
            double y  = fix.getY();
            double z = fix.getZ();

            double curGForce = Math.sqrt(x*x + y*y + z*z) / 9.80665f;
            if(curGForce>0)
                accSum += curGForce;
            if (curGForce > maxValue) {
                maxValue = curGForce;
            }

            if (curGForce < minValue) {
                minValue = curGForce;
            }
        }

        average = accSum / ((double) fixes.size());
    }

    @Override
    public String toString() {
        String result    = "MIN:\t" + minValue + "\n";
        result          += "MAX:\t" + maxValue + "\n";
        result          += "AVG:\t" + average;
        return result;
    }
}

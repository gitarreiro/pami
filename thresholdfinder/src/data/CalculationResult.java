package data;

import java.util.List;

/**
 * Created by MiMo on 05.06.2016.
 */
public class CalculationResult {
    private double minValue = Double.MAX_VALUE;
    private double maxValue = Double.MIN_VALUE;
    private double average;
    private List<AccFix> fixes;

    public CalculationResult(List<AccFix> fixes) {
        this.fixes = fixes;
        calculate();
    }

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
        System.out.println("AccSum is "+accSum);
        average = accSum / ((double) fixes.size());


    }

    @Override
    public String toString() {
        String result = "MIN:\t" + minValue + "\n";
        result += "MAX:\t" + maxValue + "\n";
        result += "AVG:\t" + average;
        return result;
    }
}

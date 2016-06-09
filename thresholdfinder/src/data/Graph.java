package data;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import java.util.List;

/**
 * Created by MiMo
 */
public class Graph extends JPanel {
    private double[] data;
    final int PAD = 20;


    public Graph(List<AccFix> fixes, CalculationResult result) {
        double[] data = new double[fixes.size()];
        for(int i = 0; i < fixes.size(); i++) {
            data[i] = fixes.get(i).getGForce();
        }
        this.data = data;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        // Draw ordinate.
        g2.draw(new Line2D.Double(PAD, PAD, PAD, h - PAD));
        // Draw abcissa.
        g2.draw(new Line2D.Double(PAD, h - PAD, w - PAD, h - PAD));
        double xInc = (double) (w - 2 * PAD) / (data.length - 1);
        double scale = (double) (h - 2 * PAD) / getMax();
        // Mark data points.
        g2.setPaint(Color.red);
        for (int i = 0; i < data.length; i++) {
            double x = PAD + i * xInc;
            double y = h - PAD - scale * data[i];
            g2.fill(new Ellipse2D.Double(x - 2, y - 2, 2, 2));
        }
    }

    private double getMax() {
        double max = -Integer.MAX_VALUE;
        for (int i = 0; i < data.length; i++) {
            if (data[i] > max)
                max = data[i];
        }
        return max;
    }


}
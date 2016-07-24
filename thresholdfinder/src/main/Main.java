package main;

import data.AccFix;
import data.CalculationResult;
import data.Graph;

import javax.swing.*;
import java.util.List;

/**
 * Main class for calculating
 *
 * Created by MiMo
 */
public class Main {

    public static void main(String[] args) {

        /**
         * the full basic path to the folder of the files that will be examinated; the defult location has to be adapted to your file
         * location!
         */
        String path = "D:\\pami\\pami\\data\\prerec\\";

        /**
         * Array that stores the filenames of the files that will be examinated
         */
        String[] filenames = {path + "smooth.txt", path + "smooth2.txt", path + "cobble.txt"};

        AccFixFileReader reader = new AccFixFileReader();

        for (String filename : filenames) {
            List<AccFix> contents = reader.readFromFile(filename);

            CalculationResult cres = new CalculationResult(contents);

            System.out.println("File: " + filename);
            System.out.println(cres.toString());
            System.out.println("____________________________________________\n");


            String[] filenameSplit = filename.split("\\\\");
            String fileIdentifier = filenameSplit[filenameSplit.length-1].replace(".txt", "");

            Graph graph = new Graph(contents, cres);
            JFrame f = new JFrame();
            f.setTitle(fileIdentifier);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.add(graph);
            f.setSize(400,400);
            f.setLocation(200,200);
            f.setVisible(true);
        }
    }
}

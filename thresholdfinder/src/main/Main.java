package main;

import data.AccFix;
import data.CalculationResult;
import data.Graph;

import javax.swing.*;
import java.util.List;

/**
 * Created by MiMo on 05.06.2016.
 */
public class Main {

    public static void main(String[] args) {
        //File einlesen D:\pami\pami\data\prerec

        String path = "D:\\pami\\pami\\data\\prerec\\";

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

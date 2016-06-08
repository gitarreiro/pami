package main;

import data.AccFix;
import data.CalculationResult;

import java.util.List;

/**
 * Created by MiMo on 05.06.2016.
 */
public class Main {

    public static void main(String[] args) {
        //File einlesen D:\pami\pami\data\prerec
        String filename = "D:\\pami\\pami\\data\\prerec\\smooth2.txt";
        AccFixFileReader reader = new AccFixFileReader();
        List<AccFix> contents = reader.readFromFile(filename);

        CalculationResult cres = new CalculationResult(contents);
        System.out.println(cres.toString());

    }
}

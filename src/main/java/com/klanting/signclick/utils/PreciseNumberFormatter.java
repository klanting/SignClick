package com.klanting.signclick.utils;


import org.apache.commons.lang3.tuple.Pair;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PreciseNumberFormatter {
    public static String format(double value){
        DecimalFormat df = new DecimalFormat("#.##");

        List<Pair<Double, String>> nameMapping = new ArrayList<>();

        nameMapping.add(Pair.of(1000000000.0, "billion"));
        nameMapping.add(Pair.of(1000000.0, "million"));
        nameMapping.add(Pair.of(1000.0, "thousand"));

        for (Pair<Double, String> p: nameMapping){
            if (value >= p.getLeft()){
                return df.format(value/p.getLeft()) +" " + p.getRight();
            }
        }
        return String.valueOf(value);
    }
}

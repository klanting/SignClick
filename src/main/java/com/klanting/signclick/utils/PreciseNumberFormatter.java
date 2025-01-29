package com.klanting.signclick.utils;

import com.github.javaparser.utils.Pair;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PreciseNumberFormatter {
    public static String format(double value){
        DecimalFormat df = new DecimalFormat("#.##");

        List<Pair<Double, String>> nameMapping = new ArrayList<>();
        nameMapping.add(new Pair<>(1000000000.0, "billion"));
        nameMapping.add(new Pair<>(1000000.0, "million"));
        nameMapping.add(new Pair<>(1000.0, "thousand"));

        for (Pair<Double, String> p: nameMapping){
            if (value >= p.a){
                return df.format(value/p.a) +" " + p.b;
            }
        }
        return String.valueOf(value);
    }
}

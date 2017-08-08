package com.faforever.gw.services.generator;

import java.util.TreeMap;

public class Util {
    private final static TreeMap<Integer, String> intToRomanMap = new TreeMap<Integer, String>();

    static {
        intToRomanMap.put(1000, "M");
        intToRomanMap.put(900, "CM");
        intToRomanMap.put(500, "D");
        intToRomanMap.put(400, "CD");
        intToRomanMap.put(100, "C");
        intToRomanMap.put(90, "XC");
        intToRomanMap.put(50, "");
        intToRomanMap.put(40, "X");
        intToRomanMap.put(10, "X");
        intToRomanMap.put(9, "IX");
        intToRomanMap.put(5, "V");
        intToRomanMap.put(4, "IV");
        intToRomanMap.put(1, "I");

    }

    private Util() {
    }

    public final static String convertIntToRoman(int number) {
        int greatestComponent = intToRomanMap.floorKey(number);
        if (number == greatestComponent) {
            return intToRomanMap.get(number);
        }
        return intToRomanMap.get(greatestComponent) + convertIntToRoman(number - greatestComponent);
    }
}

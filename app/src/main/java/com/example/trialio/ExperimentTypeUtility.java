package com.example.trialio;

public class ExperimentTypeUtility {

    public static boolean isCount(String s) {
        return s.equals(getCountType());
    }

    public static boolean isBinomial(String s) {
        return s.equals(getBinomialType());
    }

    public static boolean isNonNegative(String s) {
        return s.equals(getNonNegativeType());
    }

    public static boolean isMeasurement(String s) {
        return s.equals(getMeasurementType());
    }

    public static String getCountType() {
        return "COUNT";
    }

    public static String getBinomialType() {
        return "BINOMIAL";
    }

    public static String getNonNegativeType() {
        return "NONNEGATIVE";
    }

    public static String getMeasurementType() {
        return "MEASUREMENT";
    }
}

package com.example.trialio.utils;

public class ExperimentTypeUtility {

    /**
     * This checks whether a string matches the count experiment type.
     * @param s The candidate string to check.
     * @return true if the string matches the count experiment type, false otherwise
     */
    public static boolean isCount(String s) {
        return s.equals(getCountType());
    }

    /**
     * This checks whether a string matches the binomial experiment type.
     * @param s The candidate string to check.
     * @return true if the string matches the binomial experiment type, false otherwise
     */
    public static boolean isBinomial(String s) {
        return s.equals(getBinomialType());
    }

    /**
     * This checks whether a string matches the nonnegative experiment type.
     * @param s The candidate string to check.
     * @return true if the string matches the nonnegative experiment type, false otherwise
     */
    public static boolean isNonNegative(String s) {
        return s.equals(getNonNegativeType());
    }

    /**
     * This checks whether a string matches the measurement experiment type.
     * @param s The candidate string to check.
     * @return true if the string matches the measurement experiment type, false otherwise
     */
    public static boolean isMeasurement(String s) {
        return s.equals(getMeasurementType());
    }

    /**
     * This gets a string representing the count experiment type
     * @return Returns the string representation of the count experiment type
     */
    public static String getCountType() {
        return "COUNT";
    }

    /**
     * This gets a string representing the binomial experiment type
     * @return Returns the string representation of the binomial experiment type
     */
    public static String getBinomialType() {
        return "BINOMIAL";
    }

    /**
     * This gets a string representing the nonnegative experiment type
     * @return Returns the string representation of the nonnegative experiment type
     */
    public static String getNonNegativeType() {
        return "NONNEGATIVE";
    }

    /**
     * This gets a string representing the measurement experiment type
     * @return Returns the string representation of the measurement experiment type
     */
    public static String getMeasurementType() {
        return "MEASUREMENT";
    }
}

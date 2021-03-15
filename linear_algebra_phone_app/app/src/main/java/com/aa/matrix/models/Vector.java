package com.aa.matrix.models;

import java.util.Arrays;

/**
 * @author Adam Akiva
 * Abstract class used for functions for a double[]
 */
public abstract class Vector extends BaseModel {

    /**
     * Method to convert a String[] to a matching double[]
     * @param vectorValuesString String[] holding the values to be turned to double[]
     * @return double[] with the String[]
     * @throws NumberFormatException If one or more of the String[] is not a number
     */
    public static double[] convertStringArrayTo1DArray(String[] vectorValuesString)
            throws NumberFormatException {
        int size = vectorValuesString.length;
        final double[] vector = new double[size];
        for (int i = 0; i < size; i++) {
            vector[i] = Double.parseDouble(vectorValuesString[i]);
        }
        return vector;
    }

    /**
     * Method to make a hard-copy of a double[]
     * @param vector double[] to make a hard-copy of
     * @return double[] which is a hard-copy the given double[]
     */
    public static double[] hardCopyVector(double[] vector) {
        return Arrays.copyOf(vector, vector.length);
    }
}

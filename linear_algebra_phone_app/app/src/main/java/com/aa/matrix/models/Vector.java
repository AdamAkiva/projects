package com.aa.matrix.models;

import java.util.Arrays;

public abstract class Vector extends BaseModel {

    public static double[] convertStringArrayTo1DArray(String[] vectorValuesString)
            throws NumberFormatException {
        int size = vectorValuesString.length;
        final double[] vector = new double[size];
        for (int i = 0; i < size; i++) {
            vector[i] = Double.parseDouble(vectorValuesString[i]);
        }
        return vector;
    }

    public static double[] hardCopyVector(double[] vector) {
        return Arrays.copyOf(vector, vector.length);
    }
}

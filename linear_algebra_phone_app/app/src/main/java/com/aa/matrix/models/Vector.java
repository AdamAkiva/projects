package com.aa.matrix.models;

import java.util.Arrays;

public class Vector {

    private final double[] vector;
    private final int size;

    public Vector(final double[] vector) {
        this.vector = vector;
        this.size = vector.length;
    }

    public static Vector convertStringArrayToVector(String[] vectorValuesString)
            throws NumberFormatException {
        int size = vectorValuesString.length;
        final double[] vector = new double[size];
        for (int i = 0; i < size; i++) {
            vector[i] = Double.parseDouble(vectorValuesString[i]);
        }
        return new Vector(vector);
    }

    public static double[] hardCopyVector(double[] vector) {
        return Arrays.copyOf(vector, vector.length);
    }

    public double[] getVector() {
        return vector;
    }
}

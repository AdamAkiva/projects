package com.aa.matrix.models;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class CalculationResult {

    private final int rows;
    private final int cols;
    private String result;
    private final Map<String, double[][]> steps;

    private static final String TEXT_VIEW_SEPARATOR = "--------------------------------------------" +
            "------------";

    public CalculationResult(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.result = "";
        steps = new LinkedHashMap<>();
    }

    public void setResult(String result) {
        this.result = result;
    }

    public static String doubleToString(double value) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.UP);
        return df.format(value);
    }

    public void put(String str, double[][] matrix) {
        steps.put(str, deepCopyMatrix(matrix));
    }

    private double[][] deepCopyMatrix(double[][] matrix) {
        double[][] copy = new double[rows][cols];
        for (int i = 0; i < matrix.length; i++) {
            copy[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
        return copy;
    }

    private String matrixToPrintableString(double[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sb.append(doubleToString(matrix[i][j])).append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String snapShotToPrintableString(Map.Entry<String, double[][]> snapShot) {
        String str = "";
        String newLine = System.lineSeparator();
        str += snapShot.getKey() + newLine + matrixToPrintableString(snapShot.getValue()) + newLine
                + TEXT_VIEW_SEPARATOR + newLine + newLine;
        return str;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, double[][]> snapShot : steps.entrySet()) {
            sb.append(snapShotToPrintableString(snapShot));
        }
        sb.append(result);
        return sb.toString();
    }
}

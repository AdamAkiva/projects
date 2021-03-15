package com.aa.matrix.models;

import java.util.Locale;
import java.util.concurrent.Callable;

/**
 * @author Adam Akiva
 * Class used to calculate a Gauss Jordan elimination value of the matrix held in the model
 */
public class GaussJordan extends BaseModel implements Callable<Calculation> {

    private final Matrix matrix;
    private final String freeVarName;
    private final int variables;
    private final Calculation result;

    private static final String NO_SOLUTIONS = "No solutions" + System.lineSeparator() +
            "(R%d is a non-true equation)" + System.lineSeparator();

    /**
     * @param freeVarName String how to show to the user the results, default is x
     */
    public GaussJordan(String freeVarName) {
        this.matrix = BaseModel.getInstance().getMatrixObject();
        this.freeVarName = freeVarName;
        this.result = matrix.getResult();
        result.put(BASE_MATRIX, matrix.getMatrix());
        variables = matrix.getCols() - 1;
    }

    @Override
    public Calculation call() {
        int row = matrix.matrixToLowerEchelonForm();
        if (row != -1) {
            result.setResult(String.format(Locale.US, NO_SOLUTIONS, row));
            return result;
        }
        row = matrix.matrixToReducedRowEchelonForm();
        if (row != -1) {
            result.setResult(String.format(Locale.US, NO_SOLUTIONS, row));
            return result;
        }
        result.setResult(findResults());
        return result;
    }

    /**
     * @return Method used to find the result for the Gauss Jordan elimination, can be either
     * singular solution or infinite solutions, if reached here it is assured that there's a solution
     */
    private String findResults() {
        int numberOfSolutions = 0;
        StringBuilder resultString = new StringBuilder();
        for (int i = 0; i < matrix.getRows(); i++) {
            if (!matrix.checkForZeroVector(i)) {
                numberOfSolutions++;
            }
        }
        if (numberOfSolutions == variables) {
            buildSingleSolutionStringResult(resultString);
        } else {
            buildInfiniteSolutionsStringResult(resultString, numberOfSolutions);
        }
        return resultString.toString();
    }

    /**
     * Method which builds a string to display to the user of all the results for a singular solution
     * @param resultString StringBuilder to append the results to
     */
    private void buildSingleSolutionStringResult(StringBuilder resultString) {
        for (int i = 0; i < variables; i++) {
            String value = doubleToString(matrix.getMatrix()[i][matrix.getCols() - 1]);
            resultString.append(freeVarName).append(i + 1).append(" = ").append(value).append(System.lineSeparator());
        }
    }

    /**
     * Method which builds a string to display to the user of all the results for a infinite solutions
     * @param resultString StringBuilder to append the results to
     * @param numberOfSolutions Integer for the number of actual results to compare with the number
     *                          of the expected results
     */
    private void buildInfiniteSolutionsStringResult(StringBuilder resultString, int numberOfSolutions) {
        double[][] m = matrix.getMatrix();
        int cols = matrix.getCols();
        for (int i = 0; i < numberOfSolutions; i++) {
            StringBuilder values = new StringBuilder();
            for (int j = i + 1; j < cols; j++) {
                // Free variables
                if (j != cols - 1 && m[i][j] != ZERO) {
                    double value = m[i][j] * NEGATIVE_ONE;
                    if (value < ZERO || values.toString().isEmpty()) {
                        values.append(" ").append(doubleToString(value)).append(freeVarName).append(j + 1);
                    } else {
                        values.append(" +").append(doubleToString(value)).append(freeVarName).append(j + 1);
                    }

                    // Free vector
                } else if (j == cols - 1 && m[i][j] != ZERO) {
                    double value = m[i][j];
                    if (value < ZERO || values.toString().isEmpty()) {
                        values.append(" ").append(value);
                    } else {
                        values.append(" +").append(doubleToString(m[i][j]));
                    }
                }
            }
            resultString.append(freeVarName).append(i + 1).append(" = ").append(values.toString()).append(System.lineSeparator());
        }
        for (int i = numberOfSolutions; i < variables; i++) {
            resultString.append(freeVarName).append(i + 1).append(" = Free").append(System.lineSeparator());
        }
    }
}

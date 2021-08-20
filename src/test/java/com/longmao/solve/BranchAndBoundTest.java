package com.longmao.solve;

import com.longmao.dto.*;
import com.longmao.model.IntegerLinearProgramming;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * @Description TODO
 * @Author zimu young
 * Date 2021/8/18 8:53
 * Version 1.0
 **/
public class BranchAndBoundTest {
    @Test
    public void testBranchAndBoundPipelineV1(){
        int variableCount = 2, equationCount = 3;

        String[] valueCoefficientsNumerator = {"1", "5"};
        String[] valueCoefficientsDenominator = {"1", "1"};
        String[][] coefficientMatrixNumerator = {{"1", "-1"}, {"5", "6"}, {"1", "0"}};
        String[][] coefficientMatrixDenominator = {{"1", "1"}, {"1", "1"}, {"1", "1"}};
        String[] bVectorNumerator = {"-2", "30", "4"};
        String[] bVectorDenominator = {"1", "1", "1"};

        Fraction[] valueCoefficients = new Fraction[variableCount];
        for (int i = 0; i < valueCoefficients.length; i++){
            valueCoefficients[i] = new Fraction();
            valueCoefficients[i].setNumerator(new BigInteger(valueCoefficientsNumerator[i]));
            valueCoefficients[i].setDenominator(new BigInteger(valueCoefficientsDenominator[i]));
        }

        Fraction[][] coefficientMatrix = new Fraction[equationCount][];
        for (int i = 0; i < coefficientMatrix.length; i++){
            coefficientMatrix[i] = new Fraction[variableCount];
            for (int j = 0; j < coefficientMatrix[i].length; j++){
                coefficientMatrix[i][j] = new Fraction();
                coefficientMatrix[i][j].setNumerator(new BigInteger(coefficientMatrixNumerator[i][j]));
                coefficientMatrix[i][j].setDenominator(new BigInteger(coefficientMatrixDenominator[i][j]));
            }
        }

        Fraction[] bVector = new Fraction[equationCount];
        for (int i = 0; i < bVector.length; i++){
            bVector[i] = new Fraction();
            bVector[i].setNumerator(new BigInteger(bVectorNumerator[i]));
            bVector[i].setDenominator(new BigInteger(bVectorDenominator[i]));
        }

        EQUATION[] equations = new EQUATION[equationCount];
        equations[0] = EQUATION.GREATER_THAN_OR_EQUAL;
        equations[1] = EQUATION.LESS_THAN_OR_EQUAL;
        equations[2] = EQUATION.LESS_THAN_OR_EQUAL;

        OBJECTIVE objective = OBJECTIVE.MAX;

        CONSTRAINT[] constraints = new CONSTRAINT[variableCount];
        Arrays.fill(constraints, CONSTRAINT.GREATER_THAN_OR_EQUAL_TO_ZERO);

        boolean[] isInteger = new boolean[variableCount];
        for (int i = 0; i < variableCount; i++){
            isInteger[i] = true;
        }

        IntegerLinearProgramming integerLinearProgramming = new IntegerLinearProgramming();
        integerLinearProgramming.setValueCoefficients(valueCoefficients);
        integerLinearProgramming.setCoefficientMatrix(coefficientMatrix);
        integerLinearProgramming.setEquations(equations);
        integerLinearProgramming.setBVector(bVector);
        integerLinearProgramming.setObjective(objective);
        integerLinearProgramming.setConstraints(constraints);
        integerLinearProgramming.setIsInteger(isInteger);

        BranchAndBound branchAndBound = new BranchAndBound();
        Solution solution = branchAndBound.branchAndBoundPipelineV1(integerLinearProgramming);
        Assert.assertEquals(solution.getObjectiveValue().getNumerator(), new BigInteger("17"));
        Assert.assertEquals(solution.getObjectiveValue().getDenominator(), BigInteger.ONE);
        Assert.assertEquals(solution.getOptimalSolution()[0].getNumerator(), new BigInteger("2"));
        Assert.assertEquals(solution.getOptimalSolution()[0].getDenominator(), BigInteger.ONE);
        Assert.assertEquals(solution.getOptimalSolution()[1].getNumerator(), new BigInteger("3"));
        Assert.assertEquals(solution.getOptimalSolution()[1].getDenominator(), BigInteger.ONE);
    }

    @Test
    public void testBranchAndBoundPipelineV2(){
        int variableCount = 2, equationCount = 3;

        String[] valueCoefficientsNumerator = {"1", "5"};
        String[] valueCoefficientsDenominator = {"1", "1"};
        String[][] coefficientMatrixNumerator = {{"1", "-1"}, {"5", "6"}, {"1", "0"}};
        String[][] coefficientMatrixDenominator = {{"1", "1"}, {"1", "1"}, {"1", "1"}};
        String[] bVectorNumerator = {"-2", "30", "4"};
        String[] bVectorDenominator = {"1", "1", "1"};

        Fraction[] valueCoefficients = new Fraction[variableCount];
        for (int i = 0; i < valueCoefficients.length; i++){
            valueCoefficients[i] = new Fraction();
            valueCoefficients[i].setNumerator(new BigInteger(valueCoefficientsNumerator[i]));
            valueCoefficients[i].setDenominator(new BigInteger(valueCoefficientsDenominator[i]));
        }

        Fraction[][] coefficientMatrix = new Fraction[equationCount][];
        for (int i = 0; i < coefficientMatrix.length; i++){
            coefficientMatrix[i] = new Fraction[variableCount];
            for (int j = 0; j < coefficientMatrix[i].length; j++){
                coefficientMatrix[i][j] = new Fraction();
                coefficientMatrix[i][j].setNumerator(new BigInteger(coefficientMatrixNumerator[i][j]));
                coefficientMatrix[i][j].setDenominator(new BigInteger(coefficientMatrixDenominator[i][j]));
            }
        }

        Fraction[] bVector = new Fraction[equationCount];
        for (int i = 0; i < bVector.length; i++){
            bVector[i] = new Fraction();
            bVector[i].setNumerator(new BigInteger(bVectorNumerator[i]));
            bVector[i].setDenominator(new BigInteger(bVectorDenominator[i]));
        }

        EQUATION[] equations = new EQUATION[equationCount];
        equations[0] = EQUATION.GREATER_THAN_OR_EQUAL;
        equations[1] = EQUATION.LESS_THAN_OR_EQUAL;
        equations[2] = EQUATION.LESS_THAN_OR_EQUAL;

        OBJECTIVE objective = OBJECTIVE.MAX;

        CONSTRAINT[] constraints = new CONSTRAINT[variableCount];
        Arrays.fill(constraints, CONSTRAINT.GREATER_THAN_OR_EQUAL_TO_ZERO);

        boolean[] isInteger = new boolean[variableCount];
        for (int i = 0; i < variableCount; i++){
            isInteger[i] = true;
        }

        IntegerLinearProgramming integerLinearProgramming = new IntegerLinearProgramming();
        integerLinearProgramming.setValueCoefficients(valueCoefficients);
        integerLinearProgramming.setCoefficientMatrix(coefficientMatrix);
        integerLinearProgramming.setEquations(equations);
        integerLinearProgramming.setBVector(bVector);
        integerLinearProgramming.setObjective(objective);
        integerLinearProgramming.setConstraints(constraints);
        integerLinearProgramming.setIsInteger(isInteger);

        BranchAndBound branchAndBound = new BranchAndBound();
        Solution solution = branchAndBound.branchAndBoundPipelineV2(integerLinearProgramming);
        Assert.assertEquals(solution.getObjectiveValue().getNumerator(), new BigInteger("17"));
        Assert.assertEquals(solution.getObjectiveValue().getDenominator(), BigInteger.ONE);
        Assert.assertEquals(solution.getOptimalSolution()[0].getNumerator(), new BigInteger("2"));
        Assert.assertEquals(solution.getOptimalSolution()[0].getDenominator(), BigInteger.ONE);
        Assert.assertEquals(solution.getOptimalSolution()[1].getNumerator(), new BigInteger("3"));
        Assert.assertEquals(solution.getOptimalSolution()[1].getDenominator(), BigInteger.ONE);
    }
}

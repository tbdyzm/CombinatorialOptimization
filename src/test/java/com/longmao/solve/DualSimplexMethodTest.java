package com.longmao.solve;

import com.longmao.dto.*;
import com.longmao.model.LinearProgramming;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigInteger;

/**
 * @Description TODO
 * @Author zimu young
 * Date 2021/8/3 12:14
 * Version 1.0
 **/
public class DualSimplexMethodTest {
    @Test
    public void testDualSimplexMethodPipeline(){
        int variableCount = 3, equationCount = 2;

        String[] valueCoefficientsNumerator = {"2", "3", "4"};
        String[] valueCoefficientsDenominator = {"1", "1", "1"};
        String[][] coefficientMatrixNumerator = {{"1", "2", "1"}, {"2", "-1", "3"}};
        String[][] coefficientMatrixDenominator = {{"1", "1", "1"}, {"1", "1", "1"}};
        String[] bVectorNumerator = {"3", "4"};

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
        }

        EQUATION[] equations = new EQUATION[equationCount];
        equations[0] = EQUATION.GREATER_THAN_OR_EQUAL;
        equations[1] = EQUATION.GREATER_THAN_OR_EQUAL;

        OBJECTIVE objective = OBJECTIVE.MIN;

        CONSTRAINT[] constraints = new CONSTRAINT[variableCount];
        for (int i = 0; i < constraints.length; i++){
            constraints[i] = CONSTRAINT.GREATER_THAN_OR_EQUAL_TO_ZERO;
        }

        LinearProgramming linearProgramming = new LinearProgramming();
        linearProgramming.setValueCoefficients(valueCoefficients);
        linearProgramming.setCoefficientMatrix(coefficientMatrix);
        linearProgramming.setEquations(equations);
        linearProgramming.setBVector(bVector);
        linearProgramming.setObjective(objective);
        linearProgramming.setConstraints(constraints);

        DualSimplexMethod dualSimplexMethod = new DualSimplexMethod();
        Solution solution = dualSimplexMethod.dualSimplexMethodPipeline(linearProgramming);
        Assert.assertEquals(solution.getObjectiveValue().getNumerator(), new BigInteger("28"));
        Assert.assertEquals(solution.getObjectiveValue().getDenominator(), new BigInteger("5"));
        Assert.assertEquals(solution.getOptimalSolution()[0].getNumerator(), new BigInteger("11"));
        Assert.assertEquals(solution.getOptimalSolution()[0].getDenominator(), new BigInteger("5"));
        Assert.assertEquals(solution.getOptimalSolution()[1].getNumerator(), new BigInteger("2"));
        Assert.assertEquals(solution.getOptimalSolution()[1].getDenominator(), new BigInteger("5"));
        Assert.assertEquals(solution.getOptimalSolution()[2].getNumerator(), BigInteger.ZERO);
    }
}

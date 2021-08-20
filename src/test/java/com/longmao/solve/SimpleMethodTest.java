package com.longmao.solve;

import com.longmao.dto.*;
import com.longmao.model.LinearProgramming;
import com.longmao.model.SimplexTable;
import com.longmao.utils.StandardizeLinearProgramming;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigInteger;

/**
 * @Description TODO
 * @Author zimu young
 * Date 2021/7/26 20:39
 * Version 1.0
 **/
public class SimpleMethodTest {
    @Test
    public void testGetIn(){
        String[] numerator = {"5", "9", "-3", "13", "-7"};
        String[] infinityNumerator = {"0", "1", "4", "0", "4"};
        Boolean[] infinity = {false, true, true, false, true};

        Fraction[] sigma = new Fraction[5];
        for (int i = 0; i < sigma.length; i++){
            sigma[i] = new Fraction();
            sigma[i].setNumerator(new BigInteger(numerator[i]));
            sigma[i].setInfinityNumerator(new BigInteger(infinityNumerator[i]));
            sigma[i].setInfinity(infinity[i]);
        }

        SimplexTable simplexTable = new SimplexTable();
        simplexTable.setSigma(sigma);

        SimplexMethod simplexMethod = new SimplexMethod();
        simplexMethod.setSimplexTable(simplexTable);
        int in = simplexMethod.getIn();
        Assert.assertEquals(in, 2);
    }

    @Test
    public void testGetOut(){
        int in = 2;
        String[] coefficientColNumerator = {"9", "4", "-2", "3", "-4"};
        String[] bVectorNumerator = {"18", "9", "22", "6", "1"};
        int[] baseVariables = {3, 6, 5, 2, 7};

        Fraction[][] coefficientMatrix = new Fraction[5][];
        for (int i = 0; i < coefficientMatrix.length; i++){
            coefficientMatrix[i] = new Fraction[6];
            coefficientMatrix[i][in] = new Fraction();
            coefficientMatrix[i][in].setNumerator(new BigInteger(coefficientColNumerator[i]));
        }

        Fraction[] bVector = new Fraction[5];
        for (int i = 0; i < bVector.length; i++){
            bVector[i] = new Fraction();
            bVector[i].setNumerator(new BigInteger(bVectorNumerator[i]));
        }

        SimplexTable simplexTable = new SimplexTable();
        SimplexMethod simplexMethod = new SimplexMethod();
        simplexTable.setCoefficientMatrix(coefficientMatrix);
        simplexTable.setBVector(bVector);
        simplexTable.setBaseVariables(baseVariables);
        simplexMethod.setSimplexTable(simplexTable);
        int out = simplexMethod.getOut(in);

        Assert.assertEquals(out, 3);
    }
    
    @Test
    public void testCoefficientToOne(){
        int in = 2, out = 3;
        String[] coefficientRowNumerator = {"4", "7", "5", "5", "1", "6"};
        String[] coefficientRowDenominator = {"3", "8", "2", "1", "1", "9"};
        String bVectorNumerator = "7";
        String bVectorDenominator = "3";

        Fraction[][] coefficientMatrix = new Fraction[5][];
        coefficientMatrix[out] = new Fraction[6];
        for (int i = 0; i < coefficientMatrix[out].length; i++){
            coefficientMatrix[out][i] = new Fraction();
            coefficientMatrix[out][i].setNumerator(new BigInteger(coefficientRowNumerator[i]));
            coefficientMatrix[out][i].setDenominator(new BigInteger(coefficientRowDenominator[i]));
        }

        Fraction[] bVector = new Fraction[5];
        bVector[out] = new Fraction();
        bVector[out].setNumerator(new BigInteger(bVectorNumerator));
        bVector[out].setDenominator(new BigInteger(bVectorDenominator));

        SimplexTable simplexTable = new SimplexTable();
        SimplexMethod simplexMethod = new SimplexMethod();
        simplexTable.setCoefficientMatrix(coefficientMatrix);
        simplexMethod.setSimplexTable(simplexTable);
        simplexTable.setBVector(bVector);
        simplexMethod.coefficientToOne(in, out);
        Assert.assertEquals(simplexMethod.getSimplexTable().getCoefficientMatrix()[out][in].getNumerator(), BigInteger.ONE);
        Assert.assertEquals(simplexMethod.getSimplexTable().getCoefficientMatrix()[out][in].getDenominator(), BigInteger.ONE);
        Assert.assertEquals(simplexMethod.getSimplexTable().getBVector()[out].getNumerator(), new BigInteger("14"));
        Assert.assertEquals(simplexMethod.getSimplexTable().getBVector()[out].getDenominator(), new BigInteger("15"));
    }

    @Test
    public void testCoefficientToZero(){
        /*
         * 增广矩阵(系数矩阵+b值)
         * |----------------系数矩阵------------------|    b值
         * 1/6    -1/7    1/8    1/9    -1/10    1/11    7/3
         * 2/7    -1/4    2/9    2/10   -2/11    2/12    8/4
         * 3/8    -1/3    3/10   3/11   -1/4     3/13    9/5
         * 4/9    -2/5   [4/11]  4/12   -4/13    4/14    10/6
         * 5/10   -5/11   5/12   5/13   -5/14    5/15    11/7
         * 4/11变为1，该列其它行变为0
         */
        int row = 5, col = 6;
        Fraction[][] coefficientMatrix = new Fraction[row][];
        for (int i = 0; i < coefficientMatrix.length; i++){
            coefficientMatrix[i] = new Fraction[col];
            for (int j = 0; j < coefficientMatrix[i].length; j++){
                coefficientMatrix[i][j] = new Fraction();
                coefficientMatrix[i][j].setNumerator(new BigInteger(Integer.toString(i+1)));
                coefficientMatrix[i][j].setDenominator(new BigInteger(Integer.toString(i+j+6)));
                if (j % 3 == 1){
                    coefficientMatrix[i][j].oppositeFraction();
                }
            }
        }

        Fraction[] bVector = new Fraction[row];
        for (int i = 0; i < bVector.length; i++){
            bVector[i] = new Fraction();
            bVector[i].setNumerator(new BigInteger(Integer.toString(i+7)));
            bVector[i].setDenominator(new BigInteger(Integer.toString(i+3)));
        }

        SimplexTable simplexTable = new SimplexTable();
        SimplexMethod simplexMethod = new SimplexMethod();
        simplexTable.setCoefficientMatrix(coefficientMatrix);
        simplexTable.setBVector(bVector);
        simplexMethod.setSimplexTable(simplexTable);

        int in = 2, out = 3;
        simplexMethod.coefficientToOne(in, out);
        simplexMethod.coefficientToZero(in, out);
        for (int i = 0; i < simplexMethod.getSimplexTable().getCoefficientMatrix().length; i++){
            if (i != out){
                Assert.assertEquals(simplexMethod.getSimplexTable().getCoefficientMatrix()[i][in].getNumerator(), BigInteger.ZERO);
            }
        }
    }

    @Test
    public void testCalculateSigma(){
        /*
         * 价值系数 -3  0  1  0  0  -inf  -inf
         *         系数矩阵          b值
         *   1  1  1  1  0  0  0    4
         *  -2  1 -1  0 -1  1  0    1
         *   0  3  1  0  0  0  1    9
         * 基变量索引 3, 5, 6
         * 检验系数sigma -3-2inf  4inf  1  0  -inf  0  0
         */
        String[] valueCoefficientsNumerator = {"-3", "0", "1", "0", "0", "-inf", "-inf"};
        int[] baseVariables = {3, 5, 6};
        String[][] coefficientMatrixNumerator = {{"1", "1", "1", "1", "0", "0", "0"}, {"-2", "1", "-1", "0", "-1", "1", "0"},
                {"0", "3", "1", "0", "0", "0", "1"}};
        String[] bVectorNumerator = {"4", "1", "9"};

        Fraction[] valueCoefficients = new Fraction[7];
        for (int i = 0; i < valueCoefficients.length; i++){
            valueCoefficients[i] = new Fraction();
            if ("-inf".equals(valueCoefficientsNumerator[i])){
                valueCoefficients[i].setInfinityNumerator(new BigInteger("-1"));
                valueCoefficients[i].setInfinity(true);
            }
            else {
                valueCoefficients[i].setNumerator(new BigInteger(valueCoefficientsNumerator[i]));
            }
        }

        Fraction[][] coefficientMatrix = new Fraction[3][];
        for (int i = 0; i < coefficientMatrix.length; i++){
            coefficientMatrix[i] = new Fraction[7];
            for (int j = 0; j < coefficientMatrix[i].length; j++){
                coefficientMatrix[i][j] = new Fraction();
                coefficientMatrix[i][j].setNumerator(new BigInteger(coefficientMatrixNumerator[i][j]));
            }
        }

        Fraction[] bVector = new Fraction[3];
        for (int i = 0; i < bVector.length; i++){
            bVector[i] = new Fraction();
            bVector[i].setNumerator(new BigInteger(bVectorNumerator[i]));
        }

        Fraction[] sigma = new Fraction[7];
        for (int i = 0; i < sigma.length; i++){
            sigma[i] = new Fraction();
        }

        SimplexTable simplexTable = new SimplexTable();
        simplexTable.setValueCoefficients(valueCoefficients);
        simplexTable.setCoefficientMatrix(coefficientMatrix);
        simplexTable.setBaseVariables(baseVariables);
        simplexTable.setSigma(sigma);
        simplexTable.setBVector(bVector);

        SimplexMethod simplexMethod = new SimplexMethod();
        simplexMethod.setSimplexTable(simplexTable);
        simplexMethod.calculateSigma();
        Assert.assertEquals(simplexMethod.getSimplexTable().getSigma()[0].getNumerator(), new BigInteger("-3"));
        Assert.assertEquals(simplexMethod.getSimplexTable().getSigma()[0].getInfinityNumerator(), new BigInteger("-2"));
        Assert.assertEquals(simplexMethod.getSimplexTable().getSigma()[1].getNumerator(), new BigInteger("0"));
        Assert.assertEquals(simplexMethod.getSimplexTable().getSigma()[1].getInfinityNumerator(), new BigInteger("4"));
        Assert.assertEquals(simplexMethod.getSimplexTable().getSigma()[2].getNumerator(), new BigInteger("1"));
        Assert.assertEquals(simplexMethod.getSimplexTable().getSigma()[2].getInfinityNumerator(), new BigInteger("0"));
        Assert.assertEquals(simplexMethod.getSimplexTable().getSigma()[3].getNumerator(), BigInteger.ZERO);
        Assert.assertEquals(simplexMethod.getSimplexTable().getSigma()[3].getInfinityNumerator(), BigInteger.ZERO);
        Assert.assertEquals(simplexMethod.getSimplexTable().getSigma()[4].getNumerator(), new BigInteger("0"));
        Assert.assertEquals(simplexMethod.getSimplexTable().getSigma()[4].getInfinityNumerator(), new BigInteger("-1"));
        Assert.assertEquals(simplexMethod.getSimplexTable().getSigma()[5].getNumerator(), BigInteger.ZERO);
        Assert.assertEquals(simplexMethod.getSimplexTable().getSigma()[5].getInfinityNumerator(), BigInteger.ZERO);
        Assert.assertEquals(simplexMethod.getSimplexTable().getSigma()[6].getNumerator(), BigInteger.ZERO);
        Assert.assertEquals(simplexMethod.getSimplexTable().getSigma()[6].getInfinityNumerator(), BigInteger.ZERO);
    }

    @Test
    public void testGetBaseVariables(){
        /*
         * 价值系数 -3  0  1
         *   系数矩阵    b值
         *   3  0  1    3
         *  -2  1 -1    1
         *   0  3  1    6
         */
        String[] valueCoefficientsNumerator = {"-3", "0", "1"};
        String[][] coefficientMatrixNumerator = {{"1", "1", "1"}, {"-2", "1", "-1"}, {"0", "3", "1"}};
        String[] bVectorNumerator = {"4", "1", "9"};

        Fraction[] valueCoefficients = new Fraction[3];
        for (int i = 0; i < valueCoefficients.length; i++){
            valueCoefficients[i] = new Fraction();
            valueCoefficients[i].setNumerator(new BigInteger(valueCoefficientsNumerator[i]));
        }

        Fraction[][] coefficientMatrix = new Fraction[3][];
        for (int i = 0; i < coefficientMatrix.length; i++){
            coefficientMatrix[i] = new Fraction[3];
            for (int j = 0; j < coefficientMatrix[i].length; j++){
                coefficientMatrix[i][j] = new Fraction();
                coefficientMatrix[i][j].setNumerator(new BigInteger(coefficientMatrixNumerator[i][j]));
            }
        }

        Fraction[] bVector = new Fraction[3];
        for (int i = 0; i < bVector.length; i++){
            bVector[i] = new Fraction();
            bVector[i].setNumerator(new BigInteger(bVectorNumerator[i]));
        }

        EQUATION[] equations = new EQUATION[3];
        equations[0] = EQUATION.LESS_THAN_OR_EQUAL;
        equations[1] = EQUATION.GREATER_THAN_OR_EQUAL;
        equations[2] = EQUATION.EQUAL;

        OBJECTIVE objective = OBJECTIVE.MAX;

        CONSTRAINT[] constraints = new CONSTRAINT[3];
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

        StandardizeLinearProgramming standardizeLinearProgramming = new StandardizeLinearProgramming();
        standardizeLinearProgramming.setLinearProgramming(linearProgramming);
        standardizeLinearProgramming.standardizeObjective();
        standardizeLinearProgramming.standardizeEquations();
        standardizeLinearProgramming.standardizeConstraints();

        SimplexTable simplexTable = new SimplexTable();
        simplexTable.setCoefficientMatrix(linearProgramming.getCoefficientMatrix());
        int[] baseVariables = new int[3];
        simplexTable.setBaseVariables(baseVariables);

        SimplexMethod simplexMethod = new SimplexMethod();
        simplexMethod.setSimplexTable(simplexTable);

        simplexMethod.getBaseVariables();
        Assert.assertEquals(simplexMethod.getSimplexTable().getBaseVariables()[0], 3);
        Assert.assertEquals(simplexMethod.getSimplexTable().getBaseVariables()[1], 5);
        Assert.assertEquals(simplexMethod.getSimplexTable().getBaseVariables()[2], 6);
    }

    @Test
    public void testSimplexMethodPipeline(){
        int variableCount = 4, equationCount = 3;

        String[] valueCoefficientsNumerator = {"3", "-150", "1", "-6"};
        String[] valueCoefficientsDenominator = {"4", "1", "50", "1"};
        String[][] coefficientMatrixNumerator = {{"1", "-60", "-1", "9"}, {"1", "-90", "-1", "3"}, {"0", "0", "1", "0"}};
        String[][] coefficientMatrixDenominator = {{"4", "1", "25", "1"}, {"2", "1", "50", "1"}, {"1", "1", "1", "1"}};
        String[] bVectorNumerator = {"0", "0", "1"};

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
        equations[0] = EQUATION.LESS_THAN_OR_EQUAL;
        equations[1] = EQUATION.LESS_THAN_OR_EQUAL;
        equations[2] = EQUATION.LESS_THAN_OR_EQUAL;

        OBJECTIVE objective = OBJECTIVE.MAX;

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

        SimplexMethod simplexMethod = new SimplexMethod();
        Solution solution = simplexMethod.simplexMethodPipeline(linearProgramming);
        Assert.assertEquals(solution.getObjectiveValue().getNumerator(), BigInteger.ONE);
        Assert.assertEquals(solution.getObjectiveValue().getDenominator(), new BigInteger("20"));
        Assert.assertEquals(solution.getOptimalSolution()[0].getNumerator(), BigInteger.ONE);
        Assert.assertEquals(solution.getOptimalSolution()[0].getDenominator(), new BigInteger("25"));
        Assert.assertEquals(solution.getOptimalSolution()[1].getNumerator(), BigInteger.ZERO);
        Assert.assertEquals(solution.getOptimalSolution()[2].getNumerator(), BigInteger.ONE);
        Assert.assertEquals(solution.getOptimalSolution()[2].getDenominator(), BigInteger.ONE);
        Assert.assertEquals(solution.getOptimalSolution()[3].getNumerator(), BigInteger.ZERO);
    }
}

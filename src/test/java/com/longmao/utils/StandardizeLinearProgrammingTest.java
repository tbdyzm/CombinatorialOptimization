package com.longmao.utils;

import com.longmao.dto.CONSTRAINT;
import com.longmao.dto.EQUATION;
import com.longmao.dto.Fraction;
import com.longmao.dto.OBJECTIVE;
import com.longmao.model.LinearProgramming;
import com.longmao.utils.StandardizeLinearProgramming;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigInteger;

/**
 * @Description TODO
 * @Author zimu young
 * Date 2021/7/18 23:00
 * Version 1.0
 **/
public class StandardizeLinearProgrammingTest {
    @Test
    public void testStandardizeLinearProgramming(){
        /*
         *              原始线性规划模型                        标准线性规划模型
         * 价值系数         3, 2, 1                        -3, 2, -1, 0, -inf, 1
         * 系数矩阵  [[2， 1， 0], [2, 1, 0]]     [[-2, 1, 0, 1, 0, 0], [2, -1, 0, 0, 1, 0]]
         * 不等式             >=, =                                =, =
         * b值               -1, 0                                1, 0
         * 目标                min                                 max
         * 变量约束条件     >=, <=, 无约束                   >=, >=, >=, >=, >=, >=
         */
        LinearProgramming linearProgramming = new LinearProgramming();
        Fraction[] valueCoefficients = new Fraction[3];
        Fraction[][] coefficientMatrix = new Fraction[2][3];
        EQUATION[] equations = new EQUATION[2];
        Fraction[] bVector = new Fraction[2];
        OBJECTIVE objective = OBJECTIVE.MIN;
        CONSTRAINT[] constraints = new CONSTRAINT[3];
        int i = 0, j  = 0;
        for (i = 0; i < valueCoefficients.length; i++){
            valueCoefficients[i] = new Fraction();
            valueCoefficients[i].setNumerator(new BigInteger(Integer.toString(valueCoefficients.length-i)));
        }
        for (i = 0; i < coefficientMatrix.length; i++){
            for (j = 0; j < coefficientMatrix[i].length; j++){
                coefficientMatrix[i][j] = new Fraction();
                coefficientMatrix[i][j].setNumerator(new BigInteger(Integer.toString(coefficientMatrix[i].length-j-1)));
            }
        }
        for (i = 0; i < equations.length; i++){
            if (i % 3 == 0){
                equations[i] = EQUATION.GREATER_THAN_OR_EQUAL;
            }
            else if(i % 3 == 1){
                equations[i] = EQUATION.EQUAL;
            }
            else {
                equations[i] = EQUATION.LESS_THAN_OR_EQUAL;
            }
        }
        for (i = 0; i < bVector.length; i++){
            bVector[i] = new Fraction();
            bVector[i].setNumerator(new BigInteger(Integer.toString(i-1)));
        }
        for (i = 0; i < constraints.length; i++){
            if (i % 3 == 0){
                constraints[i] = CONSTRAINT.GREATER_THAN_OR_EQUAL_TO_ZERO;
            }
            else if(i % 3 == 1){
                constraints[i] = CONSTRAINT.LESS_THAN_OT_EQUAL_TO_ZERO;
            }
            else {
                constraints[i] = CONSTRAINT.NO_CONSTRAINT;
            }
        }

        linearProgramming.setValueCoefficients(valueCoefficients);
        linearProgramming.setCoefficientMatrix(coefficientMatrix);
        linearProgramming.setEquations(equations);
        linearProgramming.setBVector(bVector);
        linearProgramming.setObjective(objective);
        linearProgramming.setConstraints(constraints);

        StandardizeLinearProgramming standardizeLinearProgramming = new StandardizeLinearProgramming();
        standardizeLinearProgramming.setLinearProgramming(linearProgramming);
        standardizeLinearProgramming.standardizeLinearProgramming();

        Assert.assertEquals(linearProgramming.getValueCoefficients().length, 6);
        Assert.assertEquals(linearProgramming.getCoefficientMatrix()[0].length, 6);
        Assert.assertEquals(linearProgramming.getCoefficientMatrix()[1].length, 6);
        Assert.assertEquals(linearProgramming.getConstraints().length, 6);
        Assert.assertEquals(linearProgramming.getValueCoefficients()[0].getNumerator(), new BigInteger("-3"));
        Assert.assertEquals(linearProgramming.getValueCoefficients()[2].getNumerator(), new BigInteger("-1"));
        Assert.assertEquals(linearProgramming.getValueCoefficients()[4].isInfinity(), true);
        Assert.assertEquals(linearProgramming.getCoefficientMatrix()[0][1].getNumerator(), BigInteger.ONE);
        Assert.assertEquals(linearProgramming.getCoefficientMatrix()[0][3].getNumerator(), BigInteger.ONE);
        Assert.assertEquals(linearProgramming.getCoefficientMatrix()[1][1].getNumerator(), new BigInteger("-1"));
        Assert.assertEquals(linearProgramming.getCoefficientMatrix()[1][4].getNumerator(), BigInteger.ONE);
        Assert.assertEquals(linearProgramming.getObjective(), OBJECTIVE.MAX);
    }
}

package com.longmao.utils;

import com.longmao.dto.Fraction;
import com.longmao.enums.CONSTRAINT;
import com.longmao.enums.EQUATION;
import com.longmao.enums.OBJECTIVE;
import com.longmao.model.IntegerLinearProgramming;
import com.longmao.model.LinearProgramming;
import com.longmao.model.OptimalMatching;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * @Description Excel数据转线性规划对象
 * @Author zimu young
 * Date 2021/11/1 19:53
 * Version 1.0
 **/
public class DataToEntity {
    public LinearProgramming dataToLinearProgrammingV1(String c, String a, String b, String e, String o, String vc){
        // process data from excel
        String[] dataValueCoefficients = c.split(",");
        int variableCount = dataValueCoefficients.length;

        String[] dataCoefficientMatrixRow = a.split(";");
        int equationCount = dataCoefficientMatrixRow.length;

        String[] dataBVector = b.split(",");

        String[] dataEquations = e.split(",");

        String[] dataConstraints = vc.split(",");

        // load data
        Fraction[] valueCoefficients = new Fraction[variableCount];
        for (int i = 0; i < variableCount; i++){
            String[] fraction = dataValueCoefficients[i].split("/");
            valueCoefficients[i] = new Fraction();
            if (fraction.length == 2) {
                valueCoefficients[i].setNumerator(new BigInteger(fraction[0]));
                valueCoefficients[i].setDenominator(new BigInteger(fraction[1]));
            }
            else {
                valueCoefficients[i].setNumerator(new BigInteger(fraction[0]));
            }
        }

        Fraction[][] coefficientMatrix = new Fraction[equationCount][];
        for (int i = 0; i < coefficientMatrix.length; i++){
            coefficientMatrix[i] = new Fraction[variableCount];
            String[] dataCoefficientMatrixRowElements = dataCoefficientMatrixRow[i].split(",");
            for (int j = 0; j < variableCount; j++){
                String[] dataCoefficientMatrixRowElement = dataCoefficientMatrixRowElements[j].split("/");
                coefficientMatrix[i][j] = new Fraction();
                if (dataCoefficientMatrixRowElement.length == 2) {
                    coefficientMatrix[i][j].setNumerator(new BigInteger(dataCoefficientMatrixRowElement[0]));
                    coefficientMatrix[i][j].setDenominator(new BigInteger(dataCoefficientMatrixRowElement[1]));
                }
                else {
                    coefficientMatrix[i][j].setNumerator(new BigInteger(dataCoefficientMatrixRowElement[0]));
                }
            }
        }

        Fraction[] bVector = new Fraction[equationCount];
        for (int i = 0; i < equationCount; i++){
            String[] fraction = dataBVector[i].split("/");
            bVector[i] = new Fraction();
            if (fraction.length == 2) {
                bVector[i].setNumerator(new BigInteger(fraction[0]));
                bVector[i].setDenominator(new BigInteger(fraction[1]));
            }
            else {
                bVector[i].setNumerator(new BigInteger(fraction[0]));
            }
        }

        EQUATION[] equations = new EQUATION[equationCount];
        for (int i = 0; i < equationCount; i++){
            if ("-1".equals(dataEquations[i])){
                equations[i] = EQUATION.GREATER_THAN_OR_EQUAL;
            }
            else if ("0".equals(dataEquations[i])){
                equations[i] = EQUATION.EQUAL;
            }
            else {
                equations[i] = EQUATION.LESS_THAN_OR_EQUAL;
            }
        }

        OBJECTIVE objective;
        if ("1".equals(o)){
            objective = OBJECTIVE.MAX;
        }
        else {
            objective = OBJECTIVE.MIN;
        }

        CONSTRAINT[] constraints = new CONSTRAINT[variableCount];
        for (int i = 0; i < variableCount; i++){
            if ("-1".equals(dataConstraints[i])){
                constraints[i] = CONSTRAINT.GREATER_THAN_OR_EQUAL_TO_ZERO;
            }
            else if ("0".equals(dataConstraints[i])){
                constraints[i] = CONSTRAINT.LESS_THAN_OT_EQUAL_TO_ZERO;
            }
            else {
                constraints[i] = CONSTRAINT.NO_CONSTRAINT;
            }
        }

        LinearProgramming linearProgramming = new LinearProgramming();
        linearProgramming.setValueCoefficients(valueCoefficients);
        linearProgramming.setCoefficientMatrix(coefficientMatrix);
        linearProgramming.setEquations(equations);
        linearProgramming.setBVector(bVector);
        linearProgramming.setObjective(objective);
        linearProgramming.setConstraints(constraints);
        return linearProgramming;
    }

    public IntegerLinearProgramming dataToLinearProgrammingV2(String c, String a, String b, String e, String o, String vc, String integer){
// process data from excel
        String[] dataValueCoefficients = c.split(",");
        int variableCount = dataValueCoefficients.length;

        String[] dataCoefficientMatrixRow = a.split(";");
        int equationCount = dataCoefficientMatrixRow.length;

        String[] dataBVector = b.split(",");

        String[] dataEquations = e.split(",");

        String[] dataConstraints = vc.split(",");

        String[] dataInteger = integer.split(",");

        // load data
        Fraction[] valueCoefficients = new Fraction[variableCount];
        for (int i = 0; i < variableCount; i++){
            String[] fraction = dataValueCoefficients[i].split("/");
            valueCoefficients[i] = new Fraction();
            if (fraction.length == 2) {
                valueCoefficients[i].setNumerator(new BigInteger(fraction[0]));
                valueCoefficients[i].setDenominator(new BigInteger(fraction[1]));
            }
            else {
                valueCoefficients[i].setNumerator(new BigInteger(fraction[0]));
            }
        }

        Fraction[][] coefficientMatrix = new Fraction[equationCount][];
        for (int i = 0; i < coefficientMatrix.length; i++){
            coefficientMatrix[i] = new Fraction[variableCount];
            String[] dataCoefficientMatrixRowElements = dataCoefficientMatrixRow[i].split(",");
            for (int j = 0; j < variableCount; j++){
                String[] dataCoefficientMatrixRowElement = dataCoefficientMatrixRowElements[j].split("/");
                coefficientMatrix[i][j] = new Fraction();
                if (dataCoefficientMatrixRowElement.length == 2) {
                    coefficientMatrix[i][j].setNumerator(new BigInteger(dataCoefficientMatrixRowElement[0]));
                    coefficientMatrix[i][j].setDenominator(new BigInteger(dataCoefficientMatrixRowElement[1]));
                }
                else {
                    coefficientMatrix[i][j].setNumerator(new BigInteger(dataCoefficientMatrixRowElement[0]));
                }
            }
        }

        Fraction[] bVector = new Fraction[equationCount];
        for (int i = 0; i < equationCount; i++){
            String[] fraction = dataBVector[i].split("/");
            bVector[i] = new Fraction();
            if (fraction.length == 2) {
                bVector[i].setNumerator(new BigInteger(fraction[0]));
                bVector[i].setDenominator(new BigInteger(fraction[1]));
            }
            else {
                bVector[i].setNumerator(new BigInteger(fraction[0]));
            }
        }

        EQUATION[] equations = new EQUATION[equationCount];
        for (int i = 0; i < equationCount; i++){
            if ("-1".equals(dataEquations[i])){
                equations[i] = EQUATION.GREATER_THAN_OR_EQUAL;
            }
            else if ("0".equals(dataEquations[i])){
                equations[i] = EQUATION.EQUAL;
            }
            else {
                equations[i] = EQUATION.LESS_THAN_OR_EQUAL;
            }
        }

        OBJECTIVE objective;
        if ("1".equals(o)){
            objective = OBJECTIVE.MAX;
        }
        else {
            objective = OBJECTIVE.MIN;
        }

        CONSTRAINT[] constraints = new CONSTRAINT[variableCount];
        for (int i = 0; i < variableCount; i++){
            if ("-1".equals(dataConstraints[i])){
                constraints[i] = CONSTRAINT.GREATER_THAN_OR_EQUAL_TO_ZERO;
            }
            else if ("0".equals(dataConstraints[i])){
                constraints[i] = CONSTRAINT.LESS_THAN_OT_EQUAL_TO_ZERO;
            }
            else {
                constraints[i] = CONSTRAINT.NO_CONSTRAINT;
            }
        }

        boolean[] isInteger = new boolean[variableCount];
        for (int i = 0; i < variableCount; i++){
            isInteger[i] = "1".equals(dataInteger[i]);
        }

        IntegerLinearProgramming integerLinearProgramming = new IntegerLinearProgramming();
        integerLinearProgramming.setValueCoefficients(valueCoefficients);
        integerLinearProgramming.setCoefficientMatrix(coefficientMatrix);
        integerLinearProgramming.setEquations(equations);
        integerLinearProgramming.setBVector(bVector);
        integerLinearProgramming.setObjective(objective);
        integerLinearProgramming.setConstraints(constraints);
        integerLinearProgramming.setIsInteger(isInteger);

        return integerLinearProgramming;
    }

    public OptimalMatching dataToOptimalMatching(String workers, String wc, String works, String em, String objective, String rw){
        OptimalMatching optimalMatching = new OptimalMatching();

        optimalMatching.setWorker(workers.split(","));
        optimalMatching.setWorkerCapacities(Arrays.stream(wc.split(",")).mapToInt(Integer::parseInt).toArray());
        optimalMatching.setWork(works.split(","));
        String[] efficiencyMatrixRow = em.split(";");
        double[][] efficiencyMatrix = new double[efficiencyMatrixRow.length][];
        for (int i = 0; i < efficiencyMatrixRow.length; i++){
            efficiencyMatrix[i] = Arrays.stream(efficiencyMatrixRow[i].split(",")).mapToDouble(Double::parseDouble).toArray();
        }
        optimalMatching.setEfficiencyMatrix(efficiencyMatrix);
        optimalMatching.setEfficiencyMatrix(efficiencyMatrix);
        if ("-1".equals(objective)) {
            optimalMatching.setObjective(OBJECTIVE.MIN);
        }
        else
            optimalMatching.setObjective(OBJECTIVE.MAX);
        optimalMatching.setRemainWork("1".equals(rw));

        return optimalMatching;
    }
}

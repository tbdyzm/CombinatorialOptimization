package com.longmao.solve;

import com.longmao.dto.*;
import com.longmao.enums.CONSTRAINT;
import com.longmao.enums.EQUATION;
import com.longmao.enums.OBJECTIVE;
import com.longmao.model.LinearProgramming;
import com.longmao.model.SimplexTable;
import com.longmao.utils.StandardizeLinearProgramming;
import com.qaprosoft.carina.core.foundation.IAbstractTest;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.math.BigInteger;

/**
 * @Description 单纯形法单元测试
 * @Author zimu young
 * Date 2021/7/26 20:39
 * Version 1.0
 **/
public class SimpleMethodTest implements IAbstractTest {
    DataToEntity dataToEntity;

    @BeforeTest
    public void initialize(){
        dataToEntity = new DataToEntity();
    }

    @Test(dataProvider = "DataProvider")
    @XlsDataSourceParameters(path = "xls/solve/SimplexMethod.xlsx", sheet = "GetIn", dsArgs = "s,infinity,si,expect")
    public void testGetIn(String s, String infinity, String si, String expect){
        String[] dataSigma = s.split(",");

        Fraction[] sigma = new Fraction[dataSigma.length];

        for (int i = 0; i < dataSigma.length; i++){
            sigma[i] = new Fraction();
            String[] finitudeFraction = dataSigma[i].split("/");
            if (finitudeFraction.length == 2) {
                sigma[i].setNumerator(new BigInteger(finitudeFraction[0]));
                sigma[i].setDenominator(new BigInteger(finitudeFraction[1]));
            }
            else {
                sigma[i].setNumerator(new BigInteger(finitudeFraction[0]));
            }
        }

        if ("".equals(infinity)){
            for (int i = 0; i < dataSigma.length; i++){
                sigma[i].setInfinity(false);
            }
        }
        else {
            String[] dataIsInfinity = infinity.split(",");
            String[] dataInfinity = si.split(",");

            for (int i = 0; i < dataIsInfinity.length; i++){
                if ("true".equals(dataIsInfinity[i])) {
                    sigma[i].setInfinity(true);
                    String[] infinityFraction = dataInfinity[i].split("/");
                    if (infinityFraction.length == 2){
                        sigma[i].setInfinityNumerator(new BigInteger(infinityFraction[0]));
                        sigma[i].setInfinityDenominator(new BigInteger(infinityFraction[1]));
                    }
                    else {
                        sigma[i].setInfinityNumerator(new BigInteger(infinityFraction[0]));
                    }
                }
                else {
                    sigma[i].setInfinity(false);
                }
            }
        }

        SimplexTable simplexTable = new SimplexTable();
        simplexTable.setSigma(sigma);

        SimplexMethod simplexMethod = new SimplexMethod();
        simplexMethod.setSimplexTable(simplexTable);
        int in = simplexMethod.getIn();
        Assert.assertEquals(in, Integer.parseInt(expect));
    }

    @Test(dataProvider = "DataProvider")
    @XlsDataSourceParameters(path = "xls/solve/SimplexMethod.xlsx", sheet = "GetOut", dsArgs = "a,b,Xb,expect")
    public void testGetOut(String a, String b, String Xb, String expect){
        int in = 2;
        String[] dataCoefficient = a.split(",");
        String[] dataBVector = b.split(",");
        String[] dataBaseVariables = Xb.split(",");

        Fraction[][] coefficientMatrix = new Fraction[dataCoefficient.length][];
        for (int i = 0; i < coefficientMatrix.length; i++){
            coefficientMatrix[i] = new Fraction[in+1];
            coefficientMatrix[i][in] = new Fraction();

            String[] dataFraction = dataCoefficient[i].split("/");
            if (dataFraction.length == 2) {
                coefficientMatrix[i][in].setNumerator(new BigInteger(dataFraction[0]));
                coefficientMatrix[i][in].setDenominator(new BigInteger(dataFraction[1]));
            }
            else {
                coefficientMatrix[i][in].setNumerator(new BigInteger(dataFraction[0]));
            }
        }

        Fraction[] bVector = new Fraction[dataBVector.length];
        for (int i = 0; i < bVector.length; i++){
            bVector[i] = new Fraction();

            String[] dataFraction = dataBVector[i].split("/");
            if (dataFraction.length == 2) {
                bVector[i].setNumerator(new BigInteger(dataFraction[0]));
                bVector[i].setDenominator(new BigInteger(dataFraction[1]));
            }
            else {
                bVector[i].setNumerator(new BigInteger(dataFraction[0]));
            }
        }

        int[] baseVariables = new int[dataBaseVariables.length];
        for (int i = 0; i < baseVariables.length; i++){
            baseVariables[i] = Integer.parseInt(dataBaseVariables[i]);
        }

        SimplexTable simplexTable = new SimplexTable();
        SimplexMethod simplexMethod = new SimplexMethod();
        simplexTable.setCoefficientMatrix(coefficientMatrix);
        simplexTable.setBVector(bVector);
        simplexTable.setBaseVariables(baseVariables);
        simplexMethod.setSimplexTable(simplexTable);
        int out = simplexMethod.getOut(in);

        Assert.assertEquals(out, Integer.parseInt(expect));
    }

    @Test(dataProvider = "DataProvider")
    @XlsDataSourceParameters(path = "xls/solve/SimplexMethod.xlsx", sheet = "CoefficientToOne", dsArgs = "in_1,out_1,a,b,expectA,expectB")
    public void testCoefficientToOne(String in_1, String out_1, String a, String b, String expectA, String expectB){
        int in = Integer.parseInt(in_1);
        int out = Integer.parseInt(out_1);
        String[] dataCoefficient = a.split(",");
        String[] dataBVector = b.split("/");
        String[] expectCoefficient = expectA.split(",");
        String[] expectBVector = expectB.split("/");

        Fraction[][] coefficientMatrix = new Fraction[out+1][];
        coefficientMatrix[out] = new Fraction[dataCoefficient.length];
        for (int i = 0; i < coefficientMatrix[out].length; i++){
            coefficientMatrix[out][i] = new Fraction();

            String[] dataFraction = dataCoefficient[i].split("/");
            if (dataFraction.length == 2) {
                coefficientMatrix[out][i].setNumerator(new BigInteger(dataFraction[0]));
                coefficientMatrix[out][i].setDenominator(new BigInteger(dataFraction[1]));
            }
            else {
                coefficientMatrix[out][i].setNumerator(new BigInteger(dataFraction[0]));
            }
        }

        Fraction[] bVector = new Fraction[out+1];
        bVector[out] = new Fraction();
        if (dataBVector.length == 2) {
            bVector[out].setNumerator(new BigInteger(dataBVector[0]));
            bVector[out].setDenominator(new BigInteger(dataBVector[1]));
        }
        else {
            bVector[out].setNumerator(new BigInteger(dataBVector[0]));
        }

        SimplexTable simplexTable = new SimplexTable();
        SimplexMethod simplexMethod = new SimplexMethod();
        simplexTable.setCoefficientMatrix(coefficientMatrix);
        simplexMethod.setSimplexTable(simplexTable);
        simplexTable.setBVector(bVector);
        simplexMethod.coefficientToOne(in, out);
        for (int i = 0; i < expectCoefficient.length; i++){
            String[] dataFraction = expectCoefficient[i].split("/");
            if (dataFraction.length == 2){
                Assert.assertEquals(simplexMethod.getSimplexTable().getCoefficientMatrix()[out][i].getNumerator(), new BigInteger(dataFraction[0]));
                Assert.assertEquals(simplexMethod.getSimplexTable().getCoefficientMatrix()[out][i].getDenominator(), new BigInteger(dataFraction[1]));
            }
            else {
                Assert.assertEquals(simplexMethod.getSimplexTable().getCoefficientMatrix()[out][i].getNumerator(), new BigInteger(dataFraction[0]));
            }
        }
        if (expectBVector.length == 2){
            Assert.assertEquals(simplexMethod.getSimplexTable().getBVector()[out].getNumerator(), new BigInteger(expectBVector[0]));
            Assert.assertEquals(simplexMethod.getSimplexTable().getBVector()[out].getDenominator(), new BigInteger(expectBVector[1]));
        }
        else {
            Assert.assertEquals(simplexMethod.getSimplexTable().getBVector()[out].getNumerator(), new BigInteger(expectBVector[0]));
        }
    }

    @Test(dataProvider = "DataProvider")
    @XlsDataSourceParameters(path = "xls/solve/SimplexMethod.xlsx", sheet = "CoefficientToZero", dsArgs = "a,b,in,out,expectA,expectB")
    public void testCoefficientToZero(String a, String b, String in, String out, String expectA, String expectB){
        String[] dataCoefficient = a.split(";");
        String[] dataBVector = b.split(",");
        String[] expectCoefficient = expectA.split(";");
        String[] expectBVector = expectB.split(",");

        int row = dataCoefficient.length;
        Fraction[][] coefficientMatrix = new Fraction[row][];
        for (int i = 0; i < coefficientMatrix.length; i++){
            String[] dataCoefficientRow = dataCoefficient[i].split(",");
            int col = dataCoefficientRow.length;
            coefficientMatrix[i] = new Fraction[col];
            for (int j = 0; j < coefficientMatrix[i].length; j++){
                coefficientMatrix[i][j] = new Fraction();

                String[] dataFraction = dataCoefficientRow[j].split("/");
                if (dataFraction.length == 2) {
                    coefficientMatrix[i][j].setNumerator(new BigInteger(dataFraction[0]));
                    coefficientMatrix[i][j].setDenominator(new BigInteger(dataFraction[1]));
                }
                else {
                    coefficientMatrix[i][j].setNumerator(new BigInteger(dataFraction[0]));
                }
            }
        }

        Fraction[] bVector = new Fraction[row];
        for (int i = 0; i < bVector.length; i++){
            bVector[i] = new Fraction();

            String[] dataFraction = dataBVector[i].split("/");
            if (dataFraction.length == 2) {
                bVector[i].setNumerator(new BigInteger(dataFraction[0]));
                bVector[i].setDenominator(new BigInteger(dataFraction[1]));
            }
            else {
                bVector[i].setNumerator(new BigInteger(dataFraction[0]));
            }
        }

        SimplexTable simplexTable = new SimplexTable();
        SimplexMethod simplexMethod = new SimplexMethod();
        simplexTable.setCoefficientMatrix(coefficientMatrix);
        simplexTable.setBVector(bVector);
        simplexMethod.setSimplexTable(simplexTable);

        simplexMethod.coefficientToOne(Integer.parseInt(in), Integer.parseInt(out));
        simplexMethod.coefficientToZero(Integer.parseInt(in), Integer.parseInt(out));
        for (int i = 0; i < expectCoefficient.length; i++){
            String[] expectCoefficientRow = expectCoefficient[i].split(",");
            for (int j = 0; j < expectCoefficientRow.length; j++){
                String[] dataFraction = expectCoefficientRow[j].split("/");
                if (dataFraction.length == 2){
                    Assert.assertEquals(simplexMethod.getSimplexTable().getCoefficientMatrix()[i][j].getNumerator(), new BigInteger(dataFraction[0]));
                    Assert.assertEquals(simplexMethod.getSimplexTable().getCoefficientMatrix()[i][j].getDenominator(), new BigInteger(dataFraction[1]));
                }
                else {
                    Assert.assertEquals(simplexMethod.getSimplexTable().getCoefficientMatrix()[i][j].getNumerator(), new BigInteger(dataFraction[0]));
                }
            }
        }
        for (int i = 0; i < expectBVector.length; i++){
            String[] dataFraction = expectBVector[i].split("/");
            if (dataFraction.length == 2){
                Assert.assertEquals(simplexMethod.getSimplexTable().getBVector()[i].getNumerator(), new BigInteger(dataFraction[0]));
                Assert.assertEquals(simplexMethod.getSimplexTable().getBVector()[i].getDenominator(), new BigInteger(dataFraction[1]));
            }
            else {
                Assert.assertEquals(simplexMethod.getSimplexTable().getBVector()[i].getNumerator(), new BigInteger(dataFraction[0]));
            }
        }
    }

    @Test(dataProvider = "DataProvider")
    @XlsDataSourceParameters(path = "xls/solve/SimplexMethod.xlsx", sheet = "CalculateSigma", dsArgs = "c,a,Xb,expectSigma")
    public void testCalculateSigma(String c, String a, String Xb, String expectSigma){
        String[] dataValueCoefficients = c.split(",");
        String[] dataCoefficient = a.split(";");
        String[] dataBaseVariables = Xb.split(",");
        String[] dataSigma = expectSigma.split(",");

        Fraction[] valueCoefficients = new Fraction[dataValueCoefficients.length];
        for (int i = 0; i < valueCoefficients.length; i++){
            valueCoefficients[i] = new Fraction();
            if ("-M".equals(dataValueCoefficients[i])){
                valueCoefficients[i].setInfinityNumerator(new BigInteger("-1"));
                valueCoefficients[i].setInfinity(true);
            }
            else {
                String[] dataFraction = dataValueCoefficients[i].split("/");
                if (dataFraction.length == 2) {
                    valueCoefficients[i].setNumerator(new BigInteger(dataFraction[0]));
                    valueCoefficients[i].setDenominator(new BigInteger(dataFraction[1]));
                }
                else {
                    valueCoefficients[i].setNumerator(new BigInteger(dataFraction[0]));
                }
            }
        }

        Fraction[][] coefficientMatrix = new Fraction[dataCoefficient.length][];
        for (int i = 0; i < coefficientMatrix.length; i++){
            String[] dataCoefficientRow = dataCoefficient[i].split(",");
            coefficientMatrix[i] = new Fraction[dataCoefficientRow.length];
            for (int j = 0; j < coefficientMatrix[i].length; j++){
                coefficientMatrix[i][j] = new Fraction();

                String[] dataFraction = dataCoefficientRow[j].split("/");
                if (dataFraction.length == 2) {
                    coefficientMatrix[i][j].setNumerator(new BigInteger(dataFraction[0]));
                    coefficientMatrix[i][j].setDenominator(new BigInteger(dataFraction[1]));
                }
                else {
                    coefficientMatrix[i][j].setNumerator(new BigInteger(dataFraction[0]));
                }
            }
        }

        int[] baseVariables = new int[dataBaseVariables.length];
        for (int i = 0; i < baseVariables.length; i++){
            baseVariables[i] = Integer.parseInt(dataBaseVariables[i]);
        }

        Fraction[] sigma = new Fraction[dataSigma.length];
        for (int i = 0; i < sigma.length; i++){
            sigma[i] = new Fraction();
        }

        SimplexTable simplexTable = new SimplexTable();
        simplexTable.setValueCoefficients(valueCoefficients);
        simplexTable.setCoefficientMatrix(coefficientMatrix);
        simplexTable.setBaseVariables(baseVariables);
        simplexTable.setSigma(sigma);

        SimplexMethod simplexMethod = new SimplexMethod();
        simplexMethod.setSimplexTable(simplexTable);
        simplexMethod.calculateSigma();

        for (int i = 0; i < dataSigma.length; i++){
            int idx = dataSigma[i].indexOf('M');
            int j = 0;
            if (idx != -1) {
                for (int k = idx - 1; k >= 0; k--) {
                    if (dataSigma[i].charAt(k) == '-' || dataSigma[i].charAt(i) == '+') {
                        j = k;
                        break;
                    }
                }
            }
            else j = dataSigma[i].length();
            String sigmaConstant = dataSigma[i].substring(0, j);
            String sigmaInfinity = dataSigma[i].substring(j).replaceAll("M", "");
            if ("-".equals(sigmaInfinity)){
                sigmaInfinity = "-1";
            }
            else if ("+".equals(sigmaInfinity)){
                sigmaInfinity = "+1";
            }
            if (!"".equals(sigmaConstant)){
                String[] dataFraction = sigmaConstant.split("/");
                if (dataFraction.length == 2){
                    Assert.assertEquals(simplexMethod.getSimplexTable().getSigma()[i].getNumerator(), new BigInteger(dataFraction[0]));
                    Assert.assertEquals(simplexMethod.getSimplexTable().getSigma()[i].getDenominator(), new BigInteger(dataFraction[1]));
                }
                else
                    Assert.assertEquals(simplexMethod.getSimplexTable().getSigma()[i].getNumerator(), new BigInteger(dataFraction[0]));
            }
            if (!"".equals(sigmaInfinity)){
                String[] dataFraction = sigmaInfinity.split("/");
                if (dataFraction.length == 2){
                    Assert.assertEquals(simplexMethod.getSimplexTable().getSigma()[i].getInfinityNumerator(), new BigInteger(dataFraction[0]));
                    Assert.assertEquals(simplexMethod.getSimplexTable().getSigma()[i].getInfinityDenominator(), new BigInteger(dataFraction[1]));
                }
                else
                    Assert.assertEquals(simplexMethod.getSimplexTable().getSigma()[i].getInfinityNumerator(), new BigInteger(dataFraction[0]));
            }
        }
    }

    @Test(dataProvider = "DataProvider")
    @XlsDataSourceParameters(path = "xls/solve/SimplexMethod.xlsx", sheet = "GetBaseVariables", dsArgs = "c,a,b,e,o,vc,Xb")
    public void testGetBaseVariables(String c, String a, String b, String e, String o, String vc, String Xb){
        String[] dataValueCoefficients = c.split(",");
        String[] dataCoefficient = a.split(";");
        String[] dataBVector = b.split(",");
        String[] dataEquations = e.split(",");
        String[] dataConstraints = vc.split(",");
        String[] dataBaseVariables = Xb.split(",");

        int variableCount = dataValueCoefficients.length;
        int equationCount = dataEquations.length;

        Fraction[] valueCoefficients = new Fraction[dataValueCoefficients.length];
        for (int i = 0; i < valueCoefficients.length; i++){
            valueCoefficients[i] = new Fraction();
            if ("-M".equals(dataValueCoefficients[i])){
                valueCoefficients[i].setInfinityNumerator(new BigInteger("-1"));
                valueCoefficients[i].setInfinity(true);
            }
            else {
                String[] dataFraction = dataValueCoefficients[i].split("/");
                if (dataFraction.length == 2) {
                    valueCoefficients[i].setNumerator(new BigInteger(dataFraction[0]));
                    valueCoefficients[i].setDenominator(new BigInteger(dataFraction[1]));
                }
                else {
                    valueCoefficients[i].setNumerator(new BigInteger(dataFraction[0]));
                }
            }
        }

        Fraction[][] coefficientMatrix = new Fraction[dataCoefficient.length][];
        for (int i = 0; i < coefficientMatrix.length; i++){
            String[] dataCoefficientRow = dataCoefficient[i].split(",");
            coefficientMatrix[i] = new Fraction[dataCoefficientRow.length];
            for (int j = 0; j < coefficientMatrix[i].length; j++){
                coefficientMatrix[i][j] = new Fraction();

                String[] dataFraction = dataCoefficientRow[j].split("/");
                if (dataFraction.length == 2) {
                    coefficientMatrix[i][j].setNumerator(new BigInteger(dataFraction[0]));
                    coefficientMatrix[i][j].setDenominator(new BigInteger(dataFraction[1]));
                }
                else {
                    coefficientMatrix[i][j].setNumerator(new BigInteger(dataFraction[0]));
                }
            }
        }

        Fraction[] bVector = new Fraction[dataBVector.length];
        for (int i = 0; i < bVector.length; i++){
            bVector[i] = new Fraction();

            String[] dataFraction = dataBVector[i].split("/");
            if (dataFraction.length == 2) {
                bVector[i].setNumerator(new BigInteger(dataFraction[0]));
                bVector[i].setDenominator(new BigInteger(dataFraction[1]));
            }
            else
                bVector[i].setNumerator(new BigInteger(dataFraction[0]));
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
        for (int i = 0; i < dataBaseVariables.length; i++){
            Assert.assertEquals(simplexMethod.getSimplexTable().getBaseVariables()[i], Integer.parseInt(dataBaseVariables[i]));
        }
    }

    @Test(dataProvider = "DataProvider")
    @XlsDataSourceParameters(path = "xls/solve/SimplexMethod.xlsx", sheet = "SimplexMethodPipeline", dsArgs = "c,a,b,e,o,vc,objectiveValue,expectX")
    public void testSimplexMethodPipeline(String c, String a, String b, String e, String o, String vc, String objectiveValue, String expectX){
        LinearProgramming linearProgramming = dataToEntity.dataToLinearProgrammingV1(c, a, b, e, o, vc);

        String[] objectiveValueFraction = objectiveValue.split("/");
        String[] dataVariables = expectX.split(",");

        SimplexMethod simplexMethod = new SimplexMethod();
        Solution solution = simplexMethod.simplexMethodPipeline(linearProgramming);

        if (objectiveValueFraction.length == 2) {
            Assert.assertEquals(solution.getObjectiveValue().getNumerator(), new BigInteger(objectiveValueFraction[0]));
            Assert.assertEquals(solution.getObjectiveValue().getDenominator(), new BigInteger(objectiveValueFraction[1]));
        }
        else {
            Assert.assertEquals(solution.getObjectiveValue().getNumerator(), new BigInteger(objectiveValueFraction[0]));
        }
        Assert.assertEquals(solution.getOptimalSolution().length, dataVariables.length);
        for (int i = 0; i < dataVariables.length; i++){
            String[] dataFraction = dataVariables[i].split("/");
            if (dataFraction.length == 2){
                Assert.assertEquals(solution.getOptimalSolution()[i].getNumerator(), new BigInteger(dataFraction[0]));
                Assert.assertEquals(solution.getOptimalSolution()[i].getDenominator(), new BigInteger(dataFraction[1]));
            }
            else {
                Assert.assertEquals(solution.getOptimalSolution()[i].getNumerator(), new BigInteger(dataFraction[0]));
            }
        }
    }
}

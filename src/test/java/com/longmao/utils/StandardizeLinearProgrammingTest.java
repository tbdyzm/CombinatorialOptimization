package com.longmao.utils;

import com.longmao.model.LinearProgramming;
import com.qaprosoft.carina.core.foundation.IAbstractTest;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.math.BigInteger;

/**
 * @Description 标准化线性规划模型单元测试
 * @Author zimu young
 * Date 2021/7/18 23:00
 * Version 1.0
 **/
public class StandardizeLinearProgrammingTest implements IAbstractTest {
    DataToEntity dataToEntity;

    @BeforeTest
    public void initialize(){
        dataToEntity = new DataToEntity();
    }

    @Test(dataProvider = "DataProvider")
    @XlsDataSourceParameters(path = "xls/utils/StandardizeLinearProgramming.xlsx", sheet = "StandardizeLinearProgrammingV1", dsArgs = "c,a,b,e,o,vc,expectC,expectA,expectB")
    public void testStandardizeLinearProgrammingV1(String c, String a, String b, String e, String o, String vc, String expectC, String expectA, String expectB){
        LinearProgramming linearProgramming = dataToEntity.dataToLinearProgrammingV1(c, a, b, e, o, vc);

        String[] dataValueCoefficients = expectC.split(",");
        String[] dataCoefficientMatrixRow = expectA.split(";");
        String[] dataBVector = expectB.split(",");

        StandardizeLinearProgramming standardizeLinearProgramming = new StandardizeLinearProgramming();
        standardizeLinearProgramming.setLinearProgramming(linearProgramming);
        standardizeLinearProgramming.standardizeLinearProgramming();

        Assert.assertEquals(linearProgramming.getValueCoefficients().length, dataValueCoefficients.length);
        for (int i = 0; i < dataValueCoefficients.length; i++) {
            if (dataValueCoefficients[i].indexOf('M') != -1) {
                Assert.assertEquals(linearProgramming.getValueCoefficients()[i].getInfinityNumerator(), new BigInteger("-1"));
            } else {
                String[] dataFraction = dataValueCoefficients[i].split("/");
                if (dataFraction.length == 2) {
                    Assert.assertEquals(linearProgramming.getValueCoefficients()[i].getNumerator(), new BigInteger(dataFraction[0]));
                    Assert.assertEquals(linearProgramming.getValueCoefficients()[i].getDenominator(), new BigInteger(dataFraction[1]));
                } else
                    Assert.assertEquals(linearProgramming.getValueCoefficients()[i].getNumerator(), new BigInteger(dataFraction[0]));
            }
        }

        Assert.assertEquals(linearProgramming.getCoefficientMatrix().length, dataCoefficientMatrixRow.length);
        for (int i = 0; i < dataCoefficientMatrixRow.length; i++) {
            String[] dataCoefficientMatrixRowElements = dataCoefficientMatrixRow[i].split(",");
            for (int j = 0; j < dataCoefficientMatrixRowElements.length; j++) {
                String[] dataCoefficientMatrixRowElement = dataCoefficientMatrixRowElements[j].split("/");
                if (dataCoefficientMatrixRowElement.length == 2) {
                    Assert.assertEquals(linearProgramming.getCoefficientMatrix()[i][j].getNumerator(), new BigInteger(dataCoefficientMatrixRowElement[0]));
                    Assert.assertEquals(linearProgramming.getCoefficientMatrix()[i][j].getDenominator(), new BigInteger(dataCoefficientMatrixRowElement[1]));
                }
                else {
                    Assert.assertEquals(linearProgramming.getCoefficientMatrix()[i][j].getNumerator(), new BigInteger(dataCoefficientMatrixRowElement[0]));
                }
            }
        }

        Assert.assertEquals(linearProgramming.getBVector().length, dataBVector.length);
        for (int i = 0; i < dataBVector.length; i++){
            String[] dataFraction = dataBVector[i].split("/");
            if (dataFraction.length == 2){
                Assert.assertEquals(linearProgramming.getBVector()[i].getNumerator(), new BigInteger(dataFraction[0]));
                Assert.assertEquals(linearProgramming.getBVector()[i].getDenominator(), new BigInteger(dataFraction[1]));
            }
            else
                Assert.assertEquals(linearProgramming.getBVector()[i].getNumerator(), new BigInteger(dataFraction[0]));
        }
    }

    @Test(dataProvider = "DataProvider")
    @XlsDataSourceParameters(path = "xls/utils/StandardizeLinearProgramming.xlsx", sheet = "StandardizeLinearProgrammingV2", dsArgs = "c,a,b,e,o,vc,expectC,expectA,expectB")
    public void testStandardizeLinearProgrammingV2(String c, String a, String b, String e, String o, String vc, String expectC, String expectA, String expectB){
        LinearProgramming linearProgramming = dataToEntity.dataToLinearProgrammingV1(c, a, b, e, o, vc);

        String[] dataValueCoefficients = expectC.split(",");
        String[] dataCoefficientMatrixRow = expectA.split(";");
        String[] dataBVector = expectB.split(",");

        StandardizeLinearProgramming standardizeLinearProgramming = new StandardizeLinearProgramming();
        standardizeLinearProgramming.setLinearProgramming(linearProgramming);
        standardizeLinearProgramming.standardizeLinearProgrammingDual();

        Assert.assertEquals(linearProgramming.getValueCoefficients().length, dataValueCoefficients.length);
        for (int i = 0; i < dataValueCoefficients.length; i++) {
            String[] dataFraction = dataValueCoefficients[i].split("/");
            if (dataFraction.length == 2) {
                Assert.assertEquals(linearProgramming.getValueCoefficients()[i].getNumerator(), new BigInteger(dataFraction[0]));
                Assert.assertEquals(linearProgramming.getValueCoefficients()[i].getDenominator(), new BigInteger(dataFraction[1]));
            }
            else
                Assert.assertEquals(linearProgramming.getValueCoefficients()[i].getNumerator(), new BigInteger(dataFraction[0]));
        }

        Assert.assertEquals(linearProgramming.getCoefficientMatrix().length, dataCoefficientMatrixRow.length);
        for (int i = 0; i < dataCoefficientMatrixRow.length; i++) {
            String[] dataCoefficientMatrixRowElements = dataCoefficientMatrixRow[i].split(",");
            for (int j = 0; j < dataCoefficientMatrixRowElements.length; j++) {
                String[] dataCoefficientMatrixRowElement = dataCoefficientMatrixRowElements[j].split("/");
                if (dataCoefficientMatrixRowElement.length == 2) {
                    Assert.assertEquals(linearProgramming.getCoefficientMatrix()[i][j].getNumerator(), new BigInteger(dataCoefficientMatrixRowElement[0]));
                    Assert.assertEquals(linearProgramming.getCoefficientMatrix()[i][j].getDenominator(), new BigInteger(dataCoefficientMatrixRowElement[1]));
                } else {
                    Assert.assertEquals(linearProgramming.getCoefficientMatrix()[i][j].getNumerator(), new BigInteger(dataCoefficientMatrixRowElement[0]));
                }
            }
        }

        Assert.assertEquals(linearProgramming.getBVector().length, dataBVector.length);
        for (int i = 0; i < dataBVector.length; i++){
            String[] dataFraction = dataBVector[i].split("/");
            if (dataFraction.length == 2){
                Assert.assertEquals(linearProgramming.getBVector()[i].getNumerator(), new BigInteger(dataFraction[0]));
                Assert.assertEquals(linearProgramming.getBVector()[i].getDenominator(), new BigInteger(dataFraction[1]));
            }
            else
                Assert.assertEquals(linearProgramming.getBVector()[i].getNumerator(), new BigInteger(dataFraction[0]));
        }
    }
}

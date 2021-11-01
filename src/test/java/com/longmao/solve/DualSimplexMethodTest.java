package com.longmao.solve;

import com.longmao.dto.*;
import com.longmao.model.LinearProgramming;
import com.qaprosoft.carina.core.foundation.IAbstractTest;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.math.BigInteger;

/**
 * @Description 对偶单纯形法单元测试
 * @Author zimu young
 * Date 2021/8/3 12:14
 * Version 1.0
 **/
public class DualSimplexMethodTest implements IAbstractTest {
    DataToEntity dataToEntity;

    @BeforeTest
    public void initialize(){
        dataToEntity = new DataToEntity();
    }

    @Test(dataProvider = "DataProvider")
    @XlsDataSourceParameters(path = "xls/solve/DualSimplexMethod.xlsx", sheet = "DualSimplexMethodPipeline", dsArgs = "c,a,b,e,o,vc,objectiveValue,expectX")
    public void testDualSimplexMethodPipeline(String c, String a, String b, String e, String o, String vc, String objectiveValue, String expectX){
        LinearProgramming linearProgramming = dataToEntity.dataToLinearProgrammingV1(c, a, b, e, o, vc);

        String[] objectiveValueFraction = objectiveValue.split("/");
        String[] dataVariables = expectX.split(",");

        DualSimplexMethod dualSimplexMethod = new DualSimplexMethod();
        Solution solution = dualSimplexMethod.dualSimplexMethodPipeline(linearProgramming);

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

package com.longmao.solve;

import com.longmao.dto.*;
import com.longmao.model.IntegerLinearProgramming;
import com.longmao.utils.DataToEntity;
import com.qaprosoft.carina.core.foundation.IAbstractTest;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.math.BigInteger;

/**
 * @Description 分支限界法单元测试
 * @Author zimu young
 * Date 2021/8/18 8:53
 * Version 1.0
 **/
public class BranchAndBoundTest implements IAbstractTest {
    DataToEntity dataToEntity;

    @BeforeTest
    public void initialize(){
        dataToEntity = new DataToEntity();
    }

    @Test(dataProvider = "DataProvider")
    @XlsDataSourceParameters(path = "xls/solve/BranchAndBound.xlsx", sheet = "BranchAndBoundPipeline", dsArgs = "c,a,b,e,o,vc,integer,objectiveValue,expectX")
    public void testBranchAndBoundPipelineV1(String c, String a, String b, String e, String o, String vc, String integer, String objectiveValue,
                                             String expectX){
        IntegerLinearProgramming integerLinearProgramming = dataToEntity.dataToLinearProgrammingV2(c, a, b, e, o, vc, integer);

        String[] objectiveValueFraction = objectiveValue.split("/");
        String[] dataVariables = expectX.split(",");

        BranchAndBound branchAndBound = new BranchAndBound();
        Solution solution = branchAndBound.branchAndBoundPipelineV1(integerLinearProgramming);

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

    @Test(dataProvider = "DataProvider")
    @XlsDataSourceParameters(path = "xls/solve/BranchAndBound.xlsx", sheet = "BranchAndBoundPipeline", dsArgs = "c,a,b,e,o,vc,integer,objectiveValue,expectX")
    public void testBranchAndBoundPipelineV2(String c, String a, String b, String e, String o, String vc, String integer, String objectiveValue,
                                             String expectX){
        IntegerLinearProgramming integerLinearProgramming = dataToEntity.dataToLinearProgrammingV2(c, a, b, e, o, vc, integer);

        String[] objectiveValueFraction = objectiveValue.split("/");
        String[] dataVariables = expectX.split(",");

        BranchAndBound branchAndBound = new BranchAndBound();
        Solution solution = branchAndBound.branchAndBoundPipelineV2(integerLinearProgramming);

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

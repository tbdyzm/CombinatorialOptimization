package com.longmao.utils;

import com.longmao.enums.OBJECTIVE;
import com.longmao.model.OptimalMatching;
import com.qaprosoft.carina.core.foundation.IAbstractTest;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Arrays;

/**
 * @Description 标准化最优化匹配单元测试
 * @Author zimu young
 * Date 2021/9/18 16:07
 * Version 1.0
 **/
public class StandardizeOptimalMatchingTest implements IAbstractTest {
    DataToEntity dataToEntity;

    @BeforeTest
    public void initialize(){
        this.dataToEntity = new DataToEntity();
    }

    @Test(dataProvider = "DataProvider")
    @XlsDataSourceParameters(path = "xls/utils/StandardizeOptimalMatching.xlsx", sheet = "StandardizeObjective", dsArgs = "workers,works,em,objective,expectEM")
    public void testStandardizeObjective(String workers, String works, String em, String objective, String expectEM){
        OptimalMatching optimalMatching = new OptimalMatching();

        optimalMatching.setWorker(workers.split(","));
        optimalMatching.setWork(works.split(","));

        String[] efficiencyMatrixRow = em.split(";");
        double[][] efficiencyMatrix = new double[efficiencyMatrixRow.length][];
        for (int i = 0; i < efficiencyMatrixRow.length; i++){
            efficiencyMatrix[i] = Arrays.stream(efficiencyMatrixRow[i].split(",")).mapToDouble(Double::parseDouble).toArray();
        }
        optimalMatching.setEfficiencyMatrix(efficiencyMatrix);
        if ("-1".equals(objective)) {
            optimalMatching.setObjective(OBJECTIVE.MIN);
        }
        else
            optimalMatching.setObjective(OBJECTIVE.MAX);

        StandardizeOptimalMatching standardizeOptimalMatching = new StandardizeOptimalMatching();

        standardizeOptimalMatching.setOptimalMatching(optimalMatching);
        standardizeOptimalMatching.standardizeObjective();

        String[] expectEfficiencyMatrixRow = expectEM.split(";");
        double[][] expectEfficiencyMatrix = new double[expectEfficiencyMatrixRow.length][];
        for (int i = 0; i < expectEfficiencyMatrixRow.length; i++){
            expectEfficiencyMatrix[i] = Arrays.stream(expectEfficiencyMatrixRow[i].split(",")).mapToDouble(Double::parseDouble).toArray();
        }

        for (int i = 0; i < expectEfficiencyMatrix.length; i++){
            for (int j = 0; j < expectEfficiencyMatrix[i].length; j++){
                Assert.assertEquals(optimalMatching.getEfficiencyMatrix()[i][j], expectEfficiencyMatrix[i][j]);
            }
        }
    }

    @Test(dataProvider = "DataProvider")
    @XlsDataSourceParameters(path = "xls/utils/StandardizeOptimalMatching.xlsx", sheet = "StandardizeEfficientMatrix", dsArgs = "workers,wc,works,em,objective,rw,expectEM,expectMW")
    public void testStandardizeEfficientMatrix(String workers, String wc, String works, String em, String objective, String rw, String expectEM, String expectMW){
        OptimalMatching optimalMatching = this.dataToEntity.dataToOptimalMatching(workers, wc, works, em, objective, rw);
        StandardizeOptimalMatching standardizeOptimalMatching = new StandardizeOptimalMatching();
        standardizeOptimalMatching.setOptimalMatching(optimalMatching);

        standardizeOptimalMatching.initializeMappingWorker();
        standardizeOptimalMatching.standardizeObjective();
        standardizeOptimalMatching.standardizeEfficientMatrix();

        String[] expectEfficiencyMatrixRow = expectEM.split(";");
        double[][] expectEfficiencyMatrix = new double[expectEfficiencyMatrixRow.length][];
        for (int i = 0; i < expectEfficiencyMatrixRow.length; i++){
            expectEfficiencyMatrix[i] = Arrays.stream(expectEfficiencyMatrixRow[i].split(",")).mapToDouble(Double::parseDouble).toArray();
        }

        for (int i = 0; i < expectEfficiencyMatrix.length; i++){
            for (int j = 0; j < expectEfficiencyMatrix[i].length; j++){
                Assert.assertEquals(optimalMatching.getEfficiencyMatrix()[i][j], expectEfficiencyMatrix[i][j]);
            }
        }

        int[] expectMappingWorker = Arrays.stream(expectMW.split(",")).mapToInt(Integer::parseInt).toArray();
        for (int i = 0; i < expectMappingWorker.length; i++){
            Assert.assertEquals(standardizeOptimalMatching.getOptimalMatching().getMappingWorker()[i], expectMappingWorker[i]);
        }
    }
}

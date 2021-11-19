package com.longmao.solve;

import com.longmao.model.OptimalMatching;
import com.longmao.utils.DataToEntity;
import com.longmao.utils.StandardizeOptimalMatching;
import com.qaprosoft.carina.core.foundation.IAbstractTest;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Map;

/**
 * @Description 匈牙利算法单元测试
 * @Author zimu young
 * Date 2021/11/3 22:11
 * Version 1.0
 **/
public class HungarianAlgorithmTest implements IAbstractTest {
    DataToEntity dataToEntity;

    @BeforeTest
    public void initialize(){
        this.dataToEntity = new DataToEntity();
    }

    @Test(dataProvider = "DataProvider")
    @XlsDataSourceParameters(path = "xls/solve/HungarianAlgorithm.xlsx", sheet = "GetRowMinValue", dsArgs = "em,rmv")
    public void testGetRowMinValue(String em, String rmv){
        String[] efficiencyMatrixRow = em.split(";");
        double[][] efficiencyMatrix = new double[efficiencyMatrixRow.length][];
        for (int i = 0; i < efficiencyMatrixRow.length; i++){
            efficiencyMatrix[i] = Arrays.stream(efficiencyMatrixRow[i].split(",")).mapToDouble(Double::parseDouble).toArray();
        }

        OptimalMatching optimalMatching = new OptimalMatching();
        optimalMatching.setEfficiencyMatrix(efficiencyMatrix);

        HungarianAlgorithm hungarianAlgorithm = new HungarianAlgorithm();
        hungarianAlgorithm.setOptimalMatching(optimalMatching);

        double[] rowMinValue = hungarianAlgorithm.getRowMinValue();
        double[] expectRowMinValue = Arrays.stream(rmv.split(",")).mapToDouble(Double::parseDouble).toArray();

        for (int i = 0; i < expectRowMinValue.length; i++){
            Assert.assertEquals(rowMinValue[i], expectRowMinValue[i]);
        }
    }

    @Test(dataProvider = "DataProvider")
    @XlsDataSourceParameters(path = "xls/solve/HungarianAlgorithm.xlsx", sheet = "GetColumnMinValue", dsArgs = "em,cmv")
    public void testGetColumnMinValue(String em, String cmv){
        String[] efficiencyMatrixRow = em.split(";");
        double[][] efficiencyMatrix = new double[efficiencyMatrixRow.length][];
        for (int i = 0; i < efficiencyMatrixRow.length; i++){
            efficiencyMatrix[i] = Arrays.stream(efficiencyMatrixRow[i].split(",")).mapToDouble(Double::parseDouble).toArray();
        }

        OptimalMatching optimalMatching = new OptimalMatching();
        optimalMatching.setEfficiencyMatrix(efficiencyMatrix);

        HungarianAlgorithm hungarianAlgorithm = new HungarianAlgorithm();
        hungarianAlgorithm.setOptimalMatching(optimalMatching);

        hungarianAlgorithm.rowTransformation();

        double[] columnMinValue = hungarianAlgorithm.getColumnMinValue();
        double[] expectColumnMinValue = Arrays.stream(cmv.split(",")).mapToDouble(Double::parseDouble).toArray();

        for (int i = 0; i < expectColumnMinValue.length; i++){
            Assert.assertEquals(columnMinValue[i], expectColumnMinValue[i]);
        }
    }

    @Test(dataProvider = "DataProvider")
    @XlsDataSourceParameters(path = "xls/solve/HungarianAlgorithm.xlsx", sheet = "RowTransformation", dsArgs = "em,expectEM")
    public void testRowTransformation(String em, String expectEM){
        String[] efficiencyMatrixRow = em.split(";");
        double[][] efficiencyMatrix = new double[efficiencyMatrixRow.length][];
        for (int i = 0; i < efficiencyMatrixRow.length; i++){
            efficiencyMatrix[i] = Arrays.stream(efficiencyMatrixRow[i].split(",")).mapToDouble(Double::parseDouble).toArray();
        }

        OptimalMatching optimalMatching = new OptimalMatching();
        optimalMatching.setEfficiencyMatrix(efficiencyMatrix);

        HungarianAlgorithm hungarianAlgorithm = new HungarianAlgorithm();
        hungarianAlgorithm.setOptimalMatching(optimalMatching);

        hungarianAlgorithm.rowTransformation();

        String[] expectEfficiencyMatrixRow = expectEM.split(";");
        double[][] expectEfficiencyMatrix = new double[expectEfficiencyMatrixRow.length][];
        for (int i = 0; i < expectEfficiencyMatrixRow.length; i++){
            expectEfficiencyMatrix[i] = Arrays.stream(expectEfficiencyMatrixRow[i].split(",")).mapToDouble(Double::parseDouble).toArray();
        }

        for (int i = 0; i < expectEfficiencyMatrix.length; i++){
            for (int j = 0; j < expectEfficiencyMatrix[i].length; j++){
                Assert.assertEquals(hungarianAlgorithm.getOptimalMatching().getEfficiencyMatrix()[i][j], expectEfficiencyMatrix[i][j]);
            }
        }
    }

    @Test(dataProvider = "DataProvider")
    @XlsDataSourceParameters(path = "xls/solve/HungarianAlgorithm.xlsx", sheet = "ColumnTransformation", dsArgs = "em,expectEM")
    public void testColumnTransformation(String em, String expectEM){
        String[] efficiencyMatrixRow = em.split(";");
        double[][] efficiencyMatrix = new double[efficiencyMatrixRow.length][];
        for (int i = 0; i < efficiencyMatrixRow.length; i++){
            efficiencyMatrix[i] = Arrays.stream(efficiencyMatrixRow[i].split(",")).mapToDouble(Double::parseDouble).toArray();
        }

        OptimalMatching optimalMatching = new OptimalMatching();
        optimalMatching.setEfficiencyMatrix(efficiencyMatrix);

        HungarianAlgorithm hungarianAlgorithm = new HungarianAlgorithm();
        hungarianAlgorithm.setOptimalMatching(optimalMatching);

        hungarianAlgorithm.rowTransformation();
        hungarianAlgorithm.columnTransformation();

        String[] expectEfficiencyMatrixRow = expectEM.split(";");
        double[][] expectEfficiencyMatrix = new double[expectEfficiencyMatrixRow.length][];
        for (int i = 0; i < expectEfficiencyMatrixRow.length; i++){
            expectEfficiencyMatrix[i] = Arrays.stream(expectEfficiencyMatrixRow[i].split(",")).mapToDouble(Double::parseDouble).toArray();
        }

        for (int i = 0; i < expectEfficiencyMatrix.length; i++){
            for (int j = 0; j < expectEfficiencyMatrix[i].length; j++){
                Assert.assertEquals(hungarianAlgorithm.getOptimalMatching().getEfficiencyMatrix()[i][j], expectEfficiencyMatrix[i][j]);
            }
        }
    }

    @Test(dataProvider = "DataProvider")
    @XlsDataSourceParameters(path = "xls/solve/HungarianAlgorithm.xlsx", sheet = "GetMaximumMatching", dsArgs = "workers,wc,works,em,objective,rw,expectMN,expectMatchedWork")
    public void testGetMaximumMatching(String workers, String wc, String works, String em, String objective, String rw, String expectMN, String expectMatchedWork){
        OptimalMatching optimalMatching = this.dataToEntity.dataToOptimalMatching(workers, wc, works, em, objective, rw);
        StandardizeOptimalMatching standardizeOptimalMatching = new StandardizeOptimalMatching();
        standardizeOptimalMatching.setOptimalMatching(optimalMatching);

        standardizeOptimalMatching.initializeMappingWorker();
        standardizeOptimalMatching.standardizeObjective();
        standardizeOptimalMatching.standardizeEfficientMatrix();

        HungarianAlgorithm hungarianAlgorithm = new HungarianAlgorithm();
        hungarianAlgorithm.setOptimalMatching(standardizeOptimalMatching.getOptimalMatching());
        hungarianAlgorithm.rowTransformation();
        hungarianAlgorithm.columnTransformation();

        int[][] graph = hungarianAlgorithm.efficiencyMatrixToGraph();
        Assert.assertEquals(hungarianAlgorithm.getMaximumMatching(graph), Integer.parseInt(expectMN));

        int[] matchedWork = Arrays.stream(expectMatchedWork.split(",")).mapToInt(Integer::parseInt).toArray();
        for (int i = 0; i < matchedWork.length; i++){
            Assert.assertEquals(hungarianAlgorithm.getOptimalMatching().getMatchedWork()[i], matchedWork[i]);
        }
    }

    @Test(dataProvider = "DataProvider")
    @XlsDataSourceParameters(path = "xls/solve/HungarianAlgorithm.xlsx", sheet = "HungarianPipeline", dsArgs = "workers,wc,works,em,objective,rw,expectSolution")
    public void testHungarianPipeline(String workers, String wc, String works, String em, String objective, String rw, String expectSolution) {
        OptimalMatching optimalMatching = this.dataToEntity.dataToOptimalMatching(workers, wc, works, em, objective, rw);

        HungarianAlgorithm hungarianAlgorithm = new HungarianAlgorithm();
        Map<String, String> solution = hungarianAlgorithm.hungarianPipeline(optimalMatching);

        if (solution == null)
            Assert.assertEquals("", expectSolution);
        else
            Assert.assertEquals(solution.toString(), expectSolution);
    }
}

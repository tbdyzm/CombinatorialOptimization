package com.longmao.solve;

import com.longmao.model.OptimalMatching;
import com.longmao.utils.DataToEntity;
import com.qaprosoft.carina.core.foundation.IAbstractTest;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import org.apache.commons.lang3.SerializationUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * @Description TODO
 * @Author zimu young
 * Date 2021/11/14 22:06
 * Version 1.0
 **/
public class KMAlgorithmTest implements IAbstractTest {
    DataToEntity dataToEntity;

    @BeforeTest
    public void initialize(){
        this.dataToEntity = new DataToEntity();
    }

    @Test(dataProvider = "DataProvider")
    @XlsDataSourceParameters(path = "xls/solve/HungarianAlgorithm.xlsx", sheet = "HungarianPipeline", dsArgs = "workers,wc,works,em,objective,rw,expectSolution")
    public void testKMPipeline(String workers, String wc, String works, String em, String objective, String rw, String expectSolution) {
        OptimalMatching optimalMatching = this.dataToEntity.dataToOptimalMatching(workers, wc, works, em, objective, rw);
        OptimalMatching optimalMatchingKM = SerializationUtils.clone(optimalMatching);

        HungarianAlgorithm hungarianAlgorithm = new HungarianAlgorithm();
        Map<String, String> solution = hungarianAlgorithm.hungarianPipeline(optimalMatching);

        KMAlgorithm kmAlgorithm = new KMAlgorithm();
        Map<String, String> solutionKM = kmAlgorithm.KMPipeline(optimalMatchingKM);

        Assert.assertEquals(solutionKM, solution);
    }
}

package com.longmao.solve;

import com.longmao.model.OptimalMatching;
import com.longmao.utils.StandardizeOptimalMatching;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description KM算法
 * @Author zimu young
 * Date 2021/11/14 15:23
 * Version 1.0
 **/
public class KMAlgorithm extends HungarianAlgorithm{
    // 工人可行顶点标号
    private double[] workers;

    // 工作可行顶点标号
    private double[] works;

    // 记录被匹配的工人
    private boolean[] visitWorker;

    // 记录被匹配的工作
    private boolean[] visitWork;

    double slack = -1;

    /**
     * @title vertexLabel
     * @description 计算效率矩阵的可行顶点标号
     * @author longmao
     * @updateTime 2021/11/14 16:01
     * @throws
     */
    public void initializeVertexLabel(){
        int works = this.getOptimalMatching().getWork().length;
        this.workers = new double[works];
        this.works = new double[works];
        Arrays.fill(this.workers, -1);
        Arrays.fill(this.works, 0);
        for (int i = 0; i < works; i++){
            for (int j = 0; j < works; j++){
                double efficiency = this.getOptimalMatching().getEfficiencyMatrix()[i][j];
                if (efficiency > this.workers[i]){
                    this.workers[i] = efficiency;
                }
            }
        }
    }

    public boolean dfs(int worker){
        this.visitWorker[worker] = true;
        for (int i = 0; i < this.getOptimalMatching().getWork().length; i++){
            // 当前工作已有工人做
            if (this.visitWork[i])
                continue;
            double d = this.workers[worker]+this.works[i] - this.getOptimalMatching().getEfficiencyMatrix()[worker][i];
            // 边权等于顶点标号之和, 即相等子图中的边
            if (d == 0){
                this.visitWork[i] = true;
                int matchedWork = this.getOptimalMatching().getMatchedWork()[i];
                // 尝试为第worker个工人分配第i个工作, 如果当前工作没有工人做或者做当前工作的人可以换一份工作则分配工作i给第worker个工人
                if (matchedWork == -1 || dfs(matchedWork)) {
                    this.getOptimalMatching().getMatchedWork()[i] = i;
                    return true;
                }
            }
            // 非相等子图中的边即M交错树上不在匹配中的边, 记录顶点标号和与边权差值的最小值
            else {
                if (this.slack == -1 || d < slack){
                    this.slack = d;
                }
            }
        }
        return false;
    }

    public Map<String, String> KMPipeline(OptimalMatching optimalMatching){
        StandardizeOptimalMatching standardizeOptimalMatching = new StandardizeOptimalMatching();
        standardizeOptimalMatching.setOptimalMatching(optimalMatching);

        // 标准化目标函数&效率矩阵
        standardizeOptimalMatching.initializeMappingWorker();
        standardizeOptimalMatching.standardizeObjectiveKM();
        standardizeOptimalMatching.standardizeEfficientMatrix();

        this.setOptimalMatching(standardizeOptimalMatching.getOptimalMatching());

        // 初始化可行顶点标号
        this.initializeVertexLabel();
        int works = this.getOptimalMatching().getWork().length;

        this.visitWorker = new boolean[works];
        this.visitWork = new boolean[works];

        int[] matchedWork = new int[works];
        Arrays.fill(matchedWork, -1);
        this.getOptimalMatching().setMatchedWork(matchedWork);

        for (int i = 0; i < works; i++){
            while (true){
                Arrays.fill(this.visitWorker, false);
                Arrays.fill(this.visitWork, false);
                this.slack = -1;
                // 为每个工人都分配了工作时退出循环
                if (dfs(i)) break;
                // 每次得到一个最大匹配时, 已匹配的工人的顶点标号减去slack, 已匹配的工作的顶点标号加上slack
                for (int j = 0; j < works; j++){
                    if (this.visitWorker[j]) this.workers[j] -= this.slack;
                    if (this.visitWork[j]) this.works[j] += this.slack;
                }
            }
        }

        Map<String, String> solution = new LinkedHashMap<>();
        // 匹配工作和工人
        for (int i = 0; i < works; i++){
            int worker_idx = this.getOptimalMatching().getMappingWorker()[this.getOptimalMatching().getMatchedWork()[i]];
            if (worker_idx == -1){
                // 可剩余工作时, 剩余该工作
                if (this.getOptimalMatching().isRemainWork()){
                    continue;
                }
                // 工作不可剩余时, 尝试分配给工作时间最短的工人
                else {
                    double minEfficiency = -1;
                    // 不可将工作分配给不存在的工人, 即this.getOptimalMatching().getMappingWorker()[j]=-1的工人
                    for (int j = 0; j < works && this.getOptimalMatching().getMappingWorker()[j] != -1; j++){
                        double efficiency = this.getOptimalMatching().getEfficiencyMatrix()[j][i];
                        // 工作也不可以分配给不做该工作的工人
                        if ((minEfficiency == -1 || efficiency < minEfficiency) && efficiency != -1){
                            minEfficiency = efficiency;
                            worker_idx = this.getOptimalMatching().getMappingWorker()[j];
                        }
                    }
                    // 无法分配该工作, 即所有工人都不做工作work[i](等容量且无剩余工作才可能出现的情况)
                    if (worker_idx == -1){
                        return null;
                    }
                }
            }
            String worker = this.getOptimalMatching().getWorker()[worker_idx];
            solution.put(this.getOptimalMatching().getWork()[i], worker);
        }

        return solution;
    }
}

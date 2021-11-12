package com.longmao.utils;

import com.longmao.enums.OBJECTIVE;
import com.longmao.model.OptimalMatching;

/**
 * @Description 标准化最优匹配问题
 * @Author zimu young
 * Date 2021/9/14 15:58
 * Version 1.0
 **/
public class StandardizeOptimalMatching {
    private OptimalMatching optimalMatching;

    public void setOptimalMatching(OptimalMatching optimalMatching) {
        this.optimalMatching = optimalMatching;
    }

    public OptimalMatching getOptimalMatching() {
        return optimalMatching;
    }

    public void initializeMappingWorker(){
        int[] mappingWorker = new int[this.optimalMatching.getWorker().length];
        for (int i = 0; i < mappingWorker.length; i++){
            mappingWorker[i] = i;
        }
        this.optimalMatching.setMappingWorker(mappingWorker);
    }

    public void standardizeObjective(){
        if (this.optimalMatching.getObjective().equals(OBJECTIVE.MAX)){
            this.optimalMatching.setObjective(OBJECTIVE.MIN);
            double max = 0.0;
            for (int i = 0; i< this.optimalMatching.getWorker().length; i++){
                for ( int j =0; j < this.optimalMatching.getWork().length; j++){
                    if (this.optimalMatching.getEfficiencyMatrix()[i][j] > max){
                        max = this.optimalMatching.getEfficiencyMatrix()[i][j];
                    }
                }
            }
            for (int i = 0; i< this.optimalMatching.getWorker().length; i++){
                for ( int j =0; j < this.optimalMatching.getWork().length; j++){
                    if (this.optimalMatching.getEfficiencyMatrix()[i][j] != -1){
                        this.optimalMatching.getEfficiencyMatrix()[i][j] = max-this.optimalMatching.getEfficiencyMatrix()[i][j];
                    }
                }
            }
        }
    }

    // 工人数与工作数不对等的时候, 根据工人容量和工作数将效率矩阵变为方阵
    public void standardizeEfficientMatrix() {
        int workers = this.optimalMatching.getWorker().length;
        int works = this.optimalMatching.getWork().length;
        int[] newMappingWorker = new int[works];

        System.arraycopy(this.optimalMatching.getMappingWorker(), 0, newMappingWorker, 0, this.optimalMatching.getWorker().length);

        if (workers < works) {
            double[][] efficiencyMatrix = new double[works][];
            for (int i = 0; i < workers; i++) {
                efficiencyMatrix[i] = new double[works];
                // 复制原始效率矩阵前workers行
                System.arraycopy(this.optimalMatching.getEfficiencyMatrix()[i], 0, efficiencyMatrix[i], 0, works);
            }
            int i = workers;
            // 分配workCapacities[i]工作的工人的效率矩阵行复制workerCapacities[i]-1次
            for (int j = 0; j < this.optimalMatching.getWorkerCapacities().length; j++) {
                newMappingWorker[i] = this.optimalMatching.getMappingWorker()[j];
                for (int k = 1; k < this.optimalMatching.getWorkerCapacities()[j]; k++) {
                    efficiencyMatrix[i] = new double[works];
                    System.arraycopy(efficiencyMatrix[j], 0, efficiencyMatrix[i], 0, works);
                    i += 1;
                }
            }
            // 工人容量和小于工作数, 剩余部分工作
            for (; i < works; i++) {
                efficiencyMatrix[i] = new double[works];
                for (int j = 0; j < works; j++) {
                    efficiencyMatrix[i][j] = 0.0;

                }
                newMappingWorker[i] = -1;
            }
            this.optimalMatching.setEfficiencyMatrix(efficiencyMatrix);
        }

        this.optimalMatching.setMappingWorker(newMappingWorker);
    }
}

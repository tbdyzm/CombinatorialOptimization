package com.longmao.solve;

import com.longmao.model.OptimalMatching;
import com.longmao.utils.StandardizeOptimalMatching;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description 匈牙利算法
 * @Author zimu young
 * Date 2021/9/14 15:41
 * Version 1.0
 **/
public class HungarianAlgorithm {
    private OptimalMatching optimalMatching;

    // 画线时. 记录选择的行
    private boolean[] selectedRow;

    // 画线时, 记录选择的列
    private boolean[] selectedColumn;

    // 记录效率矩阵元素是否被所画直线覆盖
    private boolean[][] selected;

    public void setOptimalMatching(OptimalMatching optimalMatching) {
        this.optimalMatching = optimalMatching;
    }

    public OptimalMatching getOptimalMatching() {
        return optimalMatching;
    }

    public double[] getRowMinValue(){
        double[] rowMinValue = new double[this.optimalMatching.getEfficiencyMatrix().length];
        Arrays.fill(rowMinValue, -1);
        for (int i = 0; i < this.optimalMatching.getEfficiencyMatrix().length; i++){
            for (int j = 0; j < this.optimalMatching.getEfficiencyMatrix()[i].length; j++){
                if (this.optimalMatching.getEfficiencyMatrix()[i][j] < rowMinValue[i] && this.optimalMatching.getEfficiencyMatrix()[i][j] != -1 || rowMinValue[i] == -1){
                    rowMinValue[i] = this.optimalMatching.getEfficiencyMatrix()[i][j];
                }
            }
        }
        return rowMinValue;
    }

    public double[] getColumnMinValue(){
        double[] columnMinValue = new double[this.optimalMatching.getEfficiencyMatrix()[0].length];
        Arrays.fill(columnMinValue, -1);
        for (int i = 0; i < this.optimalMatching.getEfficiencyMatrix().length; i++){
            for (int j = 0; j < this.optimalMatching.getEfficiencyMatrix()[i].length; j++){
                if (this.optimalMatching.getEfficiencyMatrix()[i][j] < columnMinValue[j] && this.optimalMatching.getEfficiencyMatrix()[i][j] != -1 || columnMinValue[j] == -1){
                    columnMinValue[j] = this.optimalMatching.getEfficiencyMatrix()[i][j];
                }
            }
        }
        return columnMinValue;
    }

    /**
     * @title rowTransformation
     * @description 效率矩阵行变换, 每行减去当行的最小值, -1不变
     * @author longmao
     * @updateTime 2021/11/4 12:17
     * @throws
     */
    public void rowTransformation(){
        double[] rowMinValue = this.getRowMinValue();
        for (int i = 0; i < this.optimalMatching.getEfficiencyMatrix().length; i++){
            for (int j = 0; j < this.optimalMatching.getEfficiencyMatrix()[i].length; j++){
                if (this.optimalMatching.getEfficiencyMatrix()[i][j] != -1) {
                    this.optimalMatching.getEfficiencyMatrix()[i][j] -= rowMinValue[i];
                }
            }
        }
    }

    /**
     * @title columnTransformation
     * @description 效率矩阵列变换, 每列减去当列的最小值, -1不变
     * @author longmao
     * @updateTime 2021/11/4 12:24
     * @throws
     */
    public void columnTransformation(){
        double[] columnMinValue = this.getColumnMinValue();
        for (int i = 0; i < this.optimalMatching.getEfficiencyMatrix().length; i++){
            for (int j = 0; j < this.optimalMatching.getEfficiencyMatrix()[i].length; j++){
                if (this.optimalMatching.getEfficiencyMatrix()[i][j] != -1) {
                    this.optimalMatching.getEfficiencyMatrix()[i][j] -= columnMinValue[j];
                }
            }
        }
    }

    /**
     * @title efficiencyMatrixToGraph
     * @description 返回效率矩阵对应的图表示, 以工人和工作为顶点, 0时工人和工作之间连边
     * @author longmao
     * @updateTime 2021/11/4 15:46
     * @return: int[][]
     * @throws
     */
    public int[][] efficiencyMatrixToGraph(){
        int works = this.optimalMatching.getWork().length;
        int[][] graph = new int[works][];
        for (int i = 0; i < works; i++){
            graph[i] = new int[works];
            for (int j = 0; j < works; j++){
                if (this.optimalMatching.getEfficiencyMatrix()[i][j] == 0){
                    graph[i][j] = 1;
                }
                else
                    graph[i][j] = 0;
            }
        }

        return graph;
    }

    public boolean dfs(int worker, int[][] graph, int[] matchedWork, boolean[] usedWork){
        // 为第worker个工人匹配工作, 遍历第worker个工人可做的工作
        for (int i = 0; i < usedWork.length; i++){
            // 第worker个工人可做第i个工作时并且第i个工作没其他人做时, 为第worker个工人分配第i个工作
            if (!usedWork[i] && graph[worker][i] == 1){
                usedWork[i] = true;
                // 第i个工作未被分配时, 分配给第worker个工人; 第i个工作被分配时, 重新为第matchedWork[i]个工人分配工作, i工作分配给第worker个工人
                if (matchedWork[i] == -1 || dfs(matchedWork[i], graph, matchedWork, usedWork)){
                    matchedWork[i] = worker;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @title maximumMatching
     * @description 求效率矩阵对应的图的最大匹配
     * @author longmao
     * @updateTime 2021/11/4 15:49
     * @throws
     */
    public int getMaximumMatching(){
        int works = this.optimalMatching.getWork().length;
        int[][] graph = this.efficiencyMatrixToGraph();
        int[] matchedWork = new int[works]; // matchedWork[i]表示第i个工人分配的工作编号
        int count = 0;
        Arrays.fill(matchedWork, -1);

        /*
         * 假设情侣配对, 女生匹配男生, 每个人优先匹配她的初恋.
         * 当B的初恋1已经被A匹配了, (下面称可与A配对的人除了配对之外的人都是备胎, 即1之外可与A配对的都是备胎)
         * (1)如果A还有备胎, 那么将A的备胎2与A配对, B的初恋1与B配对; (2)否则A与1配对保持不变, B只能选择备胎或者无人可选;
         * 对于(1), 新加入B与1配对, A与1配对修改为A与2配对, 显然加入一个B的结果是新增了一个匹配, 原来是最大匹配现在则还是;
         * 对于(2), A无备胎意味着A只有与1这一种配对可能, B无备胎时意味着B也只有与与1这一种配对可能, A/B只能与1配对, 匹配数不变; B有备胎时, A与1配对, B与备胎配对, 匹配数加1. 两种情况原来是最大匹配现在则还是.
         * 那么问题来了, 与A配对的1是A的初恋吗?不一定!
         * 后匹配者优先匹配初恋(即使匹配了也可能抢过来), 那是不是后匹配的人更容易匹配初恋?
         * 其实先匹配的人也是可能把初恋抢回来的! 考虑A可以匹配的人有1, 2, 3, 结果1被B抢了, A与2匹配了; 这时候来了一个C, 初恋2, C匹配2, 在B有备胎的情况下A是可以抢回1的
         */
        for (int i = 0; i < works; i++){
            boolean[] usedWork = new boolean[works];
            Arrays.fill(usedWork, false);
            if (dfs(i, graph, matchedWork, usedWork))
                count++;
        }
        this.optimalMatching.setMatchedWork(matchedWork);
        return count;
    }

    public int countRowZero(int row){
        int count = 0;
        for (int i = 0; i < this.optimalMatching.getEfficiencyMatrix()[row].length; i++){
            if (this.optimalMatching.getEfficiencyMatrix()[row][i] == 0 && !this.selected[row][i])
                count++;
        }

        return count;
    }

    public int countColumnZero(int column){
        int count = 0;
        for (int i = 0; i < this.optimalMatching.getEfficiencyMatrix().length; i++){
            if (this.optimalMatching.getEfficiencyMatrix()[i][column] == 0 && !this.selected[i][column])
                count++;
        }

        return count;
    }

    public void selectRowAndColumn(){
        int works = this.optimalMatching.getWork().length;
        this.selectedRow = new boolean[works];
        this.selectedColumn = new boolean[works];
        Arrays.fill(this.selectedRow, false);
        Arrays.fill(this.selectedColumn, false);

        this.selected = new boolean[works][];
        for (int i = 0; i < works; i++){
            this.selected[i] = new boolean[works];
            Arrays.fill(this.selected[i], false);
        }

        int minLineCount = 0;// 覆盖所有0元素所需最小直线数
        for (int i = 0; i < works; i++){
            if (this.optimalMatching.getMatchedWork()[i] != -1)
                minLineCount++;
        }

        // 完成minLineCount条直线的选择
        while (minLineCount-->0){
            int[] rowZeroCount = new int[works];
            int[] columnZeroCount = new int[works];
            Arrays.fill(rowZeroCount, 0);
            Arrays.fill(columnZeroCount, 0);

            // 计算每行和每列未被画线的0元素个数
            for (int i = 0; i < works; i++){
                for (int j = 0; j < works; j++){
                    if (this.optimalMatching.getEfficiencyMatrix()[i][j] == 0 && !this.selected[i][j]){
                        rowZeroCount[i]++;
                        columnZeroCount[j]++;
                    }
                }
            }

            int selectedRow = -1; // 被选择的行当且仅当该行0元素个数最少
            int selectedColumn = -1; // 被选择的列当且仅当该列的0元素最少
            int minRowZeroCount = -1;
            int minColumnZeroCount = -1;
            // 计算0元素最少的行和列, 记录最小值
            for (int i = 0; i < works; i++){
                if ((minRowZeroCount == -1 || rowZeroCount[i] < minRowZeroCount) && rowZeroCount[i] != 0){
                    minRowZeroCount = rowZeroCount[i];
                    selectedRow = i;
                }
                if ((minColumnZeroCount == -1 || columnZeroCount[i] < minColumnZeroCount) && columnZeroCount[i] != 0){
                    minColumnZeroCount = columnZeroCount[i];
                    selectedColumn = i;
                }
            }
            // 选择0元素最少的某行, 选择第一次出现的0元素, 并比较该0元素所在行的0元素个数和列的0元素个数, 选择尽可能多的行或列画线覆盖
            if (minRowZeroCount <= minColumnZeroCount){
                int column = -1;
                for (int i = 0; i < works; i++){
                    if (this.optimalMatching.getEfficiencyMatrix()[selectedRow][i] == 0 && !this.selected[selectedRow][i]){
                        column = i;
                        break;
                    }
                }
                if (minRowZeroCount > countColumnZero(column)) {
                    this.selectedRow[selectedRow] = true;
                    Arrays.fill(this.selected[selectedRow], true);
                }
                else {
                    this.selectedColumn[column] = true;
                    for (int i = 0; i < works; i++){
                        this.selected[i][column] = true;
                    }
                }
            }
            // 选择0元素最少的某列, 选择第一次出现的0元素, 并比较该0元素所在行的0元素个数和列的0元素个数, 选择尽可能多的行或列画线覆盖
            else {
                int row = -1;
                for (int i = 0; i < works; i++){
                    if (this.optimalMatching.getEfficiencyMatrix()[i][selectedColumn] == 0 && !this.selected[i][selectedColumn]){
                        row = i;
                        break;
                    }
                }
                if (countRowZero(row) > minColumnZeroCount){
                    this.selectedRow[row] = true;
                    Arrays.fill(this.selected[row], true);
                }
                else {
                    this.selectedColumn[selectedColumn] = true;
                    for (int i = 0; i < works; i++) {
                        this.selected[i][selectedColumn] = true;
                    }
                }
            }
        }
    }

    /**
     * @title getMinValue
     * @description 返回未被画线的元素的最小值
     * @author longmao
     * @updateTime 2021/11/10 21:28
     * @return: double
     * @throws
     */
    public double getMinValue(){
        double min = -1;
        for (int i = 0; i < this.optimalMatching.getEfficiencyMatrix().length; i++) {
            if (!this.selectedRow[i]) {
                for (int j = 0; j < this.optimalMatching.getEfficiencyMatrix()[i].length; j++) {
                    if (!this.selectedColumn[j]) {
                        if (this.optimalMatching.getEfficiencyMatrix()[i][j] < min && this.optimalMatching.getEfficiencyMatrix()[i][j] != -1 || min == -1) {
                            min = this.optimalMatching.getEfficiencyMatrix()[i][j];
                        }
                    }
                }
            }
        }

        return min;
    }

    /**
     * @title efficiencyMatrixTransformation
     * @description 直线相交元素+min, 直线上其它元素不变, 非直线上元素-min
     * @author longmao
     * @param: min
     * @updateTime 2021/11/10 21:36
     * @throws
     */
    public void efficiencyMatrixTransformation(double min){
        for (int i = 0; i < this.optimalMatching.getEfficiencyMatrix().length; i++){
            for (int j = 0; j < this.optimalMatching.getEfficiencyMatrix()[i].length; j++){
                if (this.selectedRow[i] && this.selectedColumn[j] && this.optimalMatching.getEfficiencyMatrix()[i][j] != -1){
                    this.optimalMatching.getEfficiencyMatrix()[i][j] += min;
                }
                else if (!this.selectedRow[i] && !this.selectedColumn[j] && this.optimalMatching.getEfficiencyMatrix()[i][j] != -1){
                    this.optimalMatching.getEfficiencyMatrix()[i][j] -= min;
                }
            }
        }
    }

    public Map<String, String> hungarianPipeline(OptimalMatching optimalMatching){
        this.setOptimalMatching(optimalMatching);
        StandardizeOptimalMatching standardizeOptimalMatching = new StandardizeOptimalMatching();
        standardizeOptimalMatching.setOptimalMatching(this.getOptimalMatching());

        // 标准化目标函数&效率矩阵
        standardizeOptimalMatching.initializeMappingWorker();
        standardizeOptimalMatching.standardizeObjective();
        standardizeOptimalMatching.standardizeEfficientMatrix();

        this.setOptimalMatching(standardizeOptimalMatching.getOptimalMatching());

        // 效率矩阵行&列变换
        this.rowTransformation();
        this.columnTransformation();
        int works = this.optimalMatching.getWork().length;

        while (this.getMaximumMatching() < works){
            // 最大匹配数小于工作数时, 选择覆盖所有0元素的直线
            this.selectRowAndColumn();
            // 计算未被覆盖元素的最小值
            double min = this.getMinValue();
            // 最小值为-1, 即未被直线覆盖的元素全为-1, 由于工人和工作匹配数完不成工作, 又无法再继续匹配, 故问题无解
            if (min == -1){
                return null;
            }
            // 效率矩阵根据min值变换, 重新计算最大匹配
            this.efficiencyMatrixTransformation(min);
        }
        System.out.println(this.optimalMatching);

        Map<String, String> solution = new LinkedHashMap<>();
        // 匹配工作和工人
        for (int i = 0; i < works; i++){
            int worker_idx = this.optimalMatching.getMappingWorker()[this.optimalMatching.getMatchedWork()[i]];
            if (worker_idx == -1){
                // 可剩余工作时, 剩余该工作
                if (this.optimalMatching.isRemainWork()){
                    continue;
                }
                // 工作不可剩余时, 尝试分配给工作时间最短的工人
                else {
                    double minEfficiency = -1;
                    // 不可将工作分配给不存在的工人, 即this.optimalMatching.getMappingWorker()[j]=-1的工人
                    for (int j = 0; j < works && this.optimalMatching.getMappingWorker()[j] != -1; j++){
                        double efficiency = this.optimalMatching.getEfficiencyMatrix()[j][i];
                        // 工作也不可以分配给不做该工作的工人
                        if ((minEfficiency == -1 || efficiency < minEfficiency) && efficiency != -1){
                            minEfficiency = efficiency;
                            worker_idx = this.optimalMatching.getMappingWorker()[j];
                        }
                    }
                    // 无法分配该工作, 即所有工人都不做工作work[i](等容量且无剩余工作才可能出现的情况)
                    if (worker_idx == -1){
                        return null;
                    }
                }
            }
            String worker = this.optimalMatching.getWorker()[worker_idx];
            solution.put(this.optimalMatching.getWork()[i], worker);
        }

        return solution;
    }
}

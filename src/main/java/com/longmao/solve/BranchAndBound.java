package com.longmao.solve;

import com.longmao.dto.Fraction;
import com.longmao.dto.Solution;
import com.longmao.model.IntegerLinearProgramming;
import com.longmao.model.SimplexTable;
import org.apache.commons.lang3.SerializationUtils;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

/**
 * @Description TODO
 * @Author zimu young
 * Date 2021/8/17 20:45
 * Version 1.0
 **/
public class BranchAndBound extends DualSimplexMethod{
    private Fraction bound = new Fraction();

    // 待扩展结点队列
    private List<SimplexTable> simplexTables = new LinkedList<>();

    public void setSimplexTables(List<SimplexTable> simplexTables) {
        this.simplexTables = simplexTables;
    }


    public List<SimplexTable> getSimplexTables() {
        return simplexTables;
    }

    public void setBound(Fraction bound) {
        this.bound = bound;
    }

    public Fraction getBound() {
        return bound;
    }

    /**
     * @title getBranchIndex
     * @description 为松弛线性规划问题确定分支变量, 返回第一个不满足整数约束条件的变量的索引
     * @author longmao
     * @param: variableCount
     * @param: judgeInteger
     * @updateTime 2021/8/17 21:05
     * @return: int
     * @throws
     */
    public int getBranchIndex(int variableCount, boolean[] judgeInteger){
        int index = -1;
        for (int i = 0; i < this.getSimplexTable().getBVector().length; i++){
            Fraction bVector = SerializationUtils.clone(this.getSimplexTable().getBVector()[i]);
            // 当前基变量非整数且没有超过原始变量数
            if (!bVector.judgeInteger() && this.getSimplexTable().getBaseVariables()[i] < variableCount) {
                // 当前基变量非整数, 而满足整数约束
                if (judgeInteger[this.getSimplexTable().getBaseVariables()[i]]) {
                    if (index == -1){
                        index = i;
                    }
                    else if (this.getSimplexTable().getBaseVariables()[index] > this.getSimplexTable().getBaseVariables()[i]){
                        index = i;
                    }
                }
            }
        }

        return index;
    }

    /**
     * @title branchV1
     * @description 当松弛线性规划的最优解不满足整数线性规划的整数约束条件时, 分支, 在开始计算前的单纯表中加入分支约束条件
     * @author longmao
     * @updateTime 2021/8/17 21:07
     * @throws
     */
    public void branchV1(int branchIndex){
        int row = this.getSimplexTableBak().getCoefficientMatrix().length;
        int col = this.getSimplexTableBak().getCoefficientMatrix()[0].length;

        /*
         * 深复制单纯形表价值系数两份
         * 第一份新增一列, 系数为0
         * 第二份新增两列， 第一列系数为0, 第二列系数为-inf
         */
        Fraction[][] valueCoefficients = new Fraction[2][];
        valueCoefficients[0] = new Fraction[col+1];
        valueCoefficients[1] = new Fraction[col+2];
        for (int i = 0; i < col; i++){
            valueCoefficients[0][i] = SerializationUtils.clone(this.getSimplexTableBak().getValueCoefficients()[i]);
            valueCoefficients[1][i] = SerializationUtils.clone(this.getSimplexTableBak().getValueCoefficients()[i]);
        }
        valueCoefficients[0][col] = new Fraction();
        valueCoefficients[1][col] = new Fraction();
        valueCoefficients[1][col+1] = new Fraction();
        valueCoefficients[1][col+1].setInfinityNumerator(new BigInteger("-1"));
        valueCoefficients[1][col+1].setInfinity(true);

        /*
         * 深复制系数矩阵两份
         * 第一份新增一行一列, 前row行最后一列的系数为0
         * 第二份新增一行两列, 前row行最后两列的系数为0
         */
        Fraction[][][] coefficientMatrix = new Fraction[2][][];
        coefficientMatrix[0] = new Fraction[row+1][];
        coefficientMatrix[1] = new Fraction[row+1][];
        for (int i = 0; i < row; i++){
            coefficientMatrix[0][i] = new Fraction[col+1];
            coefficientMatrix[1][i] = new Fraction[col+2];
            for (int j = 0; j < col; j++){
                coefficientMatrix[0][i][j] = SerializationUtils.clone(this.getSimplexTableBak().getCoefficientMatrix()[i][j]);
                coefficientMatrix[1][i][j] = SerializationUtils.clone(this.getSimplexTableBak().getCoefficientMatrix()[i][j]);
            }
            coefficientMatrix[0][i][col] = new Fraction();
            coefficientMatrix[1][i][col] = new Fraction();
            coefficientMatrix[1][i][col+1] = new Fraction();
        }
        coefficientMatrix[0][row] = new Fraction[col+1];
        coefficientMatrix[1][row] = new Fraction[col+2];

        // 深复制b值两份, 每份新增一行b值
        Fraction[][] bVectors = new Fraction[2][];
        bVectors[0] = new Fraction[row+1];
        bVectors[1] = new Fraction[row+1];
        for (int i = 0; i < row; i++){
            bVectors[0][i] = SerializationUtils.clone(this.getSimplexTableBak().getBVector()[i]);
            bVectors[1][i] = SerializationUtils.clone(this.getSimplexTableBak().getBVector()[i]);
        }

        // 复制基变量两份, 每份新增一行
        int[][] baseVariables = new int[2][];
        baseVariables[0] = new int[row+1];
        baseVariables[1] = new int[row+1];
        for (int i = 0; i < row; i++){
            baseVariables[0][i] = this.getSimplexTableBak().getBaseVariables()[i];
            baseVariables[1][i] = this.getSimplexTableBak().getBaseVariables()[i];
        }

        // 深复制检验系数sigma两份, 第一份新增一列, 第二份新增两列
        Fraction[][] sigmas = new Fraction[2][];
        sigmas[0] = new Fraction[col+1];
        sigmas[1] = new Fraction[col+2];
        for (int i = 0; i < col; i++){
            sigmas[0][i] = SerializationUtils.clone(this.getSimplexTableBak().getSigma()[i]);
            sigmas[1][i] = SerializationUtils.clone(this.getSimplexTableBak().getSigma()[i]);
        }
        sigmas[0][col] = new Fraction();
        sigmas[1][col] = new Fraction();
        sigmas[1][col+1] = new Fraction();
        
        /*
         * 根据分支变量的值进行分支
         * 分支变量x[i]=a[i]+b[i], i为分支变量所在单纯形表的行索引, a[i]为整数, b[i]为真分数
         * 分支1在分支线性规划问题的单纯形表中添加x[i]+x[n+1]=a[i]的约束条件(x[i]<=a[i])
         * 分支2在分支线性规划问题的单纯形表中添加x[i]-x[n+1]+x[n+2]=a[i]+1(x[i]>=a[i]+1)
         */
        Fraction one = new Fraction();
        one.setNumerator(BigInteger.ONE);
        for (int i = 0; i < col; i++){
            if (i != this.getSimplexTable().getBaseVariables()[branchIndex]){
                coefficientMatrix[0][row][i] = new Fraction();
                coefficientMatrix[1][row][i] = new Fraction();
            }
            else {
                coefficientMatrix[0][row][i] = SerializationUtils.clone(one);
                coefficientMatrix[1][row][i] = SerializationUtils.clone(one);
            }
        }
        coefficientMatrix[0][row][col] = SerializationUtils.clone(one);
        coefficientMatrix[1][row][col] = new Fraction();
        coefficientMatrix[1][row][col].setNumerator(new BigInteger("-1"));
        coefficientMatrix[1][row][col+1] = SerializationUtils.clone(one);
        
        bVectors[0][row] = SerializationUtils.clone(this.getSimplexTable().getBVector()[branchIndex]).roundDown();
        bVectors[1][row] = SerializationUtils.clone(bVectors[0][row]).fractionAddition(one);
        
        baseVariables[0][row] = col;
        baseVariables[1][row] = col+1;
        
        SimplexTable[] simplexTables = new SimplexTable[2];
        simplexTables[0] = new SimplexTable();
        simplexTables[1] = new SimplexTable();
        simplexTables[0].setValueCoefficients(valueCoefficients[0]);
        simplexTables[0].setCoefficientMatrix(coefficientMatrix[0]);
        simplexTables[0].setBVector(bVectors[0]);
        simplexTables[0].setBaseVariables(baseVariables[0]);
        simplexTables[0].setSigma(sigmas[0]);
        simplexTables[1].setValueCoefficients(valueCoefficients[1]);
        simplexTables[1].setCoefficientMatrix(coefficientMatrix[1]);
        simplexTables[1].setBVector(bVectors[1]);
        simplexTables[1].setBaseVariables(baseVariables[1]);
        simplexTables[1].setSigma(sigmas[1]);

        this.simplexTables.add(simplexTables[0]);
        this.simplexTables.add(simplexTables[1]);
    }

    /**
     * @title branchV2
     * @description 当松弛线性规划的最优解不满足整数线性规划的整数约束条件时, 分支, 在开始计算前的单纯表中加入分支约束条件
     * @author longmao
     * @param: branchIndex
     * @updateTime 2021/8/19 11:32
     * @throws
     */
    public void branchV2(int branchIndex){
        int row = this.getSimplexTableBak().getCoefficientMatrix().length;
        int col = this.getSimplexTableBak().getCoefficientMatrix()[0].length;

        /*
         * 深复制单纯形表价值系数两份
         * 第一份新增一列, 系数为0
         * 第二份新增一列, 系数为0
         */
        Fraction[][] valueCoefficients = new Fraction[2][];
        valueCoefficients[0] = new Fraction[col+1];
        valueCoefficients[1] = new Fraction[col+1];
        for (int i = 0; i < col; i++){
            valueCoefficients[0][i] = SerializationUtils.clone(this.getSimplexTableBak().getValueCoefficients()[i]);
            valueCoefficients[1][i] = SerializationUtils.clone(this.getSimplexTableBak().getValueCoefficients()[i]);
        }
        valueCoefficients[0][col] = new Fraction();
        valueCoefficients[1][col] = new Fraction();

        /*
         * 深复制系数矩阵两份
         * 第一份新增一行一列, 前row行最后一列的系数为0
         * 第二份新增一行一列, 前row行最后一列的系数为0
         */
        Fraction[][][] coefficientMatrix = new Fraction[2][][];
        coefficientMatrix[0] = new Fraction[row+1][];
        coefficientMatrix[1] = new Fraction[row+1][];
        for (int i = 0; i < row; i++){
            coefficientMatrix[0][i] = new Fraction[col+1];
            coefficientMatrix[1][i] = new Fraction[col+1];
            for (int j = 0; j < col; j++){
                coefficientMatrix[0][i][j] = SerializationUtils.clone(this.getSimplexTableBak().getCoefficientMatrix()[i][j]);
                coefficientMatrix[1][i][j] = SerializationUtils.clone(this.getSimplexTableBak().getCoefficientMatrix()[i][j]);
            }
            coefficientMatrix[0][i][col] = new Fraction();
            coefficientMatrix[1][i][col] = new Fraction();
        }
        coefficientMatrix[0][row] = new Fraction[col+1];
        coefficientMatrix[1][row] = new Fraction[col+1];

        // 深复制b值两份, 每份新增一行b值
        Fraction[][] bVectors = new Fraction[2][];
        bVectors[0] = new Fraction[row+1];
        bVectors[1] = new Fraction[row+1];
        for (int i = 0; i < row; i++){
            bVectors[0][i] = SerializationUtils.clone(this.getSimplexTableBak().getBVector()[i]);
            bVectors[1][i] = SerializationUtils.clone(this.getSimplexTableBak().getBVector()[i]);
        }

        // 复制基变量两份, 每份新增一行
        int[][] baseVariables = new int[2][];
        baseVariables[0] = new int[row+1];
        baseVariables[1] = new int[row+1];
        for (int i = 0; i < row; i++){
            baseVariables[0][i] = this.getSimplexTableBak().getBaseVariables()[i];
            baseVariables[1][i] = this.getSimplexTableBak().getBaseVariables()[i];
        }

        // 深复制检验系数sigma两份, 第一份新增一列, 第二份新增两列
        Fraction[][] sigmas = new Fraction[2][];
        sigmas[0] = new Fraction[col+1];
        sigmas[1] = new Fraction[col+1];
        for (int i = 0; i < col; i++){
            sigmas[0][i] = SerializationUtils.clone(this.getSimplexTableBak().getSigma()[i]);
            sigmas[1][i] = SerializationUtils.clone(this.getSimplexTableBak().getSigma()[i]);
        }
        sigmas[0][col] = new Fraction();
        sigmas[1][col] = new Fraction();

        /*
         * 根据分支变量的值进行分支
         * 分支变量x[i]=a[i]+b[i], i为分支变量所在单纯形表的行索引, a[i]为整数, b[i]为真分数
         * 分支1在分支线性规划问题的单纯形表中添加x[i]+x[n+1]=a[i]的约束条件(x[i]<=a[i])
         * 分支2在分支线性规划问题的单纯形表中添加-x[i]+x[n+1]= -a[i]-1(x[i]>=a[i]+1)
         */
        Fraction one = new Fraction();
        one.setNumerator(BigInteger.ONE);
        for (int i = 0; i < col; i++){
            if (i != this.getSimplexTable().getBaseVariables()[branchIndex]){
                coefficientMatrix[0][row][i] = new Fraction();
                coefficientMatrix[1][row][i] = new Fraction();
            }
            else {
                coefficientMatrix[0][row][i] = SerializationUtils.clone(one);
                coefficientMatrix[1][row][i] = SerializationUtils.clone(one).oppositeFraction();
            }
        }
        coefficientMatrix[0][row][col] = SerializationUtils.clone(one);
        coefficientMatrix[1][row][col] = SerializationUtils.clone(one);

        bVectors[0][row] = SerializationUtils.clone(this.getSimplexTable().getBVector()[branchIndex]).roundDown();
        bVectors[1][row] = SerializationUtils.clone(bVectors[0][row]).fractionAddition(one).oppositeFraction();

        baseVariables[0][row] = col;
        baseVariables[1][row] = col;

        SimplexTable[] simplexTables = new SimplexTable[2];
        simplexTables[0] = new SimplexTable();
        simplexTables[1] = new SimplexTable();
        simplexTables[0].setValueCoefficients(valueCoefficients[0]);
        simplexTables[0].setCoefficientMatrix(coefficientMatrix[0]);
        simplexTables[0].setBVector(bVectors[0]);
        simplexTables[0].setBaseVariables(baseVariables[0]);
        simplexTables[0].setSigma(sigmas[0]);
        simplexTables[1].setValueCoefficients(valueCoefficients[1]);
        simplexTables[1].setCoefficientMatrix(coefficientMatrix[1]);
        simplexTables[1].setBVector(bVectors[1]);
        simplexTables[1].setBaseVariables(baseVariables[1]);
        simplexTables[1].setSigma(sigmas[1]);

        this.simplexTables.add(simplexTables[0]);
        this.simplexTables.add(simplexTables[1]);
    }

    public Solution branchAndBoundPipelineV1(IntegerLinearProgramming integerLinearProgramming){
        // 单纯形法求解松弛线性规划问题
        Solution solution = this.simplexMethodPipeline(integerLinearProgramming);
        // 无解时直接退出
        if (solution == null){
            return null;
        }
        // 原始变量个数
        int variableCount = integerLinearProgramming.getOriginalVariableCount();
        // 原始变量整数约束条件
        boolean[] judgeInteger = integerLinearProgramming.getIsInteger();
        int branchIndex = this.getBranchIndex(variableCount, judgeInteger);

        Solution finalSolution = new Solution();
        // 当有基变量不满足整数约束条件时, 分支添加扩展节点
        if (branchIndex != -1){
            this.branchV1(branchIndex);
        }
        else {
            finalSolution = solution;
        }

        // 单纯形法求解扩展节点的松弛线性规划问题, 扩展节点为空时停止计算
        while (this.simplexTables.size() != 0){
            // 扩展节点第一个节点成为活结点
            this.setSimplexTable(SerializationUtils.clone(this.getSimplexTables().get(0)));
            this.setSimplexTableBak(SerializationUtils.clone(this.getSimplexTables().get(0)));
            // 移除第一个扩展节点
            this.simplexTables.remove(0);
            // 重新计算检验系数sigma
            this.calculateSigma();
            // 对当前新的单纯形表使用单纯形法求解
            solution = this.simplexMethod(integerLinearProgramming);
            // 无解时, 停止分支
            if (solution == null){
                continue;
            }
            // 重新计算是否有满足整数约束而取值非整数的基变量
            branchIndex = this.getBranchIndex(variableCount, judgeInteger);
            // 当前线性规划问题的解满足整数约束条件时, 尝试更新界, 并停止分支
            if (branchIndex == -1){
                if (SerializationUtils.clone(this.bound).fractionSubtraction(solution.getObjectiveValue()).getNumerator().compareTo(BigInteger.ZERO) == -1){
                    this.bound = solution.getObjectiveValue();
                    finalSolution = solution;
                }
                continue;
            }
            // 当前线性规划最优解小于界时, 停止分支
            if (SerializationUtils.clone(this.bound).fractionSubtraction(solution.getObjectiveValue()).getNumerator().compareTo(BigInteger.ZERO) == 1){
                continue;
            }
            this.branchV1(branchIndex);
        }

        return finalSolution;
    }

    public Solution branchAndBoundPipelineV2(IntegerLinearProgramming integerLinearProgramming){
        // 对偶单纯形法求解松弛线性规划问题
        Solution solution = this.dualSimplexMethodPipeline(integerLinearProgramming);
        // 无解时直接退出
        if (solution == null){
            return null;
        }
        // 原始变量个数
        int variableCount = integerLinearProgramming.getOriginalVariableCount();
        // 原始变量整数约束条件
        boolean[] judgeInteger = integerLinearProgramming.getIsInteger();
        int branchIndex = this.getBranchIndex(variableCount, judgeInteger);

        Solution finalSolution = new Solution();
        // 当有基变量不满足整数约束条件时, 分支添加扩展节点
        if (branchIndex != -1){
            this.branchV2(branchIndex);
        }
        else {
            finalSolution = solution;
        }

        // 对偶单纯形法求解扩展节点的松弛线性规划问题, 扩展节点为空时停止计算
        while (this.simplexTables.size() != 0){
            // 扩展节点第一个节点成为活结点
            this.setSimplexTable(SerializationUtils.clone(this.getSimplexTables().get(0)));
            this.setSimplexTableBak(SerializationUtils.clone(this.getSimplexTables().get(0)));
            // 移除第一个扩展节点
            this.simplexTables.remove(0);
            // 重新计算检验系数sigma
            this.calculateSigma();
            // 对当前新的单纯形表使用对偶单纯形法求解
            solution = this.dualSimplexMethod(integerLinearProgramming);
            // 无解时, 停止分支
            if (solution == null){
                continue;
            }
            // 重新计算是否有满足整数约束而取值非整数的基变量
            branchIndex = this.getBranchIndex(variableCount, judgeInteger);
            // 当前线性规划问题的解满足整数约束条件时, 尝试更新界, 并停止分支
            if (branchIndex == -1){
                if (SerializationUtils.clone(this.bound).fractionSubtraction(solution.getObjectiveValue()).getNumerator().compareTo(BigInteger.ZERO) == -1){
                    this.bound = solution.getObjectiveValue();
                    finalSolution = solution;
                }
                continue;
            }
            // 当前线性规划最优解小于界时, 停止分支
            if (SerializationUtils.clone(this.bound).fractionSubtraction(solution.getObjectiveValue()).getNumerator().compareTo(BigInteger.ZERO) == 1){
                continue;
            }
            this.branchV2(branchIndex);
        }

        return finalSolution;
    }
}

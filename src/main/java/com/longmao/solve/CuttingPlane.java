package com.longmao.solve;

import com.longmao.dto.Fraction;
import com.longmao.dto.Solution;
import com.longmao.model.IntegerLinearProgramming;
import org.apache.commons.lang3.SerializationUtils;

import java.math.BigInteger;

/**
 * @Description 单纯形法版割平面法&对偶单纯形法版割平面法, 以下提到的带分数定义为, 对于数c=a+b, a为c向下取整, 称a+b为c的带分数表示
 * @Author zimu young
 * Date 2021/8/4 10:34
 * Version 1.0
 **/
public class CuttingPlane extends DualSimplexMethod{
    /**
     * @title getPlaneVariable
     * @description 结束计算时的单纯形表中, 选择取值非整数的基变量中, 带分数形式表示下真分数部分最大的基变量行作为割平面(只关注要求取值为整数的基变量)
     * @author longmao
     * @updateTime 2021/8/4 11:04
     * @return: int
     * @throws
     */
    public int getPlaneIndex(int variableCount, boolean[] judgeInteger){
        int index = -1;
        Fraction maxFraction = new Fraction();
        for (int i = 0; i < this.getSimplexTable().getBVector().length; i++){
            Fraction bVector = SerializationUtils.clone(this.getSimplexTable().getBVector()[i]);
            // 当前基变量非整数且没有超过原始变量数
            if (!bVector.judgeInteger() && this.getSimplexTable().getBaseVariables()[i] < variableCount) {
                // 当前基变量非整数, 而满足整数约束
                if (judgeInteger[this.getSimplexTable().getBaseVariables()[i]]) {
                    Fraction properFraction = bVector.fractionSubtraction(bVector.roundDown());
                    if (SerializationUtils.clone(properFraction).fractionSubtraction(maxFraction).getNumerator().compareTo(BigInteger.ZERO) == 1) {
                        maxFraction = properFraction;
                        index = i;
                    }
                }
            }
        }

        return index;
    }

    /**
     * @title addCuttingPlaneV1 单纯形法版割平面
     * @description 为松弛线性规划模型添加新的约束(即割平面), 更新单纯形表(以大M法的方式在单纯形表中添加新的行), 该单纯形表指的是每次使用单纯形法计算结束后的单纯形表
     * @author longmao
     * @updateTime 2021/8/4 11:24
     * @return:
     * @throws
     */
    public void addCuttingPlaneV1(int planeIndex){
        int row = this.getSimplexTable().getCoefficientMatrix().length;
        int col = this.getSimplexTable().getCoefficientMatrix()[0].length;

        // 深复制单纯形表价值系数, 新增两列， 第一列系数为0, 第二列系数为-inf
        Fraction[] valueCoefficients = new Fraction[col+2];
        for (int i = 0; i < col; i++){
            valueCoefficients[i] = SerializationUtils.clone(this.getSimplexTable().getValueCoefficients()[i]);
        }
        valueCoefficients[col] = new Fraction();
        valueCoefficients[col+1] = new Fraction();
        valueCoefficients[col+1].setInfinityNumerator(new BigInteger("-1"));
        valueCoefficients[col+1].setInfinity(true);

        // 深复制系数矩阵, 新增一行两列, 前row行的最后两列系数为0
        Fraction[][] coefficientMatrix = new Fraction[row+1][];
        for (int i = 0; i < row; i++){
            coefficientMatrix[i] = new Fraction[col+2];
            for (int j = 0; j < col; j++){
                coefficientMatrix[i][j] = SerializationUtils.clone(this.getSimplexTable().getCoefficientMatrix()[i][j]);
            }
            coefficientMatrix[i][col] = new Fraction();
            coefficientMatrix[i][col+1] = new Fraction();
        }
        coefficientMatrix[row] = new Fraction[col+2];

        // 深复制b值, 新增一行b值
        Fraction[] bVector = new Fraction[row+1];
        for (int i = 0; i < row; i++){
            bVector[i] = SerializationUtils.clone(this.getSimplexTable().getBVector()[i]);
        }

        // 复制基变量, 新增一行
        int[] baseVariables = new int[row+1];
        for (int i = 0; i < row; i++){
            baseVariables[i] = this.getSimplexTable().getBaseVariables()[i];
        }

        // 深复制检验系数  sigma, 新增两列
        Fraction[] sigma = new Fraction[col+2];
        for (int i = 0; i < col; i++){
            sigma[i] = SerializationUtils.clone(this.getSimplexTable().getSigma()[i]);
        }
        sigma[col] = new Fraction();
        sigma[col+1] = new Fraction();

        /*
         * 根据选择的基变量确定的平面, 计算新的约束条件
         * 选择的基变量x[i]在单纯形表行的约束条件可表示成x[i]+(a[k]+b[k])x[k]+(a[k+1]+b[k+1])x[k+1]+...+(a[n]+b[n])x[n]=(a[0]+b[0]), k为非基变量起始下标
         * a为非基变量系数向下取整, b为非基变量带分数的真分数部分
         * 移项得x[i]+a[k]x[k]+a[k+1]x[k+1]+...+a[n]x[n]-a[0]=b[0]-b[k]x[k]-b[k+1]x[k+1]-...-b[n]x[n] <= 0
         * b[k]x[k]+b[k+1]x[k+1]+...+b[n]x[n] >= b[0][0为新的约束条件]
         */
        for (int i = 0; i < this.getSimplexTable().getCoefficientMatrix()[planeIndex].length; i++){
            Fraction coefficient = this.getSimplexTable().getCoefficientMatrix()[planeIndex][i];
            if (!coefficient.judgeInteger()){
                coefficientMatrix[row][i] = SerializationUtils.clone(coefficient).fractionSubtraction(coefficient.roundDown());
            }
            else {
                coefficientMatrix[row][i] = new Fraction();
            }
        }
        // 第row+1行第col+1列为-1, 第row+1行第col+2列系数为1
        coefficientMatrix[row][col] = new Fraction();
        coefficientMatrix[row][col].setNumerator(new BigInteger("-1"));
        coefficientMatrix[row][col+1] = new Fraction();
        coefficientMatrix[row][col+1].setNumerator(BigInteger.ONE);

        // b值新增行的值为选择的基变量行b值的带分数表示下真分数部分
        bVector[row] = SerializationUtils.clone(this.getSimplexTable().getBVector()[planeIndex]).fractionSubtraction(this.getSimplexTable().getBVector()[planeIndex].roundDown());

        // 新增基变量值为col+1
        baseVariables[row] = col+1;

        this.getSimplexTable().setValueCoefficients(valueCoefficients);
        this.getSimplexTable().setCoefficientMatrix(coefficientMatrix);
        this.getSimplexTable().setBVector(bVector);
        this.getSimplexTable().setBaseVariables(baseVariables);
        this.getSimplexTable().setSigma(sigma);
    }

    /**
     * @title addCuttingPlaneV2 对偶单纯形法版割平面
     * @description 为松弛线性规划模型添加新的约束(即割平面), 更新单纯形表(以对偶单纯形法方式在单纯形表中添加新的行), 该单纯形表指的是每次使用单纯形法计算结束后的单纯形表
     * @author longmao
     * @updateTime 2021/8/4 11:24
     * @return:
     * @throws
     */
    public void addCuttingPlaneV2(int planeIndex){
        int row = this.getSimplexTable().getCoefficientMatrix().length;
        int col = this.getSimplexTable().getCoefficientMatrix()[0].length;

        // 深复制单纯形表价值系数, 新增一列， 第一列系数为0
        Fraction[] valueCoefficients = new Fraction[col+1];
        for (int i = 0; i < col; i++){
            valueCoefficients[i] = SerializationUtils.clone(this.getSimplexTable().getValueCoefficients()[i]);
        }
        valueCoefficients[col] = new Fraction();

        // 深复制系数矩阵, 新增一行一列, 前row行的最后一列系数为0
        Fraction[][] coefficientMatrix = new Fraction[row+1][];
        for (int i = 0; i < row; i++){
            coefficientMatrix[i] = new Fraction[col+1];
            for (int j = 0; j < col; j++){
                coefficientMatrix[i][j] = SerializationUtils.clone(this.getSimplexTable().getCoefficientMatrix()[i][j]);
            }
            coefficientMatrix[i][col] = new Fraction();
        }
        coefficientMatrix[row] = new Fraction[col+1];

        // 深复制b值, 新增一行b值
        Fraction[] bVector = new Fraction[row+1];
        for (int i = 0; i < row; i++){
            bVector[i] = SerializationUtils.clone(this.getSimplexTable().getBVector()[i]);
        }

        // 复制基变量, 新增一行
        int[] baseVariables = new int[row+1];
        for (int i = 0; i < row; i++){
            baseVariables[i] = this.getSimplexTable().getBaseVariables()[i];
        }

        // 深复制检验系数sigma, 新增一列
        Fraction[] sigma = new Fraction[col+1];
        for (int i = 0; i < col; i++){
            sigma[i] = SerializationUtils.clone(this.getSimplexTable().getSigma()[i]);
        }
        sigma[col] = new Fraction();

        /*
         * 根据选择的基变量确定的平面, 计算新的约束条件
         * 选择的基变量x[i]在单纯形表行的约束条件可表示成x[i]+(a[k]+b[k])x[k]+(a[k+1]+b[k+1])x[k+1]+...+(a[n]+b[n])x[n]=(a[0]+b[0]), k为非基变量起始下标
         * a为非基变量系数向下取整, b为非基变量带分数的真分数部分
         * 移项得x[i]+a[k]x[k]+a[k+1]x[k+1]+...+a[n]x[n]-a[0]=b[0]-b[k]x[k]-b[k+1]x[k+1]-...-b[n]x[n] <= 0
         * -b[k]x[k]-b[k+1]x[k+1]-...-b[n]x[n] <= -b[0]
         * 标准化为-b[k]x[k]-b[k+1]x[k+1]-...-b[n]x[n] + x[n+1]= -b[0]
         */
        for (int i = 0; i < this.getSimplexTable().getCoefficientMatrix()[planeIndex].length; i++){
            Fraction coefficient = this.getSimplexTable().getCoefficientMatrix()[planeIndex][i];
            if (!coefficient.judgeInteger()){
                coefficientMatrix[row][i] = SerializationUtils.clone(coefficient).fractionSubtraction(coefficient.roundDown()).oppositeFraction();
            }
            else {
                coefficientMatrix[row][i] = new Fraction();
            }
        }
        // 第row+1行第col+1列为1
        coefficientMatrix[row][col] = new Fraction();
        coefficientMatrix[row][col].setNumerator(BigInteger.ONE);

        // b值新增行的值为选择的基变量行b值的带分数表示下真分数部分的相反数
        bVector[row] = SerializationUtils.clone(this.getSimplexTable().getBVector()[planeIndex]).fractionSubtraction(this.getSimplexTable().getBVector()[planeIndex].roundDown()).oppositeFraction();

        // 新增基变量值为col
        baseVariables[row] = col;

        this.getSimplexTable().setValueCoefficients(valueCoefficients);
        this.getSimplexTable().setCoefficientMatrix(coefficientMatrix);
        this.getSimplexTable().setBVector(bVector);
        this.getSimplexTable().setBaseVariables(baseVariables);
        this.getSimplexTable().setSigma(sigma);
    }

    /**
     * @title cuttingPlanePipelineV1
     * @description 单纯形法版割平面法
     * @author longmao
     * @param: linearProgramming
     * @updateTime 2021/8/5 18:58
     * @throws
     */
    public Solution cuttingPlanePipelineV1(IntegerLinearProgramming integerLinearProgramming){
        // 单纯形法求解
        Solution solution = this.simplexMethodPipeline(integerLinearProgramming);
        // 无解时直接退出
        if (solution == null){
            return null;
        }
        // 原始变量个数
        int variableCount = integerLinearProgramming.getOriginalVariableCount();
        // 原始变量整数约束条件
        boolean[] judgeInteger = integerLinearProgramming.getIsInteger();
        int planeIndex = this.getPlaneIndex(variableCount, judgeInteger);
        // 最终单纯形表得到的解中, 基变量的取值中, 满足整数约束条件的基变量为整数时停止计算
        while (planeIndex != -1){
            // 以最终单纯形表中, 选择的基变量行的割平面添加新的约束条件, 生成新的单纯形表
            this.addCuttingPlaneV1(planeIndex);
            // 重新计算检验系数sigma
            this.calculateSigma();
            // 对当前新的单纯形表使用单纯形法求解
            solution = this.simplexMethod(integerLinearProgramming);
            // 重新计算是否有满足整数约束而取值非整数的基变量
            planeIndex = this.getPlaneIndex(variableCount, judgeInteger);
        }

        return solution;
    }

    /**
     * @title cuttingPlanePipelineV2
     * @description 对偶单纯形法版割平面法
     * @author longmao
     * @param: linearProgramming
     * @updateTime 2021/8/5 18:58
     * @throws
     */
    public Solution cuttingPlanePipelineV2(IntegerLinearProgramming integerLinearProgramming){
        // 单纯形法求解
        Solution solution = this.simplexMethodPipeline(integerLinearProgramming);
        // 无解时直接退出
        if (solution == null){
            return null;
        }
        // 原始变量个数
        int variableCount = integerLinearProgramming.getOriginalVariableCount();
        // 原始变量整数约束条件
        boolean[] judgeInteger = integerLinearProgramming.getIsInteger();
        int planeIndex = this.getPlaneIndex(variableCount, judgeInteger);
        // 最终单纯形表得到的解中, 基变量的取值中, 满足整数约束条件的基变量为整数时停止计算
        while (planeIndex != -1){
            // 以最终单纯形表中, 选择的基变量行的割平面添加新的约束条件, 生成新的单纯形表
            this.addCuttingPlaneV2(planeIndex);
            // 重新计算检验系数sigma
            this.calculateSigma();
            // 对当前新的单纯形表使用对偶单纯形法求解
            solution = this.dualSimplexMethod(integerLinearProgramming);
            // 重新计算是否有满足整数约束而取值非整数的基变量
            planeIndex = this.getPlaneIndex(variableCount, judgeInteger);
        }

        return solution;
    }
}

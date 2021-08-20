package com.longmao.solve;

import com.longmao.dto.Fraction;
import com.longmao.dto.Solution;
import com.longmao.model.LinearProgramming;
import com.longmao.model.SimplexTable;
import com.longmao.utils.StandardizeLinearProgramming;
import org.apache.commons.lang3.SerializationUtils;

import java.math.BigInteger;

/**
 * @Description TODO
 * @Author zimu young
 * Date 2021/7/14 21:44
 * Version 1.0
 **/
public class SimplexMethod {
    private SimplexTable simplexTable;

    private SimplexTable simplexTableBak;

    public void setSimplexTable(SimplexTable simpleTable) {
        this.simplexTable = simpleTable;
    }

    public SimplexTable getSimplexTable() {
        return simplexTable;
    }

    public void setSimplexTableBak(SimplexTable simplexTableBak) {
        this.simplexTableBak = simplexTableBak;
    }

    public SimplexTable getSimplexTableBak() {
        return simplexTableBak;
    }

    /**
     * @title getIn
     * @description 确定换入变量, 返回sigma[i]>0中最大的sigma(且最先出现)的下标
     * @author longmao
     * @updateTime 2021/7/19 17:41
     * @return: int
     * @throws
     */
    public int getIn(){
        int index = -1;
        Fraction maxSigma = new Fraction();
        // sigma为a+b*inf的形式, b=0时a>0, b>0时a无约束
        for (int i = 0; i < this.simplexTable.getSigma().length; i++){
            Fraction sigma = SerializationUtils.clone(this.simplexTable.getSigma()[i]);
            // b=0, a>0, maxSigma的b1 = 0, a > a1时
            if (sigma.getInfinityNumerator().compareTo(BigInteger.ZERO) == 0){
                if (sigma.getNumerator().compareTo(BigInteger.ZERO) == 1){
                    if (maxSigma.getInfinityNumerator().compareTo(BigInteger.ZERO) == 0){
                        Fraction difference = SerializationUtils.clone(sigma).fractionSubtraction(maxSigma);
                        if (difference.getNumerator().compareTo(BigInteger.ZERO) == 1){
                            maxSigma = SerializationUtils.clone(sigma);
                            index = i;
                        }
                    }
                }
            }
            // b > 0
            else if (sigma.getInfinityNumerator().compareTo(BigInteger.ZERO) == 1){
                // maxSigma中b1 > 0
                if (maxSigma.getInfinityNumerator().compareTo(BigInteger.ZERO) == 1){
                    Fraction difference = SerializationUtils.clone(sigma).fractionSubtraction(maxSigma);
                    // b - b1 > 0
                    if (difference.getInfinityNumerator().compareTo(BigInteger.ZERO) == 1){
                        maxSigma = SerializationUtils.clone(sigma);
                        index = i;
                    }
                    // b - b1 = 0 & a - a1 > 0
                    else if (difference.getInfinityNumerator().compareTo(BigInteger.ZERO) == 0){
                        if (difference.getNumerator().compareTo(BigInteger.ZERO) == 1){
                            maxSigma = SerializationUtils.clone(sigma);
                            index = i;
                        }
                    }
                }
                else {
                    maxSigma = SerializationUtils.clone(sigma);
                    index = i;
                }
            }
        }
        return index;
    }

    /**
     * @title getInBland
     * @description 返回sigma[i]>0中最小的下标, j = min{j|sigma[j]>0}
     * @author longmao
     * @updateTime 2021/8/1 14:30
     * @return: int
     * @throws
     */
    public int getInBland(){
        int index = -1;
        // sigma为a+b*inf的形式, b=0时a>0, b>0时a无约束
        for (int i = 0; i < this.simplexTable.getSigma().length; i++){
            Fraction sigma = this.simplexTable.getSigma()[i];
            // b=0, a>0时
            if (sigma.getInfinityNumerator().compareTo(BigInteger.ZERO) == 0){
                if (sigma.getNumerator().compareTo(BigInteger.ZERO) == 1){
                    index = i;
                    break;
                }
            }
            // b > 0
            else if (sigma.getInfinityNumerator().compareTo(BigInteger.ZERO) == 1){
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * @title getOut
     * @description 确定换出变量, 对于换入变量x[k], 如果a[i][k]>0, 计算theta=b[i]/a[i][k], 返回最小的theta(且基变量下标最小)的行下标
     * @author longmao
     * @param: in 换入变量下标
     * @updateTime 2021/7/19 17:40
     * @return: int
     * @throws
     */
    public int getOut(int in){
        int index = -1;
        Fraction minTheta = new Fraction();
        minTheta.setInfinityNumerator(BigInteger.ONE);
        minTheta.setInfinity(true);
        for (int i = 0; i < this.simplexTable.getCoefficientMatrix().length; i++){
            Fraction coefficient = this.simplexTable.getCoefficientMatrix()[i][in];
            if (coefficient.getNumerator().compareTo(BigInteger.ZERO) == 1){
                Fraction theta = SerializationUtils.clone(this.simplexTable.getBVector()[i]).fractionDivision(coefficient);
                if (minTheta.getInfinityNumerator().compareTo(BigInteger.ZERO) == 1){
                    minTheta = SerializationUtils.clone(theta);
                    index = i;
                }
                if (SerializationUtils.clone(theta).fractionSubtraction(minTheta).getNumerator().compareTo(BigInteger.ZERO) == -1){
                    minTheta = SerializationUtils.clone(theta);
                    index = i;
                }
                if (SerializationUtils.clone(theta).fractionSubtraction(minTheta).getNumerator().compareTo(BigInteger.ZERO) == 0){
                    if (this.simplexTable.getBaseVariables()[i] < this.simplexTable.getBaseVariables()[index]){
                        index = i;
                    }
                }
            }
        }
        return index;
    }

    /**
     * @title coefficientToOne
     * @description 单纯形表中换入变量与换出变量交叉位置变换为1
     * @author longmao
     * @param: in 换入变量下标
     * @param: out 换出变量下标
     * @updateTime 2021/7/19 17:40
     * @throws
     */
    public void coefficientToOne(int in, int out){
        Fraction one = new Fraction();
        one.setNumerator(BigInteger.ONE);

        Fraction[] coefficientRow = this.simplexTable.getCoefficientMatrix()[out];
        Fraction factor = one.fractionDivision(coefficientRow[in]);
        for (int i = 0; i < coefficientRow.length; i++){
            coefficientRow[i] = coefficientRow[i].fractionMultiplication(factor);
        }
        this.simplexTable.getBVector()[out] = this.simplexTable.getBVector()[out].fractionMultiplication(factor);
    }

    /**
     * @title coefficientToZero
     * @description 单纯形表中换入变量对应的列, 除了与换出变量交叉位置为1, 其它位置变为0
     * @author longmao
     * @param: in 换入变量下标
     * @param: out 换出变量下标
     * @updateTime 2021/7/19 17:44
     * @throws
     */
    public void coefficientToZero(int in, int out){
        Fraction[] coefficientRow = new Fraction[this.simplexTable.getCoefficientMatrix()[0].length];
        int i = 0, j;
        /*
         * 将换入变量的列向量部分变为0(即in与out交叉之外的位置), 矩阵行变换
         * 首先计算每行相对于out行的倍数, factor = a[i][in]/a[out][in]
         * 计算行变换系数c[i] = a[out]*factor
         * 计算行变换的结果a[i] = a[i]-c[i]
         */
        for (i = 0; i < this.simplexTable.getCoefficientMatrix().length; i++){
            if (out != i){
                Fraction factor = SerializationUtils.clone(this.simplexTable.getCoefficientMatrix()[i][in]);
                for (j = 0; j < this.simplexTable.getCoefficientMatrix()[out].length; j++){
                    coefficientRow[j] = SerializationUtils.clone(factor).fractionMultiplication(this.simplexTable.getCoefficientMatrix()[out][j]);
                }
                for (j = 0; j <this.simplexTable.getCoefficientMatrix()[i].length; j++){
                    this.simplexTable.getCoefficientMatrix()[i][j].fractionSubtraction(coefficientRow[j]);
                }
                Fraction bVector = SerializationUtils.clone(factor).fractionMultiplication(this.simplexTable.getBVector()[out]);
                this.simplexTable.getBVector()[i].fractionSubtraction(bVector);
            }
        }
    }

    /**
     * @title calculateSigma
     * @description 计算sigma, sigma[i]=c[i]-Sigma{c[j]*a[j][i]}, c[j]为基变量的价值系数
     * @author longmao
     * @updateTime 2021/7/29 18:16
     * @throws
     */
    public void calculateSigma(){
        for (int i = 0; i < this.simplexTable.getValueCoefficients().length; i++){
            this.simplexTable.getSigma()[i] = SerializationUtils.clone(this.simplexTable.getValueCoefficients()[i]);
            for (int j = 0; j < this.simplexTable.getBaseVariables().length; j++){
                this.simplexTable.getSigma()[i].fractionSubtraction(SerializationUtils.clone(this.simplexTable.getValueCoefficients()[this.simplexTable.getBaseVariables()[j]])
                .fractionMultiplication(this.simplexTable.getCoefficientMatrix()[j][i]));
            }
        }
    }

    /**
     * @title getBaseVariables
     * @description 系数矩阵每行, 倒数第一个系数为1的下标列为基变量
     * @author longmao
     * @updateTime 2021/7/31 17:21
     * @throws
     */
    public void getBaseVariables(){
        for (int i = 0; i < this.simplexTable.getCoefficientMatrix().length; i++){
            for (int j = this.simplexTable.getCoefficientMatrix()[i].length-1; j >=0; j--){
                if (this.simplexTable.getCoefficientMatrix()[i][j].getNumerator().compareTo(BigInteger.ONE) == 0){
                    this.simplexTable.getBaseVariables()[i] = j;
                    break;
                }
            }
        }
    }

    /**
     * @title isDegraded
     * @description 基变量取值为0时, 问题是退化的, 单纯形法的换入规则有一定概率造成求解死循环
     * @author longmao
     * @updateTime 2021/8/2 12:04
     * @return: boolean
     * @throws
     */
    public boolean isDegraded(){
        boolean degrade = false;
        for (int i = 0; i < this.simplexTable.getBVector().length; i++){
            if (this.simplexTable.getBVector()[i].getNumerator().compareTo(BigInteger.ZERO) == 0){
                degrade = true;
                break;
            }
        }
        return degrade;
    }

    public Fraction getMaxValue(){
        Fraction maxValue = new Fraction();
        for (int i = 0; i < this.simplexTable.getBaseVariables().length; i++){
            maxValue.fractionAddition(SerializationUtils.clone(this.simplexTable.getValueCoefficients()[this.simplexTable.getBaseVariables()[i]]).fractionMultiplication(this.simplexTable.getBVector()[i]));
        }
        return maxValue;
    }

    /**
     * @title checkSolution
     * @description 检验解, 基变量取值非0且价值系数为-inf是问题是无解的
     * @author longmao
     * @updateTime 2021/8/19 10:50
     * @return: boolean
     * @throws
     */
    public boolean checkSolution(){
        boolean isSolution = true;
        for (int i = 0; i < this.simplexTable.getBaseVariables().length; i++){
            if (this.simplexTable.getValueCoefficients()[this.simplexTable.getBaseVariables()[i]].isInfinity() && this.simplexTable.getBVector()[i].getNumerator().compareTo(BigInteger.ZERO) == 1){
                isSolution = false;
                break;
            }
        }

        return isSolution;
    }

    public void initializeSimplexTable(LinearProgramming linearProgramming){
        // 标准化线性规划模型
        StandardizeLinearProgramming standardizeLinearProgramming = new StandardizeLinearProgramming();
        standardizeLinearProgramming.setLinearProgramming(linearProgramming);
        standardizeLinearProgramming.standardizeLinearProgramming();

        // 初始化单纯形表, 价值系数, 系数矩阵, b值, 基变量和sigma
        this.simplexTable = new SimplexTable();
        this.simplexTable.setValueCoefficients(linearProgramming.getValueCoefficients());
        this.simplexTable.setCoefficientMatrix(linearProgramming.getCoefficientMatrix());
        this.simplexTable.setBVector(linearProgramming.getBVector());

        int[] baseVariables = new int[linearProgramming.getCoefficientMatrix().length];
        Fraction[] sigma = new Fraction[linearProgramming.getValueCoefficients().length];
        for (int i = 0; i < sigma.length; i++){
            sigma[i] = new Fraction();
        }
        this.simplexTable.setBaseVariables(baseVariables);
        this.simplexTable.setSigma(sigma);

        this.getBaseVariables();
        this.calculateSigma();

        this.simplexTableBak = SerializationUtils.clone(this.simplexTable);
    }

    /**
     * @title simplexMethod
     * @description 单纯形法
     * @author longmao
     * @updateTime 2021/7/31 19:09
     * @throws
     */
    public Solution simplexMethod(LinearProgramming linearProgramming){
        int in = 0, out;
        boolean solvable = true;
        boolean degrade;
        while(in != -1){
            //判断问题是否是退化的
            degrade = this.isDegraded();
            // 确定换入变量, 所有检验系数都<=0时停止求解
            if (degrade) {
                in = this.getInBland();
            }
            else {
                in = this.getIn();
            }
            // 所有检验系数sigma<=0
            if (in == -1){
                break;
            }
            // 确定换出变量, 换入变量列系数无>0值时无解退出
            out = this.getOut(in);
            if (out == -1){
                solvable = false;
                break;
            }
            // 换入、换出变量交叉位置变为1，换入变量列其它行变为0
            this.coefficientToOne(in, out);
            this.coefficientToZero(in, out);
            // 换出变量替换为换入变量
            this.simplexTable.getBaseVariables()[out] = in;
            // 重新计算检验系数
            this.calculateSigma();
        }
        if (!checkSolution()){
            solvable = false;
        }

        Solution solution = new Solution();
        // 可解时, 计算最优解和最优值
        if (solvable) {
            // 从单纯形表中获取标准模型的最优解, 包括基变量和非基变量两部分
            Fraction[] optimalSolution = new Fraction[this.simplexTable.getValueCoefficients().length];
            for (int i = 0; i < optimalSolution.length; i++) {
                optimalSolution[i] = new Fraction();
            }
            for (int i = 0; i < this.simplexTable.getBaseVariables().length; i++) {
                optimalSolution[this.simplexTable.getBaseVariables()[i]] = SerializationUtils.clone(this.simplexTable.getBVector()[i]);
            }
            this.simplexTable.setOptimalSolution(optimalSolution);

            // 根据原始模型和标准模型变量映射关系, 计算原始模型的最优解
            Fraction[] newOptimalSolution = new Fraction[linearProgramming.getOriginalVariableCount()];
            for (int i = 0; i < linearProgramming.getOriginalVariableCount(); i++) {
                String[] variables = linearProgramming.getMappingVariables()[i].split("\\+");
                if (variables.length == 1) {
                    int index = Integer.parseInt(variables[0]);
                    if (variables[0].contains("-")) {
                        index = Integer.parseInt(variables[0].replace("-", ""));
                        newOptimalSolution[i] = this.simplexTable.getOptimalSolution()[index].oppositeFraction();
                    } else {
                        newOptimalSolution[i] = this.simplexTable.getOptimalSolution()[index];
                    }
                } else {
                    int index1 = Integer.parseInt(variables[0]);
                    int index2 = Integer.parseInt(variables[1].replace("-", ""));
                    newOptimalSolution[i] = this.simplexTable.getOptimalSolution()[index1].fractionSubtraction(this.simplexTable.getOptimalSolution()[index2]);
                }
            }
            solution.setOptimalSolution(newOptimalSolution);
            // 计算最优值
            solution.setObjectiveValue(getMaxValue());
            if (linearProgramming.isMinToMax()) {
                solution.getObjectiveValue().oppositeFraction();
            }
        }
        else {
            return null;
        }

        return solution;
    }

    public Solution simplexMethodPipeline(LinearProgramming linearProgramming){
        this.initializeSimplexTable(linearProgramming);
        return this.simplexMethod(linearProgramming);
    }
}

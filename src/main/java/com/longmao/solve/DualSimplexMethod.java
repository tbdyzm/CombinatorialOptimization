package com.longmao.solve;

import com.longmao.dto.Fraction;
import com.longmao.dto.Solution;
import com.longmao.model.LinearProgramming;
import com.longmao.model.SimplexTable;
import com.longmao.utils.StandardizeLinearProgramming;
import org.apache.commons.lang3.SerializationUtils;

import java.math.BigInteger;

/**
 * @Description 对偶单纯形法不支持大M法, 即不等式不可以出现等于
 * @Author zimu young
 * Date 2021/8/2 12:20
 * Version 1.0
 **/
public class DualSimplexMethod extends SimplexMethod{
    public int getIn(int out){
        int index = -1;
        Fraction minTheta = new Fraction();
        minTheta.setInfinity(true);
        minTheta.setInfinityNumerator(BigInteger.ONE);
        for (int i = 0; i < this.getSimplexTable().getCoefficientMatrix()[out].length; i++){
            if (this.getSimplexTable().getCoefficientMatrix()[out][i].getNumerator().compareTo(BigInteger.ZERO) == -1){
                Fraction theta = SerializationUtils.clone(this.getSimplexTable().getSigma()[i]).fractionDivision(this.getSimplexTable().getCoefficientMatrix()[out][i]);
                if (minTheta.isInfinity()){
                    minTheta = theta;
                    index = i;
                }
                Fraction difference = SerializationUtils.clone(theta).fractionSubtraction(minTheta);
                if (difference.getNumerator().compareTo(BigInteger.ZERO) == -1){
                    minTheta = theta;
                    index = i;
                }
            }
        }

        return index;
    }

    public int getOut(){
        int index = -1;
        Fraction minBVector = new Fraction();
        for (int i = 0; i < this.getSimplexTable().getBVector().length; i++){
            Fraction bVector = SerializationUtils.clone(this.getSimplexTable().getBVector()[i]);
            if (bVector.getNumerator().compareTo(BigInteger.ZERO) == -1){
                Fraction difference = SerializationUtils.clone(bVector).fractionSubtraction(minBVector);
                if (difference.getNumerator().compareTo(BigInteger.ZERO) == -1){
                    minBVector = bVector;
                    index = i;
                }
            }
        }

        return index;
    }

    public void initializeSimplexTableDual(LinearProgramming linearProgramming){
        // 标准化线性规划模型
        StandardizeLinearProgramming standardizeLinearProgramming = new StandardizeLinearProgramming();
        standardizeLinearProgramming.setLinearProgramming(linearProgramming);
        standardizeLinearProgramming.standardizeLinearProgrammingDual();

        // 初始化单纯形表, 价值系数, 系数矩阵, b值, 基变量和sigma
        SimplexTable simplexTable = new SimplexTable();
        simplexTable.setValueCoefficients(linearProgramming.getValueCoefficients());
        simplexTable.setCoefficientMatrix(linearProgramming.getCoefficientMatrix());
        simplexTable.setBVector(linearProgramming.getBVector());
        this.setSimplexTable(simplexTable);

        int[] baseVariables = new int[linearProgramming.getCoefficientMatrix().length];
        Fraction[] sigma = new Fraction[linearProgramming.getValueCoefficients().length];
        for (int i = 0; i < sigma.length; i++){
            sigma[i] = new Fraction();
        }
        this.getSimplexTable().setBaseVariables(baseVariables);
        this.getSimplexTable().setSigma(sigma);

        this.getBaseVariables();
        this.calculateSigma();

        this.setSimplexTableBak(simplexTable);
    }

    public Solution dualSimplexMethod(LinearProgramming linearProgramming){
        int in, out = 0;
        boolean solvable = true;
        while (out != -1){
            // 确定换出变量, 所有b值>=0时停止求解
            out = this.getOut();
            if (out == -1){
                break;
            }
            // 确定换入变量, 换入变量行无<0值时无解退出
            in = this.getIn(out);
            if (in == -1){
                solvable = false;
                break;
            }
            // 换入、换出变量交叉位置变为1，换入变量列其它行变为0
            this.coefficientToOne(in, out);
            this.coefficientToZero(in, out);
            // 换出变量替换为换入变量
            this.getSimplexTable().getBaseVariables()[out] = in;
            // 重新计算检验系数
            this.calculateSigma();
        }

        // b>=0且sigma<=0停止求解, 否则继续使用单纯形法求解
        in = this.getIn();
        if (in != -1){
            boolean degrade;
            while (in != -1){
                //判断问题是否是退化的
                degrade = this.isDegraded();
                // 确定换入变量, 所有检验系数都<=0时停止求解
                if (degrade) {
                    in = this.getInBland();
                }
                else {
                    in = this.getIn();
                }
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
                this.getSimplexTable().getBaseVariables()[out] = in;
                // 重新计算检验系数
                this.calculateSigma();
            }
        }

        Solution solution = new Solution();
        // 可解时, 计算最优解和最优值
        if (solvable) {
            // 从单纯形表中获取标准模型的最优解, 包括基变量和非基变量两部分
            Fraction[] optimalSolution = new Fraction[this.getSimplexTable().getValueCoefficients().length];
            for (int i = 0; i < optimalSolution.length; i++) {
                optimalSolution[i] = new Fraction();
            }
            for (int i = 0; i < this.getSimplexTable().getBaseVariables().length; i++) {
                optimalSolution[this.getSimplexTable().getBaseVariables()[i]] = SerializationUtils.clone(this.getSimplexTable().getBVector()[i]);
            }
            this.getSimplexTable().setOptimalSolution(optimalSolution);

            // 根据原始模型和标准模型变量映射关系, 计算原始模型的最优解
            Fraction[] newOptimalSolution = new Fraction[linearProgramming.getOriginalVariableCount()];
            for (int i = 0; i < linearProgramming.getOriginalVariableCount(); i++) {
                String[] variables = linearProgramming.getMappingVariables()[i].split("\\+");
                if (variables.length == 1) {
                    int index = Integer.parseInt(variables[0]);
                    if (variables[0].contains("-")) {
                        index = Integer.parseInt(variables[0].replace("-", ""));
                        newOptimalSolution[i] = this.getSimplexTable().getOptimalSolution()[index].oppositeFraction();
                    } else {
                        newOptimalSolution[i] = this.getSimplexTable().getOptimalSolution()[index];
                    }
                } else {
                    int index1 = Integer.parseInt(variables[0]);
                    int index2 = Integer.parseInt(variables[1].replace("-", ""));
                    newOptimalSolution[i] = this.getSimplexTable().getOptimalSolution()[index1].fractionSubtraction(this.getSimplexTable().getOptimalSolution()[index2]);
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
            solution = null;
        }

        return solution;
    }

    public Solution dualSimplexMethodPipeline(LinearProgramming linearProgramming){
        this.initializeSimplexTable(linearProgramming);
        Solution solution = this.dualSimplexMethod(linearProgramming);
        return solution;
    }
}

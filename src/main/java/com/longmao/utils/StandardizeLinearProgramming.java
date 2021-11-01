package com.longmao.utils;

import com.longmao.enums.CONSTRAINT;
import com.longmao.enums.EQUATION;
import com.longmao.dto.Fraction;
import com.longmao.enums.OBJECTIVE;
import com.longmao.model.LinearProgramming;
import org.apache.commons.lang3.SerializationUtils;

import java.math.BigInteger;

/**
 * @Description 标准化线性规划模型(包括标准化目标函数,, 不等式和变量)
 * @Author zimu young
 * Date 2021/7/18 22:53
 * Version 1.0
 **/
public class StandardizeLinearProgramming {
    private LinearProgramming linearProgramming;

    public void setLinearProgramming(LinearProgramming linearProgramming) {
        this.linearProgramming = linearProgramming;
    }

    public void standardizeLinearProgramming(){
        this.standardizeObjective();
        this.standardizeEquations();
        this.standardizeConstraints();
    }

    public void standardizeLinearProgrammingDual(){
        this.standardizeObjective();
        this.standardizeEquationsDual();
        this.standardizeConstraints();
    }

    public void standardizeObjective(){
        // 如果目标函数求最小值, 价值系数变成原来的相反数
        if (this.linearProgramming.getObjective().equals(OBJECTIVE.MIN)){
            this.linearProgramming.setObjective(OBJECTIVE.MAX);
            this.linearProgramming.setMinToMax(true);
            for (int i = 0; i < this.linearProgramming.getValueCoefficients().length; i++){
                this.linearProgramming.getValueCoefficients()[i] = this.linearProgramming.getValueCoefficients()[i].oppositeFraction();
            }
        }
        this.linearProgramming.setOriginalVariableCount(this.linearProgramming.getConstraints().length);
    }

    public void standardizeEquations(){
        int row = this.linearProgramming.getCoefficientMatrix().length;
        int col = this.linearProgramming.getCoefficientMatrix()[0].length;
        int colCount = 0;
        int i, j;

        // 如果b < 0, 不等式左右两边乘-1(系数矩阵和b), 不等式反号(>= to <=, <= to >=)
        for (i = 0; i < this.linearProgramming.getBVector().length; i++){
            if (this.linearProgramming.getBVector()[i].getNumerator().compareTo(BigInteger.ZERO) == -1){
                for (j = 0; j < this.linearProgramming.getCoefficientMatrix()[i].length; j++){
                    this.linearProgramming.getCoefficientMatrix()[i][j].oppositeFraction();
                }
                this.linearProgramming.getBVector()[i].oppositeFraction();
                if (this.linearProgramming.getEquations()[i].equals(EQUATION.LESS_THAN_OR_EQUAL)){
                    this.linearProgramming.getEquations()[i] = EQUATION.GREATER_THAN_OR_EQUAL;
                }
                else if (this.linearProgramming.getEquations()[i].equals(EQUATION.GREATER_THAN_OR_EQUAL)){
                    this.linearProgramming.getEquations()[i] = EQUATION.LESS_THAN_OR_EQUAL;
                }
            }
        }

        // 计算需要新增的变量数
        for (i = 0; i < this.linearProgramming.getEquations().length; i++){
            if (this.linearProgramming.getEquations()[i].equals(EQUATION.LESS_THAN_OR_EQUAL)){
                colCount += 1;
            }
            else if (this.linearProgramming.getEquations()[i].equals(EQUATION.EQUAL)){
                colCount += 1;
            }
            else {
                colCount += 2;
            }
        }


        // 深复制价值系数, 系数矩阵
        Fraction[][] newCoefficientMatrix = new Fraction[row][];
        for (i = 0; i < row; i++){
            newCoefficientMatrix[i] = new Fraction[col+colCount];
            for (j = 0; j < this.linearProgramming.getCoefficientMatrix()[i].length; j++){
                newCoefficientMatrix[i][j] = SerializationUtils.clone(this.linearProgramming.getCoefficientMatrix()[i][j]);
            }
            for (j = 0; j < newCoefficientMatrix[i].length; j++){
                if (newCoefficientMatrix[i][j] == null){
                    newCoefficientMatrix[i][j] = new Fraction();
                }
            }
        }
        Fraction[] newValueCoefficients = new Fraction[col+colCount];
        for (i = 0; i < this.linearProgramming.getValueCoefficients().length; i++){
            newValueCoefficients[i] = SerializationUtils.clone(this.linearProgramming.getValueCoefficients()[i]);
        }
        for (i = 0; i < newValueCoefficients.length; i++){
            if (newValueCoefficients[i] == null){
                newValueCoefficients[i] = new Fraction();
            }
        }
        CONSTRAINT[] newConstraints = new CONSTRAINT[col+colCount];
        for (i = 0; i < this.linearProgramming.getConstraints().length; i++){
            newConstraints[i] = this.linearProgramming.getConstraints()[i];
        }

        /*
         * 如果不等式是<=, 系数矩阵新增一列, 系数为1, 价值系数新增一列, 系数为0, 新增1个变量, 约束条件大于等于0
         * 如果不等式是=, 系数矩阵新增一列, 系数为1, 价值系数新增一列, 系数为负无穷小, 新增1个变量, 约束条件大于等于0
         * 如果不等式是>=, 系数矩阵新增两列, 新增第一列的系数为-1, 第二列系数为1, 价值系数新增两列, 第一列的系数为0, 第二列的系数为负无穷小,
         * 新增2个变量, 约束条件大于等于0
         */
        Fraction one = new Fraction();
        Fraction negativeOne = new Fraction();
        Fraction zero = new Fraction();
        Fraction infinity = new Fraction();
        one.setNumerator(BigInteger.ONE);
        negativeOne.setNumerator(new BigInteger("-1"));
        zero.setNumerator(BigInteger.ZERO);
        infinity.setInfinityNumerator(new BigInteger("-1"));
        infinity.setInfinity(true);
        colCount = 0;

        for (i = 0; i < this.linearProgramming.getEquations().length; i++){
            if (this.linearProgramming.getEquations()[i].equals(EQUATION.LESS_THAN_OR_EQUAL)){
                this.linearProgramming.getEquations()[i] = EQUATION.EQUAL;
                newCoefficientMatrix[i][col+colCount] = one;
                newValueCoefficients[col+colCount] = zero;
                newConstraints[col+colCount] = CONSTRAINT.GREATER_THAN_OR_EQUAL_TO_ZERO;
                colCount += 1;
            }
            else if (this.linearProgramming.getEquations()[i].equals(EQUATION.EQUAL)){
                newCoefficientMatrix[i][col+colCount] = one;
                newValueCoefficients[col+colCount] = infinity;
                newConstraints[col+colCount] = CONSTRAINT.GREATER_THAN_OR_EQUAL_TO_ZERO;
                colCount += 1;
            }
            else {
                this.linearProgramming.getEquations()[i] = EQUATION.EQUAL;
                newCoefficientMatrix[i][col+colCount] = negativeOne;
                newCoefficientMatrix[i][col+colCount+1] = one;
                newValueCoefficients[col+colCount] = zero;
                newValueCoefficients[col+colCount+1] = infinity;
                newConstraints[col+colCount] = CONSTRAINT.GREATER_THAN_OR_EQUAL_TO_ZERO;
                newConstraints[col+colCount+1] = CONSTRAINT.GREATER_THAN_OR_EQUAL_TO_ZERO;
                colCount += 2;
            }
        }
        this.linearProgramming.setValueCoefficients(newValueCoefficients);
        this.linearProgramming.setCoefficientMatrix(newCoefficientMatrix);
        this.linearProgramming.setConstraints(newConstraints);
    }

    public void standardizeEquationsDual(){
        int row = this.linearProgramming.getCoefficientMatrix().length;
        int col = this.linearProgramming.getCoefficientMatrix()[0].length;
        int colCount;
        int i, j;

        // 如果不等号为>=, 不等式左右两边乘-1, 不等号反向
        for (i = 0; i < this.linearProgramming.getEquations().length; i++){
            if (this.linearProgramming.getEquations()[i].equals(EQUATION.GREATER_THAN_OR_EQUAL)){
                for (j = 0; j < this.linearProgramming.getCoefficientMatrix()[i].length; j++){
                    this.linearProgramming.getCoefficientMatrix()[i][j].oppositeFraction();
                }
                this.linearProgramming.getBVector()[i].oppositeFraction();
                this.linearProgramming.getEquations()[i] = EQUATION.LESS_THAN_OR_EQUAL;
            }
        }

        // 计算需要新增的变量数
        colCount = this.linearProgramming.getEquations().length;


        // 深复制价值系数, 系数矩阵
        Fraction[][] newCoefficientMatrix = new Fraction[row][];
        for (i = 0; i < row; i++){
            newCoefficientMatrix[i] = new Fraction[col+colCount];
            for (j = 0; j < this.linearProgramming.getCoefficientMatrix()[i].length; j++){
                newCoefficientMatrix[i][j] = SerializationUtils.clone(this.linearProgramming.getCoefficientMatrix()[i][j]);
            }
            for (j = 0; j < newCoefficientMatrix[i].length; j++){
                if (newCoefficientMatrix[i][j] == null){
                    newCoefficientMatrix[i][j] = new Fraction();
                }
            }
        }
        Fraction[] newValueCoefficients = new Fraction[col+colCount];
        for (i = 0; i < this.linearProgramming.getValueCoefficients().length; i++){
            newValueCoefficients[i] = SerializationUtils.clone(this.linearProgramming.getValueCoefficients()[i]);
        }
        for (i = 0; i < newValueCoefficients.length; i++){
            if (newValueCoefficients[i] == null){
                newValueCoefficients[i] = new Fraction();
            }
        }
        CONSTRAINT[] newConstraints = new CONSTRAINT[col+colCount];
        for (i = 0; i < this.linearProgramming.getConstraints().length; i++){
            newConstraints[i] = this.linearProgramming.getConstraints()[i];
        }

        /*
         * 如果不等式是<=, 系数矩阵新增一列, 系数为1, 价值系数新增一列, 系数为0, 新增1个变量, 约束条件大于等于0
         */
        Fraction one = new Fraction();
        Fraction negativeOne = new Fraction();
        Fraction zero = new Fraction();
        Fraction infinity = new Fraction();
        one.setNumerator(BigInteger.ONE);
        negativeOne.setNumerator(new BigInteger("-1"));
        zero.setNumerator(BigInteger.ZERO);
        infinity.setInfinityNumerator(new BigInteger("-1"));
        infinity.setInfinity(true);
        colCount = 0;

        for (i = 0; i < this.linearProgramming.getEquations().length; i++){
            this.linearProgramming.getEquations()[i] = EQUATION.EQUAL;
            newCoefficientMatrix[i][col+colCount] = one;
            newValueCoefficients[col+colCount] = zero;
            newConstraints[col+colCount] = CONSTRAINT.GREATER_THAN_OR_EQUAL_TO_ZERO;
            colCount += 1;
        }
        this.linearProgramming.setValueCoefficients(newValueCoefficients);
        this.linearProgramming.setCoefficientMatrix(newCoefficientMatrix);
        this.linearProgramming.setConstraints(newConstraints);
    }

    public void standardizeConstraints(){
        int newVariableCount = 0;
        int i, j;

        for (i = 0; i < this.linearProgramming.getConstraints().length; i++){
            if (this.linearProgramming.getConstraints()[i].equals(CONSTRAINT.NO_CONSTRAINT)){
                newVariableCount += 1;
            }
        }

        int row = this.linearProgramming.getCoefficientMatrix().length;
        int col = this.linearProgramming.getCoefficientMatrix()[0].length;

        Fraction[][] newCoefficientMatrix = new Fraction[row][];
        for (i = 0; i < row; i++){
            newCoefficientMatrix[i] = new Fraction[col+newVariableCount];
            for (j = 0; j < this.linearProgramming.getCoefficientMatrix()[i].length; j++){
                newCoefficientMatrix[i][j] = SerializationUtils.clone(this.linearProgramming.getCoefficientMatrix()[i][j]);
            }
            for (j = 0; j < newCoefficientMatrix[i].length; j++){
                if (newCoefficientMatrix[i][j] == null){
                    newCoefficientMatrix[i][j] = new Fraction();
                }
            }
        }
        Fraction[] newValueCoefficients = new Fraction[col+newVariableCount];
        for (i = 0; i < this.linearProgramming.getValueCoefficients().length; i++){
            newValueCoefficients[i] = SerializationUtils.clone(this.linearProgramming.getValueCoefficients()[i]);
        }
        CONSTRAINT[] newConstraints = new CONSTRAINT[col+newVariableCount];
        for (i = 0; i < this.linearProgramming.getConstraints().length; i++){
            newConstraints[i] = this.linearProgramming.getConstraints()[i];
        }

        newVariableCount = 0;
        this.linearProgramming.setMappingVariables(new String[this.linearProgramming.getConstraints().length]);
        for (i = 0; i < this.linearProgramming.getConstraints().length; i++){
            /*
             * if xi <= 0, 遍历所有不等式, xi的系数变为原来的相反数, 价值系数ci变为原来的相反数, xi -> -xi >= 0
             * if xi 无约束, 遍历所有不等式, 新增变量, 新增系数为xi的相反数, 新增价值系数为ci的相反数, xi -> xi - x_(n+1) >= 0
             */
            if (this.linearProgramming.getConstraints()[i].equals(CONSTRAINT.LESS_THAN_OT_EQUAL_TO_ZERO)){
                newConstraints[i] = CONSTRAINT.GREATER_THAN_OR_EQUAL_TO_ZERO;
                for (j = 0; j < this.linearProgramming.getEquations().length; j++){
                    newCoefficientMatrix[j][i] = this.linearProgramming.getCoefficientMatrix()[j][i].oppositeFraction();
                }
                newValueCoefficients[i] = this.linearProgramming.getValueCoefficients()[i].oppositeFraction();
                this.linearProgramming.getMappingVariables()[i] = Integer.toString(i*(-1));
            }
            else if (this.linearProgramming.getConstraints()[i].equals(CONSTRAINT.NO_CONSTRAINT)){
                newConstraints[i] = CONSTRAINT.GREATER_THAN_OR_EQUAL_TO_ZERO;
                newConstraints[col+newVariableCount] = CONSTRAINT.GREATER_THAN_OR_EQUAL_TO_ZERO;
                for (j = 0; j < row; j++){
                    newCoefficientMatrix[j][col+newVariableCount] = this.linearProgramming.getCoefficientMatrix()[j][i].oppositeFraction();
                }
                newValueCoefficients[col+newVariableCount] = this.linearProgramming.getValueCoefficients()[i].oppositeFraction();
                this.linearProgramming.getMappingVariables()[i] = i + "+" + (col+newVariableCount)*(-1);
                newVariableCount += 1;
            }
            else {
                this.linearProgramming.getMappingVariables()[i] = Integer.toString(i);
            }
        }
        this.linearProgramming.setValueCoefficients(newValueCoefficients);
        this.linearProgramming.setCoefficientMatrix(newCoefficientMatrix);
        this.linearProgramming.setConstraints(newConstraints);
    }
}

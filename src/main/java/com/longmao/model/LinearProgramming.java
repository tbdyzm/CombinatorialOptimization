package com.longmao.model;

import com.longmao.dto.CONSTRAINT;
import com.longmao.dto.EQUATION;
import com.longmao.dto.Fraction;
import com.longmao.dto.OBJECTIVE;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.TreeMap;

/**
 * @Description TODO
 * @Author zimu young
 * Date 2021/7/14 21:14
 * Version 1.0
 **/
@Data
@EqualsAndHashCode
public class LinearProgramming implements Serializable {
    // 价值系数
    private Fraction[] valueCoefficients;
    // 系数矩阵
    private Fraction[][] coefficientMatrix;
    // 约束条件, >=, = or <=
    private EQUATION[] equations;
    // b值
    private Fraction[] bVector;
    // 目标max or min
    private OBJECTIVE objective;
    // 变量的约束条件, >=0, <= 0 or no constraint
    private CONSTRAINT[] constraints;
    // 目标函数min2max
    private boolean minToMax;
    // 原始模型和标准模型变量映射
    private String[] mappingVariables;
    // 原始变量数
    private int originalVariableCount;
}

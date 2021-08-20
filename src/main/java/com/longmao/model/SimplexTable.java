package com.longmao.model;

import com.longmao.dto.Fraction;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Description TODO
 * @Author zimu young
 * Date 2021/7/18 21:21
 * Version 1.0
 **/
@Data
@EqualsAndHashCode
public class SimplexTable implements Serializable {
    // 价值系数
    private Fraction[] valueCoefficients;
    // 系数矩阵
    private Fraction[][] coefficientMatrix;
    // b值
    private Fraction[] bVector;
    // 基变量
    private int[] baseVariables;
    // 检验系数
    private Fraction[] sigma;
    // 最优解
    private Fraction[] optimalSolution;
}

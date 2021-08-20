package com.longmao.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description TODO
 * @Author zimu young
 * Date 2021/8/1 10:40
 * Version 1.0
 **/
@Data
@EqualsAndHashCode
public class Solution {
    // 最优解
    Fraction[] optimalSolution;
    // 目标值
    private Fraction objectiveValue;
}

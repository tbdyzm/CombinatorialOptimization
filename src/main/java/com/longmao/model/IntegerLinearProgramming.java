package com.longmao.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description TODO
 * @Author zimu young
 * Date 2021/8/3 14:38
 * Version 1.0
 **/
@Data
@EqualsAndHashCode
public class IntegerLinearProgramming extends LinearProgramming{
    private boolean[] isInteger;
}

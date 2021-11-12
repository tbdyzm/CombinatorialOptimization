package com.longmao.model;

import com.longmao.enums.OBJECTIVE;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 最优匹配的模型定义(最初是指派问题, 工人与工作之间的完美匹配), 996模式与955模式不兼容
 * @Author zimu young
 * Date 2021/9/14 15:29
 * Version 1.0
 **/
@Data
@EqualsAndHashCode
public class OptimalMatching {
    // 工人名称
    private String[] worker;

    /*
     * 工人容量, 表示每个工人必须完成的工作数, 其和不得超过工作数.
     * 等容量且remainWork=False时, 剩余工作分配给用时最少的工人; eg, 5个工人11份工作, 4个工人每人完成2份工作, 剩余1人完成3份工作. workerCapacities=[2]*5&remainWork=false
     * 等容量且remainWork无初值, 可剩余部分工作; eg, 5个工人11份工作, 每个工人完成2份工作, workerCapacities=[2]*5&remainWork=true
     * 不等容量时, worker[i]至少完成workerCapacities[i]份工作, 可剩余部分工作; eg, 5个工人11份工作, workerCapacities=[3,1,1,2,2]&remainWork=true
     */
    private int[] workerCapacities;

    // 工作名称, 一项工作只能被一个人做
    private String[] work;

    // 效率矩阵, efficiencyMatrix[i][j]=-1表示worker[i]不能做work[j], 工人为行工作为列
    private double[][] efficiencyMatrix;

    // 目标
    private OBJECTIVE objective;

    // 是否剩余工作
    private boolean remainWork = true;

    // 工人名映射成对应的索引下标
    private int[] mappingWorker;

    // 工作匹配的
    private int[] matchedWork;
}

package com.family.re.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 奖品池表
 * </p>
 *
 * @author 朱桐山
 * @since 2024-07-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("re_pool")
public class RePool implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    private String title;

    /**
     * 奖品数量
     */
    private Integer countPrize;

    /**
     * 奖励周期：1：日；2：周；3：月；4：年
     */
    private Integer rewardCycle;

    /**
     * 奖励条件（计划达成率：百分比）
     */
    private Double rewardConditions;

    /**
     * 实际达到条件（计划达成率：百分比）
     */
    private Double realityConditions;

    /**
     * 计划开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate starTime;

    /**
     * 计划结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;

    /**
     * 开关状态 ：1为开启；0为关闭（默认为开启）
     */
    private Integer state;

    /**
     * 用户表外键，用户ID（小孩）
     */
    private Long userId;

    /**
     * 用户表外键，创建用户ID（家长）
     */
    private Long createdUserId;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除标记
     */
    private Integer flagDelete;


}

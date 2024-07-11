package com.family.re.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 奖品兑现表
 * </p>
 *
 * @author 朱桐山
 * @since 2024-07-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("re_prize_reach")
public class RePrizeReach implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 奖品池外键，奖品池ID
     */
    private Long poolId;

    /**
     * 奖品池表冗余字段，名称
     */
    private String title;

    /**
     * 奖品池表冗余字段，奖品数量
     */
    private Integer countPrize;

    /**
     * 奖品池表冗余字段，奖励周期：1：日；2：周；3：月；4：年
     */
    private Integer rewardCycle;

    /**
     * 奖品池表冗余字段，奖励条件（计划达成率：百分比）
     */
    private Double rewardConditions;

    /**
     * 实际达到条件（计划达成率：百分比）
     */
    private Double realityConditions;

    /**
     * 奖励周期说明：日期、哪年第几周、哪年月份、年份
     */
    private String intervalDate;

    /**
     * 是否已抽奖：0：否；1：是
     */
    private Integer isLottery;

    /**
     * 奖品池明细表外键，抽中的奖品ID
     */
    private Long poolDetailId;

    /**
     * 奖品池明细表冗余字段，抽中的奖品图标
     */
    private String prizeIco;

    /**
     * 奖品池明细表冗余字段，抽中的奖品名称
     */
    private String prizeName;

    /**
     * 抽奖时间
     */
    private LocalDateTime timeLottery;

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

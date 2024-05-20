package com.family.pl.domain.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 功能：
 * 作者：Name
 * 日期：2024/5/19 18:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddChildTaskVO {

    /**
     * 标题
     */
    private String title;

    /**
     * 备注，包括任务说明和完成任务的备注
     */
    private String notes;

    /**
     * 日期
     */
    private Date taskDate;

    /**
     * 开始时间，允许为空，空值则表示全天
     */
    private Date taskTimeBegin;

    /**
     * 结束时间，允许为空，若开始时间为空，则结束时间不允许非空
     */
    private Date taskTimeEnd;

    /**
     * 重复：0：无；1：每天；2：每月；3：每年；4：工作日；5：法定工作日；6：艾宾浩斯记忆法
     */
    private Integer repeat;

    /**
     * 重复结束时间
     */
    private Date repeatEnd;

    /**
     * 优先级：0：无；1；低；2：中；3：高
     */
    private Integer priority;

    /**
     * 是否完成：0：否；1：是（针对已完成任务）
     */
    private Integer isComplete;

    /**
     * 是否有标签：0：否；1：是
     */
    private Integer isLabel;

    /**
     * 是否设置提醒：0：否；1：是
     */
    private Integer isRemind;

    /**
     * 是否有子任务：0：否；1：是
     */
    private Integer isHaveChild;

    /**
     * 父任务ID，空值则表示一级任务，非空表示是某个任务的子任务
     */
    private Long fatherTaskId;

    /**
     * 完成关联任务ID（已完成的任务才需要设置关联任务ID）
     */
    private Long relatedTaskId;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 标签ID
     */
    private Long labelId;

    /**
     * 标签表冗余字段，标签
     */
    private String labelName;

    /**
     * 类别：1：按（开始）时间提醒；2：按天提醒

     */
    private Object type;

    /**
     * 按时间提前提醒：0：无；1：准时；2：提前5分钟；3：提前30分钟；4：提前1天
     */
    private Object remindByTime;

    /**
     * 按天提前提醒：0：无；1：当天；2：提前1天；3：提前2天；4：提前3天
     */
    private Object remindByDate;

    /**
     * corn字符串
     */
    private String corn;


    private static final long serialVersionUID = 1L;
}
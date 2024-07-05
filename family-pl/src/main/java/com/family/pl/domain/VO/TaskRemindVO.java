package com.family.pl.domain.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能：
 * 作者：Name
 * 日期：2024/7/2 10:55
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class TaskRemindVO
{

    /**
     * 任务ID
     */
    private Long id;

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
}
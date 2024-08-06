package com.family.pl.domain.VO.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能：
 * 作者：Name
 * 日期：2024/7/10 11:42
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SelectTotalWeekVO {
    /**
     * 完成任务的数量
     */
    public Long completeTask;
    /**
     * 与上周完成任务的差值
     */
    public Long difCompleteTask;
    /**
     * 完成任务的百分比
     */
    public Long taskCompletion;
    /**
     * 与上周完成任务的百分比差值
     */
    public Long difTaskCompletion;
    /**
     * 在完成任务中，准时完成的百分比
     */
    public Long onTimeTask;
    /**
     * 与上周准时完成的百分比差值
     */
    public Long difOnTimeTask;
    /**
     * 在完成任务中，未准时完成的百分比
     */
    public Long notOnTimeTask;
    /**
     * 与上周未准时完成的百分比差值
     */
    public Long difNotOnTimeTask;

}
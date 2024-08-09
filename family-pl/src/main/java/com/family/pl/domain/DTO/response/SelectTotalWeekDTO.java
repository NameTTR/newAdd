package com.family.pl.domain.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 选择任务的数据传输对象
 * </p>
 * @author 高俊炜
 * @since 2024-7-9
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SelectTotalWeekDTO {
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
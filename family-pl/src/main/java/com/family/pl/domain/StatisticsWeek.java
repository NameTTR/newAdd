package com.family.pl.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 每周统计表
 * @TableName pl_statistics_week
 */
@TableName(value ="pl_statistics_week")
@Data
public class StatisticsWeek implements Serializable {
    /**
     * 
     */
    @TableId(value = "ID")
    private Long id;

    /**
     * 年份
     */
    @TableField(value = "task_year")
    private Integer taskYear;

    /**
     * 第几周
     */
    @TableField(value = "task_week")
    private Integer taskWeek;

    /**
     * 开始日期
     */
    @TableField(value = "begin_date")
    private Date beginDate;

    /**
     * 结束日期
     */
    @TableField(value = "end_date")
    private Date endDate;

    /**
     * 任务数
     */
    @TableField(value = "count_task")
    private Integer countTask;

    /**
     * 完成任务数
     */
    @TableField(value = "count_complete_task")
    private Integer countCompleteTask;

    /**
     * 准时完成的任务数
     */
    @TableField(value = "count_ontime_task")
    private Integer countOntimeTask;

    /**
     * 高优先级任务数
     */
    @TableField(value = "count_high_task")
    private Integer countHighTask;

    /**
     * 高优先级完成任务数
     */
    @TableField(value = "count_complete_high_task")
    private Integer countCompleteHighTask;

    /**
     * 中优先级任务数
     */
    @TableField(value = "count_mid_task")
    private Integer countMidTask;

    /**
     * 中优先级完成任务数
     */
    @TableField(value = "count_complete_mid_task")
    private Integer countCompleteMidTask;

    /**
     * 低优先级任务数
     */
    @TableField(value = "count_low_task")
    private Integer countLowTask;

    /**
     * 低优先级完成任务数
     */
    @TableField(value = "count_complete_low_task")
    private Integer countCompleteLowTask;

    /**
     * 无优先级任务数
     */
    @TableField(value = "count_no_task")
    private Integer countNoTask;

    /**
     * 无优先级完成任务数
     */
    @TableField(value = "count_complete_no_task")
    private Integer countCompleteNoTask;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private Integer userId;

    /**
     * 创建时间
     */
    @TableField(value = "created_time")
    private Date createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 删除标记
     */
    @TableField(value = "flag_delete")
    private Integer flagDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        StatisticsWeek other = (StatisticsWeek) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getTaskYear() == null ? other.getTaskYear() == null : this.getTaskYear().equals(other.getTaskYear()))
            && (this.getTaskWeek() == null ? other.getTaskWeek() == null : this.getTaskWeek().equals(other.getTaskWeek()))
            && (this.getBeginDate() == null ? other.getBeginDate() == null : this.getBeginDate().equals(other.getBeginDate()))
            && (this.getEndDate() == null ? other.getEndDate() == null : this.getEndDate().equals(other.getEndDate()))
            && (this.getCountTask() == null ? other.getCountTask() == null : this.getCountTask().equals(other.getCountTask()))
            && (this.getCountCompleteTask() == null ? other.getCountCompleteTask() == null : this.getCountCompleteTask().equals(other.getCountCompleteTask()))
            && (this.getCountOntimeTask() == null ? other.getCountOntimeTask() == null : this.getCountOntimeTask().equals(other.getCountOntimeTask()))
            && (this.getCountHighTask() == null ? other.getCountHighTask() == null : this.getCountHighTask().equals(other.getCountHighTask()))
            && (this.getCountCompleteHighTask() == null ? other.getCountCompleteHighTask() == null : this.getCountCompleteHighTask().equals(other.getCountCompleteHighTask()))
            && (this.getCountMidTask() == null ? other.getCountMidTask() == null : this.getCountMidTask().equals(other.getCountMidTask()))
            && (this.getCountCompleteMidTask() == null ? other.getCountCompleteMidTask() == null : this.getCountCompleteMidTask().equals(other.getCountCompleteMidTask()))
            && (this.getCountLowTask() == null ? other.getCountLowTask() == null : this.getCountLowTask().equals(other.getCountLowTask()))
            && (this.getCountCompleteLowTask() == null ? other.getCountCompleteLowTask() == null : this.getCountCompleteLowTask().equals(other.getCountCompleteLowTask()))
            && (this.getCountNoTask() == null ? other.getCountNoTask() == null : this.getCountNoTask().equals(other.getCountNoTask()))
            && (this.getCountCompleteNoTask() == null ? other.getCountCompleteNoTask() == null : this.getCountCompleteNoTask().equals(other.getCountCompleteNoTask()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getCreatedTime() == null ? other.getCreatedTime() == null : this.getCreatedTime().equals(other.getCreatedTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getFlagDelete() == null ? other.getFlagDelete() == null : this.getFlagDelete().equals(other.getFlagDelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getTaskYear() == null) ? 0 : getTaskYear().hashCode());
        result = prime * result + ((getTaskWeek() == null) ? 0 : getTaskWeek().hashCode());
        result = prime * result + ((getBeginDate() == null) ? 0 : getBeginDate().hashCode());
        result = prime * result + ((getEndDate() == null) ? 0 : getEndDate().hashCode());
        result = prime * result + ((getCountTask() == null) ? 0 : getCountTask().hashCode());
        result = prime * result + ((getCountCompleteTask() == null) ? 0 : getCountCompleteTask().hashCode());
        result = prime * result + ((getCountOntimeTask() == null) ? 0 : getCountOntimeTask().hashCode());
        result = prime * result + ((getCountHighTask() == null) ? 0 : getCountHighTask().hashCode());
        result = prime * result + ((getCountCompleteHighTask() == null) ? 0 : getCountCompleteHighTask().hashCode());
        result = prime * result + ((getCountMidTask() == null) ? 0 : getCountMidTask().hashCode());
        result = prime * result + ((getCountCompleteMidTask() == null) ? 0 : getCountCompleteMidTask().hashCode());
        result = prime * result + ((getCountLowTask() == null) ? 0 : getCountLowTask().hashCode());
        result = prime * result + ((getCountCompleteLowTask() == null) ? 0 : getCountCompleteLowTask().hashCode());
        result = prime * result + ((getCountNoTask() == null) ? 0 : getCountNoTask().hashCode());
        result = prime * result + ((getCountCompleteNoTask() == null) ? 0 : getCountCompleteNoTask().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getCreatedTime() == null) ? 0 : getCreatedTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getFlagDelete() == null) ? 0 : getFlagDelete().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", taskYear=").append(taskYear);
        sb.append(", taskWeek=").append(taskWeek);
        sb.append(", beginDate=").append(beginDate);
        sb.append(", endDate=").append(endDate);
        sb.append(", countTask=").append(countTask);
        sb.append(", countCompleteTask=").append(countCompleteTask);
        sb.append(", countOntimeTask=").append(countOntimeTask);
        sb.append(", countHighTask=").append(countHighTask);
        sb.append(", countCompleteHighTask=").append(countCompleteHighTask);
        sb.append(", countMidTask=").append(countMidTask);
        sb.append(", countCompleteMidTask=").append(countCompleteMidTask);
        sb.append(", countLowTask=").append(countLowTask);
        sb.append(", countCompleteLowTask=").append(countCompleteLowTask);
        sb.append(", countNoTask=").append(countNoTask);
        sb.append(", countCompleteNoTask=").append(countCompleteNoTask);
        sb.append(", userId=").append(userId);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", flagDelete=").append(flagDelete);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
package com.family.pl.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 每天统计表
 * @TableName pl_statistics_date
 */
@TableName(value ="pl_statistics_date")
@Data
public class StatisticsDate implements Serializable {
    /**
     * 
     */
    @TableId(value = "ID")
    private Long id;

    /**
     * 日期
     */
    @TableField(value = "task_date")
    private Date taskDate;

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
        StatisticsDate other = (StatisticsDate) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getTaskDate() == null ? other.getTaskDate() == null : this.getTaskDate().equals(other.getTaskDate()))
            && (this.getCountTask() == null ? other.getCountTask() == null : this.getCountTask().equals(other.getCountTask()))
            && (this.getCountCompleteTask() == null ? other.getCountCompleteTask() == null : this.getCountCompleteTask().equals(other.getCountCompleteTask()))
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
        result = prime * result + ((getTaskDate() == null) ? 0 : getTaskDate().hashCode());
        result = prime * result + ((getCountTask() == null) ? 0 : getCountTask().hashCode());
        result = prime * result + ((getCountCompleteTask() == null) ? 0 : getCountCompleteTask().hashCode());
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
        sb.append(", taskDate=").append(taskDate);
        sb.append(", countTask=").append(countTask);
        sb.append(", countCompleteTask=").append(countCompleteTask);
        sb.append(", userId=").append(userId);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", flagDelete=").append(flagDelete);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
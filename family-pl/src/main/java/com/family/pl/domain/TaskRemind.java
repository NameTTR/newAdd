package com.family.pl.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 任务提醒表
 * @TableName pl_task_remind
 */
@TableName(value ="pl_task_remind")
@Data
public class TaskRemind implements Serializable {
    /**
     * 
     */
    @TableId(value = "ID")
    private Long id;

    /**
     * 任务表ID
     */
    @TableField(value = "task_id")
    private Long taskId;

    /**
     * 类别：1：按（开始）时间提醒；2：按天提醒

     */
    @TableField(value = "type")
    private Object type;

    /**
     * 按时间提前提醒：0：无；1：准时；2：提前5分钟；3：提前30分钟；4：提前1天
     */
    @TableField(value = "remind_by_time")
    private Object remindByTime;

    /**
     * 按天提前提醒：0：无；1：当天；2：提前1天；3：提前2天；4：提前3天
     */
    @TableField(value = "remind_by_date")
    private Object remindByDate;

    /**
     * corn字符串
     */
    @TableField(value = "corn")
    private String corn;

    /**
     * 创建时间
     */
    @TableField(value = "created_time")
    private Date createdTime;

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
        TaskRemind other = (TaskRemind) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getTaskId() == null ? other.getTaskId() == null : this.getTaskId().equals(other.getTaskId()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getRemindByTime() == null ? other.getRemindByTime() == null : this.getRemindByTime().equals(other.getRemindByTime()))
            && (this.getRemindByDate() == null ? other.getRemindByDate() == null : this.getRemindByDate().equals(other.getRemindByDate()))
            && (this.getCorn() == null ? other.getCorn() == null : this.getCorn().equals(other.getCorn()))
            && (this.getCreatedTime() == null ? other.getCreatedTime() == null : this.getCreatedTime().equals(other.getCreatedTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getTaskId() == null) ? 0 : getTaskId().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getRemindByTime() == null) ? 0 : getRemindByTime().hashCode());
        result = prime * result + ((getRemindByDate() == null) ? 0 : getRemindByDate().hashCode());
        result = prime * result + ((getCorn() == null) ? 0 : getCorn().hashCode());
        result = prime * result + ((getCreatedTime() == null) ? 0 : getCreatedTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", taskId=").append(taskId);
        sb.append(", type=").append(type);
        sb.append(", remindByTime=").append(remindByTime);
        sb.append(", remindByDate=").append(remindByDate);
        sb.append(", corn=").append(corn);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
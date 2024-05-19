package com.family.pl.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 任务表
 * @TableName pl_task
 */
@TableName(value ="pl_task")
@Data
public class Task implements Serializable {
    /**
     * 
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 备注，包括任务说明和完成任务的备注
     */
    @TableField(value = "notes")
    private String notes;

    /**
     * 日期
     */
    @TableField(value = "task_date")
    private Date taskDate;

    /**
     * 开始时间，允许为空，空值则表示全天
     */
    @TableField(value = "task_time_begin")
    private Date taskTimeBegin;

    /**
     * 结束时间，允许为空，若开始时间为空，则结束时间不允许非空
     */
    @TableField(value = "task_time_end")
    private Date taskTimeEnd;

    /**
     * 重复：0：无；1：每天；2：每月；3：每年；4：工作日；5：法定工作日；6：艾宾浩斯记忆法
     */
    @TableField(value = "repeat")
    private Integer repeat;

    /**
     * 重复结束时间
     */
    @TableField(value = "repeat_end")
    private Date repeatEnd;

    /**
     * 优先级：0：无；1；低；2：中；3：高
     */
    @TableField(value = "priority")
    private Integer priority;

    /**
     * 是否完成：0：否；1：是（针对已完成任务）
     */
    @TableField(value = "is_complete")
    private Integer isComplete;

    /**
     * 是否全部结束：0：否；1：是
     */
    @TableField(value = "is_end")
    private Integer isEnd;

    /**
     * 是否有标签：0：否；1：是
     */
    @TableField(value = "is_label")
    private Integer isLabel;

    /**
     * 是否设置提醒：0：否；1：是
     */
    @TableField(value = "is_remind")
    private Integer isRemind;

    /**
     * 是否有子任务：0：否；1：是
     */
    @TableField(value = "is_have_child")
    private Integer isHaveChild;

    /**
     * 是否超时完成：0：否；1：是；结束时间之前不算超时
     */
    @TableField(value = "is_timeout")
    private Integer isTimeout;

    /**
     * 父任务ID，空值则表示一级任务，非空表示是某个任务的子任务
     */
    @TableField(value = "father_task_id")
    private Long fatherTaskId;

    /**
     * 完成关联任务ID（已完成的任务才需要设置关联任务ID）
     */
    @TableField(value = "related_task_id")
    private Long relatedTaskId;

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
        Task other = (Task) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getNotes() == null ? other.getNotes() == null : this.getNotes().equals(other.getNotes()))
            && (this.getTaskDate() == null ? other.getTaskDate() == null : this.getTaskDate().equals(other.getTaskDate()))
            && (this.getTaskTimeBegin() == null ? other.getTaskTimeBegin() == null : this.getTaskTimeBegin().equals(other.getTaskTimeBegin()))
            && (this.getTaskTimeEnd() == null ? other.getTaskTimeEnd() == null : this.getTaskTimeEnd().equals(other.getTaskTimeEnd()))
            && (this.getRepeat() == null ? other.getRepeat() == null : this.getRepeat().equals(other.getRepeat()))
            && (this.getRepeatEnd() == null ? other.getRepeatEnd() == null : this.getRepeatEnd().equals(other.getRepeatEnd()))
            && (this.getPriority() == null ? other.getPriority() == null : this.getPriority().equals(other.getPriority()))
            && (this.getIsComplete() == null ? other.getIsComplete() == null : this.getIsComplete().equals(other.getIsComplete()))
            && (this.getIsEnd() == null ? other.getIsEnd() == null : this.getIsEnd().equals(other.getIsEnd()))
            && (this.getIsLabel() == null ? other.getIsLabel() == null : this.getIsLabel().equals(other.getIsLabel()))
            && (this.getIsRemind() == null ? other.getIsRemind() == null : this.getIsRemind().equals(other.getIsRemind()))
            && (this.getIsHaveChild() == null ? other.getIsHaveChild() == null : this.getIsHaveChild().equals(other.getIsHaveChild()))
            && (this.getIsTimeout() == null ? other.getIsTimeout() == null : this.getIsTimeout().equals(other.getIsTimeout()))
            && (this.getFatherTaskId() == null ? other.getFatherTaskId() == null : this.getFatherTaskId().equals(other.getFatherTaskId()))
            && (this.getRelatedTaskId() == null ? other.getRelatedTaskId() == null : this.getRelatedTaskId().equals(other.getRelatedTaskId()))
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
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getNotes() == null) ? 0 : getNotes().hashCode());
        result = prime * result + ((getTaskDate() == null) ? 0 : getTaskDate().hashCode());
        result = prime * result + ((getTaskTimeBegin() == null) ? 0 : getTaskTimeBegin().hashCode());
        result = prime * result + ((getTaskTimeEnd() == null) ? 0 : getTaskTimeEnd().hashCode());
        result = prime * result + ((getRepeat() == null) ? 0 : getRepeat().hashCode());
        result = prime * result + ((getRepeatEnd() == null) ? 0 : getRepeatEnd().hashCode());
        result = prime * result + ((getPriority() == null) ? 0 : getPriority().hashCode());
        result = prime * result + ((getIsComplete() == null) ? 0 : getIsComplete().hashCode());
        result = prime * result + ((getIsEnd() == null) ? 0 : getIsEnd().hashCode());
        result = prime * result + ((getIsLabel() == null) ? 0 : getIsLabel().hashCode());
        result = prime * result + ((getIsRemind() == null) ? 0 : getIsRemind().hashCode());
        result = prime * result + ((getIsHaveChild() == null) ? 0 : getIsHaveChild().hashCode());
        result = prime * result + ((getIsTimeout() == null) ? 0 : getIsTimeout().hashCode());
        result = prime * result + ((getFatherTaskId() == null) ? 0 : getFatherTaskId().hashCode());
        result = prime * result + ((getRelatedTaskId() == null) ? 0 : getRelatedTaskId().hashCode());
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
        sb.append(", title=").append(title);
        sb.append(", notes=").append(notes);
        sb.append(", taskDate=").append(taskDate);
        sb.append(", taskTimeBegin=").append(taskTimeBegin);
        sb.append(", taskTimeEnd=").append(taskTimeEnd);
        sb.append(", repeat=").append(repeat);
        sb.append(", repeatEnd=").append(repeatEnd);
        sb.append(", priority=").append(priority);
        sb.append(", isComplete=").append(isComplete);
        sb.append(", isEnd=").append(isEnd);
        sb.append(", isLabel=").append(isLabel);
        sb.append(", isRemind=").append(isRemind);
        sb.append(", isHaveChild=").append(isHaveChild);
        sb.append(", isTimeout=").append(isTimeout);
        sb.append(", fatherTaskId=").append(fatherTaskId);
        sb.append(", relatedTaskId=").append(relatedTaskId);
        sb.append(", userId=").append(userId);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", flagDelete=").append(flagDelete);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
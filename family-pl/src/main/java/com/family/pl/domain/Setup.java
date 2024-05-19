package com.family.pl.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 设置表
 * @TableName pl_setup
 */
@TableName(value ="pl_setup")
@Data
public class Setup implements Serializable {
    /**
     * 
     */
    @TableId(value = "ID")
    private Long id;

    /**
     * 提醒重复：0：无；1：每天；2：每月；3：每年；4：工作日；5：法定工作日；6：艾宾浩斯记忆法
     */
    @TableField(value = "repeat")
    private Object repeat;

    /**
     * 已完成任务是否隐藏：0：不隐藏；1：隐藏
     */
    @TableField(value = "hide_complete")
    private Integer hideComplete;

    /**
     * 已选分组：1：按完成情况分组；2：按优先级分组
     */
    @TableField(value = "selected_groups")
    private Integer selectedGroups;

    /**
     * 已选排序：1：按时间排序；2：按优先级排序
     */
    @TableField(value = "selected_order")
    private Integer selectedOrder;

    /**
     * 微信提醒：0：关闭；1：开启
     */
    @TableField(value = "remind_wechat")
    private Integer remindWechat;

    /**
     * 本地日历提醒：0：关闭；1：开启
     */
    @TableField(value = "remind_calendar")
    private Integer remindCalendar;

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
        Setup other = (Setup) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getRepeat() == null ? other.getRepeat() == null : this.getRepeat().equals(other.getRepeat()))
            && (this.getHideComplete() == null ? other.getHideComplete() == null : this.getHideComplete().equals(other.getHideComplete()))
            && (this.getSelectedGroups() == null ? other.getSelectedGroups() == null : this.getSelectedGroups().equals(other.getSelectedGroups()))
            && (this.getSelectedOrder() == null ? other.getSelectedOrder() == null : this.getSelectedOrder().equals(other.getSelectedOrder()))
            && (this.getRemindWechat() == null ? other.getRemindWechat() == null : this.getRemindWechat().equals(other.getRemindWechat()))
            && (this.getRemindCalendar() == null ? other.getRemindCalendar() == null : this.getRemindCalendar().equals(other.getRemindCalendar()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getCreatedTime() == null ? other.getCreatedTime() == null : this.getCreatedTime().equals(other.getCreatedTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getRepeat() == null) ? 0 : getRepeat().hashCode());
        result = prime * result + ((getHideComplete() == null) ? 0 : getHideComplete().hashCode());
        result = prime * result + ((getSelectedGroups() == null) ? 0 : getSelectedGroups().hashCode());
        result = prime * result + ((getSelectedOrder() == null) ? 0 : getSelectedOrder().hashCode());
        result = prime * result + ((getRemindWechat() == null) ? 0 : getRemindWechat().hashCode());
        result = prime * result + ((getRemindCalendar() == null) ? 0 : getRemindCalendar().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
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
        sb.append(", repeat=").append(repeat);
        sb.append(", hideComplete=").append(hideComplete);
        sb.append(", selectedGroups=").append(selectedGroups);
        sb.append(", selectedOrder=").append(selectedOrder);
        sb.append(", remindWechat=").append(remindWechat);
        sb.append(", remindCalendar=").append(remindCalendar);
        sb.append(", userId=").append(userId);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
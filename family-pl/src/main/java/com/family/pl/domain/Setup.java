package com.family.pl.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
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
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 提醒重复：0：无；1：每天；2：每月；3：每年；4：工作日；5：法定工作日；6：艾宾浩斯记忆法
     */
    private Object repeat;

    /**
     * 已完成任务是否隐藏：0：不隐藏；1：隐藏
     */
    private Integer hideComplete;

    /**
     * 已选分组：1：按完成情况分组；2：按优先级分组
     */
    private Integer selectedGroups;

    /**
     * 已选排序：1：按时间排序；2：按优先级排序
     */
    private Integer selectedOrder;

    /**
     * 微信提醒：0：关闭；1：开启
     */
    private Integer remindWechat;

    /**
     * 本地日历提醒：0：关闭；1：开启
     */
    private Integer remindCalendar;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
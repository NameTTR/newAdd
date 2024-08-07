package com.family.en.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.family.en.enums.EnTestState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 单词测试表
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("en_test")
public class EnTest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 单词章节表外键，单词章节表ID
     */
    private Long chapterId;

    /**
     * 用户表外键，用户ID
     */
    private Long userId;

    /**
     * 测试状态：0：未完成；1：进行中；2：已完成
     */
    private EnTestState state;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}

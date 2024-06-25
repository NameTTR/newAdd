package com.family.cc.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.family.cc.enums.CcTestState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 汉字测试表
 * </p>
 *
 * @author 陈文杰
 * @since 2024-04-28
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@TableName("cc_test")
public class CcTest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 汉字章节表外键，汉字章节表ID
     */
    private Long chapterId;

    /**
     * 用户表外键，用户ID
     */
    private Long userId;

    /**
     * 测试状态：0：未完成；1：进行中；2：已完成
     */
    private CcTestState state;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}

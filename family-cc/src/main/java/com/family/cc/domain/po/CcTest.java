package com.family.cc.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

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
    private Integer id;

    /**
     * 汉字章节表外键，汉字章节表ID
     */
    private Integer chapterId;

    /**
     * 用户表外键，用户ID
     */
    private Integer userId;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}

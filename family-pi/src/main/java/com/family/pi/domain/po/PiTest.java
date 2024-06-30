package com.family.pi.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.family.pi.enums.PiPinyinTestState;
import com.family.pi.enums.PiTestState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 拼音测试表
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pi_test")
public class PiTest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 拼音单元节表外键，拼音单元表ID
     */
    private Long unitId;

    /**
     * 用户表外键，用户ID
     */
    private Long userId;

    /**
     * 测试状态：0：未完成；1：进行中；2：已完成
     */
    private PiTestState state;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}

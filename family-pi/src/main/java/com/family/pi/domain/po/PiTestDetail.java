package com.family.pi.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.family.pi.enums.PiPinyinTestState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 拼音测试明细表
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pi_test_detail")
public class PiTestDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 拼音测试表外键，拼音测试表ID
     */
    private Long testId;

    /**
     * 拼音表外键，拼音表ID
     */
    private Long pinyinId;

    /**
     * 拼音
     */
    private String pinyin;

    /**
     * 测试结果：0：错；1：对；2：未测试
     */
    private PiPinyinTestState result;

    /**
     * 用户表外键，用户ID
     */
    private Long userId;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;


}

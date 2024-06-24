package com.family.en.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 单词测试明细表
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("en_test_detail")
public class EnTestDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 汉字测试表外键，汉字测试表ID
     */
    private Integer testId;

    /**
     * 汉字表外键，汉字表ID
     */
    private Integer characterId;

    /**
     * 单词
     */
    private String word;

    /**
     * 测试结果：0：错；1：对
     */
    private Integer result;

    /**
     * 用户表外键，用户ID
     */
    private Integer userId;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;


}

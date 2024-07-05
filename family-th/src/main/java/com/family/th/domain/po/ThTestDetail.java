package com.family.th.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.family.th.enums.ThThinkingTestState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 思维测试明细表
 * </p>
 *
 * @author 陈文杰
 * @since 2024-07-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("th_test_detail")
public class ThTestDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 思维测试表外键，思维测试表ID
     */
    private Long testId;

    /**
     * 题库表外键，题库表ID
     */
    private Long testQuestionId;

    /**
     * 当前记录的答案 - 存储的几号选项
     */
    private Integer nowAnswer;

    /**
     * 正确的答案 - 存储的几号选项
     */
    private Integer  correctAnswer;

    /**
     * 测试结果：0：错误；1：正确；2：未测试；3：测试中
     */
    private ThThinkingTestState result;

    /**
     * 用户表外键，用户ID
     */
    private Long userId;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;


}

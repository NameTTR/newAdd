package com.family.th.domain.po;

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
 * 思维测试题库表
 * </p>
 *
 * @author 陈文杰
 * @since 2024-07-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("th_test_question")
public class ThTestQuestion implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 思维章节表外键，思维章节表ID
     */
    private Long chapterId;

    /**
     * 题干
     */
    private String stem;

    /**
     * 选项1
     */
    private String option1;

    /**
     * 选项2
     */
    private String option2;

    /**
     * 选项3
     */
    private String option3;

    /**
     * 选项4
     */
    private String option4;

    /**
     * 答案
     */
    private String answer;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}

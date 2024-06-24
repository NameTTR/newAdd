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
 * 单词句子表
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("en_sentence")
public class EnSentence implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 单词表外键，单词表ID
     */
    private Integer wordId;

    /**
     * 类别：1：普通句子；2：彦语
     */
    private Integer type;

    /**
     * 句子
     */
    private String sentence;

    /**
     * 读音
     */
    private String read;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;


}

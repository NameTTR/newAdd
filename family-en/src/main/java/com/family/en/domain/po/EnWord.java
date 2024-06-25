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
 * 单词表
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("en_word")
public class EnWord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 单词
     */
    private String word;

    /**
     * 音标
     */
    private String phoneticSymbol;

    /**
     * 读音
     */
    private String wordRead;

    /**
     * 图片
     */
    private String pic;

    /**
     * 单词章节表外键，单词章节表ID
     */
    private Long chapterId;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;


}

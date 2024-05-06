package com.family.cc.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 汉字表
 * </p>
 *
 * @author 陈文杰
 * @since 2024-04-28
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@TableName("cc_character")
public class CcCharacter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 汉字
     */
    @TableField(value = "`character`")
    private String character;

    /**
     * 拼音
     */
    private String pinyin;

    /**
     * 读音
     */
    private String pinyinRead;

    /**
     * 译义
     */
    private String translation;

    /**
     * 译义读音
     */
    private String translationRead;

    /**
     * 汉字章节表外键，汉字章节表ID
     */
    private Long chapterId;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;


}

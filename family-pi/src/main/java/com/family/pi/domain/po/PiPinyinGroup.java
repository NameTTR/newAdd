package com.family.pi.domain.po;

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
 * 拼音组词表
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pi_pinyin_group")
public class PiPinyinGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 拼音表外键，拼音表ID
     */
    private Long pinyinId;

    /**
     * 词
     */
    private String words;

    /**
     * 拼音
     */
    private String pinyin;

    /**
     * 读音
     */
    private String read;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;


}

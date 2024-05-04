package com.family.cc.domain.po;

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
 * 汉字组词表
 * </p>
 *
 * @author 陈文杰
 * @since 2024-04-28
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@TableName("cc_character_group")
public class CcCharacterGroup extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 汉字表外键，汉字表ID
     */
    private Integer characterId;

    /**
     * 类别：1：组词；2：近义词；3：反义词
     */
    private Integer type;

    /**
     * 词
     */
    private String words;

    /**
     * 读音
     */
    private String read;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;


}

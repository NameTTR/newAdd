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
 * 思维章节表
 * </p>
 *
 * @author 陈文杰
 * @since 2024-07-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("th_chapter")
public class ThChapter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 章节
     */
    private String chapter;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 单元表外键，单元表ID
     */
    private Long unitId;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;


}

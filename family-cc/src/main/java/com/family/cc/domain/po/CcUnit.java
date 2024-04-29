package com.family.cc.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 单元表
 * </p>
 *
 * @author 陈文杰
 * @since 2024-04-28
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("cc_unit")
public class CcUnit extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 单元名称
     */
    private String unit;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;


}

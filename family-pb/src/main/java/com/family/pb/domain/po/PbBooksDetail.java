package com.family.pb.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * 绘本明细表
 * </p>
 *
 * @author 陈文杰
 * @since 2024-07-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pb_books_detail")
public class PbBooksDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 绘本表外键，绘本表ID
     */
    private Long pbBooksId;

    /**
     * 序号
     */
    private Integer sort;

    /**
     * 插图
     */
    private String pic;

    /**
     * 内容
     */
    private String content;

    /**
     * 朗读
     */
    @TableField(value = "`read`")
    private String read;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;


}

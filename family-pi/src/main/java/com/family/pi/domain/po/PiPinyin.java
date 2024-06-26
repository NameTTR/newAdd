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
 * 拼音表
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pi_pinyin")
public class PiPinyin implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 拼音
     */
    private String pinyin;

    /**
     * 类别：1：声母；2：韵母；3：整体认读音节；4：双音节组合；5：多音节组合
     */
    private Integer type;

    /**
     * 介绍
     */
    private String introduce;

    /**
     * 介绍读音
     */
    private String introduceRead;

    /**
     * 一声
     */
    private String song1;

    /**
     * 二声
     */
    private String song2;

    /**
     * 三声
     */
    private String song3;

    /**
     * 四声
     */
    private String song4;

    /**
     * 一声读音
     */
    private String song1Read;

    /**
     * 二声读音
     */
    private String song2Read;

    /**
     * 三声读音
     */
    private String song3Read;

    /**
     * 四声读音
     */
    private String song4Read;

    /**
     * 拼音单元节表外键，拼音单元表ID
     */
    private Long unitId;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;


}

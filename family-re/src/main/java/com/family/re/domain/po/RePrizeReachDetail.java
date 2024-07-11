package com.family.re.domain.po;

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
 * 奖品池兑现明细表
 * </p>
 *
 * @author 朱桐山
 * @since 2024-07-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("re_prize_reach_detail")
public class RePrizeReachDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 奖品池兑现表外键，奖品池兑现表ID
     */
    private Long prizeReachId;

    /**
     * 奖品图标
     */
    private String prizeIco;

    /**
     * 奖品名称（不超过20字）
     */
    private String prizeName;

    /**
     * 是否被抽中：0：否；1：是
     */
    private Integer isCheck;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除标记
     */
    private Integer flagDelete;


}

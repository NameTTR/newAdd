package com.family.th.domain.dto;

import com.family.th.domain.po.ThTestDetail;
import com.family.th.enums.ThTestState;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 单元细节DTO
 *
 * @author 陈文杰
 * @since 2024-07-01
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ThTestDetailsDTO {
    /**
     * 测试ID
     */
    private Long testId;

    /**
     * 单元名称
     */
    private String unit;

    /**
     * 章节名称
     */
    private String chapter;

    /**
     * 测试结果：0：错；1：对；2：未测
     */
    private ThTestState state;

    /**
     * 题目ID列表
     */
    private List<Long> questionIds;

    /**
     * 思维个体结果信息
     */
    private List<ThTestDetail> thinkingTest;

    /**
     * 通过的个体数量
     */
    private Long passCount;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdTime;
}

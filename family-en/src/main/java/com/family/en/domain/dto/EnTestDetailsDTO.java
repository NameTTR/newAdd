package com.family.en.domain.dto;

import com.family.en.domain.po.EnTestDetail;
import com.family.en.enums.EnTestState;
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
 * @since 2024-06-25
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class EnTestDetailsDTO {
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
    private EnTestState state;

    /**
     * 单词结果信息
     */
    private List<EnTestDetail> wordTest;

    /**
     * 通过的汉字数量
     */
    private Long passCount;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdTime;
}

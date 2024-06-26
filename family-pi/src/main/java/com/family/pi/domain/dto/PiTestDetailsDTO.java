package com.family.pi.domain.dto;

import com.family.pi.domain.po.PiTestDetail;
import com.family.pi.enums.PiTestState;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 测试细节DTO
 *
 * @author 陈文杰
 * @since 2024-05-04
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class PiTestDetailsDTO {
    /**
     * 测试ID
     */
    private Long testId;

    /**
     * 单元名称
     */
    private String unit;

    /**
     * 测试状态：0：未完成；1：进行中；2：已完成
     */
    private PiTestState state;

    /**
     * 拼音结果信息
     */
    private List<PiTestDetail> pinyinTest;

    /**
     * 总的拼音数量
     */
    private Long totalCount;

    /**
     * 通过的拼音数量
     */
    private Long passCount;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdTime;
}

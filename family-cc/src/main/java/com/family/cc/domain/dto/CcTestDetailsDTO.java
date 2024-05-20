package com.family.cc.domain.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.family.cc.domain.po.CcTestDetail;
import com.family.cc.enums.CcTestState;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.ibatis.annotations.Param;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 单元细节DTO
 *
 * @author 陈文杰
 * @since 2024-05-04
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class CcTestDetailsDTO {
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
    private CcTestState state;

    /**
     * 汉字结果信息
     */
    private List<CcTestDetail> characterTest;

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

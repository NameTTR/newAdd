package com.family.pl.domain.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 数据传输对象，用于添加标签的信息传递。
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddLabelDTO {
    /**
     * 标签名称
     */
    private String labelName;
}
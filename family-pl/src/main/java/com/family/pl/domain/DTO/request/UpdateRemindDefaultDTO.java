package com.family.pl.domain.DTO.request;

import com.family.pl.domain.TimeRemindDefault;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>
 * 任务默认值传输对象。
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateRemindDefaultDTO {
    /**
     * 任务提醒默认值
     */
    private List<TimeRemindDefault> remindDefaultList;
}
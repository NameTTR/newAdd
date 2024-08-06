package com.family.pl.domain.VO.request;

import com.family.pl.domain.TimeRemindDefault;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 功能：
 * 作者：Name
 * 日期：2024/7/10 9:12
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateRemindDefaultVO {
    /**
     * 任务提醒默认值
     */
    private List<TimeRemindDefault> timeRemindDefaults;
}
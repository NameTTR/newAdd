package com.family.pl.service;

import com.family.pl.domain.StatisticsWeek;
import com.baomidou.mybatisplus.extension.service.IService;
import com.family.pl.domain.DTO.request.IntervalDateDTO;
import com.family.pl.domain.DTO.response.SelectTotalWeekDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 针对表【pl_statistics_week(每周统计表)】的数据库操作Service
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
public interface StatisticsWeekService extends IService<StatisticsWeek> {


    SelectTotalWeekDTO selectTotal(LocalDate startDate, LocalDate endDate);

    int getTaskExecutionCount(LocalDate startDate, LocalDate endDate, Long userId);

//     TaskExecutionResult getTaskExecutionDetails(LocalDate startDate, LocalDate endDate, Long userId);

    List<Map<LocalDate, Long>> selectCompletion(LocalDate startDate, LocalDate endDate);

    int addWeek(LocalDate startDate, LocalDate endDate, Integer week, Long userId);
}

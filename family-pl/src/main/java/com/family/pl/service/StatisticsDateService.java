package com.family.pl.service;

import com.family.pl.domain.StatisticsDate;
import com.baomidou.mybatisplus.extension.service.IService;
import com.family.pl.domain.DTO.request.DateDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 针对表【pl_statistics_date(每天统计表)】的数据库操作Service
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
public interface StatisticsDateService extends IService<StatisticsDate> {

    /**
     * 添加日期统计数据
     * 该方法用于根据传入的日期对象，统计该日期的任务完成情况，并更新或插入到统计数据表中。
     *
     * @param date 需要统计的日期
     * @return 返回操作状态码，1表示成功，0表示失败。
     */
    int addOrUpdateDate(LocalDate date);

    /**
     * 根据指定的日期和用户ID查询统计数据日期信息。
     *
     * @param date 需要查询的日期
     * @return 符合条件的统计数据日期对象，如果不存在则返回null。
     */
    StatisticsDate selectDate(LocalDate date);

    /**
     * 删除统计数据日期记录。
     * 此方法用于根据传入的日期VO对象删除相应的统计数据日期记录。
     * 它首先检查是否存在对应日期和用户ID的未删除统计数据，
     * 如果存在，则将该记录的删除标志设置为已删除，并更新到数据库中。
     *
     * @param date 日期VO对象，包含需要删除的日期信息。
     * @return 返回操作状态码，0表示未操作，非0表示操作成功。
     */
    int deleteDate(LocalDate date);

    List<StatisticsDate> selectRangeDate(LocalDate startDate, LocalDate endDate);
}

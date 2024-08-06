package com.family.pl.mapper;

import com.family.pl.domain.StatisticsWeek;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.family.pl.domain.Task;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 针对表【pl_statistics_week(每周统计表)】的数据库操作Mapper
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
public interface StatisticsWeekMapper extends BaseMapper<StatisticsWeek> {


}





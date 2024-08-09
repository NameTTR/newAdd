package com.family.pl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.pl.constant.TaskConstants;
import com.family.pl.domain.StatisticsDate;
import com.family.pl.domain.Task;
import com.family.pl.domain.DTO.request.DateDTO;
import com.family.pl.service.StatisticsDateService;
import com.family.pl.mapper.StatisticsDateMapper;
import com.family.pl.service.StatisticsWeekService;
import com.family.pl.service.TaskService;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 针对表【pl_statistics_date(每天统计表)】的数据库操作Service实现
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
@Service
public class StatisticsDateServiceImpl extends ServiceImpl<StatisticsDateMapper, StatisticsDate>
        implements StatisticsDateService {

    @Autowired
    private StatisticsWeekService statisticsWeekService;

    @Autowired
    private TaskService taskService;

    /**
     * 添加日期统计数据
     * 该方法用于根据传入的日期对象，统计该日期的任务完成情况，并更新或插入到统计数据表中。
     *
     * @param date 需要统计的日期
     * @return 返回操作状态码，1表示成功，0表示失败。
     */
    @Override
    public int addOrUpdateDate(LocalDate date) {
        int flag = 1;
        // 模拟默认用户ID，用于统计任务归属
        Long userId = 1L;
        // 获取待统计的日期
        LocalDate startDate = date;
        LocalDate endDate = date;
        // 统计指定日期范围内的所有任务数量
        int allTasks = statisticsWeekService.getTaskExecutionCount(startDate, endDate, userId);
        // 统计指定日期、用户、未删除且已完成的任务数量
        long count = taskService.count(new LambdaQueryWrapper<Task>()
                .eq(Task::getTaskDate, date)
                .eq(Task::getUserId, userId)
                .eq(Task::getFlagDelete, 0)
                .eq(Task::getIsComplete, TaskConstants.TASK_COMMPLETE));
        // 将任务数量转换为整型
        //count转换成int
        int countCompleteTask = (int) count;
        // 根据日期和用户ID查询已存在的统计数据
        StatisticsDate statisticsDate1 = baseMapper.selectOne(new LambdaQueryWrapper<StatisticsDate>()
                .eq(StatisticsDate::getTaskDate, date)
                .eq(StatisticsDate::getUserId, userId));
        // 如果统计数据已存在，则更新统计数据
        if (StringUtils.isNotNull(statisticsDate1)) {
            statisticsDate1.setCountCompleteTask(countCompleteTask);
            statisticsDate1.setCountTask(allTasks);
            if (allTasks != 0) {
                statisticsDate1.setCompletion(countCompleteTask * 100 / allTasks);
            } else {
                statisticsDate1.setCompletion(0);
            }
            // 更新统计数据，并返回操作状态
            flag = baseMapper.updateById(statisticsDate1);
        } else {
            // 如果统计数据不存在，则创建新的统计数据项
            flag = initStatisticsDate(startDate, allTasks, countCompleteTask, userId);
        }
        // 返回操作状态码
        return flag;
    }

    /**
     * 初始化统计数据
     * 该方法用于计算和记录指定日期的统计数据，包括任务总数、完成任务数、完成率等
     *
     * @param date          统计数据的日期
     * @param allTasks      当日所有任务数量
     * @param countCompleteTask  当日完成的任务数量
     * @param userId        用户ID，用于区分不同用户的统计数据
     * @return              返回操作状态，通常用于判断数据是否插入成功
     */
    private int initStatisticsDate(LocalDate date, int allTasks, int countCompleteTask, Long userId) {
        // 创建新的统计数据对象
        StatisticsDate statisticsDate = new StatisticsDate();
        // 设置完成任务数量
        statisticsDate.setCountCompleteTask(countCompleteTask);
        // 设置总任务数量
        statisticsDate.setCountTask(allTasks);
        // 设置统计数据的日期
        statisticsDate.setTaskDate(date);
        // 计算任务完成率，避免除以0的情况
        if (allTasks != 0) {
            statisticsDate.setCompletion(countCompleteTask * 100 / allTasks);
        } else {
            statisticsDate.setCompletion(0);
        }
        // 设置用户ID，用于区分不同用户的统计数据
        statisticsDate.setUserId(userId);
        // 插入新的统计数据，并返回操作状态
        int flag = baseMapper.insert(statisticsDate);
        // 返回操作状态，用于判断数据是否插入成功
        return flag;
    }

    /**
     * 根据指定的日期和用户ID查询统计数据日期信息。
     *
     * @param date 需要查询的日期
     * @return 符合条件的统计数据日期对象，如果不存在则返回null。
     */
    @Override
    public StatisticsDate selectDate(LocalDate date) {
        // 设置固定的用户ID，这里假设查询的是固定用户的统计数据
        Long userId = 1L;
        // 根据日期和用户ID查询统计数据日期信息
        StatisticsDate statisticsDate = baseMapper.selectOne(new LambdaQueryWrapper<StatisticsDate>()
                .eq(StatisticsDate::getTaskDate, date)
                .eq(StatisticsDate::getUserId, userId)
                .eq(StatisticsDate::getFlagDelete, TaskConstants.TASK_NOT_DELETE));
        if(StringUtils.isNull(statisticsDate)){
            // 获取待统计的日期
            LocalDate startDate = date;
            LocalDate endDate = date;
            // 统计指定日期范围内的所有任务数量
            int allTasks = statisticsWeekService.getTaskExecutionCount(startDate, endDate, userId);
            // 统计指定日期、用户、未删除且已完成的任务数量
            long count = taskService.count(new LambdaQueryWrapper<Task>()
                    .eq(Task::getTaskDate, date)
                    .eq(Task::getUserId, userId)
                    .eq(Task::getFlagDelete, 0)
                    .eq(Task::getIsComplete, TaskConstants.TASK_COMMPLETE));
            // 将任务数量转换为整型
            //count转换成int
            int countCompleteTask = (int) count;
            int flag = initStatisticsDate(startDate, allTasks, countCompleteTask, userId);
            statisticsDate = baseMapper.selectOne(new LambdaQueryWrapper<StatisticsDate>()
                    .eq(StatisticsDate::getTaskDate, date)
                    .eq(StatisticsDate::getUserId, userId)
                    .eq(StatisticsDate::getFlagDelete, TaskConstants.TASK_NOT_DELETE));
        }
        // 返回查询结果
        return statisticsDate;
    }

    /**
     * 删除统计数据日期记录。
     * 此方法用于根据传入的日期VO对象删除相应的统计数据日期记录。
     * 它首先检查是否存在对应日期和用户ID的未删除统计数据，
     * 如果存在，则将该记录的删除标志设置为已删除，并更新到数据库中。
     *
     * @param date 日期VO对象，包含需要删除的日期信息。
     * @return 返回操作状态码，0表示未操作，非0表示操作成功。
     */
    @Override
    public int deleteDate(LocalDate date) {
        // 初始化标志变量，用于后续判断操作是更新还是插入
        int flag = 0;
        // 设置固定的用户ID，这里假设查询的是固定用户的统计数据
        Long userId = 1L;
        // 根据日期和用户ID查询统计数据日期信息
        StatisticsDate statisticsDate = baseMapper.selectOne(new LambdaQueryWrapper<StatisticsDate>()
                .eq(StatisticsDate::getTaskDate, date)
                .eq(StatisticsDate::getUserId, userId)
                .eq(StatisticsDate::getFlagDelete, TaskConstants.TASK_NOT_DELETE));
        // 如果统计数据已存在，则更新统计数据
        if (StringUtils.isNotNull(statisticsDate)) {
            statisticsDate.setFlagDelete(TaskConstants.TASK_DELETE);
            // 更新统计数据，并返回操作状态
            flag = baseMapper.updateById(statisticsDate);
        }
        // 返回操作状态码
        return flag;
    }

    @Override
    public List<StatisticsDate> selectRangeDate(LocalDate startDate, LocalDate endDate) {
        Long userId = 1L;

        return null;
    }

}





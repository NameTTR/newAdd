package com.family.pl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.pl.constant.TaskConstants;
import com.family.pl.domain.StatisticsDate;
import com.family.pl.domain.StatisticsWeek;
import com.family.pl.domain.Task;
import com.family.pl.domain.DTO.request.IntervalDateDTO;
import com.family.pl.domain.DTO.response.SelectTotalWeekDTO;
import com.family.pl.mapper.TaskMapper;
import com.family.pl.service.LegalWorkingDayService;
import com.family.pl.service.StatisticsDateService;
import com.family.pl.service.StatisticsWeekService;
import com.family.pl.mapper.StatisticsWeekMapper;
import com.family.pl.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 针对表【pl_statistics_week(每周统计表)】的数据库操作Service实现
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
@Service
public class StatisticsWeekServiceImpl extends ServiceImpl<StatisticsWeekMapper, StatisticsWeek>
        implements StatisticsWeekService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private LegalWorkingDayService legalWorkingDayService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private StatisticsWeekService statisticsWeekService;

    @Autowired
    private StatisticsDateService statisticsDateService;

    @Autowired
    private StatisticsWeekMapper statisticsWeekMapper;

    /**
     * 获取任务在给定日期范围内的执行次数
     *
     * @param
     * @return
     */
    @Override
    public SelectTotalWeekDTO selectTotal(LocalDate startDate, LocalDate endDate) {
        Long userId = 1L;
        SelectTotalWeekDTO selectTotalWeekDTO = new SelectTotalWeekDTO();
        Integer unAndComTotal = getTaskExecutionCount(startDate, endDate, userId);
        Long comTotal = taskService.count(new LambdaQueryWrapper<Task>()
                .eq(Task::getUserId, userId)
                .eq(Task::getIsComplete, TaskConstants.TASK_COMMPLETE)
                .eq(Task::getFlagDelete, TaskConstants.TASK_NOT_DELETE)
                .ge(Task::getTaskDate, startDate)
                .le(Task::getTaskDate, endDate));

        selectTotalWeekDTO.setCompleteTask(comTotal);
        long completionRate = 0;
        if (unAndComTotal > 0) {
            completionRate = Math.round((double) comTotal / unAndComTotal * 100);
        }
        selectTotalWeekDTO.setTaskCompletion(completionRate);
        return null;
    }


    /**
     * 根据开始日期和结束日期查询任务完成情况。
     *
     * @param startDate 查询的开始日期。
     * @param endDate   查询的结束日期。
     * @return 返回一个列表，其中每个元素是一个映射，映射的键是日期，值是该日期的任务完成数量。
     */
    @Override
    public List<Map<LocalDate, Long>> selectCompletion(LocalDate startDate, LocalDate endDate) {
        // 设置一个默认的用户ID，这里假设查询的是固定用户的完成情况
        Long userId = 1L;

        // 初始化结果列表，用于存储每个日期的完成任务数量
        List<Map<LocalDate, Long>> list = new ArrayList<>();

        // 查询在指定日期范围内的所有统计数据
        statisticsDateService.list(new LambdaQueryWrapper<StatisticsDate>()
                        .ge(StatisticsDate::getTaskDate, startDate)
                        .le(StatisticsDate::getTaskDate, endDate))
                .stream().forEach(statisticsDate -> {
                    // 初始化单日完成情况的映射
                    Map<LocalDate, Long> map = new HashMap<>();
                    // 计算并记录该日期的完成任务数量
                    map.put(statisticsDate.getTaskDate(), taskCompletion(Long.valueOf(statisticsDate.getCountTask())
                            , Long.valueOf(statisticsDate.getCountCompleteTask())));
                    // 将单日完成情况添加到结果列表
                    list.add(map);
                });

        // 返回所有日期的完成任务数量列表
        return list;
    }

    @Override
    public int addWeek(LocalDate startDate, LocalDate endDate, Integer week, Long userId) {
        int flag = 1;
        statisticsDateService.list(new LambdaQueryWrapper<StatisticsDate>()
                        .ge(StatisticsDate::getTaskDate, startDate)
                        .le(StatisticsDate::getTaskDate, endDate)
                        .eq(StatisticsDate::getUserId, userId))
                .stream().forEach(statisticsDate -> {

                });
        return 0;
    }


    /**
     * 计算任务的完成率。
     *
     * @param total      总任务量，表示100%的完成率。
     * @param completion 已完成的任务量。
     * @return 完成率，以长整型百分比表示。
     * <p>
     * 此方法用于根据已完成的任务量和总任务量计算任务的完成率。
     * 如果总任务量为0，完成率将被计算为0，避免了除以0的错误。
     * 完成率计算结果四舍五入为整数，代表百分比。
     */
    private Long taskCompletion(Long total, Long completion) {
        long completionRate = 0;
        if (total > 0) {
            completionRate = Math.round((double) completion / total * 100);
        }

        return completionRate;
    }


    /**
     * 获取指定用户在指定日期范围内执行任务的总次数。
     * 该方法首先通过查询数据库，获取在指定日期范围内由指定用户创建的所有任务。
     * 然后，对每个任务，计算其在指定日期范围内的执行次数，并累加到总数中。
     * 最后，返回总执行次数。
     *
     * @param startDate 查询范围的起始日期，包括此日期。
     * @param endDate   查询范围的结束日期，包括此日期。
     * @param userId    指定用户的ID，用于查询该用户创建的任务。
     * @return 在指定日期范围内，指定用户执行任务的总次数。
     */
    @Override
    public int getTaskExecutionCount(LocalDate startDate, LocalDate endDate, Long userId) {
        // 通过TaskMapper查询在给定日期范围内的任务列表
        // 使用 TaskMapper 查询在给定日期范围内的任务
        List<Task> tasks = taskMapper.selectAllTaskByRangeDate(startDate, endDate, userId);
        // 初始化总执行次数为0
        int totalExecutionCount = 0;

        // 遍历任务列表，计算每个任务在指定日期范围内的执行次数，并累加到总执行次数
        // 计算每个任务的执行次数
        for (Task task : tasks) {
            totalExecutionCount += calculateExecutionCount(startDate, endDate, task);
        }

        // 返回总执行次数
        return totalExecutionCount;
    }


    /**
     * 根据任务的重复模式计算任务在给定日期范围内的执行次数。
     *
     * @param startDate 计算开始日期。
     * @param endDate   计算结束日期。
     * @param task      待计算执行次数的任务对象。
     * @return 任务在指定日期范围内的执行次数。
     */
    private int calculateExecutionCount(LocalDate startDate, LocalDate endDate, Task task) {
        /* 初始化执行次数为0 */
        int count = 0;
        /* 获取任务的指定执行日期 */
        LocalDate taskDate = task.getTaskDate();
        /* 获取任务的重复结束日期，如果未指定则使用结束日期 */
        LocalDate repeatEnd = task.getRepeatEnd() != null ? task.getRepeatEnd().toLocalDate() : endDate;

        /* 如果任务日期为空或晚于结束日期，则不计算执行次数 */
        if (taskDate == null || taskDate.isAfter(endDate)) {
            return count;
        }

        /* 如果任务没有指定重复结束日期，则重复直到结束日期 */
        /* 如果重复结束时间为空，表示无限重复 */
        if (task.getRepeatEnd() == null) {
            repeatEnd = endDate;
        }

        /* 根据任务的重复模式计算执行次数 */
        switch (task.getRepeat()) {
            // 无重复
            case 0:
                // 如果任务日期在指定日期范围内，则执行次数加1
                if (!taskDate.isBefore(startDate) && !taskDate.isAfter(endDate)) {
                    count = 1;
                }
                break;
            // 每天
            case 1:
                // 计算指定日期范围内的天数
                count = (int) ChronoUnit.DAYS.between(
                        taskDate.isBefore(startDate) ? startDate : taskDate,
                        repeatEnd.isBefore(endDate) ? repeatEnd.plusDays(1) : endDate.plusDays(1)
                );
                break;
            // 每月
            case 2:
                // 调用计算每月重复次数的函数
                count = calculateMonthlyRepetitions(startDate, endDate, taskDate, repeatEnd);
                break;
            // 每年
            case 3:
                // 调用计算每年重复次数的函数
                count = calculateYearlyRepetitions(startDate, endDate, taskDate, repeatEnd);
                break;
            // 工作日
            case 4:
                // 调用计算工作日重复次数的函数
                count = calculateWorkingDaysRepetitions(startDate, endDate, taskDate, repeatEnd);
                break;
            // 法定工作日
            case 5:
                // 调用计算法定工作日重复次数的函数
                count = calculateLegalWorkingDays(startDate, endDate, taskDate, repeatEnd);
                break;
            // 艾宾浩斯记忆法
            case 6:
                // 调用计算艾宾浩斯重复次数的函数
                count = calculateEbbinghausRepetitions(taskDate, startDate, endDate, repeatEnd);
                break;
        }

        /* 返回计算得到的执行次数 */
        return count;
    }


    /**
     * 计算任务在给定时间段内的月重复次数。
     * 该方法用于确定一个任务从开始日期到结束日期，在每个月中出现的次数。
     * 任务的重复受到任务本身的开始日期、结束日期以及重复结束日期的限制。
     *
     * @param startDate 任务开始生效的日期，用于确定任务是否开始执行。
     * @param endDate   任务结束的日期，用于确定任务是否还在执行期内。
     * @param taskDate  任务的固定执行日期，每个月的这个日期任务将会执行。
     * @param repeatEnd 任务重复的结束日期，任务在这个日期之后将不再重复执行。
     * @return 返回任务在给定时间段内重复的次数。
     */
    private int calculateMonthlyRepetitions(LocalDate startDate, LocalDate endDate, LocalDate taskDate, LocalDate repeatEnd) {
        /* 初始化计数器，用于记录任务重复的次数。 */
        int count = 0;
        /* 初始化下一个月份的日期为任务日期，用于循环检查任务的重复情况。 */
        LocalDate nextMonthDate = taskDate;

        /* 循环直到下一个月份的日期超过重复结束日期或超过任务结束日期。 */
        while (!nextMonthDate.isAfter(repeatEnd) && !nextMonthDate.isAfter(endDate)) {
            /* 如果下一个月份的日期在任务开始日期之后，则计数器加一。 */
            if (!nextMonthDate.isBefore(startDate)) {
                count++;
            }
            /* 将下一个月份的日期设置为当前日期的下一个月，用于下一次循环检查。 */
            nextMonthDate = nextMonthDate.plusMonths(1);
        }

        /* 返回任务重复的次数。 */
        return count;
    }


    /**
     * 计算任务在给定日期范围内每年重复的次数。
     * 该方法用于确定一个任务从开始日期到结束日期内，每年重复的次数。
     * 任务的重复是基于任务的特定日期（taskDate）每年重复一次的假设。
     *
     * @param startDate 计算的起始日期，任务重复的计算不包括在此日期之前的年份。
     * @param endDate   计算的结束日期，任务重复的计算不包括在此日期之后的年份。
     * @param taskDate  任务的特定日期，每年的这个日期任务被认为重复一次。
     * @param repeatEnd 任务重复的结束日期，任务重复的计算不会超过这个日期。
     * @return 返回任务在给定日期范围内每年重复的次数。
     */
    private int calculateYearlyRepetitions(LocalDate startDate, LocalDate endDate, LocalDate taskDate, LocalDate repeatEnd) {
        /* 初始化计数器为0，用于记录任务每年重复的次数。 */
        int count = 0;
        /* 使用任务的特定日期作为起始点。 */
        LocalDate nextYearDate = taskDate;
        /* 循环遍历每年的日期，直到达到重复的结束日期或计算的结束日期。 */
        while (nextYearDate.isBefore(repeatEnd.plusDays(1)) && nextYearDate.isBefore(endDate.plusDays(1))) {
            /* 如果当前年的日期不早于开始日期，则计数器加1。 */
            if (!nextYearDate.isBefore(startDate)) {
                count++;
            }
            /* 将当前年的日期推进到下一年。 */
            nextYearDate = nextYearDate.plusYears(1);
        }
        /* 返回任务每年重复的次数。 */
        return count;
    }


    /**
     * 计算在给定日期范围内任务的重复次数。
     * 该方法用于确定一个任务在指定的开始日期和结束日期内，按照指定的重复结束日期，重复了多少次。
     * 仅当任务日期是工作日时才计数，并且计数不会超过重复结束日期或指定的结束日期。
     *
     * @param startDate 范围的开始日期
     * @param endDate   范围的结束日期
     * @param taskDate  任务的基准日期
     * @param repeatEnd 任务重复的结束日期
     * @return 任务在给定日期范围内重复的次数
     */
    private int calculateWorkingDaysRepetitions(LocalDate startDate, LocalDate endDate, LocalDate taskDate, LocalDate repeatEnd) {
        /* 初始化重复计数器 */
        int count = 0;
        /* 确定开始计算的日期，如果任务日期早于开始日期，则从开始日期开始 */
        LocalDate currentDate = taskDate.isBefore(startDate) ? startDate : taskDate;
        /* 当当前日期不超过重复结束日期且不超过范围结束日期时，循环计算 */
        while (!currentDate.isAfter(repeatEnd) && !currentDate.isAfter(endDate)) {
            /* 如果当前日期是工作日，则增加重复计数 */
            if (isWorkingDay(currentDate)) {
                count++;
            }
            /* 将当前日期推进到下一天 */
            currentDate = currentDate.plusDays(1);
        }
        /* 返回计算出的重复次数 */
        return count;
    }


    /**
     * 判断给定的日期是否为工作日。
     * 工作日定义为周一到周五。
     *
     * @param date 需要判断的日期。
     * @return 如果给定日期是工作日，则返回true；否则返回false。
     */
    private boolean isWorkingDay(LocalDate date) {
        // 通过获取日期对应的星期几的值（1表示周一，7表示周日），判断是否在工作日内。
        return date.getDayOfWeek().getValue() >= 1 && date.getDayOfWeek().getValue() <= 5;
    }


    /**
     * 计算在给定日期范围内任务的法定工作日重复次数。
     * 本函数用于确定一个任务在指定的日期范围内有多少个工作日是重复的。
     * 工作日的判断基于法定工作日的规则，由legalWorkingDayService确定。
     *
     * @param startDate 范围的起始日期，任务重复计算从这个日期开始。
     * @param endDate   范围的结束日期，任务重复计算到这个日期结束。
     * @param taskDate  任务的基准日期，即任务首次出现的日期。
     * @param repeatEnd 任务重复的结束日期，任务不会再这个日期之后重复。
     * @return 返回在指定范围内任务的法定工作日重复次数。
     */
    private int calculateLegalWorkingDays(LocalDate startDate, LocalDate endDate, LocalDate taskDate, LocalDate repeatEnd) {
        /* 初始化重复计数器 */
        int count = 0;
        /* 确定开始计算的日期，如果任务日期早于起始日期，则从起始日期开始 */
        LocalDate currentDate = taskDate.isBefore(startDate) ? startDate : taskDate;
        /* 当当前日期不超过重复结束日期且不超过结束日期时，循环检查每个日期 */
        while (!currentDate.isAfter(repeatEnd) && !currentDate.isAfter(endDate)) {
            /* 如果当前日期是法定工作日，则增加重复计数 */
            if (legalWorkingDayService.isLegalWorkingDay(currentDate)) {
                count++;
            }
            /* 移动到下一个日期 */
            currentDate = currentDate.plusDays(1);
        }
        /* 返回任务的法定工作日重复次数 */
        return count;
    }


    /**
     * 根据艾宾浩斯遗忘曲线计算在给定日期范围内任务的重复次数。
     * 艾宾浩斯遗忘曲线描述了人类在学习新信息后，遗忘的速度和程度，并提出了通过重复来强化记忆的理论。
     * 本方法计算从任务日期开始，按照特定间隔（1, 2, 4, 7, 15, 30 天）在给定的开始和结束日期范围内应该重复的次数。
     *
     * @param taskDate  任务的日期。
     * @param startDate 计算重复的开始日期。
     * @param endDate   计算重复的结束日期。
     * @param repeatEnd 重复的实际结束日期，用于限制重复的最晚日期。
     * @return 在给定日期范围内，根据艾宾浩斯遗忘曲线应该进行的重复次数。
     */
    private int calculateEbbinghausRepetitions(LocalDate taskDate, LocalDate startDate, LocalDate endDate, LocalDate repeatEnd) {
        /* 初始化重复计数器 */
        int count = 0;
        // 定义艾宾浩斯遗忘曲线的间隔天数数组
        int[] intervals = {1, 2, 4, 7, 15, 30};
        // 遍历每个间隔，计算应该进行的重复次数
        for (int interval : intervals) {
            /* 计算当前间隔后的重复日期 */
            LocalDate repetitionDate = taskDate.plusDays(interval);
            /* 检查重复日期是否在开始和结束日期范围内，并且不超过实际的重复结束日期 */
            if (!repetitionDate.isBefore(startDate) && !repetitionDate.isAfter(repeatEnd) && !repetitionDate.isAfter(endDate)) {
                /* 如果在范围内，增加重复计数 */
                count++;
            }
        }
        /* 返回计算出的重复次数 */
        return count;
    }


//    /**
//     * 根据日期范围获取任务执行详情
//     *
//     * @param startDate
//     * @param endDate
//     * @param userId
//     * @return
//     */
//    public TaskExecutionResult getTaskExecutionDetails(LocalDate startDate, LocalDate endDate, Long userId) {
//        List<Task> tasks = taskMapper.findTasksBetweenDates(startDate, endDate, userId);
//        int totalExecutionCount = 0;
//        List<LocalDate> executionDates = new ArrayList<>();
//
//        for (Task task : tasks) {
//            TaskExecutionDetails details = calculateExecutionDetails(startDate, endDate, task);
//            totalExecutionCount += details.getCount();
//            executionDates.addAll(details.getDates());
//        }
//
//        return new TaskExecutionResult(totalExecutionCount, executionDates);
//    }
//
//    /**
//     * 计算指定用户在指定日期范围内的任务完成度。
//     * 完成度定义为：已完成任务数量除以（已完成任务数量 + 未完成任务数量）乘以 100。
//     * 如果在指定日期范围内没有任务，或者任务总数为0，则完成度为0。
//     *
//     * @param startDate 计算任务完成度的起始日期。
//     * @param endDate   计算任务完成度的结束日期。
//     * @param userId    指定用户的ID。
//     * @return 返回指定用户在指定日期范围内的任务完成度，以长整型百分比表示。
//     */
//    public Long taskCompletion(LocalDate startDate, LocalDate endDate, Long userId) {
//        // 获取指定用户在指定日期范围内所有任务的执行次数。
//        Integer unAndComTotal = getTaskExecutionCount(startDate, endDate, userId);
//
//        // 获取指定用户在指定日期范围内已完成的任务数量。
//        Long comTotal = taskService.count(new LambdaQueryWrapper<Task>()
//                .eq(Task::getUserId, userId)
//                .eq(Task::getIsComplete, TaskConstants.TASK_COMMPLETE)
//                .eq(Task::getFlagDelete, TaskConstants.TASK_NOT_DELETE)
//                .ge(Task::getTaskDate, startDate)
//                .le(Task::getTaskDate, endDate));
//
//        // 计算完成度，如果任务总数为0，则完成度为0。
//        long completionRate = 0;
//        if (unAndComTotal > 0) {
//                completionRate = Math.round((double) comTotal / unAndComTotal * 100);
//        }
//
//        return completionRate;
//    }

//
//    /**
//     * 计算任务在指定日期范围内的执行详情
//     *
//     * @param startDate
//     * @param endDate
//     * @param task
//     * @return
//     */
//    private TaskExecutionDetails calculateExecutionDetails(LocalDate startDate, LocalDate endDate, Task task) {
//        int count = 0;
//        List<LocalDate> dates = new ArrayList<>();
//        LocalDate taskDate = task.getTaskDate();
//        LocalDate repeatEnd = task.getRepeatEnd() != null ? task.getRepeatEnd().toLocalDate() : endDate;
//
//
//        if (taskDate == null || taskDate.isAfter(endDate)) {
//            return new TaskExecutionDetails(count, dates);
//        }
//
//        if (task.getRepeatEnd() == null) {
//            repeatEnd = endDate;
//        }
//
//        switch (task.getRepeat()) {
//            case 0: // 无重复
//                if (!taskDate.isBefore(startDate) && !taskDate.isAfter(endDate)) {
//                    count = 1;
//                    dates.add(taskDate);
//                }
//                break;
//            case 1: // 每天
//                count = (int) ChronoUnit.DAYS.between(
//                        taskDate.isBefore(startDate) ? startDate : taskDate,
//                        repeatEnd.isBefore(endDate) ? repeatEnd.plusDays(1) : endDate.plusDays(1)
//                );
//                dates = generateDailyDates(taskDate, startDate, endDate, repeatEnd);
//                break;
//            case 2: // 每月
//                dates = generateMonthlyDates(taskDate, startDate, endDate, repeatEnd);
//                count = dates.size();
//                break;
//            case 3: // 每年
//                dates = generateYearlyDates(taskDate, startDate, endDate, repeatEnd);
//                count = dates.size();
//                break;
//            case 4: // 工作日
//                dates = generateWorkingDayDates(taskDate, startDate, endDate, repeatEnd);
//                count = dates.size();
//                break;
//            case 5: // 法定工作日
//                dates = generateLegalWorkingDayDates(taskDate, startDate, endDate, repeatEnd);
//                count = dates.size();
//                break;
//            case 6: // 艾宾浩斯记忆法
//                dates = generateEbbinghausDates(taskDate, startDate, endDate, repeatEnd);
//                count = dates.size();
//                break;
//        }
//
//        return new TaskExecutionDetails(count, dates);
//    }
//
//    /**
//     * 生成每日执行日期
//     *
//     * @param taskDate
//     * @param startDate
//     * @param endDate
//     * @param repeatEnd
//     * @return
//     */
//    private List<LocalDate> generateDailyDates(LocalDate taskDate, LocalDate startDate, LocalDate endDate, LocalDate repeatEnd) {
//        List<LocalDate> dates = new ArrayList<>();
//        LocalDate currentDate = taskDate.isBefore(startDate) ? startDate : taskDate;
//        while (!currentDate.isAfter(repeatEnd) && !currentDate.isAfter(endDate)) {
//            dates.add(currentDate);
//            currentDate = currentDate.plusDays(1);
//        }
//        return dates;
//    }
//
//    /**
//     * 生成每月执行日期
//     *
//     * @param taskDate
//     * @param startDate
//     * @param endDate
//     * @param repeatEnd
//     * @return
//     */
//    private List<LocalDate> generateMonthlyDates(LocalDate taskDate, LocalDate startDate, LocalDate endDate, LocalDate repeatEnd) {
//        List<LocalDate> dates = new ArrayList<>();
//        LocalDate currentDate = taskDate.withDayOfMonth(1).isBefore(startDate) ? startDate : taskDate.withDayOfMonth(1);
//        while (!currentDate.isAfter(repeatEnd) && !currentDate.isAfter(endDate)) {
//            dates.add(currentDate);
//            currentDate = currentDate.plusMonths(1);
//        }
//        return dates;
//    }
//
//    /**
//     * 生成艾宾浩斯记忆法执行日期
//     *
//     * @param taskDate
//     * @param startDate
//     * @param endDate
//     * @param repeatEnd
//     * @return
//     */
//    private List<LocalDate> generateYearlyDates(LocalDate taskDate, LocalDate startDate, LocalDate endDate, LocalDate repeatEnd) {
//        List<LocalDate> dates = new ArrayList<>();
//        LocalDate currentDate = taskDate.withDayOfYear(1).isBefore(startDate) ? startDate : taskDate.withDayOfYear(1);
//        while (!currentDate.isAfter(repeatEnd) && !currentDate.isAfter(endDate)) {
//            dates.add(currentDate);
//            currentDate = currentDate.plusYears(1);
//        }
//        return dates;
//    }
//
//    /**
//     * 判断是否为工作日
//     *
//     * @param taskDate
//     * @param startDate
//     * @param endDate
//     * @param repeatEnd
//     * @return
//     */
//    private List<LocalDate> generateWorkingDayDates(LocalDate taskDate, LocalDate startDate, LocalDate endDate, LocalDate repeatEnd) {
//        List<LocalDate> dates = new ArrayList<>();
//        LocalDate currentDate = taskDate.isBefore(startDate) ? startDate : taskDate;
//        while (!currentDate.isAfter(repeatEnd) && !currentDate.isAfter(endDate)) {
//            if (isWorkingDay(currentDate)) {
//                dates.add(currentDate);
//            }
//            currentDate = currentDate.plusDays(1);
//        }
//        return dates;
//    }
//
//    /**
//     * 获取任务在给定日期范围内的执行次数
//     *
//     * @param taskDate
//     * @param startDate
//     * @param endDate
//     * @param repeatEnd
//     * @return
//     */
//    private List<LocalDate> generateLegalWorkingDayDates(LocalDate taskDate, LocalDate startDate, LocalDate endDate, LocalDate repeatEnd) {
//        List<LocalDate> dates = new ArrayList<>();
//        LocalDate currentDate = taskDate.isBefore(startDate) ? startDate : taskDate;
//        while (!currentDate.isAfter(repeatEnd) && !currentDate.isAfter(endDate)) {
//            if (legalWorkingDayService.isLegalWorkingDay(currentDate)) {
//                dates.add(currentDate);
//            }
//            currentDate = currentDate.plusDays(1);
//        }
//        return dates;
//    }
//
//    /**
//     * 获取任务在给定日期范围内的执行次数
//     *
//     * @param taskDate
//     * @param startDate
//     * @param endDate
//     * @param repeatEnd
//     * @return
//     */
//    private List<LocalDate> generateEbbinghausDates(LocalDate taskDate, LocalDate startDate, LocalDate endDate, LocalDate repeatEnd) {
//        List<LocalDate> dates = new ArrayList<>();
//        int[] intervals = {1, 2, 4, 7, 15, 30}; // 艾宾浩斯记忆法间隔天数
//        for (int interval : intervals) {
//            LocalDate repetitionDate = taskDate.plusDays(interval);
//            if (!repetitionDate.isBefore(startDate) && !repetitionDate.isAfter(repeatEnd) && !repetitionDate.isAfter(endDate)) {
//                dates.add(repetitionDate);
//            }
//        }
//        return dates;
//    }
//
//
}









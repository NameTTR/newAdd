package com.family.pl.controller;

import com.family.pl.domain.DTO.request.IntervalDateDTO;
import com.family.pl.service.StatisticsDateService;
import com.family.pl.service.StatisticsWeekService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

/**
 * <p>
 * 控制器类，负责处理与计划日期（周）相关的HTTP请求。
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
@RestController
@RequestMapping("/family/pl/week")
public class StatisticsWeekController extends BaseController {

    @Autowired
    private StatisticsWeekService statisticsWeekService;

    @Autowired
    private StatisticsDateService statisticsDateService;


    /**
     * 获取本周数据
     * 开发中
     *
     * @param
     * @return
     */
    @GetMapping()
    public AjaxResult total(@RequestParam LocalDate startDate,
                            @RequestParam LocalDate endDate) {
        // 检查intervalDateVO中的起始日期和结束日期是否为空，如果为空则抛出IllegalArgumentException异常
        Optional.ofNullable(startDate).orElseThrow(() -> new IllegalArgumentException("起始日期不能为空"));
        Optional.ofNullable(endDate).orElseThrow(() -> new IllegalArgumentException("结束日期不能为空"));
        return AjaxResult.success(statisticsWeekService.selectTotal(startDate, endDate));
    }

    /**
     * 通过Get请求处理统计完成情况的接口。
     * 开发中
     * @param intervalDateDTO 包含日期区间信息的VO对象，用于指定统计的时间范围。
     * @return 返回统计结果的成功响应。统计结果由statisticsWeekService的selectCompletion方法计算得出。
     */
    @GetMapping("completion")
    public AjaxResult selectCompletion(@RequestBody IntervalDateDTO intervalDateDTO) {
        // 检查intervalDateVO中的起始日期和结束日期是否为空，如果为空则抛出IllegalArgumentException异常
        Optional.ofNullable(intervalDateDTO.getStartDate()).orElseThrow(() -> new IllegalArgumentException("起始日期不能为空"));
        Optional.ofNullable(intervalDateDTO.getEndDate()).orElseThrow(() -> new IllegalArgumentException("结束日期不能为空"));
        // 默认用户ID，此处硬编码为1L，实际应用中应根据实际情况获取用户ID。
        Long userId = 1L;
        return AjaxResult.success(statisticsWeekService.selectCompletion(intervalDateDTO.getStartDate(), intervalDateDTO.getEndDate()));
    }

    /**
     * 开发中
     * @param intervalDateDTO
     * @return
     */
    @PostMapping("addweek")
    public AjaxResult addWeek(@RequestBody IntervalDateDTO intervalDateDTO) {
        Optional.ofNullable(intervalDateDTO.getStartDate()).orElseThrow(() -> new IllegalArgumentException("起始日期不能为空"));
        Optional.ofNullable(intervalDateDTO.getEndDate()).orElseThrow(() -> new IllegalArgumentException("结束日期不能为空"));
        Optional.ofNullable(intervalDateDTO.getWeek()).orElseThrow(() -> new IllegalArgumentException("周数不能为空"));
        Long userId = 1L;
        return toAjax(statisticsWeekService.addWeek(intervalDateDTO.getStartDate(), intervalDateDTO.getEndDate(), intervalDateDTO.getWeek(), userId));
    }

//    /**
//     * 通过POST请求获取指定时间段内的任务执行次数。
//     *
//     * @param intervalDateVO 包含起始日期和结束日期的间隔日期VO对象，用于指定统计的时间范围。
//     * @return 返回指定时间段内任务的执行次数。
//     */
//    @PostMapping("/untaskcount")
//    public int getTaskExecutionCount(@RequestBody IntervalDateVO intervalDateVO) {
//        // 检查intervalDateVO中的起始日期和结束日期是否为空，如果为空则抛出IllegalArgumentException异常
//        Optional.ofNullable(intervalDateVO.getStartDate()).orElseThrow(() -> new IllegalArgumentException("起始日期不能为空"));
//        Optional.ofNullable(intervalDateVO.getEndDate()).orElseThrow(() -> new IllegalArgumentException("结束日期不能为空"));
//        // 默认用户ID，此处硬编码为1L，实际应用中应根据实际情况获取用户ID。
//        Long userId = 1L;
//        // 调用统计周服务，获取指定时间段内用户任务的执行次数。
//        return statisticsWeekService.getTaskExecutionCount(intervalDateVO.getStartDate(), intervalDateVO.getEndDate(), userId);
//    }
//
//    /**
//     * 通过POST请求获取任务执行详情。
//     * <p>
//     * 此方法接收一个IntervalDateVO对象作为请求体，该对象包含了查询任务执行详情所需的起始日期和结束日期。
//     * 方法内部固定使用了一个示例用户ID来查询指定时间范围内的任务执行详情。
//     *
//     * @param intervalDateVO 包含查询日期范围的VO对象，起始日期和结束日期通过此对象传递。
//     * @return TaskExecutionResult 返回查询到的任务执行详情结果。
//     */
//    @PostMapping("/execution")
//    public TaskExecutionResult getTaskExecution(@RequestBody IntervalDateVO intervalDateVO) {
//        // 检查intervalDateVO中的起始日期和结束日期是否为空，如果为空则抛出IllegalArgumentException异常
//        Optional.ofNullable(intervalDateVO.getStartDate()).orElseThrow(() -> new IllegalArgumentException("起始日期不能为空"));
//        Optional.ofNullable(intervalDateVO.getEndDate()).orElseThrow(() -> new IllegalArgumentException("结束日期不能为空"));
//        // 固定使用的用户ID，用于查询任务执行详情
//        Long userId = 1L;
//        // 调用统计服务，查询指定日期范围和用户ID的任务执行详情
//        return statisticsWeekService.getTaskExecutionDetails(intervalDateVO.getStartDate(), intervalDateVO.getEndDate(), userId);
//    }

}
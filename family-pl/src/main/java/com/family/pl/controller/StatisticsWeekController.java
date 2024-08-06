package com.family.pl.controller;

import com.family.pl.domain.StatisticsDate;
import com.family.pl.domain.VO.request.DateVO;
import com.family.pl.domain.VO.request.IntervalDateVO;
import com.family.pl.domain.test.TaskExecutionResult;
import com.family.pl.service.StatisticsDateService;
import com.family.pl.service.StatisticsWeekService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 功能：
 * 作者：Name
 * 日期：2024/7/10 10:41
 */
@RestController
@RequestMapping("/family/statistics")
public class StatisticsController extends BaseController {

    @Autowired
    private StatisticsWeekService statisticsWeekService;

    @Autowired
    private StatisticsDateService statisticsDateService;


    /**
     * 获取本周数据
     * 开发中
     * @param intervalDateVO
     * @return
     */
    @PostMapping("total")
    public AjaxResult total(@RequestBody IntervalDateVO intervalDateVO){
        // 检查intervalDateVO中的起始日期和结束日期是否为空，如果为空则抛出IllegalArgumentException异常
        Optional.ofNullable(intervalDateVO.getStartDate()).orElseThrow(() -> new IllegalArgumentException("起始日期不能为空"));
        Optional.ofNullable(intervalDateVO.getEndDate()).orElseThrow(() -> new IllegalArgumentException("结束日期不能为空"));
        return AjaxResult.success(statisticsWeekService.selectTotal(intervalDateVO));
    }

    /**
     * 通过POST请求处理统计完成情况的接口。
     *
     * @param intervalDateVO 包含日期区间信息的VO对象，用于指定统计的时间范围。
     * @return 返回统计结果的成功响应。统计结果由statisticsWeekService的selectCompletion方法计算得出。
     */
    @PostMapping("selectcompletion")
    public AjaxResult selectCompletion(@RequestBody IntervalDateVO intervalDateVO){
        // 检查intervalDateVO中的起始日期和结束日期是否为空，如果为空则抛出IllegalArgumentException异常
        Optional.ofNullable(intervalDateVO.getStartDate()).orElseThrow(() -> new IllegalArgumentException("起始日期不能为空"));
        Optional.ofNullable(intervalDateVO.getEndDate()).orElseThrow(() -> new IllegalArgumentException("结束日期不能为空"));
        // 默认用户ID，此处硬编码为1L，实际应用中应根据实际情况获取用户ID。
        Long userId = 1L;
        return AjaxResult.success(statisticsWeekService.selectCompletion(intervalDateVO.getStartDate(), intervalDateVO.getEndDate()));
    }

    /**
     * 通过POST请求获取指定时间段内的任务执行次数。
     *
     * @param intervalDateVO 包含起始日期和结束日期的间隔日期VO对象，用于指定统计的时间范围。
     * @return 返回指定时间段内任务的执行次数。
     */
    @PostMapping("/untaskcount")
    public int getTaskExecutionCount(@RequestBody IntervalDateVO intervalDateVO) {
        // 检查intervalDateVO中的起始日期和结束日期是否为空，如果为空则抛出IllegalArgumentException异常
        Optional.ofNullable(intervalDateVO.getStartDate()).orElseThrow(() -> new IllegalArgumentException("起始日期不能为空"));
        Optional.ofNullable(intervalDateVO.getEndDate()).orElseThrow(() -> new IllegalArgumentException("结束日期不能为空"));
        // 默认用户ID，此处硬编码为1L，实际应用中应根据实际情况获取用户ID。
        Long userId = 1L;
        // 调用统计周服务，获取指定时间段内用户任务的执行次数。
        return statisticsWeekService.getTaskExecutionCount(intervalDateVO.getStartDate(), intervalDateVO.getEndDate(), userId);
    }

    /**
     * 通过POST请求获取任务执行详情。
     *
     * 此方法接收一个IntervalDateVO对象作为请求体，该对象包含了查询任务执行详情所需的起始日期和结束日期。
     * 方法内部固定使用了一个示例用户ID来查询指定时间范围内的任务执行详情。
     *
     * @param intervalDateVO 包含查询日期范围的VO对象，起始日期和结束日期通过此对象传递。
     * @return TaskExecutionResult 返回查询到的任务执行详情结果。
     */
    @PostMapping("/execution")
    public TaskExecutionResult getTaskExecution(@RequestBody IntervalDateVO intervalDateVO) {
        // 检查intervalDateVO中的起始日期和结束日期是否为空，如果为空则抛出IllegalArgumentException异常
        Optional.ofNullable(intervalDateVO.getStartDate()).orElseThrow(() -> new IllegalArgumentException("起始日期不能为空"));
        Optional.ofNullable(intervalDateVO.getEndDate()).orElseThrow(() -> new IllegalArgumentException("结束日期不能为空"));
        // 固定使用的用户ID，用于查询任务执行详情
        Long userId = 1L;
        // 调用统计服务，查询指定日期范围和用户ID的任务执行详情
        return statisticsWeekService.getTaskExecutionDetails(intervalDateVO.getStartDate(), intervalDateVO.getEndDate(), userId);
    }

    /**
     * 通过POST请求添加日期信息。
     *
     * @param dateVO 包含日期数据的VO对象，通过@RequestBody注解绑定请求体中的数据。
     * @return 返回添加操作的结果，包含成功与否、错误码和错误信息等。
     */
    @PostMapping("/addorupdate")
    public AjaxResult addOrUpdateDate(@RequestBody DateVO dateVO){
        // 检查dateVO中的日期是否为空，如果为空则抛出IllegalArgumentException异常
        Optional.ofNullable(dateVO.getDate()).orElseThrow(() -> new IllegalArgumentException("日期不能为空"));
        // 调用统计日期服务的addDate方法添加日期信息，并将结果转换为AjaxResult返回给前端
        return toAjax(statisticsDateService.addOrUpdateDate(dateVO.getDate()));
    }




    /**
     * 通过POST请求删除日期信息。
     *
     * @param dateVO 包含日期数据的VO对象，通过@RequestBody注解绑定请求体中的数据。
     * @return 返回删除操作的结果，包含成功与否、错误码和错误信息等。
     */
    @DeleteMapping("/deletedate")
    public AjaxResult deleteDate(@RequestBody DateVO dateVO){
        // 检查dateVO中的日期是否为空，如果为空则抛出IllegalArgumentException异常
        Optional.ofNullable(dateVO.getDate()).orElseThrow(() -> new IllegalArgumentException("日期不能为空"));
        // 调用统计日期服务的deleteDate方法删除日期信息，并将结果转换为AjaxResult返回给前端
        return toAjax(statisticsDateService.deleteDate(dateVO));
    }

    /**
     * 根据传入的日期查询相关信息。
     * 该方法通过POST请求访问，路径为/selectdate。
     * 接收一个DateVO对象作为请求体，用于传递查询日期。
     * 返回一个AjaxResult对象，包含查询结果或错误信息。
     *
     * @param dateVO 日期查询对象，包含待查询的日期。
     * @return 返回AjaxResult对象，其中包含查询到的StatisticsDate对象或错误信息。
     * @throws IllegalArgumentException 如果日期VO中的日期为空，或者根据该日期查询不到相应的StatisticsDate对象，则抛出此异常。
     */
    @PostMapping("/selectdate")
    public AjaxResult selectDate(@RequestBody DateVO dateVO){
        // 检查dateVO中的日期是否为空，如果为空则抛出IllegalArgumentException异常
        Optional.ofNullable(dateVO.getDate()).orElseThrow(() -> new IllegalArgumentException("日期不能为空"));
        // 如果查询结果为空，则抛出IllegalArgumentException异常
        // 调用统计日期服务的selectDate方法获取日期信息，并将结果转换为AjaxResult返回给前端
        StatisticsDate statisticsDate = Optional.ofNullable(statisticsDateService.selectDate(dateVO.getDate())).orElseThrow(() -> new IllegalArgumentException("日期不存在"));
        // 返回查询成功的AjaxResult，其中包含StatisticsDate对象
        return AjaxResult.success(statisticsDate);
    }




}
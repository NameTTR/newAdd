package com.family.pl.controller;

import com.family.pl.domain.StatisticsDate;
import com.family.pl.domain.DTO.request.DateDTO;
import com.family.pl.service.StatisticsDateService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

/**
 * <p>
 * 控制器类，负责处理与家庭计划日期相关的HTTP请求。
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-11
 */
@RestController
@RequestMapping("/family/pl/date")
public class StatisticsDateController extends BaseController {

    @Autowired
    private StatisticsDateService statisticsDateService;

    /**
     * 通过POST请求添加日期信息。
     *
     * @param date 包含日期数据的VO对象，通过@RequestBody注解绑定请求体中的数据。
     * @return 返回添加操作的结果，包含成功与否、错误码和错误信息等。
     */
    @PostMapping("/{date}")
    public AjaxResult addOrUpdateDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        // 检查dateVO中的日期是否为空，如果为空则抛出IllegalArgumentException异常
        Optional.ofNullable(date).orElseThrow(() -> new IllegalArgumentException("日期不能为空"));
        // 调用统计日期服务的addDate方法添加日期信息，并将结果转换为AjaxResult返回给前端
        return toAjax(statisticsDateService.addOrUpdateDate(date));
    }

    /**
     * 通过日期删除统计数据。
     *
     * @param date 需要删除的日期，使用LocalDate表示，通过@DateTimeFormat注解确保输入格式正确。
     * @return 返回AjaxResult对象，包含操作结果的信息。
     * @throws IllegalArgumentException 如果日期为null，抛出此异常。
     * @DeleteMapping 注解指明此方法处理DELETE类型的HTTP请求，路径为/{date}。
     */
    @DeleteMapping("/{date}")
    public AjaxResult deleteDate(@RequestBody @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        // 检查日期是否为空，如果为空则抛出IllegalArgumentException异常
        Optional.ofNullable(date).orElseThrow(() -> new IllegalArgumentException("日期不能为空"));
        // 调用statisticsDateService的deleteDate方法删除指定日期的数据，并将结果转换为AjaxResult返回
        return toAjax(statisticsDateService.deleteDate(date));
    }

    /**
     * 根据传入的日期查询相关信息。
     * 该方法通过Get请求访问，路径为/selectdate。
     * 接收一个DateVO对象作为请求体，用于传递查询日期。
     * 返回一个AjaxResult对象，包含查询结果或错误信息。
     *
     * @param date 日期查询对象，包含待查询的日期。
     * @return 返回AjaxResult对象，其中包含查询到的StatisticsDate对象或错误信息。
     * @throws IllegalArgumentException 如果日期VO中的日期为空，或者根据该日期查询不到相应的StatisticsDate对象，则抛出此异常。
     */
    @GetMapping("/{date}")
    public AjaxResult selectDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        // 检查dateVO中的日期是否为空，如果为空则抛出IllegalArgumentException异常
        Optional.ofNullable(date).orElseThrow(() -> new IllegalArgumentException("日期不能为空"));
        // 如果查询结果为空，则抛出IllegalArgumentException异常
        // 调用统计日期服务的selectDate方法获取日期信息，并将结果转换为AjaxResult返回给前端
        StatisticsDate statisticsDate = Optional.ofNullable(statisticsDateService.selectDate(date)).orElseThrow(() -> new IllegalArgumentException("日期不存在"));
        // 返回查询成功的AjaxResult，其中包含StatisticsDate对象
        return AjaxResult.success(statisticsDate);
    }
}
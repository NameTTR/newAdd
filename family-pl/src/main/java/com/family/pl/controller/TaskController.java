package com.family.pl.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.family.common.util.ExceptionLogUtil;
import com.family.pl.domain.DTO.request.AddTaskDTO;
import com.family.pl.domain.DTO.request.DateTimeDTO;
import com.family.pl.domain.DTO.request.UpdateTaskDTO;
import com.family.pl.domain.DTO.response.SelectTaskDTO;
import com.family.pl.service.TaskService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.job.TaskException;
import com.ruoyi.common.utils.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.LocalDate;

/**
 * <p>
 * 任务控制器，负责处理与任务相关的HTTP请求。
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
@RestController
@RequestMapping("/family/pl/task")
public class TaskController extends BaseController {

    @Autowired
    private TaskService taskService;

    /**
     * 根据指定日期查询已完成的任务。
     *
     * @param date    查询日期。
     * @param pageNum 分页页码。
     * @return 包含已完成任务的分页结果。
     */
    @GetMapping("/complete/time")
    public AjaxResult selectComTaskByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        IPage<SelectTaskDTO> SelectTaskVOS = taskService.selectCompleteTasks(pageNum, date);
        return AjaxResult.success(SelectTaskVOS);
    }

    /**
     * 根据指定日期查询未完成且未超时的任务。
     *
     * @param date    查询日期。
     * @param pageNum 分页页码。
     * @return 包含未完成任务的分页结果。
     */
    @GetMapping("/incomplete/in_time")
    public AjaxResult selectUnComTaskByInDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        IPage<SelectTaskDTO> SelectTaskVOS = taskService.selectInCompleteTasksAndInTime(pageNum, date);
        return AjaxResult.success(SelectTaskVOS);
    }

    /**
     * 根据指定日期查询未完成且已超时的任务。
     *
     * @param date 查询日期。
     * @param pageNum 分页页码。
     * @return 包含未完成任务的分页结果。
     */
    @GetMapping("/incomplete/out_time")
    public AjaxResult selectUnComTaskByOutDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        IPage<SelectTaskDTO> SelectTaskVOS = taskService.selectInCompleteTaskAndOutTime(pageNum, date);
        return AjaxResult.success(SelectTaskVOS);
    }

    /**
     * 根据优先级选择任务
     * 通过GET请求，使用AjaxResult封装返回结果，提供前端展示或操作
     * 主要用于根据指定日期和页数获取任务列表，以便于前端根据优先级进行任务管理
     *
     * @param date    任务日期，用于筛选特定日期的任务
     * @param pageNum 页码，用于分页查询任务，默认为第1页
     * @return 返回AjaxResult对象，包含查询到的任务信息
     */
    @GetMapping("/incomplete/priority")
    public AjaxResult selectInTaskOrderPriority(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        IPage<SelectTaskDTO> SelectTaskVOS = taskService.selectInTaskOrderPriority(pageNum, date);
        return AjaxResult.success(SelectTaskVOS);
    }

    /**
     * 根据优先级查询已完成的任务
     *
     * @param date    任务完成的日期
     * @param pageNum 页码，默认为1
     * @return 返回AjaxResult对象，其中包含查询结果
     */
    @GetMapping("/complete/priority")
    public AjaxResult selectComTaskOrderPriority(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        IPage<SelectTaskDTO> SelectTaskVOS = taskService.selectComTaskOrderPriority(pageNum, date);
        return AjaxResult.success(SelectTaskVOS);
    }

    /**
     * 根据优先级查询任务订单
     * <p>
     * 此方法用于根据指定的优先级、日期和页码查询任务订单它通过Ajax请求与前端进行交互，
     * 返回一个AjaxResult对象，其中包含查询结果
     *
     * @param priority 任务订单的优先级，用于筛选查询结果
     * @param date     查询日期，用于筛选特定日期的任务订单
     * @param pageNum  页码，默认为1，用于分页查询
     * @return 返回AjaxResult对象，其中包含查询到的任务订单数据
     */
    @GetMapping("/priority/{priority}")
    public AjaxResult selectInTaskOrderPriority(@PathVariable(value = "priority") Integer priority, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        IPage<SelectTaskDTO> SelectTaskVOS = taskService.selectInTaskOrderPriority(pageNum, date, priority);
        return AjaxResult.success(SelectTaskVOS);
    }

    /**
     * 根据任务ID查询任务详情。
     *
     * @param taskId 任务ID。
     * @return 任务详情。
     */
    @GetMapping("/{taskId}")
    public AjaxResult selectTaskById(@PathVariable Long taskId) {
        SelectTaskDTO selectTaskDTO = taskService.selectTaskById(taskId);
        if (StringUtils.isNull(selectTaskDTO)) {
            return AjaxResult.error("该任务不存在");
        }
        return AjaxResult.success(selectTaskDTO);
    }

    /**
     * 添加新任务。
     *
     * @param AddTaskDTO 新任务详情。
     * @return 添加结果。
     */
    @PostMapping("/new")
    public AjaxResult addTask(@RequestBody AddTaskDTO AddTaskDTO) {
        int flag = 1;
        try {
            flag = taskService.addTask(AddTaskDTO);
        } catch (SchedulerException e) {
            ExceptionLogUtil.saveExceptionLog(e);
            return AjaxResult.error("添加任务失败");
        } catch (TaskException e) {
            ExceptionLogUtil.saveExceptionLog(e);
            return AjaxResult.error("添加任务失败");
        } catch (ParseException e) {
            ExceptionLogUtil.saveExceptionLog(e);
            return AjaxResult.error("系统错误，添加任务失败");
        }
        return toAjax(flag);
    }

    /**
     * 将任务标记为已完成。
     *
     * @param dateTimeDTO 包含任务ID和完成时间的信息。
     * @return 操作结果。
     */
    @PutMapping("/complete")
    public AjaxResult comTask(@RequestBody DateTimeDTO dateTimeDTO) {
        return toAjax(taskService.comTask(dateTimeDTO));
    }

    /**
     * 删除指定任务。
     *
     * @param taskId 任务ID。
     * @return 删除结果。
     */
    @DeleteMapping("{taskId}")
    public AjaxResult delTask(@PathVariable Long taskId) throws SchedulerException {
        return toAjax(taskService.delTask(taskId));
    }

    /**
     * 将已完成的任务标记为未完成。
     *
     * @param taskId 任务ID。
     * @return 操作结果。
     */
    @PutMapping("/incomplete/{taskId}")
    public AjaxResult unComTask(@PathVariable Long taskId) {
        try {
            taskService.unComTask(taskId);
        } catch (SchedulerException e) {
            ExceptionLogUtil.saveExceptionLog(e);
            return AjaxResult.error("添加任务失败");
        } catch (ParseException e) {
            ExceptionLogUtil.saveExceptionLog(e);
            return AjaxResult.error("系统错误，添加任务失败");
        } catch (TaskException e) {
            ExceptionLogUtil.saveExceptionLog(e);
            return AjaxResult.error("添加任务失败");
        }
        return AjaxResult.success();
    }

    /**
     * 更新现有任务的信息。
     *
     * @param updateTaskDTO 包含任务更新信息的数据传输对象。
     * @return 更新结果。
     */
    @PutMapping()
    public AjaxResult updateTask(@RequestBody UpdateTaskDTO updateTaskDTO) {
        try {
            taskService.updateTask(updateTaskDTO);
        } catch (ParseException e) {
            ExceptionLogUtil.saveExceptionLog(e);
            return AjaxResult.error("系统错误，添加任务失败");
        } catch (TaskException e) {
            ExceptionLogUtil.saveExceptionLog(e);
            return AjaxResult.error("添加任务失败");
        } catch (SchedulerException e) {
            ExceptionLogUtil.saveExceptionLog(e);
            return AjaxResult.error("添加任务失败");
        }
        return AjaxResult.success();
    }


}
































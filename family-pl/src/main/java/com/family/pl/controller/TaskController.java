package com.family.pl.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.family.pl.constant.TaskConstants;
import com.family.pl.domain.Label;
import com.family.pl.domain.Task;
import com.family.pl.domain.VO.*;
import com.family.pl.service.LabelService;
import com.family.pl.service.TaskService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.job.TaskException;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 功能：
 * 作者：Name
 * 日期：2024/7/5 16:43
 */
@RestController
@RequestMapping("/family/task")
public class TaskController extends BaseController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private LabelService labelService;

    /**
     * 根据日期查询该日期已完成任务
     * @param dateTimeVO
     * @return
     */
    @GetMapping("selectcomtaskbydate")
    public AjaxResult selectComTaskByDate(@RequestBody DateTimeVO dateTimeVO,
                                          @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        IPage<SelectTaskVO> SelectTaskVOS = taskService.selectCompleteTasks(pageNum, dateTimeVO);
        return AjaxResult.success(SelectTaskVOS);
    }

    /**
     * 查询该日期未完成任务
     * @param dateTimeVO
     * @param pageNum
     * @return 该日期任务
     */
    @GetMapping("selectuncomtaskbydate")
    public AjaxResult selectUnComTaskByDate(@RequestBody DateTimeVO dateTimeVO,
                                            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        IPage<SelectTaskVO> SelectTaskVOS = taskService.selectUnCompleteTasks(pageNum, dateTimeVO);
        return AjaxResult.success(SelectTaskVOS);
    }

    
    /**
     * 根据Id查询任务
     * @param taskId
     * @return 任务
     */
    @GetMapping("selecttaskbyid")
    public AjaxResult selectTaskById(@RequestParam("taskId") Long taskId) {
        SelectTaskVO selectTaskVO = taskService.selectTaskById(taskId);
        return AjaxResult.success(selectTaskVO);
    }

    /**
     * 添加任务
     * @param AddTaskVO
     * @return 任务
     */
    @PostMapping("addtask")
    public AjaxResult addTask(@RequestBody AddTaskVO AddTaskVO) throws SchedulerException, TaskException {;
        return toAjax(taskService.addTask(AddTaskVO));
    }

    /**
     * 由未完成任务转完成任务
     * @param dateTimeVO
     * @return 任务
     */
    @PostMapping("comtask")
    public AjaxResult comTask(@RequestBody DateTimeVO dateTimeVO) {
        return toAjax(taskService.comTask(dateTimeVO));
    }

    /**
     * 删除任务
     * @param taskId
     * @return 任务
     */
    @DeleteMapping("deltask")
    public AjaxResult delTask(@RequestParam("taskId") Long taskId) throws SchedulerException {
        return toAjax(taskService.delTask(taskId));
    }

    /**
     * 已完成任务转未完成任务
     * @param taskId
     * @return 任务
     */
    @PostMapping("uncomtask")
    public AjaxResult unComTask(@RequestParam("taskId") Long taskId) throws SchedulerException, TaskException {
        return toAjax(taskService.unComTask(taskId));
    }

    /**
     * 修改任务
     * @param updateTaskVO
     * @return 任务
     */
    @PutMapping("updatetask")
    public AjaxResult updateTask(@RequestBody UpdateTaskVO updateTaskVO) throws SchedulerException, TaskException {
        return toAjax(taskService.updateTask(updateTaskVO));
    }

}































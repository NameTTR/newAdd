package com.family.pl.controller;

import com.family.pl.constant.TaskConstants;
import com.family.pl.domain.Task;
import com.family.pl.domain.VO.*;
import com.family.pl.service.TaskService;
import com.family.us.controller.FamilyBaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.job.TaskException;
import org.aspectj.weaver.loadtime.Aj;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 功能：
 * 作者：Name
 * 日期：2024/5/16 15:55
 */
@RestController
@RequestMapping("/family/task")
public class TaskController extends FamilyBaseController {

    @Autowired
    private TaskService taskService;

    /**
     * 根据日期查询该日期任务
     *
     * @param dateTimeVO
     * @return 该日期任务
     */
    @GetMapping("selectcomtaskbydate")
    public AjaxResult selectComTaskByDate(@RequestBody DateTimeVO dateTimeVO) {
        startPage();
        List<Task> tasks = taskService.selectCompleteTasks(dateTimeVO);
        return AjaxResult.success(tasks);
    }

    @GetMapping("selectdistaskbydate")
    public AjaxResult selectDisTaskByDate(@RequestBody DateTimeVO dateTimeVO) {
        startPage();
        List<Task> tasks = taskService.selectDisCompleteTasks(dateTimeVO);
        return AjaxResult.success(tasks);
    }

    /**
     * 添加任务
     *
     * @param addTaskVO
     * @return
     * @throws SchedulerException
     * @throws TaskException
     */
    @PostMapping("addtask")
    public AjaxResult addTask(@RequestBody AddTaskVO addTaskVO) throws SchedulerException, TaskException {
        return toAjax(taskService.addTask(addTaskVO));
    }

    /**
     * 完成任务
     *
     * @param dateTimeVO
     * @return
     * @throws SchedulerException
     */
    @PostMapping("taskcompletebyid")
    public AjaxResult taskCompleteById(@RequestBody DateTimeVO dateTimeVO) throws SchedulerException {
        return toAjax(taskService.taskCompleteById(dateTimeVO));
    }

    /**
     * 由完成的任务转为未完成的任务
     *
     * @param dateTimeVO
     * @return
     * @throws SchedulerException
     */
    @PostMapping("taskdiscompletebyid")
    public AjaxResult taskDisCompleteById(@RequestBody DateTimeVO dateTimeVO) throws SchedulerException, TaskException {
        return toAjax(taskService.taskDisCompleteById(dateTimeVO));
    }

    /**
     * 根据id删除一个任务
     *
     * @param dateTimeVO
     * @return
     * @throws SchedulerException
     * @throws TaskException
     */
    @PostMapping("taskdeletebyid")
    public AjaxResult taskDeleteById(@RequestBody DateTimeVO dateTimeVO) throws SchedulerException, TaskException {
        int flag = 1;
        Long taskId = dateTimeVO.getId();
        Task task = taskService.getById(taskId);
        if (task.getRepeat() != TaskConstants.TASK_NOT_REAPRAT) {
            return AjaxResult.warn("该任务存在重复设置");
        } else if ((task.getRepeat() == TaskConstants.TASK_NOT_REAPRAT)
                || task.getIsComplete() == TaskConstants.TASK_COMMPLETE) {
            flag = taskService.taskDeleteOneById(dateTimeVO);
        }
        return toAjax(flag);
    }

    /**
     * 根据id删除所有任务
     *
     * @param dateTimeVO
     * @return
     * @throws SchedulerException
     * @throws TaskException
     */
    @PostMapping("taskdeleteallbyid")
    public AjaxResult taskDeleteAllById(@RequestBody DateTimeVO dateTimeVO) throws SchedulerException, TaskException {
        return toAjax(taskService.taskDeleteAllById(dateTimeVO));
    }

    /**
     * 根据id修改任务标题
     *
     * @param taskTitleVO
     * @return
     * @throws SchedulerException
     * @throws TaskException
     */
    @PostMapping("taskupdatetitlebyid")
    public AjaxResult taskUpdateTitleById(@RequestBody TaskTitleVO taskTitleVO) throws SchedulerException, TaskException {
        int flag = 1;
        Long taskId = taskTitleVO.getId();
        Task task = taskService.getById(taskId);
        if (task.getRepeat() != TaskConstants.TASK_NOT_REAPRAT) {
            return AjaxResult.warn("该任务存在重复设置");
        } else if ((task.getRepeat() == TaskConstants.TASK_NOT_REAPRAT)
                || task.getIsComplete() == TaskConstants.TASK_COMMPLETE) {
            flag = taskService.taskUpdateTitleById(taskTitleVO);
        }
        return toAjax(flag);
    }

    /**
     * 确认修改任务标题
     *
     * @param taskTitleVO
     * @return
     * @throws SchedulerException
     * @throws TaskException
     */
    @PostMapping("confirmupdatetile")
    public AjaxResult confirmUpdateTile(@RequestBody TaskTitleVO taskTitleVO) throws SchedulerException, TaskException {
        return toAjax(taskService.taskUpdateTitleById(taskTitleVO));
    }

    /**
     * 根据id修改任务优先级
     *
     * @param taskPriorityVO
     * @return
     * @throws SchedulerException
     * @throws TaskException
     */
    @PostMapping("taskupdateprioritybyid")
    public AjaxResult taskUpdatePriorityById(@RequestBody TaskPriorityVO taskPriorityVO) throws SchedulerException, TaskException {
        int flag = 1;
        Long taskId = taskPriorityVO.getId();
        Task task = taskService.getById(taskId);
        if (task.getRepeat() != TaskConstants.TASK_NOT_REAPRAT) {
            return AjaxResult.warn("该任务存在重复设置");
        } else if ((task.getRepeat() == TaskConstants.TASK_NOT_REAPRAT)
                || task.getIsComplete() == TaskConstants.TASK_COMMPLETE) {
            flag = taskService.taskUpdatePriorityById(taskPriorityVO);
        }
        return toAjax(flag);
    }

    /**
     * 确认修改任务优先级
     *
     * @param taskPriorityVO
     * @return
     * @throws SchedulerException
     * @throws TaskException
     */
    @PostMapping("confirmupdatepriority")
    public AjaxResult confirmUpdatePriority(@RequestBody TaskPriorityVO taskPriorityVO) throws SchedulerException, TaskException {
        return toAjax(taskService.taskUpdatePriorityById(taskPriorityVO));
    }

    /**
     * 根据id修改任务说明
     *
     * @param taskNotesVO
     * @return
     * @throws SchedulerException
     * @throws TaskException
     */
    @PostMapping("taskupdatenotesbyid")
    public AjaxResult taskUpdateNotesById(@RequestBody TaskNotesVO taskNotesVO) throws SchedulerException, TaskException {
        int flag = 1;
        Long taskId = taskNotesVO.getId();
        Task task = taskService.getById(taskId);
        if (task.getRepeat() != TaskConstants.TASK_NOT_REAPRAT) {
            return AjaxResult.warn("该任务存在重复设置");
        } else if ((task.getRepeat() == TaskConstants.TASK_NOT_REAPRAT)
                || task.getIsComplete() == TaskConstants.TASK_COMMPLETE) {
            flag = taskService.taskUpdateNotesById(taskNotesVO);
        }
        return toAjax(flag);
    }

    /**
     * 确认修改任务说明
     *
     * @param taskNotesVO
     * @return
     * @throws SchedulerException
     * @throws TaskException
     */
    @PostMapping("confirmupdatenotes")
    public AjaxResult confirmUpdateNotes(@RequestBody TaskNotesVO taskNotesVO) throws SchedulerException, TaskException {
        return toAjax(taskService.taskUpdateNotesById(taskNotesVO));
    }
}


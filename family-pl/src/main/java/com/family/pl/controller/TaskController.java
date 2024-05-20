package com.family.pl.controller;

import com.family.pl.domain.Label;
import com.family.pl.domain.Task;
import com.family.pl.domain.TaskRemind;
import com.family.pl.domain.VO.AddTaskVO;
import com.family.pl.service.TaskLabelService;
import com.family.pl.service.TaskService;
import com.family.us.controller.FamilyBaseController;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.job.TaskException;
import org.apache.poi.ss.formula.functions.T;
import org.aspectj.weaver.loadtime.Aj;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.events.Event;

import javax.annotation.Resource;
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

//    @Anonymous
    @GetMapping("info")
    public AjaxResult info(){
        List<Task> tasks = taskService.list();
        return AjaxResult.success(tasks);
    }

    @PostMapping("addtask")
    public AjaxResult addTask(@RequestBody AddTaskVO addTaskVO) throws SchedulerException, TaskException {
        return toAjax(taskService.addTask(addTaskVO));
    }

}
































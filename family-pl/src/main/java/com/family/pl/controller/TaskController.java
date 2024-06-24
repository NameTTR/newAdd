package com.family.pl.controller;

import com.family.pl.domain.Task;
import com.family.pl.domain.VO.AddTaskVO;
import com.family.pl.domain.VO.DateTimeVO;
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
    @GetMapping("seletetaskbydate")
    public AjaxResult seleteTaskByDate(@RequestBody DateTimeVO dateTimeVO){
        List<Task> tasks = taskService.selectTasks(dateTimeVO);
        return AjaxResult.success(tasks);
    }

//    @Anonymous
//    @GetMapping("info")
//    public AjaxResult info(){
//        List<Task> tasks = taskService.listAllCompletedTasks();
//        return AjaxResult.success(tasks);
//    }

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

//    @PostMapping("deletetaskbyid")
//    public AjaxResult deleteTaskByID(@Param("ID") Long id) throws SchedulerException{
//        return toAjax(taskService.deleteByID(id));
//    }

    /**
     * 完成任务
     *
     * @param dateTimeVO
     * @return
     * @throws SchedulerException
     */
    @PostMapping("taskcompletebyid")
    public AjaxResult taskCompleteById(@RequestBody DateTimeVO dateTimeVO) throws SchedulerException{
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
    public AjaxResult taskDisCompleteById(@RequestBody DateTimeVO dateTimeVO) throws SchedulerException{
        return toAjax(taskService.taskDisCompleteById(dateTimeVO));
    }

//    @PostMapping("test")
//    public AjaxResult test(){
//        Task task = taskService.getById(18);
//        Date date = new Date();
//        Date date1 = new Date(date.getTime() + (1000 * 60 * 60 * 9));
//        if(compareTimeOnly(task.getTaskTimeEnd(), date1)){
//            System.out.println(task.getTaskTimeEnd());
//            System.out.println(date1);
//            System.out.println("nihao");
//        }else {
//            System.out.println("no");
//        }
//        return AjaxResult.success();
//    }
//
//    public static boolean compareTimeOnly(Date date1, Date date2) {
//        Calendar cal1 = Calendar.getInstance();
//        cal1.setTime(date1);
//        cal1.set(Calendar.YEAR, 0);
//        cal1.set(Calendar.DAY_OF_YEAR, 0);
//
//        Calendar cal2 = Calendar.getInstance();
//        cal2.setTime(date2);
//        cal2.set(Calendar.YEAR, 0);
//        cal2.set(Calendar.DAY_OF_YEAR, 0);
//
//        return cal1.getTime().before(cal2.getTime());
//    }

}

































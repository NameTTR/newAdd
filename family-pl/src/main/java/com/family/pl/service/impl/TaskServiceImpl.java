package com.family.pl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.pl.constant.TaskConstants;
import com.family.pl.domain.*;
import com.family.pl.domain.VO.AddChildTaskVO;
import com.family.pl.domain.VO.AddTaskVO;
import com.family.pl.domain.VO.DateTimeVO;
import com.family.pl.service.LabelService;
import com.family.pl.service.TaskLabelService;
import com.family.pl.service.TaskRemindService;
import com.family.pl.service.TaskService;
import com.family.pl.mapper.TaskMapper;
import com.family.pl.utils.PlScheduleUtils;
import com.family.us.utils.FamilySecurityUtils;
import com.ruoyi.common.constant.ScheduleConstants;
import com.ruoyi.common.exception.job.TaskException;
import com.ruoyi.common.utils.StringUtils;
import org.apache.commons.math3.analysis.function.Add;
import org.apache.poi.ss.formula.functions.T;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.Resources;
import java.util.Date;
import java.util.List;

/**
* @author 名字
* @description 针对表【pl_task(任务表)】的数据库操作Service实现
* @createDate 2024-05-19 21:01:45
*/
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task>
    implements TaskService{

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskLabelService taskLabelService;

    @Autowired
    private LabelService labelService;

    @Autowired
    private TaskRemindService taskRemindService;

    @Autowired
    private TaskMapper taskMapper;
    /**
     * 添加任务
     *
     * @param addTaskVO
     * @return
     * @throws SchedulerException
     * @throws TaskException
     */
    @Override
    @Transactional
    public int addTask(AddTaskVO addTaskVO) throws SchedulerException, TaskException {

        int flag = 1;
        Long userid = FamilySecurityUtils.getUserID();
        Task task = new Task();
        task.setUserId(userid);
        task = setTask(task, addTaskVO);
        if(!taskService.save(task)){
            flag = 0;
        }
        Long taskid = task.getId();
        System.out.println(taskid);

        PlJob job = new PlJob();
        job.setJobId(taskid);
        job.setJobName(addTaskVO.getTitle());
        job.setCronExpression(addTaskVO.getCorn());
        insertJob(job);

        if(task.getIsLabel() == 1){
            TaskLabel taskLabel = setTaskLabel(addTaskVO, userid, taskid);
            if(!taskLabelService.save(taskLabel)){
                flag = 0;
            }
        }

        if(task.getIsRemind() == 1){
            TaskRemind taskRemind = new TaskRemind();
            taskRemind.setTaskId(taskid);
            taskRemind.setType(addTaskVO.getType());
            taskRemind.setRemindByTime(addTaskVO.getRemindByTime());
            taskRemind.setRemindByDate(addTaskVO.getRemindByDate());
            taskRemind.setCorn(addTaskVO.getCorn());
            if(!taskRemindService.save(taskRemind)){
                flag = 0;
            }
        }

        if(task.getIsHaveChild() == 1){
            for(AddChildTaskVO child : addTaskVO.getAddChildTaskVO()){
                if(addChildTask(child) != 1){
                    flag = 0;
                }
            }
        }

        return flag;
    }

    /**
     * 添加子任务
     *
     * @param addChildTaskVO
     * @return
     * @throws SchedulerException
     * @throws TaskException
     */
    @Override
    @Transactional
    public int addChildTask(AddChildTaskVO addChildTaskVO) throws SchedulerException, TaskException {
        int flag = 1;
        Long userid = FamilySecurityUtils.getUserID();
        Task task = new Task();
        task.setUserId(userid);
        task = setTask(task, addChildTaskVO);
        if(!taskService.save(task)){
            flag = 0;
        }
        Long taskid = task.getId();
        System.out.println(taskid);

        PlJob job = new PlJob();
        job.setJobId(taskid);
        job.setJobName(addChildTaskVO.getTitle());
        job.setCronExpression(addChildTaskVO.getCorn());
        insertJob(job);

        if(task.getIsLabel() == 1){
            TaskLabel taskLabel = setTaskLabel(addChildTaskVO, userid, taskid);
            if(!taskLabelService.save(taskLabel)){
                flag = 0;
            }
        }

        if(task.getIsRemind() == 1){
            TaskRemind taskRemind = new TaskRemind();
            taskRemind.setTaskId(taskid);
            taskRemind.setType(addChildTaskVO.getType());
            taskRemind.setRemindByTime(addChildTaskVO.getRemindByTime());
            taskRemind.setRemindByDate(addChildTaskVO.getRemindByDate());
            taskRemind.setCorn(addChildTaskVO.getCorn());
            if(!taskRemindService.save(taskRemind)){
                flag = 0;
            }
        }
        return flag;
    }

    /**
     * 完成任务
     *
     * @param dateTimeVO
     * @return
     * @throws SchedulerException
     */
    @Override
    @Transactional
    public int taskCompleteById(DateTimeVO dateTimeVO) throws SchedulerException{
        Long id = dateTimeVO.getId();
        DateTimeVO date = dateTimeVO;

        int flag = 1;
        Task task = taskService.getById(id);
        Task newTask = copyTask(task);
        isTimeOut(newTask, date);

        if(!taskService.save(newTask)){
            flag = 0;
        }

        JobDetail jobDetail = scheduler.getJobDetail(PlScheduleUtils.getJobKey(id));
        jobDetail.getJobDataMap().put(TaskConstants.TASK_SKIP, true);
        scheduler.addJob(jobDetail, true, true);

        if(task.getIsLabel() == 1){
            LambdaQueryWrapper<TaskLabel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(TaskLabel::getTaskId, task.getId());
            List<TaskLabel> list = taskLabelService.list(lambdaQueryWrapper);
            for(TaskLabel taskLabel : list){
                TaskLabel newTaskLabel = new TaskLabel();
                newTaskLabel.setTaskId(newTask.getId());
                newTaskLabel.setLabelId(taskLabel.getLabelId());
                newTaskLabel.setLabelName(taskLabel.getLabelName());
                if(!taskLabelService.save(newTaskLabel)){
                    flag = 0;
                }
            }
        }

        if(task.getIsRemind() == 1){
            LambdaQueryWrapper<TaskRemind> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(TaskRemind::getTaskId, task.getId());
            TaskRemind remind = taskRemindService.getOne(lambdaQueryWrapper);
            TaskRemind newRemind = new TaskRemind();
            newRemind.setTaskId(newTask.getId());
            newRemind.setType(remind.getType());
            newRemind.setRemindByTime(remind.getRemindByTime());
            newRemind.setRemindByDate(remind.getRemindByDate());
            newRemind.setCorn(remind.getCorn());
            if(!taskRemindService.save(newRemind)){
                flag = 0;
            }
        }

        if(!copyChild(task, date)){
            flag = 0;
        };

        return flag;
    }


    /**
     * 复制子任务
     *
     * @param task
     * @param date
     * @return
     */
    @Transactional
    public boolean copyChild(Task task, DateTimeVO date){
        boolean flag = true;
        if(task.getIsHaveChild() == 1){
            LambdaQueryWrapper<Task> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Task::getFatherTaskId, task.getFatherTaskId());
            List<Task> childList = taskService.list(lambdaQueryWrapper);

            for(Task child : childList){
                child.setIsComplete(TaskConstants.TASK_COMMPLETE);
                child.setRelatedTaskId(child.getId());
                isTimeOut(child, date);
                child.setRepeat(0);
                child.setRepeatEnd(null);
                child.setId(null);
                if(!taskService.save(child)){
                    flag = false;
                }
                if(child.getIsLabel() == 1){
                    LambdaQueryWrapper<TaskLabel> childLabelWrapper = new LambdaQueryWrapper<>();
                    childLabelWrapper.eq(TaskLabel::getTaskId, child.getId());
                    List<TaskLabel> list = taskLabelService.list(childLabelWrapper);
                    for(TaskLabel taskLabel : list){
                        TaskLabel newTaskLabel = new TaskLabel();
                        newTaskLabel.setTaskId(child.getId());
                        newTaskLabel.setLabelId(taskLabel.getLabelId());
                        newTaskLabel.setLabelName(taskLabel.getLabelName());
                        if(!taskLabelService.save(newTaskLabel)){
                            flag = false;
                        }
                    }
                }
                if(child.getIsRemind() == 1){
                    LambdaQueryWrapper<TaskRemind> childRemindWrapper = new LambdaQueryWrapper<>();
                    childRemindWrapper.eq(TaskRemind::getTaskId, child.getId());
                    TaskRemind remind = taskRemindService.getOne(childRemindWrapper);
                    TaskRemind newRemind = new TaskRemind();
                    newRemind.setTaskId(child.getId());
                    newRemind.setType(remind.getType());
                    newRemind.setRemindByTime(remind.getRemindByTime());
                    newRemind.setRemindByDate(remind.getRemindByDate());
                    newRemind.setCorn(remind.getCorn());
                    if(!taskRemindService.save(newRemind)){
                        flag = false;
                    }
                }
                if(child.getIsHaveChild() == 1){
                    if(!copyChild(child, date)){
                        return false;
                    };
                }
            }

        }
        return flag;
    }

    /**
     * 由完成的任务转为未完成的任务
     *
     * @param dateTimeVO
     * @return
     * @throws SchedulerException
     */
    @Override
    public int taskDisCompleteById(DateTimeVO dateTimeVO) throws SchedulerException {

        return 1;
    }

    /**
     * 根据日期查询任务
     *
     * @param dateTimeVO
     * @return 该日期的任务
     */
    @Override
    public List<Task> selectTasks(DateTimeVO dateTimeVO) {
        Long userId = FamilySecurityUtils.getUserId();
        Date taskDate = dateTimeVO.getTime();

        return taskMapper.selectByDateAndUser(userId, taskDate);
    }

//    @Override
//    public List<Task> listAllCompletedTasks() {
//        List<Task> tasks = taskService.list();
//        for(Task task : tasks){
//            if(!(task.getIsComplete() == TaskConstants.TASK_COMMPLETE)){
//
//            }
//        }
//        return null;
//    }

    /**
     * 添加定时任务
     *
     * @param job
     * @return
     * @throws SchedulerException
     * @throws TaskException
     */
    @Override
    public int insertJob(PlJob job) throws SchedulerException, TaskException
    {
        PlScheduleUtils.createScheduleJob(scheduler, job);
        return 1;
    }

    /**
     * 给Task实体类设置相关属性
     *
     * @param task
     * @param addTaskVO
     * @return Task实体类
     */
    public Task setTask(Task task, AddTaskVO addTaskVO){
        task.setTitle(addTaskVO.getTitle());
        task.setNotes(addTaskVO.getNotes());
        task.setTaskDate(addTaskVO.getTaskDate());
        task.setTaskTimeBegin(addTaskVO.getTaskTimeBegin());
        task.setTaskTimeEnd(addTaskVO.getTaskTimeEnd());
        task.setRepeat(addTaskVO.getRepeat());
        task.setRepeatEnd(addTaskVO.getRepeatEnd());
        task.setPriority(addTaskVO.getPriority());
        task.setIsLabel(addTaskVO.getIsLabel());
        task.setIsRemind(addTaskVO.getIsRemind());
        task.setIsHaveChild(addTaskVO.getIsHaveChild());
        task.setFatherTaskId(addTaskVO.getFatherTaskId());
        return task;
    }

    /**
     * 给Task实体类设置相关属性
     *
     * @param task
     * @param addChildTaskVO
     * @return Task实体类
     */
    public Task setTask(Task task, AddChildTaskVO addChildTaskVO){
        task.setTitle(addChildTaskVO.getTitle());
        task.setNotes(addChildTaskVO.getNotes());
        task.setTaskDate(addChildTaskVO.getTaskDate());
        task.setTaskTimeBegin(addChildTaskVO.getTaskTimeBegin());
        task.setTaskTimeEnd(addChildTaskVO.getTaskTimeEnd());
        task.setRepeat(addChildTaskVO.getRepeat());
        task.setRepeatEnd(addChildTaskVO.getRepeatEnd());
        task.setPriority(addChildTaskVO.getPriority());
        task.setIsLabel(addChildTaskVO.getIsLabel());
        task.setIsRemind(addChildTaskVO.getIsRemind());
        task.setIsHaveChild(addChildTaskVO.getIsHaveChild());
        task.setFatherTaskId(addChildTaskVO.getFatherTaskId());
        return task;
    }

    /**
     * 复制Task实体类
     *
     * @param task
     * @return Task实体类
     */
    public Task copyTask(Task task){
        Task newTask = new Task();
        newTask.setTitle(task.getTitle());
        newTask.setNotes(task.getNotes());
        newTask.setTaskDate(task.getTaskDate());
        newTask.setTaskTimeBegin(task.getTaskTimeBegin());
        newTask.setTaskTimeEnd(task.getTaskTimeEnd());
        newTask.setRepeat(0);
        newTask.setRepeatEnd(null);
        newTask.setPriority(task.getPriority());
        newTask.setIsLabel(task.getIsLabel());
        newTask.setIsRemind(task.getIsRemind());
        newTask.setIsHaveChild(task.getIsHaveChild());
        newTask.setFatherTaskId(task.getFatherTaskId());
        newTask.setRelatedTaskId(task.getId());
        newTask.setUserId(task.getUserId());
        newTask.setIsComplete(TaskConstants.TASK_COMMPLETE);
        return newTask;
    }

    /**
     * 判断该任务是否超时完成
     *
     * @param task
     * @param date
     */
    public static void isTimeOut(Task task, DateTimeVO date){
        Date taskTimeBegin = task.getTaskTimeBegin();
        Date nowDate = new Date();
        if(StringUtils.isNotNull(taskTimeBegin)){
            Date taskTimeEnd = task.getTaskTimeEnd();
            if(!compareTimeOnly(date.getTime(), taskTimeEnd, nowDate)){
                task.setIsTimeout(TaskConstants.TASK_TIMEOUT);
            }
        }else {
            if(!compareDateOnly(date.getTime(), nowDate)){
                task.setIsTimeout(TaskConstants.TASK_TIMEOUT);
            }
        }
    }

    /**
     * 跟据时分秒判断date1是否在date2的前面
     *
     * @param date
     * @param date1
     * @param date2
     * @return
     */
    public static boolean compareTimeOnly(Date date, Date date1, Date date2) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(java.util.Calendar.YEAR);
        int month = cal.get(java.util.Calendar.MONTH);
        int day = cal.get(java.util.Calendar.DAY_OF_MONTH);

        java.util.Calendar cal1 = java.util.Calendar.getInstance();
        cal1.setTime(date1);
        cal1.set(java.util.Calendar.YEAR, year);
        cal1.set(java.util.Calendar.MONTH, month);
        cal1.set(java.util.Calendar.DAY_OF_MONTH, day);

        java.util.Calendar cal2 = java.util.Calendar.getInstance();
        cal2.setTime(date2);

        return cal1.getTime().before(cal2.getTime()) || cal1.getTime().equals(cal2.getTime());
    }

    /**
     * 根据年月日判断date1是否在date2的前面
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean compareDateOnly(Date date1, Date date2) {
        java.util.Calendar cal1 = java.util.Calendar.getInstance();
        cal1.setTime(date1);
        cal1.set(java.util.Calendar.HOUR, 0);
        cal1.set(java.util.Calendar.MINUTE, 0);
        cal1.set(java.util.Calendar.SECOND, 0);

        java.util.Calendar cal2 = java.util.Calendar.getInstance();
        cal2.setTime(date2);
        cal2.set(java.util.Calendar.HOUR, 0);
        cal2.set(java.util.Calendar.MINUTE, 0);
        cal2.set(java.util.Calendar.SECOND, 0);

        return cal1.getTime().before(cal2.getTime()) || cal1.getTime().equals(cal2.getTime());
    }

    /**
     * 设置TaskLabel实体类属性
     *
     * @param addTaskVO
     * @param userid
     * @param taskid
     * @return TaskLabel实体类
     */
    public TaskLabel setTaskLabel(AddTaskVO addTaskVO, Long userid, Long taskid){
        Long labelID = null;
        String name = addTaskVO.getLabelName();
        LambdaQueryWrapper<Label> lambdaQueryWrapper =
                new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Label::getName, name)
                .eq(Label::getUserId, userid);
        Label haveLabel = labelService.getOne(lambdaQueryWrapper);
        if(StringUtils.isNull(haveLabel)){
            Label label = new Label();
            label.setName(name);
            label.setUserId(userid);
            labelService.save(label);
            labelID = label.getId();
        }else {
            labelID = haveLabel.getId();
        }
        TaskLabel taskLabel = new TaskLabel();
        taskLabel.setTaskId(taskid);
        taskLabel.setLabelId(labelID);
        taskLabel.setLabelName(name);
        return taskLabel;
    }

    /**
     * 设置TaskLabel实体类属性
     *
     * @param addTaskVO
     * @param userid
     * @param taskid
     * @return TaskLabel实体类
     */
    public TaskLabel setTaskLabel(AddChildTaskVO addTaskVO, Long userid, Long taskid){
        Long labelID = null;
        String name = addTaskVO.getLabelName();
        LambdaQueryWrapper<Label> lambdaQueryWrapper =
                new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Label::getName, name)
                .eq(Label::getUserId, userid);
        Label haveLabel = labelService.getOne(lambdaQueryWrapper);
        if(StringUtils.isNull(haveLabel)){
            Label label = new Label();
            label.setName(name);
            label.setUserId(userid);
            labelService.save(label);
            labelID = label.getId();
        }else {
            labelID = haveLabel.getId();
        }
        TaskLabel taskLabel = new TaskLabel();
        taskLabel.setTaskId(taskid);
        taskLabel.setLabelId(labelID);
        taskLabel.setLabelName(name);
        return taskLabel;
    }

//    @Override
//    @Transactional
//    public int deleteByID(Long id) throws SchedulerException{
//        Task task = taskService.getById(id);
//        Long TaskId = task.getId();
//        if(task.getIsComplete() == TaskConstants.TASK_COMMPLETE){
//            taskService.removeById(id);
//
//        }else{
//            if(task.getIsRemind())
//        }
//    }
//
//    private int removeTask(Task task){
//        Long TaskId = task.getId();
//        if(task.getIsHaveChild() == TaskConstants.TASK_HAVE_CHILE){
//
//        }
//    }

}





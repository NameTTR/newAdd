package com.family.pl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.pl.domain.*;
import com.family.pl.domain.VO.AddChildTaskVO;
import com.family.pl.domain.VO.AddTaskVO;
import com.family.pl.service.LabelService;
import com.family.pl.service.TaskLabelService;
import com.family.pl.service.TaskRemindService;
import com.family.pl.service.TaskService;
import com.family.pl.mapper.TaskMapper;
import com.family.pl.utils.PlScheduleUtils;
import com.family.us.utils.FamilySecurityUtils;
import com.ruoyi.common.constant.ScheduleConstants;
import com.ruoyi.common.exception.job.TaskException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public int addTask(AddTaskVO addTaskVO) throws SchedulerException, TaskException {

        int flag = 1;
        Long userid = FamilySecurityUtils.getUserID();

        PlJob job = new PlJob();
        job.setJobId(addTaskVO.getTaskId());
        job.setJobName(addTaskVO.getTitle());
        job.setCronExpression(addTaskVO.getCorn());
        insertJob(job);

        Task task = new Task();
        task.setTitle(addTaskVO.getTitle());
        task.setNotes(addTaskVO.getNotes());
        task.setTaskDate(addTaskVO.getTaskDate());
        task.setTaskTimeBegin(addTaskVO.getTaskTimeBegin());
        task.setTaskTimeEnd(addTaskVO.getTaskTimeEnd());
        task.setRepeat(addTaskVO.getRepeat());
        task.setRepeatEnd(addTaskVO.getRepeatEnd());
        task.setPriority(addTaskVO.getPriority());
        task.setIsComplete(addTaskVO.getIsComplete());
        task.setIsLabel(addTaskVO.getIsLabel());
        task.setIsRemind(addTaskVO.getIsRemind());
        task.setIsHaveChild(addTaskVO.getIsHaveChild());
        task.setFatherTaskId(addTaskVO.getFatherTaskId());
        task.setRelatedTaskId(addTaskVO.getRelatedTaskId());
        if(!taskService.save(task)){
            flag = 0;
        }
        Long taskid = task.getId();

        if(task.getIsLabel() == 1){
            Long labelID = null;
            String name = addTaskVO.getLabelName();
            LambdaQueryWrapper<Label> lambdaQueryWrapper =
                    new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Label::getName, name)
            .eq(Label::getUserId, userid);
            Long row = labelService.count(lambdaQueryWrapper);
            if(row != 0){
                Label label = new Label();
                labelID = label.getId();
                label.setName(name);
                label.setUserId(userid);
                labelService.save(label);
            }
            TaskLabel taskLabel = new TaskLabel();
            taskLabel.setTaskId(taskid);
            taskLabel.setLabelId(labelID);
            taskLabel.setLabelName(name);
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
            AddChildTaskVO addChildTaskVO = addTaskVO.getAddChildTaskVO();
            flag = addChildTask(addChildTaskVO);
        }

        return flag;
    }

    @Override
    @Transactional
    public int addChildTask(AddChildTaskVO addChildTaskVO) throws SchedulerException, TaskException {
        int flag = 1;
        Long userid = FamilySecurityUtils.getUserID();

        PlJob job = new PlJob();
        job.setJobId(addChildTaskVO.getTaskId());
        job.setJobName(addChildTaskVO.getTitle());
        job.setCronExpression(addChildTaskVO.getCorn());
        insertJob(job);

        Task task = new Task();
        task.setTitle(addChildTaskVO.getTitle());
        task.setNotes(addChildTaskVO.getNotes());
        task.setTaskDate(addChildTaskVO.getTaskDate());
        task.setTaskTimeBegin(addChildTaskVO.getTaskTimeBegin());
        task.setTaskTimeEnd(addChildTaskVO.getTaskTimeEnd());
        task.setRepeat(addChildTaskVO.getRepeat());
        task.setRepeatEnd(addChildTaskVO.getRepeatEnd());
        task.setPriority(addChildTaskVO.getPriority());
        task.setIsComplete(addChildTaskVO.getIsComplete());
        task.setIsLabel(addChildTaskVO.getIsLabel());
        task.setIsRemind(addChildTaskVO.getIsRemind());
        task.setIsHaveChild(addChildTaskVO.getIsHaveChild());
        task.setFatherTaskId(addChildTaskVO.getFatherTaskId());
        task.setRelatedTaskId(addChildTaskVO.getRelatedTaskId());
        Long taskid = task.getId();

        if(task.getIsLabel() == 1){
            Long labelID = null;
            String name = addChildTaskVO.getLabelName();
            LambdaQueryWrapper<Label> lambdaQueryWrapper =
                    new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Label::getName, name)
                    .eq(Label::getUserId, userid);
            Long row = labelService.count(lambdaQueryWrapper);
            if(row != 0){
                Label label = new Label();
                labelID = label.getId();
                label.setName(name);
                label.setUserId(userid);
                labelService.save(label);
            }
            TaskLabel taskLabel = new TaskLabel();
            taskLabel.setTaskId(taskid);
            taskLabel.setLabelId(labelID);
            taskLabel.setLabelName(name);
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

    @Override
    public int insertJob(PlJob job) throws SchedulerException, TaskException
    {
        PlScheduleUtils.createScheduleJob(scheduler, job);
        return 1;
    }

}





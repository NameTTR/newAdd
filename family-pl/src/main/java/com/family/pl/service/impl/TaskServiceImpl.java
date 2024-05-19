package com.family.pl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.pl.domain.Label;
import com.family.pl.domain.Task;
import com.family.pl.domain.TaskLabel;
import com.family.pl.domain.TaskRemind;
import com.family.pl.domain.VO.AddTaskVO;
import com.family.pl.service.LabelService;
import com.family.pl.service.TaskLabelService;
import com.family.pl.service.TaskRemindService;
import com.family.pl.service.TaskService;
import com.family.pl.mapper.TaskMapper;
import com.family.us.utils.FamilySecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author 名字
* @description 针对表【pl_task(任务表)】的数据库操作Service实现
* @createDate 2024-05-19 21:01:45
*/
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task>
    implements TaskService{

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskLabelService taskLabelService;

    @Autowired
    private LabelService labelService;

    @Autowired
    private TaskRemindService taskRemindService;

    @Override
    public int addTask(AddTaskVO addTaskVO) {
        int flag = 1;
        Long userid = FamilySecurityUtils.getUserID();

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
            taskLabel.setTaskId(task.getId());
            taskLabel.setLabelId(labelID);
            taskLabel.setLabelName(name);
            taskLabelService.save(taskLabel);
        }
        return flag;
    }
}





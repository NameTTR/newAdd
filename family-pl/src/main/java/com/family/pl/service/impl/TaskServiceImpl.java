package com.family.pl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.pl.constant.TaskConstants;
import com.family.pl.domain.*;
import com.family.pl.domain.VO.*;
import com.family.pl.mapper.TaskMapper;
import com.family.pl.service.LabelService;
import com.family.pl.service.TaskLabelService;
import com.family.pl.service.TaskRemindService;
import com.family.pl.service.TaskService;
import com.family.pl.utils.PlScheduleUtils;
import com.family.pl.utils.TaskDateUtils;
import com.family.pl.utils.TaskUtils;
import com.family.us.utils.FamilySecurityUtils;
import com.ruoyi.common.exception.job.TaskException;
import com.ruoyi.common.utils.StringUtils;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author 名字
 * @description 针对表【pl_task(任务表)】的数据库操作Service实现
 * @createDate 2024-05-19 21:01:45
 */
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task>
        implements TaskService {

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
        if (!taskService.save(task)) {
            flag = 0;
        }
        Long taskid = task.getId();

        if (task.getIsLabel() == 1) {
            List<String> labelNames = addTaskVO.getLabelName();
            for (String labelName : labelNames) {
                TaskLabel taskLabel = setTaskLabel(labelName, userid, taskid);
                if (!taskLabelService.save(taskLabel)) {
                    flag = 0;
                }
            }
        }

        if (task.getIsRemind() == 1) {

            List<TaskRemindVO> taskRemindVOs = addTaskVO.getTaskRemindVO();
            for (TaskRemindVO taskRemindVO : taskRemindVOs) {
                TaskRemind taskRemind = setTaskRemind(taskRemindVO, taskid);
                if (!taskRemindService.save(taskRemind)) {
                    flag = 0;
                }
                PlJob job = setJob(task, taskRemind, taskid);
                insertJob(job);
            }

        }

        if (task.getIsHaveChild() == 1) {
            for (AddChildTaskVO child : addTaskVO.getAddChildTaskVO()) {
                if (addChildTask(child) != 1) {
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
        if (!taskService.save(task)) {
            flag = 0;
        }
        Long taskid = task.getId();
        System.out.println(taskid);

        if (task.getIsLabel() == 1) {
            List<String> labelNames = addChildTaskVO.getLabelName();
            for (String labelName : labelNames) {
                TaskLabel taskLabel = setTaskLabel(labelName, userid, taskid);
                if (!taskLabelService.save(taskLabel)) {
                    flag = 0;
                }
            }
        }

        if (task.getIsRemind() == 1) {
            List<TaskRemindVO> taskRemindVOs = addChildTaskVO.getTaskRemindVO();
            for (TaskRemindVO taskRemindVO : taskRemindVOs) {
                TaskRemind taskRemind = setTaskRemind(taskRemindVO, taskid);
                if (!taskRemindService.save(taskRemind)) {
                    flag = 0;
                }
                PlJob job = setJob(task, taskRemind, taskid);
                insertJob(job);
            }
        }

        if (task.getIsHaveChild() == 1) {
            for (AddChildTaskVO child : addChildTaskVO.getAddChildTaskVO()) {
                if (addChildTask(child) != 1) {
                    flag = 0;
                }
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
    public int taskCompleteById(DateTimeVO dateTimeVO) throws SchedulerException {
        Long id = dateTimeVO.getId();
        DateTimeVO date = dateTimeVO;

        int flag = 1;
        Task task = taskService.getById(id);
        task.setTaskDate(date.getTime());
        Task newTask = copyTask(task);
        isTimeOut(newTask, date);

        if (!taskService.save(newTask)) {
            flag = 0;
        }

        JobDetail jobDetail = scheduler.getJobDetail(PlScheduleUtils.getJobKey(id));
        jobDetail.getJobDataMap().put(TaskConstants.TASK_SKIP, true);
        scheduler.addJob(jobDetail, true, true);

        if (task.getIsLabel() == 1) {
            LambdaQueryWrapper<TaskLabel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(TaskLabel::getTaskId, task.getId());
            List<TaskLabel> list = taskLabelService.list(lambdaQueryWrapper);
            for (TaskLabel taskLabel : list) {
                TaskLabel newTaskLabel = new TaskLabel();
                newTaskLabel.setTaskId(newTask.getId());
                newTaskLabel.setLabelId(taskLabel.getLabelId());
                newTaskLabel.setLabelName(taskLabel.getLabelName());
                if (!taskLabelService.save(newTaskLabel)) {
                    flag = 0;
                }
            }
        }

        if (task.getIsRemind() == 1) {
            LambdaQueryWrapper<TaskRemind> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(TaskRemind::getTaskId, task.getId());
            TaskRemind remind = taskRemindService.getOne(lambdaQueryWrapper);
            TaskRemind newRemind = new TaskRemind();
            newRemind.setTaskId(newTask.getId());
            newRemind.setType(remind.getType());
            newRemind.setRemindByTime(remind.getRemindByTime());
            newRemind.setRemindByDate(remind.getRemindByDate());
            newRemind.setCorn(remind.getCorn());
            if (!taskRemindService.save(newRemind)) {
                flag = 0;
            }
        }

        if (!copyChild(task, date)) {
            flag = 0;
        }


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
    public boolean copyChild(Task task, DateTimeVO date) {
        boolean flag = true;
        if (task.getIsHaveChild() == 1) {
            LambdaQueryWrapper<Task> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Task::getFatherTaskId, task.getFatherTaskId());
            List<Task> childList = taskService.list(lambdaQueryWrapper);

            for (Task child : childList) {
                child.setTaskDate(date.getTime());
                child.setIsComplete(TaskConstants.TASK_COMMPLETE);
                child.setRelatedTaskId(child.getId());
                isTimeOut(child, date);
                child.setRepeat(0);
                child.setRepeatEnd(null);
                child.setId(null);
                child.setUpdateTime(null);
                child.setCreatedTime(null);
                if (!taskService.save(child)) {
                    flag = false;
                }
                if (child.getIsLabel() == 1) {
                    LambdaQueryWrapper<TaskLabel> childLabelWrapper = new LambdaQueryWrapper<>();
                    childLabelWrapper.eq(TaskLabel::getTaskId, child.getId());
                    List<TaskLabel> list = taskLabelService.list(childLabelWrapper);
                    for (TaskLabel taskLabel : list) {
                        TaskLabel newTaskLabel = new TaskLabel();
                        newTaskLabel.setTaskId(child.getId());
                        newTaskLabel.setLabelId(taskLabel.getLabelId());
                        newTaskLabel.setLabelName(taskLabel.getLabelName());
                        if (!taskLabelService.save(newTaskLabel)) {
                            flag = false;
                        }
                    }
                }
                if (child.getIsRemind() == 1) {
                    LambdaQueryWrapper<TaskRemind> childRemindWrapper = new LambdaQueryWrapper<>();
                    childRemindWrapper.eq(TaskRemind::getTaskId, child.getId());
                    TaskRemind remind = taskRemindService.getOne(childRemindWrapper);
                    TaskRemind newRemind = new TaskRemind();
                    newRemind.setTaskId(child.getId());
                    newRemind.setType(remind.getType());
                    newRemind.setRemindByTime(remind.getRemindByTime());
                    newRemind.setRemindByDate(remind.getRemindByDate());
                    newRemind.setCorn(remind.getCorn());
                    if (!taskRemindService.save(newRemind)) {
                        flag = false;
                    }
                }
                if (child.getIsHaveChild() == 1) {
                    if (!copyChild(child, date)) {
                        return false;
                    }
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
    @Transactional
    public int taskDisCompleteById(DateTimeVO dateTimeVO) throws SchedulerException, TaskException {
        Long id = dateTimeVO.getId();
        DateTimeVO date = dateTimeVO;
        int flag = 1;
        Task task = taskService.getById(id);
        Long relatedTaskId = task.getRelatedTaskId();
        Task relatedTask = taskService.getById(relatedTaskId);
        if ((relatedTask.getIsEnd() == TaskConstants.TASK_END) || !compareTask(task, relatedTask)) {

            if (!addDisTask(task)) {
                flag = 0;
            }
        } else if (compareTask(task, relatedTask)) {
            taskService.taskDeleteOneById(dateTimeVO);
        }
        return flag;
    }

    /**
     * 由完成的任务转为未完成的任务时，若与原来任务不一致或原来任务已全部结束则将此已完成任务新增为独立任务
     *
     * @param task
     * @return
     * @throws SchedulerException
     * @throws TaskException
     */
    private boolean addDisTask(Task task) throws SchedulerException, TaskException {
        Long taskid = task.getId();
        task.setIsEnd(TaskConstants.TASK_NOT_END);
        task.setIsComplete(TaskConstants.TASK_NOT_COMMPLETE);
        task.setRelatedTaskId(null);
        boolean flag = true;
        if (task.getIsRemind() == TaskConstants.TASK_REMIND) {
            LambdaQueryWrapper<TaskRemind> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(TaskRemind::getTaskId, task.getId());
            List<TaskRemind> list = taskRemindService.list(lambdaQueryWrapper);
            for (TaskRemind remind : list) {
                PlJob job = setJob(task, remind, taskid);
                insertJob(job);
            }
        }

        if (task.getIsHaveChild() == TaskConstants.TASK_HAVE_CHILE) {
            LambdaQueryWrapper<Task> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Task::getId, task.getFatherTaskId());
            List<Task> childList = taskService.list(lambdaQueryWrapper);
            for (Task child : childList) {
                if (!addDisTask(child)) {
                    return false;
                }
            }
        }
        return flag;
    }


    /**
     * 比较两个任务是否一致
     *
     * @param task
     * @param relatedTask
     * @return
     */
    private boolean compareTask(Task task, Task relatedTask) {
        boolean flag = true;
        if (relatedTask.getTaskTimeEnd() != task.getTaskTimeEnd()) {
            return false;
        }
        if (relatedTask.getTitle() != task.getTitle()) {
            return false;
        }
        if (relatedTask.getNotes() != task.getNotes()) {
            return false;
        }
        if (relatedTask.getTaskTimeBegin() != task.getTaskTimeBegin()) {
            return false;
        }
        if (relatedTask.getRepeat() != task.getRepeat()) {
            return false;
        }
        if (relatedTask.getRepeatEnd() != task.getRepeatEnd()) {
            return false;
        }
        if (relatedTask.getPriority() != task.getPriority()) {
            return false;
        }
        if (relatedTask.getIsLabel() != task.getIsLabel()) {
            return false;
        }
        if (relatedTask.getIsRemind() != task.getIsRemind()) {
            return false;
        }
        if (relatedTask.getIsHaveChild() != task.getIsHaveChild()) {
            return false;
        }
        // 分别查询两个任务的标签，比较是否一致
        if (relatedTask.getIsLabel() == TaskConstants.TASK_LABEL) {
            LambdaQueryWrapper<TaskLabel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(TaskLabel::getTaskId, relatedTask.getId());
            List<TaskLabel> relatedTaskLabels = taskLabelService.list(lambdaQueryWrapper);
            lambdaQueryWrapper.eq(TaskLabel::getTaskId, task.getId());
            List<TaskLabel> taskLabels = taskLabelService.list(lambdaQueryWrapper);
            if (!TaskUtils.compareLabels(relatedTaskLabels, taskLabels)) {
                return false;
            }
        }
        // 分别查询两个任务的提醒，比较是否一致
        if (relatedTask.getIsRemind() == TaskConstants.TASK_REMIND) {
            LambdaQueryWrapper<TaskRemind> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(TaskRemind::getTaskId, relatedTask.getId());
            List<TaskRemind> relatedTaskReminds = taskRemindService.list(lambdaQueryWrapper);
            lambdaQueryWrapper.eq(TaskRemind::getTaskId, task.getId());
            List<TaskRemind> taskReminds = taskRemindService.list(lambdaQueryWrapper);
            if (!TaskUtils.compareReminds(relatedTaskReminds, taskReminds)) {
                return false;
            }
        }

        // 分别查询两个任务的子任务，比较是否一致
        if (relatedTask.getIsHaveChild() == TaskConstants.TASK_HAVE_CHILE) {
            LambdaQueryWrapper<Task> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Task::getFatherTaskId, relatedTask.getId()).orderByAsc(Task::getCreatedTime);
            List<Task> relatedChildTasks = taskService.list(lambdaQueryWrapper);
            LambdaQueryWrapper<Task> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(Task::getFatherTaskId, task.getId()).orderByAsc(Task::getCreatedTime);
            List<Task> childTasks = taskService.list(lambdaQueryWrapper1);
            if (childTasks.size() != relatedChildTasks.size()) {
                return false;
            }
            for (int i = 0; i < childTasks.size(); i++) {
                if (!compareTask(childTasks.get(i), relatedChildTasks.get(i))) {
                    return false;
                }
            }
        }
        return flag;
    }

    /**
     * 根据日期查询已完成任务
     *
     * @param dateTimeVO
     * @return 该日期的任务
     */
    @Override
    public List<Task> selectCompleteTasks(DateTimeVO dateTimeVO) {
        Long userId = FamilySecurityUtils.getUserId();
        LocalDate taskDate = dateTimeVO.getTime();

        List<Task> tasks = taskMapper.selectByComDateAndUser(userId, taskDate);
        return tasks;
    }

    /**
     * 根据日期查询未完成任务
     *
     * @param dateTimeVO
     * @return
     */
    @Override
    public List<Task> selectDisCompleteTasks(DateTimeVO dateTimeVO) {
        Long userId = FamilySecurityUtils.getUserId();
        LocalDate taskDate = dateTimeVO.getTime();
        List<Task> tasks = taskMapper.selectByDisDateAndUser(userId, taskDate);
        return tasks;
    }

    /**
     * 根据id删除此任务
     *
     * @param dateTimeVO
     * @return
     */
    @Override
    public int taskDeleteOneById(DateTimeVO dateTimeVO) {
        int flag = 1;

        Long taskId = dateTimeVO.getId();
        Task task = taskService.getById(taskId);

        if (task.getIsComplete() == TaskConstants.TASK_COMMPLETE) {
            if (task.getIsLabel() == TaskConstants.TASK_LABEL) {
                LambdaQueryWrapper<TaskLabel> taskLabelLambdaQueryWrapper = new LambdaQueryWrapper<>();
                taskLabelLambdaQueryWrapper.eq(TaskLabel::getTaskId, task.getId());
                TaskLabel taskLabel = taskLabelService.getOne(taskLabelLambdaQueryWrapper);
                if (!taskLabelService.removeById(taskLabel.getId())) {
                    flag = 0;
                }
            }

            if (task.getIsRemind() == TaskConstants.TASK_REMIND) {
                LambdaQueryWrapper<TaskRemind> taskRemindLambdaQueryWrapper = new LambdaQueryWrapper<>();
                taskRemindLambdaQueryWrapper.eq(TaskRemind::getTaskId, task.getId());
                TaskRemind taskRemind = taskRemindService.getOne(taskRemindLambdaQueryWrapper);
                if (!taskRemindService.removeById(taskRemind.getId())) {
                    flag = 0;
                }
            }

            if (!taskService.removeById(taskId)) {
                flag = 0;
            }
        } else {
            task.setFlagDelete(TaskConstants.TASK_DELETE);
            if (!taskService.updateById(task)) {
                flag = 0;
            }
        }


        return flag;
    }

    @Override
    public int taskDeleteAllById(DateTimeVO dateTimeVO) throws SchedulerException {
        int flag = 1;

        Long taskId = dateTimeVO.getId();
        Task task = taskService.getById(taskId);
        if (task.getIsRemind() == TaskConstants.TASK_REMIND) {
            JobKey jobKey = PlScheduleUtils.getJobKey(taskId);
            scheduler.deleteJob(jobKey);
        }
        task.setFlagDelete(TaskConstants.TASK_DELETE);
        if (!taskService.updateById(task)) {
            flag = 0;
        }
        return flag;
    }

    @Override
    public int taskUpdateTitleById(UpdateTaskTitleVO updataTaskTitleVO) {
        int flag = 1;
        Long taskId = updataTaskTitleVO.getId();
        Task task = taskService.getById(taskId);
        task.setTitle(updataTaskTitleVO.getTitle());
        if (!taskService.updateById(task)) {
            flag = 0;
        }
        return flag;
    }

    @Override
    public int taskUpdatePriorityById(UpdateTaskPriorityVO taskPriorityVO) {
        int flag = 1;
        Long taskId = taskPriorityVO.getId();
        Task task = taskService.getById(taskId);
        task.setPriority(taskPriorityVO.getPriority());
        if (!taskService.updateById(task)) {
            flag = 0;
        }
        return flag;
    }

    @Override
    public int taskUpdateNotesById(UpdateTaskNotesVO taskNotesVO) {
        int flag = 1;
        Long taskId = taskNotesVO.getId();
        Task task = taskService.getById(taskId);
        task.setNotes(taskNotesVO.getNotes());
        if (!taskService.updateById(task)) {
            flag = 0;
        }
        return flag;
    }

//    @Override
//    public int updateTaskById(UpdateTaskVO updateTaskVO) throws SchedulerException, TaskException {
//        return 0;
//    }

    @Override
    public int updateTaskById(UpdateTaskVO updateTaskVO) throws SchedulerException, TaskException {
        int flag = 1;
        Task lastTask = taskService.getById(updateTaskVO.getId());
        LambdaUpdateWrapper<Task> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(Task::getTitle, updateTaskVO.getTitle());
        lambdaUpdateWrapper.set(Task::getNotes, updateTaskVO.getNotes());
        lambdaUpdateWrapper.set(Task::getPriority, updateTaskVO.getPriority());

        lambdaUpdateWrapper.set(Task::getTaskDate, updateTaskVO.getTaskDate());
        lambdaUpdateWrapper.set(Task::getTaskTimeBegin, updateTaskVO.getTaskTimeBegin());
        lambdaUpdateWrapper.set(Task::getTaskTimeEnd, updateTaskVO.getTaskTimeEnd());
        lambdaUpdateWrapper.set(Task::getRepeat, updateTaskVO.getRepeat());
        lambdaUpdateWrapper.set(Task::getRepeatEnd, updateTaskVO.getRepeatEnd());
        lambdaUpdateWrapper.set(Task::getIsRemind, updateTaskVO.getIsRemind());

        lambdaUpdateWrapper.set(Task::getIsLabel, updateTaskVO.getIsLabel());

        lambdaUpdateWrapper.set(Task::getIsHaveChild, updateTaskVO.getIsHaveChild());
        lambdaUpdateWrapper.eq(Task::getId, updateTaskVO.getId());

        // 比较任务提醒是否有变化，有变化则删除原来的任务提醒，添加新的任务提醒
        if (!Objects.equals(lastTask.getTaskDate(), updateTaskVO.getTaskDate())
                && !Objects.equals(lastTask.getTaskTimeBegin(), updateTaskVO.getTaskTimeBegin())
                && !Objects.equals(lastTask.getTaskTimeEnd(), updateTaskVO.getTaskTimeEnd())
                && !Objects.equals(lastTask.getRepeat(), updateTaskVO.getRepeat())
                && !Objects.equals(lastTask.getRepeatEnd(), updateTaskVO.getRepeatEnd())
                && !Objects.equals(lastTask.getIsRemind(), updateTaskVO.getIsRemind())) {
            LambdaQueryWrapper<TaskRemind> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(TaskRemind::getTaskId, lastTask.getId());
            List<TaskRemind> list = taskRemindService.list(lambdaQueryWrapper);
            for (TaskRemind taskRemind : list) {
                JobKey jobKey = PlScheduleUtils.getJobKey(taskRemind.getTaskId());
                scheduler.deleteJob(jobKey);
                taskRemindService.removeById(taskRemind.getId());
            }
            if (updateTaskVO.getIsRemind() == TaskConstants.TASK_REMIND) {
                List<TaskRemind> taskReminds = updateTaskVO.getTaskReminds();
                for (TaskRemind taskRemind : taskReminds) {
                    if (!taskRemindService.save(taskRemind)) {
                        flag = 0;
                    }
                    PlJob job = setJob(lastTask, taskRemind, lastTask.getId());
                    insertJob(job);
                }
            }
        } else {
            if (lastTask.getIsRemind() == TaskConstants.TASK_REMIND) {
                LambdaQueryWrapper<TaskRemind> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(TaskRemind::getTaskId, lastTask.getId());
                List<TaskRemind> reminds = taskRemindService.list(lambdaQueryWrapper);
                if (!TaskUtils.compareReminds(reminds, updateTaskVO.getTaskReminds())) {
                    for (TaskRemind taskRemind : reminds) {
                        JobKey jobKey = PlScheduleUtils.getJobKey(taskRemind.getTaskId());
                        scheduler.deleteJob(jobKey);
                        taskRemindService.removeById(taskRemind.getId());
                    }

                    List<TaskRemind> taskReminds = updateTaskVO.getTaskReminds();
                    for (TaskRemind taskRemind : taskReminds) {
                        if (!taskRemindService.save(taskRemind)) {
                            flag = 0;
                        }
                        PlJob job = setJob(lastTask, taskRemind, lastTask.getId());
                        insertJob(job);
                    }

                }
            }
        }

        // 比较任务标签是否有变化，有变化则删除原来的任务标签，添加新的任务标签
        if (!Objects.equals(lastTask.getIsLabel(), updateTaskVO.getIsLabel())) {
            LambdaQueryWrapper<TaskLabel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(TaskLabel::getTaskId, lastTask.getId());
            List<TaskLabel> list = taskLabelService.list(lambdaQueryWrapper);
            for (TaskLabel taskLabel : list) {
                if (!taskLabelService.removeById(taskLabel.getId())) {
                    flag = 0;
                }
            }
            if (updateTaskVO.getIsLabel() == TaskConstants.TASK_LABEL) {
                List<TaskLabel> taskLabels = updateTaskVO.getTaskLabels();
                for (TaskLabel taskLabel : taskLabels) {
                    if (!taskLabelService.save(taskLabel)) {
                        flag = 0;
                    }
                }
            }
        } else {
            if (lastTask.getIsLabel() == TaskConstants.TASK_LABEL) {
                LambdaQueryWrapper<TaskLabel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(TaskLabel::getTaskId, lastTask.getId());
                List<TaskLabel> labels = taskLabelService.list(lambdaQueryWrapper);
                if (!TaskUtils.compareLabels(labels, updateTaskVO.getTaskLabels())) {
                    for (TaskLabel taskLabel : labels) {
                        if (!taskLabelService.removeById(taskLabel.getId())) {
                            flag = 0;
                        }
                    }
                    List<TaskLabel> taskLabels = updateTaskVO.getTaskLabels();
                    for (TaskLabel taskLabel : taskLabels) {
                        TaskLabel newTaskLabel = null;
                        if (taskLabel.getTaskId() == null) {
                            newTaskLabel = setTaskLabel(taskLabel.getLabelName(), FamilySecurityUtils.getUserId(), lastTask.getId());
                        }
                        if (!taskLabelService.save(newTaskLabel)) {
                            flag = 0;
                        }
                    }
                }
            }
        }

        // 比较任务是否有子任务，有子任务则删除原来的子任务，添加新的子任务
        if (!Objects.equals(lastTask.getIsHaveChild(), updateTaskVO.getIsHaveChild())) {
            if (updateTaskVO.getIsHaveChild() == TaskConstants.TASK_HAVE_NOT_CHILE) {
                LambdaQueryWrapper<Task> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(Task::getFatherTaskId, lastTask.getId());
                List<Task> list = taskService.list(lambdaQueryWrapper);
                for (Task task : list) {
                    if (!taskService.removeById(task.getId())) {
                        flag = 0;
                    }
                }
            }
            if (updateTaskVO.getIsHaveChild() == TaskConstants.TASK_HAVE_CHILE) {
                List<Task> childTasks = updateTaskVO.getChildTasks();
                for (Task task : childTasks) {
                    if (addChildTask(addChildTaskVO) != 1) {
                        flag = 0;
                    }
                }
            }
        } else {
            boolean flag1 = true;

            LambdaQueryWrapper<Task> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Task::getFatherTaskId, lastTask.getId()).orderByAsc(Task::getCreatedTime);
            List<Task> lastChildTask = taskService.list(lambdaQueryWrapper);
            if(lastChildTask.size() != updateTaskVO.getChildTasks().size()){
                flag1 = false;
            }
            for(int i = 0; i < lastChildTask.size(); i++){
                if(!compareChildTask(lastChildTask.get(i), updateTaskVO.getChildTasks().get(i))){
                    flag1 = false;
                }
            }
            if(!flag1){
                LambdaQueryWrapper<Task> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(Task::getFatherTaskId, lastTask.getId());
                List<Task> childTasks = taskService.list(lambdaQueryWrapper);
                if (!TaskUtils.compareChildTasks(childTasks, updateTaskVO.getChildTasks())) {
                    for (Task childTask : childTasks) {
                        if (!taskService.removeById(childTask.getId())) {
                            flag = 0;
                        }
                    }
                    List<Task> tasks = updateTaskVO.getChildTasks();
                    for (Task task : tasks) {
                        if (addChildTask(addChildTaskVO) != 1) {
                            flag = 0;
                        }
                    }
                }
            }

        }

        return flag;
    }


    /**
     * 递归删除子任务
     *
     * @param task
     * @return
     */
    private boolean deleteChild(Task task) {
        boolean flag = true;
        if (task.getIsHaveChild() == 1) {
            LambdaQueryWrapper<Task> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Task::getFatherTaskId, task.getId());
            List<Task> childList = taskService.list(lambdaQueryWrapper);
            for (Task child : childList) {
                if (!taskService.removeById(child.getId())) {
                    flag = false;
                }
                if (child.getIsHaveChild() == 1) {
                    if (!deleteChild(child)) {
                        return false;
                    }
                }
            }
        }
        return flag;
    }

    /**
     * 比较子任务是否相同
     *
     * @param childTask1
     * @param childTask2
     * @return
     */
    private boolean compareChildTask(Task childTask1, Task childTask2) {
        if(!TaskUtils.compareChildTasks(childTask1, childTask2)){
            return false;
        }
        LambdaQueryWrapper<Task> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Task::getFatherTaskId, childTask1.getId()).orderByAsc(Task::getCreatedTime);
        List<Task> childTasks1 = taskService.list(lambdaQueryWrapper);
        LambdaQueryWrapper<Task> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(Task::getFatherTaskId, childTask2.getId()).orderByAsc(Task::getCreatedTime);
        List<Task> childTasks2 = taskService.list(lambdaQueryWrapper1);
        if (childTasks1.size() != childTasks2.size()) {
            return false;
        }
        for (int i = 0; i < childTasks1.size(); i++) {
            if (!compareChildTask(childTasks1.get(i), childTasks2.get(i))) {
                return false;
            }
        }
        return true;
    }
        /**
         * 添加定时任务
         *
         * @param job
         * @return
         * @throws SchedulerException
         * @throws TaskException
         */
        @Override
        public int insertJob (PlJob job) throws SchedulerException, TaskException {
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
        public Task setTask (Task task, AddTaskVO addTaskVO){
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
        public Task setTask (Task task, AddChildTaskVO addChildTaskVO){
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
        public Task copyTask (Task task){
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
        public static void isTimeOut (Task task, DateTimeVO date){
            LocalTime taskTimeBegin = task.getTaskTimeBegin();
            LocalTime taskTimeEnd = task.getTaskTimeEnd();
            LocalDate taskDate = date.getTime();
            LocalTime now = LocalTime.now();
            LocalDate nowDate = LocalDate.now();
            if (nowDate.isAfter(taskDate)) {
                task.setIsTimeout(TaskConstants.TASK_TIMEOUT);
            } else {
                if (taskTimeBegin != null && taskTimeEnd != null) {
                    if (now.isAfter(taskTimeEnd)) {
                        task.setIsTimeout(TaskConstants.TASK_TIMEOUT);
                    }
                }
            }
        }

        /**
         * 设置TaskLabel实体类属性
         *
         * @param labelName
         * @param userid
         * @param taskid
         * @return
         */
        public TaskLabel setTaskLabel (String labelName, Long userid, Long taskid){
            Long labelID = null;
            //查询是否有该标签
            LambdaQueryWrapper<Label> lambdaQueryWrapper =
                    new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Label::getName, labelName)
                    .eq(Label::getUserId, userid);
            Label haveLabel = labelService.getOne(lambdaQueryWrapper);
            if (StringUtils.isNull(haveLabel)) {
                //若没有则新增Label表
                Label label = new Label();
                label.setName(labelName);
                label.setUserId(userid);
                labelService.save(label);
                labelID = label.getId();
            } else {
                //若有则获取该标签的ID
                labelID = haveLabel.getId();
            }
            //新增TaskLabel表
            TaskLabel taskLabel = new TaskLabel();
            taskLabel.setTaskId(taskid);
            taskLabel.setLabelId(labelID);
            taskLabel.setLabelName(labelName);
            return taskLabel;
        }

        /**
         * 设置TaskRemind实体类属性
         *
         * @param taskRemindVO
         * @param taskid
         * @return
         */
        public TaskRemind setTaskRemind (TaskRemindVO taskRemindVO, Long taskid){
            TaskRemind taskRemind = new TaskRemind();
            taskRemind.setTaskId(taskid);
            taskRemind.setType(taskRemindVO.getType());
            taskRemind.setRemindByTime(taskRemindVO.getRemindByTime());
            taskRemind.setRemindByDate(taskRemindVO.getRemindByDate());
            taskRemind.setCorn(taskRemindVO.getCorn());
            return taskRemind;
        }

        /**
         * 设置PlJob实体类属性
         *
         * @param taskRemind
         * @param taskid
         * @return
         */
        public PlJob setJob (Task task, TaskRemind taskRemind, Long taskid){
            PlJob job = new PlJob();
            Date startDate = TaskDateUtils.LocalDateToDate(task.getTaskDate());
            if (StringUtils.isNotNull(task.getRepeatEnd())) {
                Date date = TaskDateUtils.LocalDateTimeToDate(task.getRepeatEnd());
                job.setRepeatEnd(date);
            }
            if (StringUtils.isNotNull(taskRemind.getRemindByTime())) {
                job.setRemindByTime(Integer.parseInt(taskRemind.getRemindByTime().toString()));
            }
            if (StringUtils.isNotNull(taskRemind.getRemindByDate())) {
                job.setRemindByDate(Integer.parseInt(taskRemind.getRemindByDate().toString()));
            }
            job.setRemindByTime(Integer.parseInt(taskRemind.getRemindByTime().toString()));
            job.setStartTime(startDate);
            job.setJobId(taskRemind.getId());
            job.setJobName(task.getTitle());
            job.setCronExpression(taskRemind.getCorn());
            return job;
        }
    }





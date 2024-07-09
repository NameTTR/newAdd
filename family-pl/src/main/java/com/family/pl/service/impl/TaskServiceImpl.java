package com.family.pl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.pl.constant.TaskConstants;
import com.family.pl.domain.*;
import com.family.pl.domain.VO.*;
import com.family.pl.mapper.TaskLabelMapper;
import com.family.pl.mapper.TaskRemindMapper;
import com.family.pl.service.LabelService;
import com.family.pl.service.TaskLabelService;
import com.family.pl.service.TaskRemindService;
import com.family.pl.service.TaskService;
import com.family.pl.mapper.TaskMapper;
import com.family.pl.utils.PlScheduleUtils;
import com.family.pl.utils.TaskDateUtils;
import com.ruoyi.common.exception.job.TaskException;
import com.ruoyi.common.utils.StringUtils;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 名字
 * @description 针对表【pl_task(任务表)】的数据库操作Service实现
 * @createDate 2024-07-05 17:05:16
 */
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task>
        implements TaskService {

    @Autowired
    private TaskRemindService taskRemindService;

    @Autowired
    private TaskLabelService taskLabelService;

    @Autowired
    private LabelService labelService;

    @Autowired
    private TaskLabelMapper taskLabelMapper;

    @Autowired
    private TaskRemindMapper taskRemindMapper;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private TaskMapper taskMapper;

    /**
     * 查询已完成的任务
     *
     * @param pageNum
     * @param dateTimeVO
     * @return
     */
    @Override
    public IPage<SelectTaskVO> selectCompleteTasks(Integer pageNum, DateTimeVO dateTimeVO) {
        LocalDate queryDate = dateTimeVO.getDate();
        int pageSize = 10; // 固定页面大小为10

        // Step 1: 获取所有符合条件的任务，包括主任务和子任务
        QueryWrapper<Task> baseWrapper = new QueryWrapper<>();
        baseWrapper.eq("is_complete", 0)
                .eq("flag_delete", 0)
                .and(wrapper ->
                        wrapper.eq("`repeat`", 0).eq("task_date", queryDate)
                                .or().eq("`repeat`", 1).ge("repeat_end", queryDate).le("task_date", queryDate)
                                .or().eq("`repeat`", 2).apply("DATE_FORMAT(task_date, '%d') = DATE_FORMAT({0}, '%d')", queryDate).le("repeat_end", queryDate)
                                .or().eq("`repeat`", 3).apply("DATE_FORMAT(task_date, '%m-%d') = DATE_FORMAT({0}, '%m-%d')", queryDate).le("repeat_end", queryDate)
                                .or().eq("`repeat`", 4).le("task_date", queryDate).ge("repeat_end", queryDate).apply("WEEKDAY(task_date) < 5").apply("WEEKDAY({0}) < 5", queryDate)
                                .or().eq("`repeat`", 5).le("task_date", queryDate).ge("repeat_end", queryDate).apply("WEEKDAY(task_date) = 5").apply("WEEKDAY({0}) = 5", queryDate)
                                .or().eq("`repeat`", 6).le("task_date", queryDate).ge("repeat_end", queryDate)
                );

        // 获取符合条件的所有任务
        List<Task> allTasks = taskMapper.selectList(baseWrapper);

        if (allTasks.isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }

        // 将所有任务按ID存储在一个映射中
        Map<Long, Task> taskMap = allTasks.stream().collect(Collectors.toMap(Task::getId, task -> task));

        // Step 2: 获取所有任务的标签和提醒
        List<Long> taskIds = allTasks.stream().map(Task::getId).collect(Collectors.toList());

        QueryWrapper<TaskRemind> remindWrapper = new QueryWrapper<>();
        remindWrapper.in("task_id", taskIds);
        List<TaskRemind> taskReminds = taskRemindMapper.selectList(remindWrapper);

        QueryWrapper<TaskLabel> labelWrapper = new QueryWrapper<>();
        labelWrapper.in("task_id", taskIds);
        List<TaskLabel> taskLabels = taskLabelMapper.selectList(labelWrapper);

        // Step 3: 转换为SelectTaskVO对象
        List<SelectTaskVO> selectTaskVOS = mapTasksToSelectTaskVOs(allTasks, taskReminds, taskLabels, taskMap, queryDate);

        // Step 4: 排序
        selectTaskVOS.sort(Comparator.comparing((SelectTaskVO vo) ->
                        Optional.ofNullable(vo.getTask().getTaskTimeBegin()).orElse(LocalTime.MAX))
                .thenComparing(vo -> vo.getTask().getCreatedTime()));

        // Step 5: 分页处理
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, selectTaskVOS.size());
        List<SelectTaskVO> paginatedList = selectTaskVOS.subList(start, end);

        // Step 6: 将转换后的任务对象设定到分页对象中
        Page<SelectTaskVO> resultPage = new Page<>(pageNum, pageSize, selectTaskVOS.size());
        resultPage.setRecords(paginatedList);

        return resultPage;
    }

    /**
     * 查询未完成的任务
     *
     * @param pageNum
     * @param dateTimeVO
     * @return
     */
    @Override
    public IPage<SelectTaskVO> selectUnCompleteTasks(Integer pageNum, DateTimeVO dateTimeVO) {
        LocalDate queryDate = dateTimeVO.getDate();
        int pageSize = 10; // 固定页面大小为10

        // Step 1: 获取所有符合条件的任务，包括主任务和子任务
        QueryWrapper<Task> baseWrapper = new QueryWrapper<>();
        baseWrapper.eq("is_complete", 1)
                .eq("flag_delete", 0)
                .and(wrapper ->
                        wrapper.eq("`repeat`", 0).eq("task_date", queryDate)
                                .or().eq("`repeat`", 1).ge("repeat_end", queryDate).le("task_date", queryDate)
                                .or().eq("`repeat`", 2).apply("DATE_FORMAT(task_date, '%d') = DATE_FORMAT({0}, '%d')", queryDate).le("repeat_end", queryDate)
                                .or().eq("`repeat`", 3).apply("DATE_FORMAT(task_date, '%m-%d') = DATE_FORMAT({0}, '%m-%d')", queryDate).le("repeat_end", queryDate)
                                .or().eq("`repeat`", 4).le("task_date", queryDate).ge("repeat_end", queryDate).apply("WEEKDAY(task_date) < 5").apply("WEEKDAY({0}) < 5", queryDate)
                                .or().eq("`repeat`", 5).le("task_date", queryDate).ge("repeat_end", queryDate).apply("WEEKDAY(task_date) = 5").apply("WEEKDAY({0}) = 5", queryDate)
                                .or().eq("`repeat`", 6).le("task_date", queryDate).ge("repeat_end", queryDate)
                );

        // 获取符合条件的所有任务
        List<Task> allTasks = taskMapper.selectList(baseWrapper);

        if (allTasks.isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }

        // 将所有任务按ID存储在一个映射中
        Map<Long, Task> taskMap = allTasks.stream().collect(Collectors.toMap(Task::getId, task -> task));

        // Step 2: 获取所有任务的标签和提醒
        List<Long> taskIds = allTasks.stream().map(Task::getId).collect(Collectors.toList());

        QueryWrapper<TaskRemind> remindWrapper = new QueryWrapper<>();
        remindWrapper.in("task_id", taskIds);
        List<TaskRemind> taskReminds = taskRemindMapper.selectList(remindWrapper);

        QueryWrapper<TaskLabel> labelWrapper = new QueryWrapper<>();
        labelWrapper.in("task_id", taskIds);
        List<TaskLabel> taskLabels = taskLabelMapper.selectList(labelWrapper);

        // Step 3: 转换为SelectTaskVO对象
        List<SelectTaskVO> selectTaskVOS = mapTasksToSelectTaskVOs(allTasks, taskReminds, taskLabels, taskMap, queryDate);

        // Step 4: 排序
        selectTaskVOS.sort(Comparator.comparing((SelectTaskVO vo) ->
                        Optional.ofNullable(vo.getTask().getTaskTimeBegin()).orElse(LocalTime.MAX))
                .thenComparing(vo -> vo.getTask().getCreatedTime()));

        // Step 5: 分页处理
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, selectTaskVOS.size());
        List<SelectTaskVO> paginatedList = selectTaskVOS.subList(start, end);

        // Step 6: 将转换后的任务对象设定到分页对象中
        Page<SelectTaskVO> resultPage = new Page<>(pageNum, pageSize, selectTaskVOS.size());
        resultPage.setRecords(paginatedList);

        return resultPage;
    }

    /**
     * 将任务列表转换为SelectTaskVO对象列表
     * @param tasks
     * @param taskReminds
     * @param taskLabels
     * @param taskMap
     * @param queryDate
     * @return
     */
    private List<SelectTaskVO> mapTasksToSelectTaskVOs(List<Task> tasks, List<TaskRemind> taskReminds, List<TaskLabel> taskLabels, Map<Long, Task> taskMap, LocalDate queryDate) {
        Map<Long, List<TaskRemind>> remindMap = taskReminds.stream().collect(Collectors.groupingBy(TaskRemind::getTaskId));
        Map<Long, List<TaskLabel>> labelMap = taskLabels.stream().collect(Collectors.groupingBy(TaskLabel::getTaskId));

        // 建立父任务和子任务映射
        Map<Long, List<SelectChildTaskVO>> childTaskMap = new HashMap<>();
        for (Task task : tasks) {
            if (task.getFatherTaskId() != null) {
                childTaskMap.computeIfAbsent(task.getFatherTaskId(), k -> new ArrayList<>()).add(new SelectChildTaskVO(task.getId(), new ArrayList<>()));
            }
        }

        Set<Long> mainTaskIds = tasks.stream()
                .filter(task -> task.getFatherTaskId() == null || isTaskInDate(taskMap.get(task.getFatherTaskId()), queryDate))
                .map(Task::getId)
                .collect(Collectors.toSet());

        // 处理主任务和父任务不在查询日期中的子任务
        return tasks.stream().filter(task ->
                task.getFatherTaskId() == null || !mainTaskIds.contains(task.getFatherTaskId())
        ).map(task -> {
            Long taskId = task.getId();
            List<TaskRemind> reminds = remindMap.getOrDefault(taskId, Collections.emptyList());
            List<TaskLabel> labels = labelMap.getOrDefault(taskId, Collections.emptyList());

            // 获取并递归处理子任务
            List<SelectChildTaskVO> childTaskVOS = getChildTasks(taskId, childTaskMap, taskMap);

            return new SelectTaskVO(task, reminds, labels, childTaskVOS);
        }).collect(Collectors.toList());
    }

    /**
     * 判断任务是否在指定日期内
     * @param task
     * @param queryDate
     * @return
     */
    private boolean isTaskInDate(Task task, LocalDate queryDate) {
        if (task == null) return false;

        LocalDate repeatEndDate = task.getRepeatEnd() != null ? task.getRepeatEnd().toLocalDate() : null;

        return task.getTaskDate().equals(queryDate) ||
                (task.getRepeat() == 1 && queryDate.isAfter(task.getTaskDate()) && (repeatEndDate == null || queryDate.isBefore(repeatEndDate))) ||
                (task.getRepeat() == 2 && queryDate.getDayOfMonth() == task.getTaskDate().getDayOfMonth() && (repeatEndDate == null || queryDate.isBefore(repeatEndDate))) ||
                (task.getRepeat() == 3 && queryDate.getDayOfYear() == task.getTaskDate().getDayOfYear() && (repeatEndDate == null || queryDate.isBefore(repeatEndDate))) ||
                (task.getRepeat() == 4 && queryDate.isAfter(task.getTaskDate()) && (repeatEndDate == null || queryDate.isBefore(repeatEndDate)) && queryDate.getDayOfWeek().getValue() < 6) ||
                (task.getRepeat() == 5 && queryDate.isAfter(task.getTaskDate()) && (repeatEndDate == null || queryDate.isBefore(repeatEndDate)) && queryDate.getDayOfWeek().getValue() == 6) ||
                (task.getRepeat() == 6 && queryDate.isAfter(task.getTaskDate()) && (repeatEndDate == null || queryDate.isBefore(repeatEndDate)));
    }

    /**
     * 获取子任务
     * @param parentId
     * @param childTaskMap
     * @param taskMap
     * @return
     */
    private List<SelectChildTaskVO> getChildTasks(Long parentId, Map<Long, List<SelectChildTaskVO>> childTaskMap, Map<Long, Task> taskMap) {
        List<SelectChildTaskVO> childTasks = childTaskMap.getOrDefault(parentId, new ArrayList<>());

        for (SelectChildTaskVO childTaskVO : childTasks) {
            Long childTaskId = childTaskVO.getChildTaskId();
            childTaskVO.setChildTaskVOS(getChildTasks(childTaskId, childTaskMap, taskMap));
        }

        // 排序子任务
        childTasks.sort(Comparator.comparing((SelectChildTaskVO vo) ->
                        Optional.ofNullable(taskMap.get(vo.getChildTaskId()).getTaskDate()).orElse(LocalDate.MAX))
                .thenComparing(vo -> Optional.ofNullable(taskMap.get(vo.getChildTaskId()).getTaskTimeBegin()).orElse(LocalTime.MAX))
                .thenComparing(vo -> Optional.ofNullable(taskMap.get(vo.getChildTaskId()).getCreatedTime()).orElse(LocalDateTime.MAX)));

        return childTasks;
    }

    /**
     * 根据任务Id查询任务
     *
     * @param taskId
     * @return
     */
    @Override
    public SelectTaskVO selectTaskById(Long taskId) {
        // 获取主任务
        Task task = taskMapper.selectOne(new QueryWrapper<Task>().eq("id", taskId).eq("flag_delete", 0));
        if (task == null) {
            return null; // 如果任务不存在，返回null或抛出异常
        }

        // 获取任务的提醒和标签
        List<TaskRemind> taskReminds = taskRemindMapper.selectList(new QueryWrapper<TaskRemind>().eq("task_id", taskId));
        List<TaskLabel> taskLabels = taskLabelMapper.selectList(new QueryWrapper<TaskLabel>().eq("task_id", taskId));

        // 获取所有子任务
        List<Task> allTasks = getAllTasksByParentId(taskId);
        Map<Long, Task> taskMap = allTasks.stream().collect(Collectors.toMap(Task::getId, t -> t));

        // 建立子任务映射
        Map<Long, List<SelectChildTaskVO>> childTaskMap = new HashMap<>();
        for (Task t : allTasks) {
            if (t.getFatherTaskId() != null) {
                childTaskMap.computeIfAbsent(t.getFatherTaskId(), k -> new ArrayList<>()).add(new SelectChildTaskVO(t.getId(), new ArrayList<>()));
            }
        }

        // 获取并递归处理子任务
        List<SelectChildTaskVO> childTaskVOS = getChildTasks(taskId, childTaskMap, taskMap);

        return new SelectTaskVO(task, taskReminds, taskLabels, childTaskVOS);
    }

    /**
     * 获取所有子任务
     * @param parentId
     * @return
     */
    private List<Task> getAllTasksByParentId(Long parentId) {
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("father_task_id", parentId).or().eq("id", parentId);
        return taskMapper.selectList(queryWrapper);
    }

    /**
     * 添加任务
     *
     * @param addTaskVO
     * @return
     * @throws SchedulerException
     * @throws TaskException
     */
    @Transactional
    @Override
    public int addTask(AddTaskVO addTaskVO) throws SchedulerException, TaskException {
        //获取用户Id
        Long userId = 1L;

        //添加主任务
        Task task = addTaskVO.getTask();
        task.setUserId(userId);
        baseMapper.insert(task);

        //获取任务Id
        Long taskId = task.getId();

        //添加任务提醒
        if (task.getIsRemind() != TaskConstants.TASK_REMIND || addTaskVO.getTaskReminds() != null) {
            List<TaskRemind> taskReminds = addTaskVO.getTaskReminds();
            for (TaskRemind taskRemind : taskReminds) {
                taskRemind.setTaskId(taskId);
                taskRemindService.save(taskRemind);
                //添加定时任务
                PlJob job = setJob(task, taskRemind);
                PlScheduleUtils.createScheduleJob(scheduler, job);
            }
        }

        //添加任务标签
        if (task.getIsLabel() != TaskConstants.TASK_LABEL || addTaskVO.getTaskLabels() != null) {
            List<TaskLabel> taskLabels = addTaskVO.getTaskLabels();
            for (TaskLabel taskLabel : taskLabels) {
                TaskLabel existTaskLabel = findOrCreateLabel(taskLabel, userId);
                taskLabel.setTaskId(taskId);
                taskLabelService.save(existTaskLabel);
            }
        }

        //递归添加子任务
        if (task.getIsHaveChild() == TaskConstants.TASK_HAVE_CHILE || addTaskVO.getAddTaskVOS() != null) {
            List<AddTaskVO> childTaskVOS = addTaskVO.getAddTaskVOS();
            for (AddTaskVO childTaskVO : childTaskVOS) {
                childTaskVO.getTask().setFatherTaskId(taskId);
                addTask(childTaskVO);
            }
        }
        return 1;
    }

    /**
     * 由未完成任务转完成任务
     *
     * @param dateTimeVO
     * @return
     */
    @Transactional
    @Override
    public int comTask(DateTimeVO dateTimeVO) {
        //获取用户Id
        Long userId = 1L;
        //获取任务Id
        Long taskId = dateTimeVO.getTaskId();
        //获取完成日期
        LocalDate completionDate = dateTimeVO.getDate();
        //查找原任务
        Task originalTask = baseMapper.selectById(taskId);
        if (originalTask == null) {
            return 1;
        }
        //判断是否超时
        LocalDateTime finishDateTime = LocalDateTime.now();
        LocalDate finishDate = finishDateTime.toLocalDate();
        if (originalTask.getTaskTimeBegin() != null) {
            LocalDateTime endTime = LocalDateTime.of(originalTask.getTaskDate(), originalTask.getTaskTimeEnd());
            if (finishDateTime.isAfter(endTime)) {
                originalTask.setIsTimeout(TaskConstants.TASK_TIMEOUT);
            }
        } else {
            if (finishDate.isAfter(originalTask.getTaskDate())) {
                originalTask.setIsTimeout(TaskConstants.TASK_TIMEOUT);
            }
        }
        //复制原任务并设置相关字段
        Task newTask = copyTask(originalTask, completionDate);

        //插入新任务
        baseMapper.insert(newTask);
        Long newTaskId = newTask.getId();

        //复制任务提醒
        copyTaskReminds(taskId, newTaskId);

        //复制任务标签
        copyTaskLabels(taskId, newTaskId);

        //递归复制子任务
        copyChildTasks(taskId, newTaskId, completionDate);

        return 1;
    }

    /**
     * 复制子任务
     *
     * @param taskId
     * @param newTaskId
     * @param completionDate
     */
    private void copyChildTasks(Long taskId, Long newTaskId, LocalDate completionDate) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getFatherTaskId, taskId)
                .eq(Task::getFlagDelete, TaskConstants.TASK_NOT_DELETE)
                .orderByAsc(Task::getCreatedTime);
        List<Task> childTasks = baseMapper.selectList(wrapper);
        for (Task childTask : childTasks) {
            Task newChildTask = copyTask(childTask, completionDate);
            newChildTask.setFatherTaskId(newTaskId);
            baseMapper.insert(newChildTask);
            Long newChildTaskId = newChildTask.getId();
            copyTaskReminds(childTask.getId(), newChildTaskId);
            copyTaskLabels(childTask.getId(), newChildTaskId);
            copyChildTasks(childTask.getId(), newChildTaskId, completionDate);
        }
    }

    /**
     * 复制任务标签
     *
     * @param originalTaskId
     * @param newTaskId
     */
    private void copyTaskLabels(Long originalTaskId, Long newTaskId) {
        LambdaQueryWrapper<TaskLabel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskLabel::getTaskId, originalTaskId);
        List<TaskLabel> taskLabels = taskLabelService.list(wrapper);
        for (TaskLabel taskLabel : taskLabels) {
            TaskLabel newTaskLabel = new TaskLabel();
            newTaskLabel.setTaskId(newTaskId);
            newTaskLabel.setLabelId(taskLabel.getLabelId());
            newTaskLabel.setLabelName(taskLabel.getLabelName());
            taskLabelService.save(newTaskLabel);
        }
    }

    /**
     * 复制任务提醒
     *
     * @param originalTaskId
     * @param newTaskId
     */
    private void copyTaskReminds(Long originalTaskId, Long newTaskId) {
        LambdaQueryWrapper<TaskRemind> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskRemind::getTaskId, originalTaskId);
        List<TaskRemind> taskReminds = taskRemindService.list(wrapper);
        for (TaskRemind taskRemind : taskReminds) {
            TaskRemind newTaskRemind = new TaskRemind();
            newTaskRemind.setTaskId(newTaskId);
            newTaskRemind.setType(taskRemind.getType());
            newTaskRemind.setRemindByTime(taskRemind.getRemindByTime());
            newTaskRemind.setRemindByDate(taskRemind.getRemindByDate());
            newTaskRemind.setCorn(taskRemind.getCorn());
            taskRemindService.save(newTaskRemind);
        }
    }

    /**
     * 复制任务
     *
     * @param originalTask
     * @param completionDate
     * @return
     */
    private Task copyTask(Task originalTask, LocalDate completionDate) {
        Task newTask = new Task();
        newTask.setTitle(originalTask.getTitle());
        newTask.setNotes(originalTask.getNotes());
        newTask.setTaskDate(completionDate.isBefore(originalTask.getTaskDate()) ? originalTask.getTaskDate() : completionDate);
        newTask.setTaskTimeBegin(originalTask.getTaskTimeBegin());
        newTask.setTaskTimeEnd(originalTask.getTaskTimeEnd());
        newTask.setRepeat(0);
        newTask.setRepeatEnd(null);
        newTask.setPriority(originalTask.getPriority());
        newTask.setIsComplete(1);
        newTask.setIsEnd(originalTask.getIsEnd());
        newTask.setIsLabel(originalTask.getIsLabel());
        newTask.setIsRemind(originalTask.getIsRemind());
        newTask.setIsHaveChild(originalTask.getIsHaveChild());
        newTask.setIsTimeout(completionDate.isBefore(originalTask.getTaskDate()) ? 0 : originalTask.getIsTimeout());
        newTask.setFatherTaskId(originalTask.getFatherTaskId());
        newTask.setRelatedTaskId(originalTask.getId());
        newTask.setUserId(originalTask.getUserId());
        return newTask;
    }


    /**
     * 计算任务完成度
     *
     * @param startDate
     * @param endDate
     * @param userId
     * @return
     */
    @Override
    public Double taskCompletion(LocalDate startDate, LocalDate endDate, Long userId) {
        int unComTask = baseMapper.countIncompleteTasksByDateRange(userId, startDate, endDate);
        int comTask = baseMapper.countCompleteTasksByDateRange(userId, startDate, endDate);
        if (unComTask + comTask != 0) {
            return (double) comTask / (unComTask + comTask);
        }
        return 0.0;
    }

    /**
     * 删除任务
     *
     * @param taskId
     * @return
     */
    @Transactional
    @Override
    public int delTask(Long taskId) throws SchedulerException {
        Task task = baseMapper.selectById(taskId);

        if (task.getIsComplete() == 0) {
            // 对于未完成任务，设置删除标记为1
            task.setFlagDelete(1);
            baseMapper.updateById(task);
            // 删除定时任务
            if (task.getIsRemind() == TaskConstants.TASK_REMIND) {
                List<TaskRemind> taskReminds = taskRemindMapper.selectList(new LambdaQueryWrapper<TaskRemind>()
                        .eq(TaskRemind::getTaskId, taskId));
                for (TaskRemind taskRemind : taskReminds) {
                    JobKey jobKey = PlScheduleUtils.getJobKey(taskRemind.getId());
                    scheduler.deleteJob(jobKey);
                }
            }
        } else {
            // 对于已完成任务，直接删除任务、提醒、标签及所有子任务
            deleteCompletedTaskAndChildren(taskId);
        }
        return 1;
    }

    /**
     * 由已完成任务转未完成任务
     *
     * @param taskId
     * @return
     */
    @Transactional
    @Override
    public int unComTask(Long taskId) throws SchedulerException, TaskException {

        Task task = baseMapper.selectOne(new LambdaQueryWrapper<Task>()
                .eq(Task::getId, taskId)
                .eq(Task::getFlagDelete, TaskConstants.TASK_NOT_DELETE));
        Task originalTask = baseMapper.selectOne(new LambdaQueryWrapper<Task>()
                .eq(Task::getId, task.getRelatedTaskId())
                .eq(Task::getFlagDelete, TaskConstants.TASK_NOT_DELETE));

        boolean isSame = isTaskSame(task, originalTask);
        if (isSame) {
            deleteCompletedTaskAndChildren(taskId);
        } else {
            // 不完全一致，设置为独立的未完成任务
            setTaskUnComplete(task);
        }

        return 1;
    }

    /**
     * 修改任务
     *
     * @param updateTaskVO
     * @return
     */
    @Transactional
    @Override
    public int updateTask(UpdateTaskVO updateTaskVO) throws SchedulerException, TaskException {
        Task newTask = updateTaskVO.getTask();
        Long taskId = newTask.getId();
        Task originalTask = baseMapper.selectById(taskId);
        if (originalTask == null) {
            return 0;
        }
        if (originalTask.getIsComplete() == 1) {

        } else {
            // 若原任务有提醒
            if (originalTask.getIsRemind() == TaskConstants.TASK_REMIND) {
                //查询原任务提醒
                List<TaskRemind> taskReminds = taskRemindMapper.selectList(new LambdaQueryWrapper<TaskRemind>()
                        .eq(TaskRemind::getTaskId, taskId)
                        .orderByAsc(TaskRemind::getCreatedTime));
                //若新任务没有提醒或提醒不同
                if (updateTaskVO.getTaskReminds() == null
                        || !isTaskRemindSame(updateTaskVO.getTaskReminds(), taskReminds)
                        || !Objects.equals(newTask.getTaskDate(), originalTask.getTaskDate())
                        || !Objects.equals(newTask.getTaskTimeBegin(), originalTask.getTaskTimeBegin())
                        || !Objects.equals(newTask.getTaskTimeEnd(), originalTask.getTaskTimeEnd())
                        || !Objects.equals(newTask.getRepeat(), originalTask.getRepeat())
                        || !Objects.equals(newTask.getRepeatEnd(), originalTask.getRepeatEnd())) {
                    //删除原任务提醒
                    for (TaskRemind taskRemind : taskReminds) {
                        JobKey jobKey = PlScheduleUtils.getJobKey(taskRemind.getId());
                        scheduler.deleteJob(jobKey);
                    }
                    taskRemindMapper.delete(new LambdaQueryWrapper<TaskRemind>().eq(TaskRemind::getTaskId, taskId));
                    //若新任务有提醒
                    if (updateTaskVO.getTaskReminds() != null) {
                        //添加新任务提醒
                        for (TaskRemind taskRemind : updateTaskVO.getTaskReminds()) {
                            taskRemind.setTaskId(taskId);
                            taskRemindService.save(taskRemind);
                            PlJob job = setJob(newTask, taskRemind);
                            PlScheduleUtils.createScheduleJob(scheduler, job);
                        }
                    }
                }
                //若原任务无提醒
            } else {
                //若新任务有提醒
                if (updateTaskVO.getTaskReminds() != null) {
                    for (TaskRemind taskRemind : updateTaskVO.getTaskReminds()) {
                        taskRemind.setTaskId(taskId);
                        taskRemindService.save(taskRemind);
                        PlJob job = setJob(newTask, taskRemind);
                        PlScheduleUtils.createScheduleJob(scheduler, job);
                    }
                }
            }

            // 若原任务有标签
            if (originalTask.getIsLabel() == TaskConstants.TASK_LABEL) {
                //查询原任务标签
                List<TaskLabel> taskLabels = taskLabelMapper.selectList(new LambdaQueryWrapper<TaskLabel>()
                        .eq(TaskLabel::getTaskId, taskId)
                        .orderByAsc(TaskLabel::getCreatedTime));
                //若新任务没有标签或标签不同
                if (updateTaskVO.getTaskLabels() == null
                        || !isTaskLabelSame(updateTaskVO.getTaskLabels(), taskLabels)) {
                    //删除原任务标签
                    taskLabelMapper.delete(new LambdaQueryWrapper<TaskLabel>().eq(TaskLabel::getTaskId, taskId));
                    //若新任务有标签
                    if (updateTaskVO.getTaskLabels() != null) {
                        //添加新任务标签
                        for (TaskLabel taskLabel : updateTaskVO.getTaskLabels()) {
                            TaskLabel existTaskLabel = findOrCreateLabel(taskLabel, newTask.getUserId());
                            taskLabel.setTaskId(taskId);
                            taskLabelService.save(existTaskLabel);
                        }
                    }
                }
                //若原任务无标签
            } else {
                //若新任务有标签
                if (updateTaskVO.getTaskLabels() != null) {
                    for (TaskLabel taskLabel : updateTaskVO.getTaskLabels()) {
                        TaskLabel existTaskLabel = findOrCreateLabel(taskLabel, newTask.getUserId());
                        taskLabel.setTaskId(taskId);
                        taskLabelService.save(existTaskLabel);
                    }
                }
            }
        }
        return 1;
    }

    /**
     * 将不相同的任务设置为独立未完成的任务
     *
     * @param task
     */
    private void setTaskUnComplete(Task task) throws SchedulerException, TaskException {
        Long taskId = task.getId();
        task.setRelatedTaskId(null);
        task.setIsEnd(0);
        task.setIsComplete(0);
        baseMapper.updateById(task);
        if (task.getIsRemind() == TaskConstants.TASK_REMIND) {
            List<TaskRemind> taskReminds = taskRemindMapper.selectList(new LambdaQueryWrapper<TaskRemind>()
                    .eq(TaskRemind::getTaskId, taskId)
                    .orderByAsc(TaskRemind::getCreatedTime));
            for (TaskRemind taskRemind : taskReminds) {
                PlJob job = setJob(task, taskRemind);
                PlScheduleUtils.createScheduleJob(scheduler, job);
            }
        }
        if (task.getIsHaveChild() == TaskConstants.TASK_HAVE_CHILE) {
            List<Task> childTasks = baseMapper.selectList(new LambdaQueryWrapper<Task>().eq(Task::getFatherTaskId, taskId));
            for (Task childTask : childTasks) {
                setTaskUnComplete(childTask);
            }
        }
    }


    /**
     * 判断任务是否相同
     *
     * @param task
     * @param originalTask
     * @return
     */
    private boolean isTaskSame(Task task, Task originalTask) {
        if (!Objects.equals(task.getTitle(), originalTask.getTitle())) return false;
        if (!Objects.equals(task.getNotes(), originalTask.getNotes())) return false;
        if (!Objects.equals(task.getTaskTimeBegin(), originalTask.getTaskTimeBegin())) return false;
        if (!Objects.equals(task.getTaskTimeEnd(), originalTask.getTaskTimeEnd())) return false;
        if (!Objects.equals(task.getPriority(), originalTask.getPriority())) return false;
        if (!Objects.equals(task.getIsLabel(), originalTask.getIsLabel())) return false;
        if (!Objects.equals(task.getIsRemind(), originalTask.getIsRemind())) return false;
        if (!Objects.equals(task.getIsHaveChild(), originalTask.getIsHaveChild())) return false;
        if (!Objects.equals(task.getIsEnd(), originalTask.getIsEnd())) return false;
        if (task.getIsLabel() == TaskConstants.TASK_LABEL) {
            if (!isTaskLabelSame(task.getId(), originalTask.getId())) return false;
        }
        if (task.getIsRemind() == TaskConstants.TASK_REMIND) {
            if (!isTaskRemindSame(task.getId(), originalTask.getId())) return false;
        }
        if (task.getIsHaveChild() == TaskConstants.TASK_HAVE_CHILE) {
            if (!isChildTaskSame(task.getId(), originalTask.getId())) return false;
        }
        return true;
    }

    /**
     * 判断子任务是否相同
     *
     * @param taskId
     * @param originalTaskId
     * @return
     */
    private boolean isChildTaskSame(Long taskId, Long originalTaskId) {
        List<Task> childTasks = baseMapper.selectList(new LambdaQueryWrapper<Task>()
                .eq(Task::getFatherTaskId, taskId)
                .orderByAsc(Task::getCreatedTime));
        List<Task> originalChildTasks = baseMapper.selectList(new LambdaQueryWrapper<Task>()
                .eq(Task::getFatherTaskId, originalTaskId)
                .orderByAsc(Task::getCreatedTime));
        if (childTasks.size() != originalChildTasks.size()) return false;
        int index = 0;
        for (Task childTask : childTasks) {
            if (!isTaskSame(childTask, originalChildTasks.get(index))) return false;
        }
        return true;
    }

    /**
     * 判断TaskRemind是否相同
     *
     * @param taskId
     * @param originalTaskId
     * @return
     */
    private boolean isTaskRemindSame(Long taskId, Long originalTaskId) {
        List<TaskRemind> taskReminds = taskRemindMapper.selectList(new LambdaQueryWrapper<TaskRemind>()
                .eq(TaskRemind::getTaskId, taskId)
                .orderByAsc(TaskRemind::getCreatedTime));
        List<TaskRemind> originalTaskReminds = taskRemindMapper.selectList(new LambdaQueryWrapper<TaskRemind>()
                .eq(TaskRemind::getTaskId, originalTaskId)
                .orderByAsc(TaskRemind::getCreatedTime));
        if (taskReminds.size() != originalTaskReminds.size()) return false;
        int index = 0;
        for (TaskRemind taskRemind : taskReminds) {
            if (!Objects.equals(taskRemind.getType(), originalTaskReminds.get(index).getType())) return false;
            if (!Objects.equals(taskRemind.getRemindByTime(), originalTaskReminds.get(index).getRemindByTime()))
                return false;
            if (!Objects.equals(taskRemind.getRemindByDate(), originalTaskReminds.get(index).getRemindByDate()))
                return false;
            if (!Objects.equals(taskRemind.getCorn(), originalTaskReminds.get(index).getCorn())) return false;
        }
        return true;
    }

    /**
     * 判断TaskRemind是否相同
     *
     * @param taskReminds
     * @param originalTaskReminds
     * @return
     */
    private boolean isTaskRemindSame(List<TaskRemind> taskReminds, List<TaskRemind> originalTaskReminds) {
        if (taskReminds.size() != originalTaskReminds.size()) return false;
        int index = 0;
        for (TaskRemind taskRemind : taskReminds) {
            if (!Objects.equals(taskRemind.getType(), originalTaskReminds.get(index).getType())) return false;
            if (!Objects.equals(taskRemind.getRemindByTime(), originalTaskReminds.get(index).getRemindByTime()))
                return false;
            if (!Objects.equals(taskRemind.getRemindByDate(), originalTaskReminds.get(index).getRemindByDate()))
                return false;
            if (!Objects.equals(taskRemind.getCorn(), originalTaskReminds.get(index).getCorn())) return false;
        }
        return true;
    }

    /**
     * 判断TaskLabel是否相同
     *
     * @param taskId
     * @param originalTaskId
     * @return
     */
    private boolean isTaskLabelSame(Long taskId, Long originalTaskId) {
        List<TaskLabel> taskLabels = taskLabelMapper.selectList(new LambdaQueryWrapper<TaskLabel>()
                .eq(TaskLabel::getTaskId, taskId)
                .orderByAsc(TaskLabel::getCreatedTime));
        List<TaskLabel> originalTaskLabels = taskLabelMapper.selectList(new LambdaQueryWrapper<TaskLabel>()
                .eq(TaskLabel::getTaskId, originalTaskId)
                .orderByAsc(TaskLabel::getCreatedTime));
        if (taskLabels.size() != originalTaskLabels.size()) return false;
        int index = 0;
        for (TaskLabel taskLabel : taskLabels) {
            if (!Objects.equals(taskLabel.getLabelName(), originalTaskLabels.get(index).getLabelName())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断TaskLabel是否相同
     *
     * @param taskLabels
     * @param originalTaskLabels
     * @return
     */
    private boolean isTaskLabelSame(List<TaskLabel> taskLabels, List<TaskLabel> originalTaskLabels) {
        if (taskLabels.size() != originalTaskLabels.size()) return false;
        int index = 0;
        for (TaskLabel taskLabel : taskLabels) {
            if (!Objects.equals(taskLabel.getLabelName(), originalTaskLabels.get(index).getLabelName())) {
                return false;
            }
        }
        return true;
    }


    /**
     * 递归删除已完成任务及其所有子任务
     *
     * @param taskId
     */
    private void deleteCompletedTaskAndChildren(Long taskId) {
        // 删除任务提醒
        taskRemindMapper.delete(new LambdaQueryWrapper<TaskRemind>().eq(TaskRemind::getTaskId, taskId));

        // 删除任务标签
        taskLabelMapper.delete(new LambdaQueryWrapper<TaskLabel>().eq(TaskLabel::getTaskId, taskId));

        // 递归删除所有子任务
        List<Task> childTasks = baseMapper.selectList(new LambdaQueryWrapper<Task>().eq(Task::getFatherTaskId, taskId));
        for (Task childTask : childTasks) {
            deleteCompletedTaskAndChildren(childTask.getId());
        }

        // 删除任务本身
        baseMapper.deleteById(taskId);
    }


    /**
     * 查找或创建标签
     *
     * @param taskLabel
     * @return
     */
    private TaskLabel findOrCreateLabel(TaskLabel taskLabel, Long userId) {
        LambdaQueryWrapper<Label> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Label::getName, taskLabel.getLabelName())
                .eq(Label::getUserId, userId);
        Label label = labelService.getOne(wrapper);
        if (label == null) {
            label = new Label();
            label.setName(taskLabel.getLabelName());
            label.setUserId(userId);
            labelService.save(label);
        }
        //设置标签Id
        taskLabel.setLabelId(label.getId());
        return taskLabel;
    }

    /**
     * 将TaskPage转换为SelectTaskVOPage
     *
     * @param taskPage
     * @return
     */
    private IPage<SelectTaskVO> convertToSelectTaskVOPage(IPage<Task> taskPage) {
        List<Task> tasks = taskPage.getRecords();
        List<SelectTaskVO> taskVOS = converToSelectTaskVOList(tasks);
        Page<SelectTaskVO> taskVOPage = new Page<>(taskPage.getCurrent(), taskPage.getSize(), taskPage.getTotal());
        taskVOPage.setRecords(taskVOS);
        return taskVOPage;
    }

    /**
     * 将TaskList转换为SelectTaskVOList
     *
     * @param tasks
     * @return
     */
    private List<SelectTaskVO> converToSelectTaskVOList(List<Task> tasks) {
        // 创建一个按插入顺序存储任务的映射
        Map<Long, SelectTaskVO> taskMap = new LinkedHashMap<>();
        // 用于存储顶级任务的列表
        List<SelectTaskVO> result = new ArrayList<>();
        // 第一轮循环：将任务转换为 SelectTaskVO 对象，并存入 taskMap
        for (Task task : tasks) {
            // 将 Task 转换为 SelectTaskVO
            SelectTaskVO taskVO = convertToSelectTaskVO(task);
            // 将 SelectTaskVO 存入 taskMap
            taskMap.put(task.getId(), taskVO);
        }

        // 第二轮循环：构建父任务与子任务的层级结构
        for (Task task : tasks) {
            // 从 taskMap 中获取当前任务的 SelectTaskVO
            SelectTaskVO taskVO = taskMap.get(task.getId());
            // 如果当前任务有父任务
            if (task.getFatherTaskId() != null) {
                // 获取父任务的 SelectTaskVO
                SelectTaskVO fatherTaskVO = taskMap.get(task.getFatherTaskId());
                if (fatherTaskVO != null) {
                    // 将当前任务的（仅包含 id）添加到父任务的子任务列表中
                    SelectChildTaskVO selectChildTaskVO = new SelectChildTaskVO(task.getId(), new ArrayList<>());
                    if (fatherTaskVO.getChildTaskVOS() == null) {
                        fatherTaskVO.setChildTaskVOS(new ArrayList<>());
                    }
                    fatherTaskVO.getChildTaskVOS().add(selectChildTaskVO);
                }
            } else {
                // 将当前任务的简化版（仅包含 id）添加到父任务的子任务列表中
                result.add(taskVO);
            }
        }
        return result;
    }

    /**
     * 将Task转换为SelectTaskVO
     *
     * @param task
     * @return
     */
    private SelectTaskVO convertToSelectTaskVO(Task task) {
        SelectTaskVO selectTaskVO = new SelectTaskVO();
        selectTaskVO.setTask(task);

        // 获取提醒和标签信息
        List<TaskRemind> taskReminds = taskRemindService.selectRemindsByTaskId(task.getId());
        List<TaskLabel> taskLabels = taskLabelService.selectLabelsByTaskId(task.getId());

        selectTaskVO.setTaskReminds(taskReminds);
        selectTaskVO.setTaskLabels(taskLabels);
        // 初始化子任务列表
        selectTaskVO.setChildTaskVOS(new ArrayList<>());
        return selectTaskVO;
    }

    /**
     * 设置PlJob实体类属性
     *
     * @param taskRemind
     * @param task
     * @return
     */
    public PlJob setJob(Task task, TaskRemind taskRemind) {
        PlJob job = new PlJob();
        if (task.getTaskTimeBegin() != null) {
            job.setStartTime(TaskDateUtils.LocalDateTimeToDate(LocalDateTime.of(task.getTaskDate(), task.getTaskTimeBegin())));
        } else {
            job.setStartTime(TaskDateUtils.LocalDateTimeToDate(task.getTaskDate().atStartOfDay()));
        }
        job.setRepeat(task.getRepeat());
        if (StringUtils.isNotNull(task.getRepeatEnd())) {
            LocalDateTime time = TaskDateUtils.calculateExecuteTaskDateTime(task.getRepeatEnd()
                    , Integer.parseInt(String.valueOf(taskRemind.getRemindByTime())), Integer.parseInt(String.valueOf(taskRemind.getRemindByDate())));
            time.plusSeconds(1);
            Date date = TaskDateUtils.LocalDateTimeToDate(time);
            job.setRepeatEnd(date);
        }
        if (StringUtils.isNotNull(taskRemind.getRemindByTime())) {
            job.setRemindByTime(Integer.parseInt(taskRemind.getRemindByTime().toString()));
        }
        if (StringUtils.isNotNull(taskRemind.getRemindByDate())) {
            job.setRemindByDate(Integer.parseInt(taskRemind.getRemindByDate().toString()));
        }
        job.setRemindByTime(Integer.parseInt(taskRemind.getRemindByTime().toString()));
        job.setJobId(taskRemind.getId());
        job.setTaskId(task.getId());
        job.setJobName(task.getTitle());
        job.setCronExpression(taskRemind.getCorn());
        return job;
    }

}
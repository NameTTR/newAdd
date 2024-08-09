package com.family.pl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.pl.constant.TaskConstants;
import com.family.pl.domain.*;
import com.family.pl.domain.DTO.request.AddTaskDTO;
import com.family.pl.domain.DTO.request.DateTimeDTO;
import com.family.pl.domain.DTO.request.IntervalDateDTO;
import com.family.pl.domain.DTO.request.UpdateTaskDTO;
import com.family.pl.domain.DTO.response.SelectChildTaskDTO;
import com.family.pl.domain.DTO.response.SelectTaskDTO;
import com.family.pl.mapper.TaskLabelMapper;
import com.family.pl.mapper.TaskRemindMapper;
import com.family.pl.service.*;
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

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 针对表【pl_task(任务表)】的数据库操作Service实现
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
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

    @Autowired
    private TaskService taskService;

    @Autowired
    private StatisticsDateService statisticsDateService;

    /**
     * 查询已完成的任务
     *
     * @param pageNum 当前页码
     * @param date    日期
     * @return 返回分页后的已完成任务列表
     */
    @Override
    public IPage<SelectTaskDTO> selectCompleteTasks(Integer pageNum, LocalDate date) {
        Long userId = 1L;
        // 固定页面大小为10
        int pageSize = 15;

        // Step 1: 获取所有符合条件的任务，包括主任务和子任务
        QueryWrapper<Task> baseWrapper = new QueryWrapper<>();
        baseWrapper.eq("is_complete", 1)
                .eq("user_id", userId)
                .eq("flag_delete", 0)
                .eq("task_date", date);

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
        List<SelectTaskDTO> selectTaskDTOS = mapTasksToSelectTaskVOs(allTasks, taskReminds, taskLabels, taskMap, date);

        // Step 4: 排序
        selectTaskDTOS.sort(Comparator.comparing((SelectTaskDTO vo) ->
                        Optional.ofNullable(vo.getTask().getTaskTimeBegin()).orElse(LocalTime.MAX))
                .thenComparing(vo -> vo.getTask().getCreatedTime()));

        // Step 5: 分页处理
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, selectTaskDTOS.size());
        List<SelectTaskDTO> paginatedList = selectTaskDTOS.subList(start, end);

        // Step 6: 将转换后的任务对象设定到分页对象中
        Page<SelectTaskDTO> resultPage = new Page<>(pageNum, pageSize, selectTaskDTOS.size());
        resultPage.setRecords(paginatedList);

        return resultPage;
    }

    /**
     * 查询未完成的任务
     * 根据指定的日期和页码，查询未完成的任务，并返回分页结果。
     * 未完成的任务是指任务的完成状态为0（未完成），且符合特定重复条件的任务。
     * （可能能提高效率：一次性将所有数据查出来，即写sql一次性将Task，TaskRemind，TaskLabel查出来）
     *
     * @param pageNum 页码，用于分页查询
     * @param date    查询日期
     * @return 返回分页后的未完成任务列表
     */
    @Override
    public IPage<SelectTaskDTO> selectInCompleteTasksAndInTime(Integer pageNum, LocalDate date) {
        Long userId = 1L;
        // 固定页面大小为10
        int pageSize = 15;

        // Step 1: 获取所有符合条件的任务，包括主任务和子任务
        List<Task> allTasks = baseMapper.selectAllTaskByOneDate(date, userId);
        Map<Long, Task> legalTasks = allTasks.stream().collect(Collectors.toMap(Task::getId, task -> task));
        LocalDateTime nowTime = LocalDateTime.now();
        LocalDate nowDate = LocalDate.now();
        allTasks.stream().forEach(task -> {
            Optional.ofNullable(task.getRelatedTaskId()).ifPresent(id -> {
                legalTasks.remove(id);
                legalTasks.remove(task.getId());
            });
            if (StringUtils.isNull(task.getTaskTimeEnd()) && date.isBefore(nowDate)) {
                legalTasks.remove(task.getId());

            } else if (StringUtils.isNotNull(task.getTaskTimeEnd())) {
                LocalDateTime timeEnd = LocalDateTime.of(date, task.getTaskTimeEnd());
                if (timeEnd.isBefore(nowTime)) {
                    legalTasks.remove(task.getId());
                }
            }
        });
        if (allTasks.isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }

        //将taskMap转为List<Task>类型的list
        List<Task> Tasks = new ArrayList<>(legalTasks.values());
        // Step 2: 获取所有任务的标签和提醒
        List<Long> taskIds = allTasks.stream().map(Task::getId).collect(Collectors.toList());

        QueryWrapper<TaskRemind> remindWrapper = new QueryWrapper<>();
        remindWrapper.in("task_id", taskIds);
        List<TaskRemind> taskReminds = taskRemindMapper.selectList(remindWrapper);

        QueryWrapper<TaskLabel> labelWrapper = new QueryWrapper<>();
        labelWrapper.in("task_id", taskIds);
        List<TaskLabel> taskLabels = taskLabelMapper.selectList(labelWrapper);

        // Step 3: 转换为SelectTaskVO对象
        List<SelectTaskDTO> selectTaskDTOS = mapTasksToSelectTaskVOs(Tasks, taskReminds, taskLabels, legalTasks, date);

        // Step 4: 排序
        selectTaskDTOS.sort(Comparator.comparing((SelectTaskDTO vo) ->
                        Optional.ofNullable(vo.getTask().getTaskTimeBegin()).orElse(LocalTime.MAX))
                .thenComparing(vo -> vo.getTask().getCreatedTime()));

        // Step 5: 分页处理
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, selectTaskDTOS.size());
        List<SelectTaskDTO> paginatedList = selectTaskDTOS.subList(start, end);

        // Step 6: 将转换后的任务对象设定到分页对象中
        Page<SelectTaskDTO> resultPage = new Page<>(pageNum, pageSize, selectTaskDTOS.size());
        resultPage.setRecords(paginatedList);

        return resultPage;
    }

    /**
     * 将任务列表转换为SelectTaskVO对象列表，用于前端展示。
     * 转换过程中，会根据任务的提醒和标签进行分组，同时构建任务的父子关系。
     *
     * @param tasks       任务列表
     * @param taskReminds 任务提醒列表
     * @param taskLabels  任务标签列表
     * @param taskMap     任务ID到任务对象的映射
     * @param queryDate   查询日期
     * @return 转换后的SelectTaskVO对象列表
     */
    private List<SelectTaskDTO> mapTasksToSelectTaskVOs(List<Task> tasks, List<TaskRemind> taskReminds, List<TaskLabel> taskLabels, Map<Long, Task> taskMap, LocalDate queryDate) {
        Map<Long, List<TaskRemind>> remindMap = taskReminds.stream().collect(Collectors.groupingBy(TaskRemind::getTaskId));
        Map<Long, List<TaskLabel>> labelMap = taskLabels.stream().collect(Collectors.groupingBy(TaskLabel::getTaskId));

        // 建立父任务和子任务映射
        Map<Long, List<SelectChildTaskDTO>> childTaskMap = new HashMap<>();
        for (Task task : tasks) {
            if (task.getFatherTaskId() != null) {
                Long taskId = task.getId();
                String title = task.getTitle();
                childTaskMap.computeIfAbsent(task.getFatherTaskId(), k -> new ArrayList<>())
                        .add(new SelectChildTaskDTO(taskId, title, new ArrayList<>()));
                // 调试信息
                System.out.println("Adding child task: ID = " + taskId + ", Title = " + title);
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
            List<SelectChildTaskDTO> childTaskVOS = getChildTasks(taskId, childTaskMap, taskMap);

            return new SelectTaskDTO(task, reminds, labels, childTaskVOS);
        }).collect(Collectors.toList());
    }

    /**
     * 获取指定父任务的子任务列表，包括子任务的子任务，即递归获取所有子任务。
     * 对子任务进行排序，排序依据为任务日期、开始时间、创建时间。
     *
     * @param parentId     父任务ID，用于从childTaskMap中查找子任务列表。
     * @param childTaskMap 一个映射，包含所有任务及其子任务列表，用于查找子任务。
     * @param taskMap      一个映射，包含所有任务的详细信息，用于填充子任务的详细信息。
     * @return 返回一个列表，包含指定父任务的所有子任务，包括子任务的子任务。
     */
    private List<SelectChildTaskDTO> getChildTasks(Long parentId, Map<Long, List<SelectChildTaskDTO>> childTaskMap, Map<Long, Task> taskMap) {
        List<SelectChildTaskDTO> childTasks = childTaskMap.getOrDefault(parentId, new ArrayList<>());

        for (SelectChildTaskDTO childTaskVO : childTasks) {
            Long childTaskId = childTaskVO.getChildTaskId();
            Task childTask = taskMap.get(childTaskId);
            if (childTask != null) {
                childTaskVO.setTitle(childTask.getTitle());
                childTaskVO.setChildTaskDTOList(getChildTasks(childTaskId, childTaskMap, taskMap));
                // 调试信息
                System.out.println("Processing child task: ID = " + childTaskId + ", Title = " + childTask.getTitle());
            }
        }

        // 排序子任务
        childTasks.sort(Comparator.comparing((SelectChildTaskDTO vo) ->
                        Optional.ofNullable(taskMap.get(vo.getChildTaskId()).getTaskDate()).orElse(LocalDate.MAX))
                .thenComparing(vo -> Optional.ofNullable(taskMap.get(vo.getChildTaskId()).getTaskTimeBegin()).orElse(LocalTime.MAX))
                .thenComparing(vo -> Optional.ofNullable(taskMap.get(vo.getChildTaskId()).getCreatedTime()).orElse(LocalDateTime.MAX)));

        return childTasks;
    }


    /**
     * 判断任务是否在指定日期内。
     * <p>
     * 本方法用于确定一个任务是否应该在给定的日期执行。任务可能是一次性的，或者按照一定的重复模式重复执行。
     * 重复模式由任务的repeat属性定义，包括每天、每周、每月、每年以及周末等选项。
     *
     * @param task      待检查的任务对象，包含任务的执行日期和重复模式等信息。
     * @param queryDate 查询日期，用于与任务的执行日期进行比较。
     * @return 如果任务应该在指定日期执行，则返回true；否则返回false。
     */
    private boolean isTaskInDate(Task task, LocalDate queryDate) {
        // 如果任务对象为空，则直接返回false。
        if (task == null) {
            return false;
        }

        // 获取任务的重复结束日期，如果任务没有设置重复结束日期，则为null。
        LocalDate repeatEndDate = task.getRepeatEnd() != null ? task.getRepeatEnd().toLocalDate() : null;

        // 根据任务的重复模式检查任务是否应该在查询日期执行。
        // 任务执行日期与查询日期相同视为有效。
        // 对于重复执行的任务，检查查询日期是否在任务开始日期之后且在重复结束日期之前。
        // 根据不同的重复模式（每天、每周、每月、每年、法定工作日、艾宾浩斯记忆法），进行具体比较。
        return task.getTaskDate().equals(queryDate) ||
                (task.getRepeat() == 1 && queryDate.isAfter(task.getTaskDate()) && (repeatEndDate == null || queryDate.isBefore(repeatEndDate))) ||
                (task.getRepeat() == 2 && queryDate.getDayOfMonth() == task.getTaskDate().getDayOfMonth() && (repeatEndDate == null || queryDate.isBefore(repeatEndDate))) ||
                (task.getRepeat() == 3 && queryDate.getDayOfYear() == task.getTaskDate().getDayOfYear() && (repeatEndDate == null || queryDate.isBefore(repeatEndDate))) ||
                (task.getRepeat() == 4 && queryDate.isAfter(task.getTaskDate()) && (repeatEndDate == null || queryDate.isBefore(repeatEndDate)) && queryDate.getDayOfWeek().getValue() < 6) ||
                (task.getRepeat() == 5 && queryDate.isAfter(task.getTaskDate()) && (repeatEndDate == null || queryDate.isBefore(repeatEndDate)) && queryDate.getDayOfWeek().getValue() == 6) ||
                (task.getRepeat() == 6 && queryDate.isAfter(task.getTaskDate()) && (repeatEndDate == null || queryDate.isBefore(repeatEndDate)));
    }


    /**
     * 根据任务ID查询任务详情，包括任务本身、提醒、标签和子任务。
     *
     * @param taskId 任务ID
     * @return 包含任务详情的DTO对象，如果任务不存在则返回null。
     */
    @Override
    public SelectTaskDTO selectTaskById(Long taskId) {
        // 获取主任务
        Task task = taskMapper.selectOne(new QueryWrapper<Task>().eq("id", taskId).eq("flag_delete", 0));
        // 如果任务不存在，则直接返回null。
        if (task == null) {
            return null;
        }

        // 查询与任务关联的提醒信息。
        List<TaskRemind> taskReminds = taskRemindMapper.selectList(new QueryWrapper<TaskRemind>().eq("task_id", taskId));
        // 查询与任务关联的标签信息。
        List<TaskLabel> taskLabels = taskLabelMapper.selectList(new QueryWrapper<TaskLabel>().eq("task_id", taskId));

        // 获取所有子任务
        List<Task> allTasks = getAllTasksByParentId(taskId);
        // 将所有子任务映射为ID-任务对象的映射，方便后续处理。
        Map<Long, Task> taskMap = allTasks.stream().collect(Collectors.toMap(Task::getId, t -> t));

        // 构建子任务的嵌套结构，以父任务ID为键，值为该父任务下的所有子任务列表。
        Map<Long, List<SelectChildTaskDTO>> childTaskMap = new HashMap<>();
        for (Task t : allTasks) {
            if (t.getFatherTaskId() != null) {
                childTaskMap.computeIfAbsent(t.getFatherTaskId(), k -> new ArrayList<>())
                        .add(new SelectChildTaskDTO(t.getId(), t.getTitle(), new ArrayList<>()));
            }
        }

        // 递归处理子任务，构建子任务的嵌套结构。
        List<SelectChildTaskDTO> childTaskVOS = getChildTasks(taskId, childTaskMap, taskMap);

        // 构建并返回包含任务详情的DTO对象。
        return new SelectTaskDTO(task, taskReminds, taskLabels, childTaskVOS);
    }


    /**
     * 获取所有子任务
     *
     * @param parentId
     * @return
     */
    private List<Task> getAllTasksByParentId(Long parentId) {
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("father_task_id", parentId).or().eq("id", parentId);
        return taskMapper.selectList(queryWrapper);
    }

    /**
     * 添加任务及其相关联的提醒和标签。如果任务包含子任务，则同时添加子任务。
     * 采用事务处理确保数据的一致性。
     *
     * @param addTaskDTO 包含待添加任务信息的数据传输对象，包括任务本身、提醒、标签和可能的子任务。
     * @return 返回1，表示任务添加操作成功（此返回值可能需要根据实际业务逻辑进行调整）。
     * @throws SchedulerException 如果调度器操作失败。
     * @throws TaskException      如果任务操作失败。
     */
    @Transactional
    @Override
    public int addTask(AddTaskDTO addTaskDTO) throws SchedulerException, TaskException, ParseException {
        int flag = 1;
        // 默认用户ID，此处应根据实际业务逻辑获取当前用户ID。
        Long userId = 1L;

        // 添加主任务。
        Task task = addTaskDTO.getTask();
        task.setUserId(userId);
        baseMapper.insert(task);

        // 获取新添加任务的ID。
        Long taskId = task.getId();

        // 如果任务需要提醒，且提供了提醒列表，则为任务添加提醒。
        if (!task.getIsRemind().equals(TaskConstants.TASK_REMIND) || addTaskDTO.getTaskRemindList() != null) {
            List<TaskRemind> taskReminds = addTaskDTO.getTaskRemindList();
            for (TaskRemind taskRemind : taskReminds) {
                taskRemind.setTaskId(taskId);
                if (!taskRemindService.save(taskRemind)) {
                    flag = 0;
                }
                // 创建定时任务。
                PlJob job = setJob(task, taskRemind);
                PlScheduleUtils.createScheduleJob(scheduler, job);
            }
        }

        // 如果任务需要标签，且提供了标签列表，则为任务添加标签。
        if (!task.getIsLabel().equals(TaskConstants.TASK_LABEL) || addTaskDTO.getTaskLabelList() != null) {
            List<TaskLabel> taskLabels = addTaskDTO.getTaskLabelList();
            for (TaskLabel taskLabel : taskLabels) {
                TaskLabel existTaskLabel = findOrCreateLabel(taskLabel, userId);
                taskLabel.setTaskId(taskId);
                if (!taskLabelService.save(existTaskLabel)) {
                    flag = 0;
                }
            }
        }

        // 如果任务有子任务，且提供了子任务列表，则递归添加子任务。
        if (task.getIsHaveChild().equals(TaskConstants.TASK_HAVE_CHILE)) {
            List<AddTaskDTO> childTaskVOS = addTaskDTO.getChildTaskDTOList();
            for (AddTaskDTO childTaskVO : childTaskVOS) {
                childTaskVO.getTask().setFatherTaskId(taskId);
                addTask(childTaskVO);
            }
        }
        // 返回成功标识。
        return flag;
    }


    /**
     * 将任务标记为完成。
     * 此方法处理的主要逻辑是将一个未完成的任务转换为完成状态，并进行一系列的关联数据操作，如创建新的完成任务记录，
     * 复制任务提醒和标签，以及递归处理子任务。同时，它还会更新任务的完成日期和判断是否超时。
     *
     * @param dateTimeDTO 包含任务ID和完成日期的数据传输对象。
     * @return 返回固定值1，表示操作已完成（可能用于指示操作成功）。
     */
    @Transactional
    @Override
    public int comTask(DateTimeDTO dateTimeDTO) {
        // 默认用户ID，这里应该根据实际业务进行获取。
        // 获取用户Id
        Long userId = 1L;
        // 从输入中获取任务ID和完成日期。
        // 获取任务Id
        Long taskId = dateTimeDTO.getTaskId();
        // 获取完成日期
        LocalDate completionDate = dateTimeDTO.getDate();

        // 根据任务ID查询原始任务信息。
        // 查找原任务
        Task originalTask = baseMapper.selectById(taskId);
        // 如果原始任务不存在，则直接返回。
        if (originalTask == null) {
            return 1;
        }

        // 复制原始任务并修改为完成状态，并设置完成日期。
        Task newTask = copyTask(originalTask, completionDate);

        // 插入新的完成任务记录。
        baseMapper.insert(newTask);
        Long newTaskId = newTask.getId();

        // 复制原始任务的提醒到新的完成任务。
        copyTaskReminds(taskId, newTaskId);

        // 复制原始任务的标签到新的完成任务。
        copyTaskLabels(taskId, newTaskId);

        // 递归处理原始任务的子任务，将其转换为完成状态。
        copyChildTasks(taskId, newTaskId, completionDate);

        // 更新统计日期信息，可能用于任务统计或报表生成。
        statisticsDateService.addOrUpdateDate(completionDate);

        return 1;
    }


    /**
     * 复制子任务及其相关信息。
     * 该方法用于在给定的任务下复制所有的子任务，并将它们关联到新的父任务下。
     * 同时，也会复制子任务的提醒和标签，并递归处理子任务下的子任务。
     *
     * @param taskId         原始父任务的ID。
     * @param newTaskId      新的父任务的ID。
     * @param completionDate 完成日期，用于复制到新子任务中。
     */
    private void copyChildTasks(Long taskId, Long newTaskId, LocalDate completionDate) {
        // 构建查询Wrapper，用于查询原始父任务下的所有子任务。
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getFatherTaskId, taskId)
                .eq(Task::getFlagDelete, TaskConstants.TASK_NOT_DELETE)
                .orderByAsc(Task::getCreatedTime);

        // 根据查询条件获取所有子任务。
        List<Task> childTasks = baseMapper.selectList(wrapper);

        // 遍历所有子任务，进行复制操作。
        for (Task childTask : childTasks) {
            // 复制子任务，并设置完成日期。
            Task newChildTask = copyTask(childTask, completionDate);
            // 设置新父任务ID。
            newChildTask.setFatherTaskId(newTaskId);
            // 插入新的子任务。
            baseMapper.insert(newChildTask);

            // 获取新复制子任务的ID。
            Long newChildTaskId = newChildTask.getId();
            // 复制原子任务的提醒到新子任务。
            copyTaskReminds(childTask.getId(), newChildTaskId);
            // 复制原子任务的标签到新子任务。
            copyTaskLabels(childTask.getId(), newChildTaskId);
            // 递归复制新子任务下的子任务。
            copyChildTasks(childTask.getId(), newChildTaskId, completionDate);
        }
    }


    /**
     * 复制任务标签
     * 该方法用于将原有任务的标签复制到新创建的任务上。通过查询原有任务的标签列表，
     * 然后为每个标签创建一个新的任务标签实体，最后保存这些新的任务标签。
     * 这样可以确保新任务具有与原有任务相同的标签，便于任务管理。
     *
     * @param originalTaskId 原有任务的ID，用于查询原有的任务标签。
     * @param newTaskId      新创建的任务的ID，用于设置新任务标签的任务ID。
     */
    private void copyTaskLabels(Long originalTaskId, Long newTaskId) {
        // 使用LambdaQueryWrapper构建查询条件，查询原有任务的标签
        LambdaQueryWrapper<TaskLabel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskLabel::getTaskId, originalTaskId);
        List<TaskLabel> taskLabels = taskLabelService.list(wrapper);

        // 遍历查询到的标签列表，为每个标签创建一个新的任务标签实体
        for (TaskLabel taskLabel : taskLabels) {
            TaskLabel newTaskLabel = new TaskLabel();
            // 设置新任务标签的任务ID
            newTaskLabel.setTaskId(newTaskId);
            // 设置新任务标签的标签ID
            newTaskLabel.setLabelId(taskLabel.getLabelId());
            // 设置新任务标签的标签名称
            newTaskLabel.setLabelName(taskLabel.getLabelName());
            // 保存新创建的任务标签
            taskLabelService.save(newTaskLabel);
        }
    }


    /**
     * 复制任务提醒
     * 该方法用于将原有任务的提醒配置复制到新创建的任务上。通过查询原有任务的提醒设置，并为新任务创建相同的提醒设置来实现复制。
     *
     * @param originalTaskId 原任务的ID，用于查询原有的提醒配置。
     * @param newTaskId      新任务的ID，将复制的提醒配置应用于该任务。
     */
    private void copyTaskReminds(Long originalTaskId, Long newTaskId) {
        // 创建查询条件，指定查询原任务ID的任务提醒
        LambdaQueryWrapper<TaskRemind> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskRemind::getTaskId, originalTaskId);
        // 根据查询条件，获取原任务的所有提醒设置
        List<TaskRemind> taskReminds = taskRemindService.list(wrapper);
        // 遍历原任务的提醒设置，为新任务创建相同的提醒设置
        for (TaskRemind taskRemind : taskReminds) {
            TaskRemind newTaskRemind = new TaskRemind();
            // 设置新任务提醒的关联任务ID为新任务的ID
            newTaskRemind.setTaskId(newTaskId);
            // 复制提醒类型、提醒时间、提醒日期和Cron表达式等提醒配置
            newTaskRemind.setType(taskRemind.getType());
            newTaskRemind.setRemindByTime(taskRemind.getRemindByTime());
            newTaskRemind.setRemindByDate(taskRemind.getRemindByDate());
            newTaskRemind.setCorn(taskRemind.getCorn());
            // 保存新任务的提醒设置
            taskRemindService.save(newTaskRemind);
        }
    }


    /**
     * 复制任务，创建一个新的任务实例，其属性基于原始任务和给定的完成日期。
     * 主要用于在任务完成后创建一个新的待完成任务，保持原始任务的属性，同时根据完成日期调整任务日期。
     *
     * @param originalTask   原始任务对象，是被复制的任务源。
     * @param completionDate 完成日期，用于确定新任务的日期是否需要调整。
     * @return 返回一个新的任务实例，复制了原始任务的属性，并根据完成日期进行了必要的调整。
     */
    private Task copyTask(Task originalTask, LocalDate completionDate) {
        // 获取当前时间作为任务完成时间，并判断任务是否超时。
        LocalDateTime finishDateTime = LocalDateTime.now();
        LocalDate finishDate = finishDateTime.toLocalDate();
        if (originalTask.getTaskTimeBegin() != null) {
            // 如果任务有开始时间，判断是否超过设定的结束时间。
            LocalDateTime endTime = LocalDateTime.of(originalTask.getTaskDate(), originalTask.getTaskTimeEnd());
            if (finishDateTime.isAfter(endTime)) {
                originalTask.setIsTimeout(TaskConstants.TASK_TIMEOUT);
            }
        } else {
            // 如果任务没有开始时间，仅判断是否超过任务设定的日期。
            if (finishDate.isAfter(originalTask.getTaskDate())) {
                originalTask.setIsTimeout(TaskConstants.TASK_TIMEOUT);
            }
        }
        // 创建一个新的任务实例
        Task newTask = new Task();
        // 复制原始任务的基本信息
        newTask.setTitle(originalTask.getTitle());
        newTask.setNotes(originalTask.getNotes());
        // 根据完成日期和任务日期决定新任务的日期，如果已完成日期在任务日期之前，则任务日期不变，否则使用完成日期
        newTask.setTaskDate(completionDate.isBefore(originalTask.getTaskDate()) ? originalTask.getTaskDate() : completionDate);
        // 复制原始任务的时间、重复、优先级等属性
        newTask.setTaskTimeBegin(originalTask.getTaskTimeBegin());
        newTask.setTaskTimeEnd(originalTask.getTaskTimeEnd());
        newTask.setRepeat(originalTask.getRepeat());
        newTask.setRepeatEnd(originalTask.getRepeatEnd());
        newTask.setPriority(originalTask.getPriority());
        // 设置新任务的状态为已完成
        newTask.setIsComplete(1);
        // 复制原始任务的其他属性
        newTask.setIsEnd(originalTask.getIsEnd());
        newTask.setIsLabel(originalTask.getIsLabel());
        newTask.setIsRemind(originalTask.getIsRemind());
        newTask.setIsHaveChild(originalTask.getIsHaveChild());
        // 如果完成日期在任务日期之前，则认为任务未超时，否则根据原始任务的超时状态设置
        newTask.setIsTimeout(completionDate.isBefore(originalTask.getTaskDate()) ? 0 : originalTask.getIsTimeout());
        // 设置新任务的相关任务ID和用户ID，保持与原始任务的关联
        newTask.setFatherTaskId(originalTask.getFatherTaskId());
        newTask.setRelatedTaskId(originalTask.getId());
        newTask.setUserId(originalTask.getUserId());
        // 返回复制后的新任务
        return newTask;
    }

    /**
     * 此方法首先检查任务是否已完成。如果任务未完成，它将标记任务为已删除而不是物理删除它，
     * 这是为了保留任务的历史记录。同时，如果任务设置了提醒，它将删除相关的定时任务。
     * 如果任务有子任务，它将递归地删除这些子任务。如果任务已完成，则调用另一个方法
     * 来物理删除任务及其相关的提醒和子任务。
     *
     * @param taskId 任务ID。
     * @return 删除操作的结果，通常为1表示删除成功。
     * @throws SchedulerException 如果调度操作失败。
     */
    @Transactional
    @Override
    public int delTask(Long taskId) throws SchedulerException {
        // 根据任务ID查询任务信息
        Task task = baseMapper.selectById(taskId);

        // 如果任务未完成
        if (task.getIsComplete() == 0) {
            // 标记任务为已删除
            task.setFlagDelete(1);
            baseMapper.updateById(task);

            // 删除定时任务
            if (task.getIsRemind().equals(TaskConstants.TASK_REMIND)) {
                List<TaskRemind> taskReminds = taskRemindMapper.selectList(new LambdaQueryWrapper<TaskRemind>()
                        .eq(TaskRemind::getTaskId, taskId));
                for (TaskRemind taskRemind : taskReminds) {
                    JobKey jobKey = PlScheduleUtils.getJobKey(taskRemind.getId());
                    scheduler.deleteJob(jobKey);
                }
            }

            // 如果任务有子任务，递归删除这些子任务
            if (task.getIsHaveChild().equals(TaskConstants.TASK_HAVE_CHILE)) {
                List<Task> childTasks = taskMapper.selectList(new LambdaQueryWrapper<Task>()
                        .eq(Task::getFatherTaskId, taskId)
                        .eq(Task::getIsComplete, TaskConstants.TASK_NOT_COMMPLETE)
                        .eq(Task::getFlagDelete, TaskConstants.TASK_NOT_DELETE));
                for (Task childTask : childTasks) {
                    delTask(childTask.getId());
                }
            }
        } else {
            // 对于已完成的任务，直接物理删除任务及其相关的提醒和子任务
            deleteCompletedTaskAndChildren(taskId);
        }

        // 返回删除操作的结果，通常为1表示删除成功
        return 1;
    }


    /**
     * 将已完成的任务转换为未完成的任务。
     * 此方法主要用于处理任务状态的反转，即将已完成的任务恢复为未完成状态。这可能由于任务完成后的审核或其他原因导致需要重新开启任务。
     * 方法首先尝试根据任务ID查找任务及其关联的原任务，以确认任务状态的转换是否适用。如果任务和原任务相同，则删除已完成的任务及其子任务；
     * 如果任务和原任务不相同，则将该任务设置为未完成状态。
     *
     * @param taskId 需要转换状态的任务ID。
     * @return 返回固定值1，表示任务状态转换操作已被触发。
     * @throws SchedulerException 如果调度操作失败。
     * @throws TaskException      如果任务操作失败。
     */
    @Transactional
    @Override
    public int unComTask(Long taskId) throws SchedulerException, TaskException, ParseException {
        // 根据任务ID查询任务信息，确保任务未被删除。
        Task task = baseMapper.selectOne(new LambdaQueryWrapper<Task>()
                .eq(Task::getId, taskId)
                .eq(Task::getFlagDelete, TaskConstants.TASK_NOT_DELETE));
        // 根据关联任务ID查询原任务信息，确保原任务未被删除。
        Task originalTask = baseMapper.selectOne(new LambdaQueryWrapper<Task>()
                .eq(Task::getId, task.getRelatedTaskId())
                .eq(Task::getFlagDelete, TaskConstants.TASK_NOT_DELETE));

        // 判断当前任务和原任务是否完全相同。
        boolean isSame = isTaskSame(task, originalTask);
        if (isSame) {
            // 如果任务和原任务相同，删除已完成的任务及其子任务。
            deleteCompletedTaskAndChildren(taskId);
        } else {
            // 如果任务和原任务不相同，将任务设置为未完成状态。
            // 不完全一致，设置为独立的未完成任务
            setTaskUnComplete(task);
        }

        // 返回固定值，表示任务状态转换操作已被触发。
        return 1;
    }


    /**
     * 更新任务信息。
     *
     * @param updateTaskDTO 包含更新后任务信息的数据传输对象。
     * @return 更新操作的结果，成功返回1，任务不存在返回0。
     * @throws SchedulerException 如果调度操作失败。
     * @throws TaskException      如果任务操作失败。
     */
    @Transactional
    @Override
    public int updateTask(UpdateTaskDTO updateTaskDTO) throws SchedulerException, TaskException, ParseException {
        Long userId = 1L;
        // 获取待更新的任务对象
        Task newTask = updateTaskDTO.getTask();
        Long taskId = newTask.getId();
        // 根据任务ID查询原任务信息
        Task originalTask = baseMapper.selectById(taskId);
        // 如果原任务不存在，则不进行更新操作
        if (originalTask == null) {
            return 0;
        }

        // 判断原任务是否设置了提醒
        if (originalTask.getIsRemind().equals(TaskConstants.TASK_REMIND)) {
            // 查询原任务的所有提醒设置
            List<TaskRemind> taskReminds = taskRemindMapper.selectList(new LambdaQueryWrapper<TaskRemind>()
                    .eq(TaskRemind::getTaskId, taskId)
                    .orderByAsc(TaskRemind::getCreatedTime));
            boolean taskRemindSame = isTaskRemindSame(updateTaskDTO.getTaskRemindList(), taskReminds);
            // 判断新任务的提醒设置是否与原任务不同，如果不同，则删除原任务的所有提醒，并根据新任务的提醒设置创建新的提醒
            if (updateTaskDTO.getTaskRemindList() == null
                    || !taskRemindSame
                    || !Objects.equals(newTask.getTaskDate(), originalTask.getTaskDate())
                    || !Objects.equals(newTask.getTaskTimeBegin(), originalTask.getTaskTimeBegin())
                    || !Objects.equals(newTask.getTaskTimeEnd(), originalTask.getTaskTimeEnd())
                    || !Objects.equals(newTask.getRepeat(), originalTask.getRepeat())
                    || !Objects.equals(newTask.getRepeatEnd(), originalTask.getRepeatEnd())) {
                // 删除原任务提醒
                for (TaskRemind taskRemind : taskReminds) {
                    JobKey jobKey = PlScheduleUtils.getJobKey(taskRemind.getId());
                    scheduler.deleteJob(jobKey);
                }
                taskRemindMapper.delete(new LambdaQueryWrapper<TaskRemind>().eq(TaskRemind::getTaskId, taskId));
                // 根据新任务的提醒设置创建新的提醒
                if (updateTaskDTO.getTaskRemindList() != null) {
                    for (TaskRemind taskRemind : updateTaskDTO.getTaskRemindList()) {
                        taskRemind.setTaskId(taskId);
                        taskRemindService.save(taskRemind);
                        if (originalTask.getIsComplete().equals(TaskConstants.TASK_NOT_COMMPLETE)) {
                            PlJob job = setJob(newTask, taskRemind);
                            PlScheduleUtils.createScheduleJob(scheduler, job);
                        }
                    }
                }
            }
        } else {
            // 如果原任务没有提醒，但新任务设置了提醒，则直接根据新任务的提醒设置创建提醒
            if (updateTaskDTO.getTaskRemindList() != null) {
                for (TaskRemind taskRemind : updateTaskDTO.getTaskRemindList()) {
                    taskRemind.setTaskId(taskId);
                    taskRemindService.save(taskRemind);
                    if (originalTask.getIsComplete().equals(TaskConstants.TASK_NOT_COMMPLETE)) {
                        PlJob job = setJob(newTask, taskRemind);
                        PlScheduleUtils.createScheduleJob(scheduler, job);
                    }
                }
            }
        }

        // 判断原任务是否设置了标签
        if (originalTask.getIsLabel().equals(TaskConstants.TASK_LABEL)) {
            // 查询原任务的所有标签
            List<TaskLabel> taskLabels = taskLabelMapper.selectList(new LambdaQueryWrapper<TaskLabel>()
                    .eq(TaskLabel::getTaskId, taskId)
                    .orderByAsc(TaskLabel::getCreatedTime));
            // 判断新任务的标签设置是否与原任务不同，如果不同，则删除原任务的所有标签，并根据新任务的标签设置创建新的标签
            // 若新任务没有标签或标签不同
            if (updateTaskDTO.getTaskLabelList() == null
                    || !isTaskLabelSame(updateTaskDTO.getTaskLabelList(), taskLabels)) {
                // 删除原任务标签
                taskLabelMapper.delete(new LambdaQueryWrapper<TaskLabel>().eq(TaskLabel::getTaskId, taskId));
                // 根据新任务的标签设置创建新的标签
                if (updateTaskDTO.getTaskLabelList() != null) {
                    for (TaskLabel taskLabel : updateTaskDTO.getTaskLabelList()) {
                        TaskLabel existTaskLabel = findOrCreateLabel(taskLabel, userId);
                        taskLabel.setTaskId(taskId);
                        taskLabelService.save(existTaskLabel);
                    }
                }
            }
        } else {
            // 如果原任务没有标签，但新任务设置了标签，则直接根据新任务的标签设置创建标签
            if (updateTaskDTO.getTaskLabelList() != null) {
                for (TaskLabel taskLabel : updateTaskDTO.getTaskLabelList()) {
                    TaskLabel existTaskLabel = findOrCreateLabel(taskLabel, userId);
                    taskLabel.setTaskId(taskId);
                    taskLabelService.save(existTaskLabel);
                }
            }
        }

        newTask.setUserId(userId);
        // 更新任务信息
        taskMapper.updateById(newTask);
        return 1;
    }

    @Override
    public List<Task> getTask(IntervalDateDTO intervalDateDTO) {
        List<Task> tasks = taskMapper.selectLegalTask(intervalDateDTO.getStartDate(), intervalDateDTO.getEndDate(), 1L);
        return tasks;
    }

    /**
     * 根据优先级选择任务
     *
     * @param pageNum 页码
     * @param date    日期
     * @return 分页封装的任务列表
     */
    @Override
    public IPage<SelectTaskDTO> selectInTaskOrderPriority(Integer pageNum, LocalDate date) {
        // 用户ID，此处为演示数据
        Long userId = 1L;
        // 固定页面大小为10
        int pageSize = 15;

        // Step 1: 获取所有符合条件的任务，包括主任务和子任务
        List<Task> allTasks = baseMapper.selectAllTaskByOneDate(date, userId);
        // 通过流收集任务，以便后续处理
        Map<Long, Task> legalTasks = allTasks.stream().collect(Collectors.toMap(Task::getId, task -> task));
        // 遍历所有任务，移除相关联的子任务
        allTasks.stream().forEach(task -> {
            // 检查并移除关联任务ID
            Optional.ofNullable(task.getRelatedTaskId()).ifPresent(id -> {
                legalTasks.remove(id);
                legalTasks.remove(task.getId());
            });
        });
        // 如果没有任务，返回空的分页对象
        if (allTasks.isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }

        // 将taskMap转为List<Task>类型的list
        List<Task> Tasks = new ArrayList<>(legalTasks.values());
        // Step 2: 获取所有任务的标签和提醒
        List<Long> taskIds = allTasks.stream().map(Task::getId).collect(Collectors.toList());

        // 构建查询包装器，用于查询任务提醒
        QueryWrapper<TaskRemind> remindWrapper = new QueryWrapper<>();
        remindWrapper.in("task_id", taskIds);
        List<TaskRemind> taskReminds = taskRemindMapper.selectList(remindWrapper);

        // 构建查询包装器，用于查询任务标签
        QueryWrapper<TaskLabel> labelWrapper = new QueryWrapper<>();
        labelWrapper.in("task_id", taskIds);
        List<TaskLabel> taskLabels = taskLabelMapper.selectList(labelWrapper);

        // Step 3: 转换为SelectTaskVO对象
        List<SelectTaskDTO> selectTaskDTOS = mapTasksToSelectTaskVOs(Tasks, taskReminds, taskLabels, legalTasks, date);

        // Step 4: 根据任务的优先级、开始时间和创建时间对任务进行排序
        selectTaskDTOS.sort(
                // 首先按照任务的优先级进行降序排序
                Comparator.comparing((SelectTaskDTO vo) -> vo.getTask().getPriority(), Comparator.reverseOrder())
                        // 如果优先级相同，则按照任务的开始时间进行升序排序，没有开始时间的任务排在后面
                        .thenComparing(vo -> Optional.ofNullable(vo.getTask().getTaskTimeBegin()).orElse(LocalTime.MAX))
                        // 如果优先级和开始时间都相同，则按照任务的创建时间进行升序排序
                        .thenComparing(vo -> vo.getTask().getCreatedTime()));

        // Step 5: 分页处理
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, selectTaskDTOS.size());
        // 获取当前页码的任务列表
        List<SelectTaskDTO> paginatedList = selectTaskDTOS.subList(start, end);

        // Step 6: 将转换后的任务对象设定到分页对象中
        Page<SelectTaskDTO> resultPage = new Page<>(pageNum, pageSize, selectTaskDTOS.size());
        resultPage.setRecords(paginatedList);

        // 返回分页结果
        return resultPage;
    }

    /**
     * 根据优先级选择公共任务
     * <p>
     * 此方法用于根据指定的优先级、页码和日期筛选并返回用户的公共任务列表它首先检索所有符合条件的任务，
     * 然后根据任务的标签和提醒信息对任务进行加工和转换，最后按照任务的优先级、开始时间和创建时间进行排序，
     * 并进行分页处理返回分页后的任务列表
     *
     * @param pageNum 页码，用于分页查询
     * @param date    指定的日期，用于筛选任务
     * @return 分页后的SelectTaskDTO对象列表
     */
    @Override
    public IPage<SelectTaskDTO> selectComTaskOrderPriority(Integer pageNum, LocalDate date) {
        Long userId = 1L;
        // 固定页面大小为10
        int pageSize = 15;

        // Step 1: 获取所有符合条件的任务，包括主任务和子任务
        QueryWrapper<Task> baseWrapper = new QueryWrapper<>();
        baseWrapper.eq("is_complete", 1)
                .eq("user_id", userId)
                .eq("flag_delete", 0)
                .eq("task_date", date);

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
        List<SelectTaskDTO> selectTaskDTOS = mapTasksToSelectTaskVOs(allTasks, taskReminds, taskLabels, taskMap, date);

        // Step 4: 根据任务的优先级、开始时间和创建时间对任务进行排序
        selectTaskDTOS.sort(
                // 首先按照任务的优先级进行降序排序
                Comparator.comparing((SelectTaskDTO vo) -> vo.getTask().getPriority(), Comparator.reverseOrder())
                        // 如果优先级相同，则按照任务的开始时间进行升序排序，没有开始时间的任务排在后面
                        .thenComparing(vo -> Optional.ofNullable(vo.getTask().getTaskTimeBegin()).orElse(LocalTime.MAX))
                        // 如果优先级和开始时间都相同，则按照任务的创建时间进行升序排序
                        .thenComparing(vo -> vo.getTask().getCreatedTime()));

        // Step 5: 分页处理
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, selectTaskDTOS.size());
        List<SelectTaskDTO> paginatedList = selectTaskDTOS.subList(start, end);

        // Step 6: 将转换后的任务对象设定到分页对象中
        Page<SelectTaskDTO> resultPage = new Page<>(pageNum, pageSize, selectTaskDTOS.size());
        resultPage.setRecords(paginatedList);

        return resultPage;
    }

    /**
     * 根据优先级查询指定日期的任务列表，并进行分页排序
     *
     * @param pageNum  页码数
     * @param date     查询的日期
     * @param priority 任务优先级
     * @return 分页后的任务列表，按优先级、开始时间和创建时间排序
     */
    @Override
    public IPage<SelectTaskDTO> selectInTaskOrderPriority(Integer pageNum, LocalDate date, Integer priority) {
        // 用户ID，此处为演示数据
        Long userId = 1L;
        // 固定页面大小为10
        int pageSize = 15;

        // Step 1: 获取所有符合条件的任务，包括主任务和子任务
        List<Task> allTasks = baseMapper.selectAllTaskByOneDateOrderPriority(date, userId, priority);
        // 通过流收集任务，以便后续处理
        Map<Long, Task> legalTasks = allTasks.stream().collect(Collectors.toMap(Task::getId, task -> task));
        // 遍历所有任务，移除相关联的子任务
        allTasks.stream().forEach(task -> {
            // 检查并移除关联任务ID
            Optional.ofNullable(task.getRelatedTaskId()).ifPresent(id -> {
                legalTasks.remove(id);
                legalTasks.remove(task.getId());
            });
        });
        // 如果没有任务，返回空的分页对象
        if (allTasks.isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }

        // 将taskMap转为List<Task>类型的list
        List<Task> Tasks = new ArrayList<>(legalTasks.values());
        // Step 2: 获取所有任务的标签和提醒
        List<Long> taskIds = allTasks.stream().map(Task::getId).collect(Collectors.toList());

        // 构建查询包装器，用于查询任务提醒
        QueryWrapper<TaskRemind> remindWrapper = new QueryWrapper<>();
        remindWrapper.in("task_id", taskIds);
        List<TaskRemind> taskReminds = taskRemindMapper.selectList(remindWrapper);

        // 构建查询包装器，用于查询任务标签
        QueryWrapper<TaskLabel> labelWrapper = new QueryWrapper<>();
        labelWrapper.in("task_id", taskIds);
        List<TaskLabel> taskLabels = taskLabelMapper.selectList(labelWrapper);

        // Step 3: 转换为SelectTaskVO对象
        List<SelectTaskDTO> selectTaskDTOS = mapTasksToSelectTaskVOs(Tasks, taskReminds, taskLabels, legalTasks, date);

        // Step 4: 根据任务的优先级、开始时间和创建时间对任务进行排序
        selectTaskDTOS.sort(
                // 首先按照任务的优先级进行降序排序
                Comparator.comparing((SelectTaskDTO vo) -> vo.getTask().getPriority(), Comparator.reverseOrder())
                        // 如果优先级相同，则按照任务的开始时间进行升序排序，没有开始时间的任务排在后面
                        .thenComparing(vo -> Optional.ofNullable(vo.getTask().getTaskTimeBegin()).orElse(LocalTime.MAX))
                        // 如果优先级和开始时间都相同，则按照任务的创建时间进行升序排序
                        .thenComparing(vo -> vo.getTask().getCreatedTime()));

        // Step 5: 分页处理
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, selectTaskDTOS.size());
        // 获取当前页码的任务列表
        List<SelectTaskDTO> paginatedList = selectTaskDTOS.subList(start, end);

        // Step 6: 将转换后的任务对象设定到分页对象中
        Page<SelectTaskDTO> resultPage = new Page<>(pageNum, pageSize, selectTaskDTOS.size());
        resultPage.setRecords(paginatedList);

        // 返回分页结果
        return resultPage;
    }

    @Override
    public IPage<SelectTaskDTO> selectInCompleteTaskAndOutTime(Integer pageNum, LocalDate date) {
        Long userId = 1L;
        // 固定页面大小为10
        int pageSize = 15;

        // Step 1: 获取所有符合条件的任务，包括主任务和子任务
        List<Task> allTasks = baseMapper.selectAllTaskByOneDate(date, userId);
        Map<Long, Task> legalTasks = allTasks.stream().collect(Collectors.toMap(Task::getId, task -> task));
        LocalDateTime nowTime = LocalDateTime.now();
        LocalDate nowDate = LocalDate.now();
        allTasks.stream().forEach(task -> {
            Optional.ofNullable(task.getRelatedTaskId()).ifPresent(id -> {
                legalTasks.remove(id);
                legalTasks.remove(task.getId());
            });
            if (StringUtils.isNull(task.getTaskTimeEnd()) && date.isAfter(nowDate)) {
                legalTasks.remove(task.getId());

            } else if (StringUtils.isNotNull(task.getTaskTimeEnd())) {
                LocalDateTime timeEnd = LocalDateTime.of(date, task.getTaskTimeEnd());
                if (timeEnd.isAfter(nowTime)) {
                    legalTasks.remove(task.getId());
                }
            }
        });
        if (allTasks.isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }

        //将taskMap转为List<Task>类型的list
        List<Task> Tasks = new ArrayList<>(legalTasks.values());
        // Step 2: 获取所有任务的标签和提醒
        List<Long> taskIds = allTasks.stream().map(Task::getId).collect(Collectors.toList());

        QueryWrapper<TaskRemind> remindWrapper = new QueryWrapper<>();
        remindWrapper.in("task_id", taskIds);
        List<TaskRemind> taskReminds = taskRemindMapper.selectList(remindWrapper);

        QueryWrapper<TaskLabel> labelWrapper = new QueryWrapper<>();
        labelWrapper.in("task_id", taskIds);
        List<TaskLabel> taskLabels = taskLabelMapper.selectList(labelWrapper);

        // Step 3: 转换为SelectTaskVO对象
        List<SelectTaskDTO> selectTaskDTOS = mapTasksToSelectTaskVOs(Tasks, taskReminds, taskLabels, legalTasks, date);

        // Step 4: 排序
        selectTaskDTOS.sort(Comparator.comparing((SelectTaskDTO vo) ->
                        Optional.ofNullable(vo.getTask().getTaskTimeBegin()).orElse(LocalTime.MAX))
                .thenComparing(vo -> vo.getTask().getCreatedTime()));

        // Step 5: 分页处理
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, selectTaskDTOS.size());
        List<SelectTaskDTO> paginatedList = selectTaskDTOS.subList(start, end);

        // Step 6: 将转换后的任务对象设定到分页对象中
        Page<SelectTaskDTO> resultPage = new Page<>(pageNum, pageSize, selectTaskDTOS.size());
        resultPage.setRecords(paginatedList);

        return resultPage;
    }


//    @Transactional
//    @Override
//    public boolean dTask() {
//        for(long i = 1; i < 400; i++) {
//            deleteCompletedTaskAndChildren(i);
//        }
//        return false;
//    }

    /**
     * 将任务设置为未完成状态。
     * 该方法主要用于将一个任务及其子任务恢复到未完成的状态，同时取消任何相关的提醒，并重新安排子任务。
     * 如果任务有子任务，方法会递归地将所有子任务设置为未完成状态。
     * 如果任务设置了提醒，方法会根据提醒信息重新创建调度任务。
     *
     * @param task 需要被设置为未完成状态的任务对象。
     * @throws SchedulerException 如果操作调度器时发生错误。
     * @throws TaskException      如果处理任务时发生错误。
     */
    private void setTaskUnComplete(Task task) throws SchedulerException, TaskException, ParseException {
        // 获取任务ID，用于后续更新任务状态和查询子任务。
        Long taskId = task.getId();

        // 将任务的相关任务ID、结束状态、完成状态重置为初始状态。
        task.setRelatedTaskId(null);
        task.setIsEnd(0);
        task.setIsComplete(0);
        task.setIsTimeout(0);

        // 更新任务状态为未完成。
        taskMapper.update(null, new UpdateWrapper<Task>()
                .set("related_task_id", null)
                .set("is_end", 0)
                .set("is_complete", 0)
                .set("is_timeout", 0)
                .set("is_complete", 0)
                .eq("ID", taskId));
        // 如果任务设置了提醒，重新创建提醒任务。
        if (task.getIsRemind().equals(TaskConstants.TASK_REMIND)) {
            // 根据任务ID查询所有的提醒信息，并按创建时间升序排序。
            List<TaskRemind> taskReminds = taskRemindMapper.selectList(new LambdaQueryWrapper<TaskRemind>()
                    .eq(TaskRemind::getTaskId, taskId)
                    .orderByAsc(TaskRemind::getCreatedTime));

            // 遍历所有的提醒信息，创建对应的调度任务。
            for (TaskRemind taskRemind : taskReminds) {
                PlJob job = setJob(task, taskRemind);
                PlScheduleUtils.createScheduleJob(scheduler, job);
            }
        }

        // 如果任务有子任务，递归地将所有子任务设置为未完成状态。
        if (task.getIsHaveChild().equals(TaskConstants.TASK_HAVE_CHILE)) {
            // 根据父任务ID查询所有的子任务。
            List<Task> childTasks = baseMapper.selectList(new LambdaQueryWrapper<Task>().eq(Task::getFatherTaskId, taskId));

            // 遍历所有的子任务，递归地设置为未完成状态。
            for (Task childTask : childTasks) {
                setTaskUnComplete(childTask);
            }
        }
    }


    /**
     * 判断两个任务是否相同。
     * 相同的定义包括：标题、备注、开始时间、结束时间、优先级、标签、提醒、子任务状态和完成状态均相同。
     * 如果任务设置了标签，则进一步比较标签内容是否相同。
     * 如果任务设置了提醒，则进一步比较提醒细节是否相同。
     * 如果任务有子任务，则进一步比较子任务是否相同。
     *
     * @param task         当前任务对象
     * @param originalTask 原始任务对象，用于比较是否相同
     * @return 如果任务相同返回true，否则返回false
     */
    private boolean isTaskSame(Task task, Task originalTask) {
        // 比较任务的基本信息：标题、备注、开始时间、结束时间、优先级、标签、提醒、子任务状态和完成状态
        if (!Objects.equals(task.getTitle(), originalTask.getTitle())) {
            return false;
        }
        if (!Objects.equals(task.getNotes(), originalTask.getNotes())) {
            return false;
        }
        if (!Objects.equals(task.getTaskTimeBegin(), originalTask.getTaskTimeBegin())) {
            return false;
        }
        if (!Objects.equals(task.getTaskTimeEnd(), originalTask.getTaskTimeEnd())) {
            return false;
        }
        if (!Objects.equals(task.getPriority(), originalTask.getPriority())) {
            return false;
        }
        if (!Objects.equals(task.getIsLabel(), originalTask.getIsLabel())) {
            return false;
        }
        if (!Objects.equals(task.getIsRemind(), originalTask.getIsRemind())) {
            return false;
        }
        if (!Objects.equals(task.getIsHaveChild(), originalTask.getIsHaveChild())) {
            return false;
        }
        if (!Objects.equals(task.getIsEnd(), originalTask.getIsEnd())) {
            return false;
        }

        // 如果任务设置了标签，进一步比较标签内容
        if (task.getIsLabel().equals(TaskConstants.TASK_LABEL)) {
            if (!isTaskLabelSame(task.getId(), originalTask.getId())) {
                return false;
            }
        }

        // 如果任务设置了提醒，进一步比较提醒细节
        if (task.getIsRemind().equals(TaskConstants.TASK_REMIND)) {
            if (!isTaskRemindSame(task.getId(), originalTask.getId())) {
                return false;
            }
        }

        // 如果任务有子任务，进一步比较子任务是否相同
        if (task.getIsHaveChild().equals(TaskConstants.TASK_HAVE_CHILE)) {
            if (!isChildTaskSame(task.getId(), originalTask.getId())) {
                return false;
            }
        }

        // 所有比较均通过，认定任务相同
        return true;
    }


    /**
     * 判断两个任务的子任务是否完全相同。
     * 通过比较两个任务的子任务列表，在列表长度不相等的情况下直接判定不相同；
     * 如果长度相同，则逐个比较每个子任务是否相同。
     * 子任务的顺序对比较结果有影响，即相同的子任务如果顺序不同，也视为不相同。
     *
     * @param taskId         当前任务的ID
     * @param originalTaskId 原始任务的ID，用于比较其子任务是否与当前任务相同
     * @return 如果两个任务的子任务完全相同，则返回true；否则返回false。
     */
    private boolean isChildTaskSame(Long taskId, Long originalTaskId) {
        // 查询当前任务的所有子任务，并按创建时间升序排序
        List<Task> childTasks = baseMapper.selectList(new LambdaQueryWrapper<Task>()
                .eq(Task::getFatherTaskId, taskId)
                .orderByAsc(Task::getCreatedTime));

        // 查询原始任务的所有子任务，并按创建时间升序排序
        List<Task> originalChildTasks = baseMapper.selectList(new LambdaQueryWrapper<Task>()
                .eq(Task::getFatherTaskId, originalTaskId)
                .orderByAsc(Task::getCreatedTime));

        // 如果子任务数量不相同，则判定两个任务的子任务不相同
        if (childTasks.size() != originalChildTasks.size()) {
            return false;
        }

        // 遍历子任务列表，比较每个子任务是否相同
        int index = 0;
        for (Task childTask : childTasks) {
            // 如果任一子任务比较不相同，则判定两个任务的子任务不相同
            if (!isTaskSame(childTask, originalChildTasks.get(index))) {
                return false;
            }
            index++;
        }

        // 所有子任务比较相同，则判定两个任务的子任务相同
        return true;
    }


    /**
     * 判断两个任务的提醒设置是否相同
     * 如果任何一项不匹配，则认为两个任务的提醒设置不同。
     * 比较的字段包括提醒类型、提醒时间、提醒日期和Cron表达式。
     * 该方法通过查询数据库中与两个任务ID对应的提醒设置，并比较这些设置是否完全相同。
     *
     * @param taskId         当前任务的ID
     * @param originalTaskId 原始任务的ID
     * @return 如果两个任务的提醒设置完全相同，则返回true；否则返回false。
     */
    private boolean isTaskRemindSame(Long taskId, Long originalTaskId) {
        // 查询当前任务的所有提醒设置，并按创建时间升序排序
        List<TaskRemind> taskReminds = taskRemindMapper.selectList(new LambdaQueryWrapper<TaskRemind>()
                .eq(TaskRemind::getTaskId, taskId)
                .orderByAsc(TaskRemind::getCreatedTime));

        // 查询原始任务的所有提醒设置，并按创建时间升序排序
        List<TaskRemind> originalTaskReminds = taskRemindMapper.selectList(new LambdaQueryWrapper<TaskRemind>()
                .eq(TaskRemind::getTaskId, originalTaskId)
                .orderByAsc(TaskRemind::getCreatedTime));

        // 如果两个任务的提醒设置数量不同，则提醒设置必然不同
        if (taskReminds.size() != originalTaskReminds.size()) {
            return false;
        }

        // 遍历当前任务的提醒设置，与原始任务的对应提醒设置进行逐项比较
        int index = 0;
        for (TaskRemind taskRemind : taskReminds) {
            // 如果任何一项提醒设置不同，则认为两个任务的提醒设置不同
            if (!Objects.equals(taskRemind.getType(), originalTaskReminds.get(index).getType())) {
                return false;
            }
            if (!Objects.equals(taskRemind.getRemindByTime(), originalTaskReminds.get(index).getRemindByTime())) {
                return false;
            }
            if (!Objects.equals(taskRemind.getRemindByDate(), originalTaskReminds.get(index).getRemindByDate())) {
                return false;
            }
            if (!Objects.equals(taskRemind.getCorn(), originalTaskReminds.get(index).getCorn())) {
                return false;
            }
            index++;
        }

        // 如果所有提醒设置均相同，则返回true
        return true;
    }


    /**
     * 判断两个任务提醒列表是否相同。
     * 相同的定义是：列表长度相同，并且每个任务提醒的类型、提醒时间、提醒日期和Cron表达式都相同。
     *
     * @param taskReminds         当前的任务提醒列表。
     * @param originalTaskReminds 原始的任务提醒列表，用于比较。
     * @return 如果两个列表中的任务提醒完全相同，则返回true；否则返回false。
     */
    private boolean isTaskRemindSame(List<TaskRemind> taskReminds, List<TaskRemind> originalTaskReminds) {
        // 首先检查两个列表的长度是否相同，如果不相同，则任务提醒肯定不相同。
        if (taskReminds.size() != originalTaskReminds.size()) {
            return false;
        }

        // 使用索引逐个比较两个列表中的任务提醒。
        int index = 0;
        for (TaskRemind taskRemind : taskReminds) {
            // 比较任务提醒的类型。
            if (!Objects.equals(taskRemind.getType(), originalTaskReminds.get(index).getType())) {
                return false;
            }
            // 比较任务提醒的提醒时间。
            if (!Objects.equals(taskRemind.getRemindByTime(), originalTaskReminds.get(index).getRemindByTime())) {
                return false;
            }
            // 比较任务提醒的提醒日期。
            if (!Objects.equals(taskRemind.getRemindByDate(), originalTaskReminds.get(index).getRemindByDate())) {
                return false;
            }
            // 比较任务提醒的Cron表达式。
            if (!Objects.equals(taskRemind.getCorn(), originalTaskReminds.get(index).getCorn())) {
                return false;
            }
            // 移动到下一个任务提醒。
            index++;
        }
        // 如果所有任务提醒都比较完毕，且没有发现不同，则认为两个列表中的任务提醒相同。
        return true;
    }


    /**
     * 判断两个任务的标签是否相同
     * 该方法通过查询两个任务的标签列表，并比较它们的标签名称和创建时间顺序来判断标签是否相同。
     * 如果两个任务的标签数量不同，或者存在标签名称不相同的情况，则认为标签不相同。
     *
     * @param taskId         当前任务的ID
     * @param originalTaskId 原始任务的ID
     * @return 如果标签相同返回true，否则返回false
     */
    private boolean isTaskLabelSame(Long taskId, Long originalTaskId) {
        // 查询当前任务的标签列表，并按创建时间升序排序
        List<TaskLabel> taskLabels = taskLabelMapper.selectList(new LambdaQueryWrapper<TaskLabel>()
                .eq(TaskLabel::getTaskId, taskId)
                .orderByAsc(TaskLabel::getCreatedTime));

        // 查询原始任务的标签列表，并按创建时间升序排序
        List<TaskLabel> originalTaskLabels = taskLabelMapper.selectList(new LambdaQueryWrapper<TaskLabel>()
                .eq(TaskLabel::getTaskId, originalTaskId)
                .orderByAsc(TaskLabel::getCreatedTime));

        // 如果两个任务的标签数量不同，则标签不相同
        if (taskLabels.size() != originalTaskLabels.size()) {
            return false;
        }

        // 遍历当前任务的标签列表，与原始任务的标签列表进行逐个比较
        int index = 0;
        for (TaskLabel taskLabel : taskLabels) {
            // 如果当前标签名称与原始任务对应位置的标签名称不相同，则标签不相同
            if (!Objects.equals(taskLabel.getLabelName(), originalTaskLabels.get(index).getLabelName())) {
                return false;
            }
            index++;
        }

        // 如果所有标签都比较完毕，且没有不相同的标签，则认为标签相同
        return true;
    }


    /**
     * 检查两个任务标签列表是否相同。
     * 相同的定义是：列表长度相同，并且每个标签的名称都相同（考虑到顺序）。
     *
     * @param taskLabels         当前任务的标签列表。
     * @param originalTaskLabels 原始任务的标签列表，用于比较。
     * @return 如果两个列表中的标签完全相同，则返回true；否则返回false。
     */
    private boolean isTaskLabelSame(List<TaskLabel> taskLabels, List<TaskLabel> originalTaskLabels) {
        // 首先检查列表长度，如果不相同，则标签必然不相同。
        if (taskLabels.size() != originalTaskLabels.size()) {
            return false;
        }

        // 通过索引逐个比较标签的名称。
        int index = 0;
        for (TaskLabel taskLabel : taskLabels) {
            // 如果当前标签的名称与原始列表中对应位置的标签名称不相同，则返回false。
            if (!Objects.equals(taskLabel.getLabelName(), originalTaskLabels.get(index).getLabelName())) {
                return false;
            }
            index++;
        }

        // 如果所有标签都比较完毕，且没有发现不相同的标签，则认为两个列表相同。
        return true;
    }


    /**
     * 递归删除已完成任务及其所有子任务
     * （直接删除）
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
     * 查找或创建任务标签。
     * 如果给定的标签名称和用户ID对应的标签已存在，则使用现有标签；否则，创建新的标签。
     * 此方法确保每个用户对每个标签名称只有一个唯一的标签实例。
     *
     * @param taskLabel 包含待处理任务的标签名称和用户ID的信息。
     * @param userId    用户的ID，用于确定标签的所有者。
     * @return 返回带有已存在或新创建标签ID的taskLabel对象。
     */
    private TaskLabel findOrCreateLabel(TaskLabel taskLabel, Long userId) {
        // 构建查询条件，查找名称和用户ID与输入匹配的标签。
        LambdaQueryWrapper<Label> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Label::getName, taskLabel.getLabelName())
                .eq(Label::getUserId, userId);

        // 根据查询条件尝试获取已存在的标签。
        Label label = labelService.getOne(wrapper);
        // 如果标签不存在，则创建新的标签。
        if (label == null) {
            label = new Label();
            label.setName(taskLabel.getLabelName());
            label.setUserId(userId);
            labelService.save(label);
        }
        // 将新获取或创建的标签ID设置到taskLabel对象中。
        // 设置标签Id
        taskLabel.setLabelId(label.getId());

        // 返回带有标签ID的taskLabel对象。
        return taskLabel;
    }


    /**
     * 将TaskPage转换为SelectTaskVOPage
     *
     * @param taskPage
     * @return
     */
    private IPage<SelectTaskDTO> convertToSelectTaskVOPage(IPage<Task> taskPage) {
        List<Task> tasks = taskPage.getRecords();
        List<SelectTaskDTO> taskVOS = converToSelectTaskVOList(tasks);
        Page<SelectTaskDTO> taskVOPage = new Page<>(taskPage.getCurrent(), taskPage.getSize(), taskPage.getTotal());
        taskVOPage.setRecords(taskVOS);
        return taskVOPage;
    }

    /**
     * 将Task列表转换为SelectTaskDTO列表，用于展示任务的树状结构。
     *
     * @param tasks 原始的任务列表。
     * @return 转换后的SelectTaskDTO列表，包含了任务及其子任务的结构。
     */
    private List<SelectTaskDTO> converToSelectTaskVOList(List<Task> tasks) {
        // 创建一个按插入顺序存储任务的映射
        Map<Long, SelectTaskDTO> taskMap = new LinkedHashMap<>();
        // 用于存储顶级任务的列表
        List<SelectTaskDTO> result = new ArrayList<>();
        // 第一轮循环：将任务转换为 SelectTaskVO 对象，并存入 taskMap
        for (Task task : tasks) {
            // 将 Task 转换为 SelectTaskVO
            SelectTaskDTO taskVO = convertToSelectTaskVO(task);
            // 将 SelectTaskVO 存入 taskMap
            taskMap.put(task.getId(), taskVO);
        }

        // 第二轮循环：构建父任务与子任务的层级结构
        for (Task task : tasks) {
            // 从 taskMap 中获取当前任务的 SelectTaskVO
            SelectTaskDTO taskVO = taskMap.get(task.getId());
            // 如果当前任务有父任务
            if (task.getFatherTaskId() != null) {
                // 获取父任务的 SelectTaskVO
                SelectTaskDTO fatherTaskVO = taskMap.get(task.getFatherTaskId());
                if (fatherTaskVO != null) {
                    // 将当前任务的（仅包含 id）添加到父任务的子任务列表中
                    SelectChildTaskDTO selectChildTaskDTO = new SelectChildTaskDTO(task.getId(), new ArrayList<>());
                    if (fatherTaskVO.getChildTaskDTOList() == null) {
                        fatherTaskVO.setChildTaskDTOList(new ArrayList<>());
                    }
                    fatherTaskVO.getChildTaskDTOList().add(selectChildTaskDTO);
                }
            } else {
                // 将当前任务的简化版（仅包含 id）添加到父任务的子任务列表中
                result.add(taskVO);
            }
        }
        return result;
    }

    /**
     * 将Task实体转换为SelectTaskDTO对象，用于前端展示任务详情。
     * 这个转换过程包括设置任务基础信息，并加载与该任务相关的提醒和标签信息。
     *
     * @param task 待转换的任务实体。
     * @return 返回包含任务详情及提醒、标签信息的SelectTaskDTO对象。
     */
    private SelectTaskDTO convertToSelectTaskVO(Task task) {
        // 创建一个新的SelectTaskDTO对象
        SelectTaskDTO selectTaskDTO = new SelectTaskDTO();
        // 设置SelectTaskDTO的task属性为传入的Task对象
        selectTaskDTO.setTask(task);

        // 获取提醒和标签信息
        List<TaskRemind> taskReminds = taskRemindService.selectRemindsByTaskId(task.getId());
        selectTaskDTO.setTaskRemindList(taskReminds);

        // 查询并设置任务的所有标签信息
        List<TaskLabel> taskLabels = taskLabelService.selectLabelsByTaskId(task.getId());
        selectTaskDTO.setTaskLabelList(taskLabels);

        // 初始化子任务列表，确保返回对象中包含一个空的子任务列表
        selectTaskDTO.setChildTaskDTOList(new ArrayList<>());

        // 返回转换后的SelectTaskDTO对象
        return selectTaskDTO;
    }


    /**
     * 根据任务和提醒信息设置PlJob对象的属性。
     * 该方法用于将任务和提醒的细节映射到PlJob对象中，以供后续的调度系统使用。
     *
     * @param task       任务对象，包含任务的基本信息。
     * @param taskRemind 提醒对象，包含任务的提醒方式和时间。
     * @return 返回一个配置完整的PlJob对象。
     */
    public PlJob setJob(Task task, TaskRemind taskRemind) {
        // 创建一个新的PlJob对象
        PlJob job = new PlJob();

        // 设置任务的开始时间
        if (task.getTaskTimeBegin() != null) {
            // 如果任务有具体的开始时间，则将其转换为日期格式
            job.setStartTime(TaskDateUtils.LocalDateTimeToDate(LocalDateTime.of(task.getTaskDate(), task.getTaskTimeBegin())));
        } else {
            // 如果任务没有具体的开始时间，则默认为任务日期的凌晨
            job.setStartTime(TaskDateUtils.LocalDateTimeToDate(task.getTaskDate().atStartOfDay()));
        }

        // 设置任务的重复属性
        job.setRepeat(task.getRepeat());

        // 如果任务有结束重复的时间，则计算结束重复的时间点
        if (StringUtils.isNotNull(task.getRepeatEnd())) {
            LocalDateTime time = TaskDateUtils.calculateExecuteTaskDateTime(task.getRepeatEnd()
                    , taskRemind.getRemindByTime(), taskRemind.getRemindByDate());
            time.plusSeconds(1);
            Date date = TaskDateUtils.LocalDateTimeToDate(time);
            job.setRepeatEnd(date);
        }

        // 如果有提醒时间，则设置提醒时间
        if (StringUtils.isNotNull(taskRemind.getRemindByTime())) {
            job.setRemindByTime(taskRemind.getRemindByTime());
        }

        // 如果有提醒日期，则设置提醒日期
        if (StringUtils.isNotNull(taskRemind.getRemindByDate())) {
            job.setRemindByDate(taskRemind.getRemindByDate());
        }

        // 设置任务调度的相关属性，包括任务ID、提醒ID、任务名称等
        job.setJobId(taskRemind.getId());
        job.setTaskId(task.getId());
        job.setJobName(task.getTitle());
        job.setCronExpression(taskRemind.getCorn());

        // 返回配置完整的PlJob对象
        return job;
    }


}
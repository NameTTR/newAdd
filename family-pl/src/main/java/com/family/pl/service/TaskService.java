package com.family.pl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.family.pl.domain.Task;
import com.baomidou.mybatisplus.extension.service.IService;
import com.family.pl.domain.DTO.request.AddTaskDTO;
import com.family.pl.domain.DTO.request.DateTimeDTO;
import com.family.pl.domain.DTO.request.IntervalDateDTO;
import com.family.pl.domain.DTO.response.SelectTaskDTO;
import com.family.pl.domain.DTO.request.UpdateTaskDTO;
import com.ruoyi.common.exception.job.TaskException;
import org.quartz.SchedulerException;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 针对表【pl_task(任务表)】的数据库操作Service
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
public interface TaskService extends IService<Task> {

    /**
     * 查询已完成的任务
     *
     * @param pageNum 当前页码
     * @param date    日期
     * @return 返回分页后的已完成任务列表
     */
    IPage<SelectTaskDTO> selectCompleteTasks(Integer pageNum, LocalDate date);

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
    IPage<SelectTaskDTO> selectInCompleteTasks(Integer pageNum, LocalDate date);

    /**
     * 根据任务ID查询任务详情，包括任务本身、提醒、标签和子任务。
     *
     * @param taskId 任务ID
     * @return 包含任务详情的DTO对象，如果任务不存在则返回null。
     */
    SelectTaskDTO selectTaskById(Long taskId);

    /**
     * 添加任务及其相关联的提醒和标签。如果任务包含子任务，则同时添加子任务。
     * 采用事务处理确保数据的一致性。
     *
     * @param addTaskDTO 包含待添加任务信息的数据传输对象，包括任务本身、提醒、标签和可能的子任务。
     * @return 返回1，表示任务添加操作成功（此返回值可能需要根据实际业务逻辑进行调整）。
     * @throws SchedulerException 如果调度器操作失败。
     * @throws TaskException      如果任务操作失败。
     */
    int addTask(AddTaskDTO addTaskDTO) throws SchedulerException, TaskException, ParseException;

    /**
     * 将任务标记为完成。
     * 此方法处理的主要逻辑是将一个未完成的任务转换为完成状态，并进行一系列的关联数据操作，如创建新的完成任务记录，
     * 复制任务提醒和标签，以及递归处理子任务。同时，它还会更新任务的完成日期和判断是否超时。
     *
     * @param dateTimeDTO 包含任务ID和完成日期的数据传输对象。
     * @return 返回固定值1，表示操作已完成（可能用于指示操作成功）。
     */
    int comTask(DateTimeDTO dateTimeDTO);

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
    int delTask(Long taskId) throws SchedulerException;

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
    int unComTask(Long taskId) throws SchedulerException, TaskException, ParseException;

    /**
     * 更新任务信息。
     *
     * @param updateTaskDTO 包含更新后任务信息的数据传输对象。
     * @return 更新操作的结果，成功返回1，任务不存在返回0。
     * @throws SchedulerException 如果调度操作失败。
     * @throws TaskException      如果任务操作失败。
     */
    int updateTask(UpdateTaskDTO updateTaskDTO) throws SchedulerException, TaskException, ParseException;

    List<Task> getTask(IntervalDateDTO intervalDateDTO);

    /**
     * 根据优先级选择任务
     *
     * @param pageNum 页码
     * @param date 日期
     * @return 分页封装的任务列表
     */
    IPage<SelectTaskDTO> selectInTaskOrderPriority(Integer pageNum, LocalDate date);

    /**
     * 根据优先级选择公共任务
     *
     * 此方法用于根据指定的优先级、页码和日期筛选并返回用户的公共任务列表它首先检索所有符合条件的任务，
     * 然后根据任务的标签和提醒信息对任务进行加工和转换，最后按照任务的优先级、开始时间和创建时间进行排序，
     * 并进行分页处理返回分页后的任务列表
     *
     * @param pageNum 页码，用于分页查询
     * @param date 指定的日期，用于筛选任务
     * @return 分页后的SelectTaskDTO对象列表
     */
    IPage<SelectTaskDTO> selectComTaskOrderPriority(Integer pageNum, LocalDate date);

    /**
     * 根据优先级查询指定日期的任务列表，并进行分页排序
     *
     * @param pageNum  页码数
     * @param date     查询的日期
     * @param priority 任务优先级
     * @return 分页后的任务列表，按优先级、开始时间和创建时间排序
     */
    IPage<SelectTaskDTO> selectInTaskOrderPriority(Integer pageNum, LocalDate date, Integer priority);


//    boolean dTask();
}

package com.family.pl.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.family.pl.domain.Task;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

/**
* @author 名字
* @description 针对表【pl_task(任务表)】的数据库操作Mapper
* @createDate 2024-07-05 17:05:16
* @Entity com.family.pl.domain.Task
*/
public interface TaskMapper extends BaseMapper<Task> {

    /**
     * 查询已完成任务
     *
     * @param page
     * @param userId
     * @param time
     * @return
     */
    @Select("WITH RECURSIVE task_cte AS ( " +
            "SELECT * FROM pl_task WHERE father_task_id IS NULL " +
            "UNION ALL " +
            "SELECT t.* FROM pl_task t " +
            "INNER JOIN task_cte c ON t.father_task_id = c.ID " +
            ") " +
            "SELECT * FROM task_cte " +
            "WHERE user_id = #{userId} " +
            "AND is_complete = 1 " +
            "AND flag_delete = 0 " +
            "AND (" +
            "  (`repeat` = 0 AND task_date = #{queryDate}) OR " +
            "  (`repeat` = 1 AND #{queryDate} BETWEEN task_date AND repeat_end) OR " +
            "  (`repeat` = 2 AND (" +
            "    DATE_FORMAT(task_date, '%d') = DATE_FORMAT(#{queryDate}, '%d') AND " +
            "    #{queryDate} <= repeat_end)) OR " +
            "  (`repeat` = 3 AND (" +
            "    DATE_FORMAT(task_date, '%m-%d') = DATE_FORMAT(#{queryDate}, '%m-%d') AND " +
            "    #{queryDate} <= repeat_end)) OR " +
            "  (`repeat` = 4 AND (" +
            "    WEEKDAY(task_date) < 5 AND " +
            "    #{queryDate} BETWEEN task_date AND repeat_end AND " +
            "    WEEKDAY(#{queryDate}) < 5)) OR " +
            "  (`repeat` = 5 AND (" +
            "    WEEKDAY(task_date) = 5 AND " +
            "    #{queryDate} BETWEEN task_date AND repeat_end AND " +
            "    WEEKDAY(#{queryDate}) = 5)) OR " +
            "  (`repeat` = 6 AND (#{queryDate} BETWEEN task_date AND repeat_end))" +
            ") " +
            "ORDER BY task_date, task_time_begin")
    IPage<Task> selectByComDateAndUser(Page<Task> page, @Param("userId") Long userId, @Param("queryDate") LocalDate time);

    /**
     * 查询未完成任务
     *
     * @param page
     * @param userId
     * @param time
     * @return
     */
    @Select("WITH RECURSIVE task_cte AS ( " +
            "SELECT * FROM pl_task WHERE father_task_id IS NULL " +
            "UNION ALL " +
            "SELECT t.* FROM pl_task t " +
            "INNER JOIN task_cte c ON t.father_task_id = c.ID " +
            ") " +
            "SELECT * FROM task_cte " +
            "WHERE user_id = #{userId} " +
            "AND is_complete = 0 " +
            "AND flag_delete = 0 " +
            "AND (" +
            "  (`repeat` = 0 AND task_date = #{queryDate}) OR " +
            "  (`repeat` = 1 AND #{queryDate} BETWEEN task_date AND repeat_end) OR " +
            "  (`repeat` = 2 AND (" +
            "    DATE_FORMAT(task_date, '%d') = DATE_FORMAT(#{queryDate}, '%d') AND " +
            "    #{queryDate} <= repeat_end)) OR " +
            "  (`repeat` = 3 AND (" +
            "    DATE_FORMAT(task_date, '%m-%d') = DATE_FORMAT(#{queryDate}, '%m-%d') AND " +
            "    #{queryDate} <= repeat_end)) OR " +
            "  (`repeat` = 4 AND (" +
            "    WEEKDAY(task_date) < 5 AND " +
            "    #{queryDate} BETWEEN task_date AND repeat_end AND " +
            "    WEEKDAY(#{queryDate}) < 5)) OR " +
            "  (`repeat` = 5 AND (" +
            "    WEEKDAY(task_date) = 5 AND " +
            "    #{queryDate} BETWEEN task_date AND repeat_end AND " +
            "    WEEKDAY(#{queryDate}) = 5)) OR " +
            "  (`repeat` = 6 AND (#{queryDate} BETWEEN task_date AND repeat_end))" +
            ") " +
            "ORDER BY task_date, task_time_begin")
    IPage<Task> selectByUncomDateAndUser(Page<Task> page, @Param("userId") Long userId, @Param("queryDate") LocalDate time);

    @Select("WITH RECURSIVE task_cte AS ( " +
            "SELECT * FROM pl_task WHERE father_task_id IS NULL " +
            "UNION ALL " +
            "SELECT t.* FROM pl_task t " +
            "INNER JOIN task_cte c ON t.father_task_id = c.ID " +
            ") " +
            "SELECT COUNT(*) FROM task_cte " +
            "WHERE user_id = #{userId} " +
            "AND is_complete = 0 " +
            "AND flag_delete = 0 " +
            "AND (" +
            "  (`repeat` = 0 AND task_date BETWEEN #{startTime} AND #{endTime}) OR " +
            "  (`repeat` = 1 AND #{startTime} BETWEEN task_date AND repeat_end AND #{endTime} BETWEEN task_date AND repeat_end) OR " +
            "  (`repeat` = 2 AND (" +
            "    DATE_FORMAT(task_date, '%d') = DATE_FORMAT(#{startTime}, '%d') AND " +
            "    DATE_FORMAT(task_date, '%d') = DATE_FORMAT(#{endTime}, '%d') AND " +
            "    #{startTime} <= repeat_end AND #{endTime} <= repeat_end)) OR " +
            "  (`repeat` = 3 AND (" +
            "    DATE_FORMAT(task_date, '%m-%d') = DATE_FORMAT(#{startTime}, '%m-%d') AND " +
            "    DATE_FORMAT(task_date, '%m-%d') = DATE_FORMAT(#{endTime}, '%m-%d') AND " +
            "    #{startTime} <= repeat_end AND #{endTime} <= repeat_end)) OR " +
            "  (`repeat` = 4 AND (" +
            "    WEEKDAY(task_date) < 5 AND " +
            "    #{startTime} BETWEEN task_date AND repeat_end AND #{endTime} BETWEEN task_date AND repeat_end AND " +
            "    WEEKDAY(#{startTime}) < 5 AND WEEKDAY(#{endTime}) < 5)) OR " +
            "  (`repeat` = 5 AND (" +
            "    WEEKDAY(task_date) = 5 AND " +
            "    #{startTime} BETWEEN task_date AND repeat_end AND #{endTime} BETWEEN task_date AND repeat_end AND " +
            "    WEEKDAY(#{startTime}) = 5 AND WEEKDAY(#{endTime}) = 5)) OR " +
            "  (`repeat` = 6 AND (#{startTime} BETWEEN task_date AND repeat_end AND #{endTime} BETWEEN task_date AND repeat_end))" +
            ")")
    int countIncompleteTasksByDateRange(@Param("userId") Long userId, @Param("startTime") LocalDate startTime, @Param("endTime") LocalDate endTime);

    @Select("WITH RECURSIVE task_cte AS ( " +
            "SELECT * FROM pl_task WHERE father_task_id IS NULL " +
            "UNION ALL " +
            "SELECT t.* FROM pl_task t " +
            "INNER JOIN task_cte c ON t.father_task_id = c.ID " +
            ") " +
            "SELECT COUNT(*) FROM task_cte " +
            "WHERE user_id = #{userId} " +
            "AND is_complete = 1 " +
            "AND flag_delete = 0 " +
            "AND (" +
            "  (`repeat` = 0 AND task_date BETWEEN #{startTime} AND #{endTime}) OR " +
            "  (`repeat` = 1 AND #{startTime} BETWEEN task_date AND repeat_end AND #{endTime} BETWEEN task_date AND repeat_end) OR " +
            "  (`repeat` = 2 AND (" +
            "    DATE_FORMAT(task_date, '%d') = DATE_FORMAT(#{startTime}, '%d') AND " +
            "    DATE_FORMAT(task_date, '%d') = DATE_FORMAT(#{endTime}, '%d') AND " +
            "    #{startTime} <= repeat_end AND #{endTime} <= repeat_end)) OR " +
            "  (`repeat` = 3 AND (" +
            "    DATE_FORMAT(task_date, '%m-%d') = DATE_FORMAT(#{startTime}, '%m-%d') AND " +
            "    DATE_FORMAT(task_date, '%m-%d') = DATE_FORMAT(#{endTime}, '%m-%d') AND " +
            "    #{startTime} <= repeat_end AND #{endTime} <= repeat_end)) OR " +
            "  (`repeat` = 4 AND (" +
            "    WEEKDAY(task_date) < 5 AND " +
            "    #{startTime} BETWEEN task_date AND repeat_end AND #{endTime} BETWEEN task_date AND repeat_end AND " +
            "    WEEKDAY(#{startTime}) < 5 AND WEEKDAY(#{endTime}) < 5)) OR " +
            "  (`repeat` = 5 AND (" +
            "    WEEKDAY(task_date) = 5 AND " +
            "    #{startTime} BETWEEN task_date AND repeat_end AND #{endTime} BETWEEN task_date AND repeat_end AND " +
            "    WEEKDAY(#{startTime}) = 5 AND WEEKDAY(#{endTime}) = 5)) OR " +
            "  (`repeat` = 6 AND (#{startTime} BETWEEN task_date AND repeat_end AND #{endTime} BETWEEN task_date AND repeat_end))" +
            ")")
    int countCompleteTasksByDateRange(@Param("userId") Long userId, @Param("startTime") LocalDate startTime, @Param("endTime") LocalDate endTime);
}





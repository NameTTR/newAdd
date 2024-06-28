package com.family.pl.mapper;

import com.family.pl.domain.Task;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
* @author 名字
* @description 针对表【pl_task(任务表)】的数据库操作Mapper
* @createDate 2024-05-19 21:01:45
* @Entity com.family.pl.domain.Task
*/
public interface TaskMapper extends BaseMapper<Task> {

    /**
     * 查询未完成任务
     *
     * @param userId
     * @param queryDate
     * @return
     */
    @Select("SELECT * FROM pl_task " +
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
            ")")
    List<Task> selectByDisDateAndUser(@Param("userId") Long userId, @Param("queryDate") Date queryDate);

    /**
     * 查询已完成任务
     *
     * @param userId
     * @param queryDate
     * @return
     */
    @Select("SELECT * FROM pl_task " +
            "WHERE user_id = #{userId} " +
            "AND flag_delete = 0 " +
            "AND is_complete = 1 " +
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
            ")")
    List<Task> selectByComDateAndUser(@Param("userId") Long userId, @Param("queryDate") Date queryDate);
}





package com.family.pl.mapper;

import com.family.pl.domain.Task;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 针对表【pl_task(任务表)】的数据库操作Mapper
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
public interface TaskMapper extends BaseMapper<Task> {

    @Select("SELECT * FROM pl_task WHERE (task_date <= #{endDate} AND (repeat_end IS NULL OR repeat_end >= #{startDate})) " +
            "And flag_delete = 0 AND user_id = #{userId} AND is_complete = 0")
    List<Task> findTasksBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("userId") Long userId);

//    @Select("SELECT * FROM pl_task WHERE task_date BETWEEN #{startDate} AND #{endDate} And flag_delete = 0 AND user_id = #{userId} And is_complete = 1")
//    List<Task> findComTasksBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("userId") Long userId);

    @Select("SELECT ID, title, notes, task_date, task_time_begin, task_time_end, `repeat`, repeat_end, priority, is_complete, is_end, is_label, is_remind, is_have_child, is_timeout, father_task_id, related_task_id, user_id, created_time, update_time, flag_delete" +
            " FROM pl_task WHERE user_id = #{userId} " +
            "AND is_complete = 0 " +
            "AND flag_delete = 0 " +
            "AND ( " +
            "    (`repeat` = 0 AND task_date BETWEEN #{startTime} AND #{endTime}) " +
            "OR  (`repeat` = 1 AND (#{startTime} BETWEEN task_date AND repeat_end " +
            "OR (repeat_end IS NULL AND #{startTime} > task_date))) " +
            "OR  (`repeat` = 2 AND task_date <= #{endTime} AND (repeat_end >= #{startTime} " +
            "OR (repeat_end IS NULL AND task_date <= #{endTime}))" +
            "    AND ( DATEDIFF(#{startTime},#{endTime})>=6" +
            "    OR (DATEDIFF(#{startTime}, #{endTime})<6" +
            "    AND DAYOFWEEK(task_date) BETWEEN DAYOFWEEK(#{startTime}) AND DAYOFWEEK(#{endTime})))) " +
            "OR  (`repeat` = 3 AND ((task_date <= #{endTime} AND repeat_end >= #{startTime})" +
            "OR (repeat_end IS NULL AND task_date <= #{endTime})) " +
            "    AND (TIMESTAMPDIFF(MONTH, #{startTime}, #{endTime}) >= 1 " +
            "    OR (TIMESTAMPDIFF(MONTH, #{startTime}, #{endTime}) < 1  " +
            "    AND DAYOFMONTH(task_date) BETWEEN DAYOFMONTH(#{startTime}) AND DAYOFMONTH(#{endTime})))) " +
            "OR  (`repeat` = 4 AND ((task_date <= #{endTime} AND repeat_end >= #{startTime})" +
            "OR (repeat_end IS NULL AND task_date <= #{endTime})) " +
            "    AND (DATEDIFF(#{startTime},#{endTime}) >= 3 " +
            "    OR (DATEDIFF(#{startTime},#{endTime}) < 3  " +
            "    AND (((DAYOFWEEK(#{startTime}) BETWEEN 1 AND 5) AND (DAYOFWEEK(#{endTime}) BETWEEN 1 AND 5)) " +
            "    OR ((DAYOFWEEK(#{startTime}) BETWEEN 1 AND 5) AND (DAYOFWEEK(#{endTime}) NOT BETWEEN 1 AND 5)) " +
            "    OR ((DAYOFWEEK(#{startTime}) NOT BETWEEN 1 AND 5) AND (DAYOFWEEK(#{endTime}) BETWEEN 1 AND 5)))))) " +
            "OR  (`repeat` = 5 AND ((task_date <= #{endTime} AND repeat_end >= #{startTime})" +
            "OR repeat_end IS NULL)) " +
            "OR  (`repeat` = 6 AND ((task_date <= #{endTime} AND repeat_end >= #{startTime})" +
            "OR (repeat_end IS NULL AND task_date <= #{endTime})) " +
            "    AND ((task_date BETWEEN #{startTime} AND #{endTime}) " +
            "    OR (DATE_ADD(task_date, INTERVAL 1 DAY) BETWEEN #{startTime} AND #{endTime}) " +
            "    OR (DATE_ADD(task_date, INTERVAL 2 DAY) BETWEEN #{startTime} AND #{endTime}) " +
            "    OR (DATE_ADD(task_date, INTERVAL 4 DAY) BETWEEN #{startTime} AND #{endTime}) " +
            "    OR (DATE_ADD(task_date, INTERVAL 7 DAY) BETWEEN #{startTime} AND #{endTime}) " +
            "    OR (DATE_ADD(task_date, INTERVAL 15 DAY) BETWEEN #{startTime} AND #{endTime}) " +
            "    OR (DATE_ADD(task_date, INTERVAL 30 DAY) BETWEEN #{startTime} AND #{endTime}))) " +
            ")")
    List<Task> selectLegalTask(@Param("startTime") LocalDate startTime, @Param("endTime") LocalDate endTime, @Param("userId") Long userId);

    @Select("SELECT ID, title, notes, task_date, task_time_begin, task_time_end, `repeat`, repeat_end, priority, is_complete, is_end, is_label, is_remind, is_have_child, is_timeout, father_task_id, related_task_id, user_id, created_time, update_time, flag_delete" +
            " FROM pl_task WHERE user_id = #{userId} " +
            "AND flag_delete = 0 " +
            "AND (is_complete = 1 AND task_date BETWEEN #{startTime} AND #{endTime})" +
            "OR  (is_complete = 0 " +
            "AND ( " +
            "    (`repeat` = 0 AND task_date BETWEEN #{startTime} AND #{endTime}) " +
            "OR  (`repeat` = 1 AND (#{startTime} BETWEEN task_date AND repeat_end " +
            "OR  (repeat_end IS NULL AND #{startTime} > task_date))) " +
            "OR  (`repeat` = 2 AND task_date <= #{endTime} AND (repeat_end >= #{startTime} " +
            "OR  (repeat_end IS NULL AND task_date <= #{endTime}))" +
            "    AND ( DATEDIFF(#{startTime},#{endTime})>=6" +
            "    OR (DATEDIFF(#{startTime}, #{endTime})<6" +
            "    AND DAYOFWEEK(task_date) BETWEEN DAYOFWEEK(#{startTime}) AND DAYOFWEEK(#{endTime})))) " +
            "OR  (`repeat` = 3 AND ((task_date <= #{endTime} AND repeat_end >= #{startTime})" +
            "OR  (repeat_end IS NULL AND task_date <= #{endTime})) " +
            "    AND (TIMESTAMPDIFF(MONTH, #{startTime}, #{endTime}) >= 1 " +
            "    OR (TIMESTAMPDIFF(MONTH, #{startTime}, #{endTime}) < 1  " +
            "    AND DAYOFMONTH(task_date) BETWEEN DAYOFMONTH(#{startTime}) AND DAYOFMONTH(#{endTime})))) " +
            "OR  (`repeat` = 4 AND ((task_date <= #{endTime} AND repeat_end >= #{startTime})" +
            "OR  (repeat_end IS NULL AND task_date <= #{endTime})) " +
            "    AND (DATEDIFF(#{startTime},#{endTime}) >= 3 " +
            "    OR (DATEDIFF(#{startTime},#{endTime}) < 3  " +
            "    AND (((DAYOFWEEK(#{startTime}) BETWEEN 2 AND 6) AND (DAYOFWEEK(#{endTime}) BETWEEN 2 AND 6)) " +
            "    OR ((DAYOFWEEK(#{startTime}) BETWEEN 2 AND 6) AND (DAYOFWEEK(#{endTime}) NOT BETWEEN 2 AND 6)) " +
            "    OR ((DAYOFWEEK(#{startTime}) NOT BETWEEN 2 AND 6) AND (DAYOFWEEK(#{endTime}) BETWEEN 2 AND 6)))))) " +
            "OR  (`repeat` = 5 AND ((task_date <= #{endTime} AND repeat_end >= #{startTime})" +
            "OR  repeat_end IS NULL)) " +
            "OR  (`repeat` = 6 AND ((task_date <= #{endTime} AND repeat_end >= #{startTime})" +
            "OR  (repeat_end IS NULL AND task_date <= #{endTime})) " +
            "    AND ((task_date BETWEEN #{startTime} AND #{endTime}) " +
            "    OR (DATE_ADD(task_date, INTERVAL 0 DAY) BETWEEN #{startTime} AND #{endTime}) " +
            "    OR (DATE_ADD(task_date, INTERVAL 1 DAY) BETWEEN #{startTime} AND #{endTime}) " +
            "    OR (DATE_ADD(task_date, INTERVAL 2 DAY) BETWEEN #{startTime} AND #{endTime}) " +
            "    OR (DATE_ADD(task_date, INTERVAL 4 DAY) BETWEEN #{startTime} AND #{endTime}) " +
            "    OR (DATE_ADD(task_date, INTERVAL 7 DAY) BETWEEN #{startTime} AND #{endTime}) " +
            "    OR (DATE_ADD(task_date, INTERVAL 15 DAY) BETWEEN #{startTime} AND #{endTime}) " +
            "    OR (DATE_ADD(task_date, INTERVAL 30 DAY) BETWEEN #{startTime} AND #{endTime}))) " +
            "))")
    List<Task> selectAllTaskByRangeDate(@Param("startTime") LocalDate startTime, @Param("endTime") LocalDate endTime, @Param("userId") Long userId);

    @Select("SELECT ID, title, notes, task_date, task_time_begin, task_time_end, `repeat`, repeat_end, priority, is_complete, is_end, is_label, is_remind, is_have_child, is_timeout, father_task_id, related_task_id, user_id, created_time, update_time, flag_delete " +
            "FROM pl_task WHERE user_id = #{userId} " +
            "AND flag_delete = 0 " +
            "AND is_complete = 0 " +
            "AND (" +
            "    (`repeat` = 0 AND task_date = #{date}) " +
            "OR  (`repeat` = 1 AND task_date <= #{date}) " +
            "OR  (`repeat` = 2 AND task_date <= #{date} AND DAYOFWEEK(task_date) = DAYOFWEEK(#{date})) " +
            "OR  (`repeat` = 3 AND task_date <= #{date} AND DAYOFMONTH(task_date) = DAYOFMONTH(#{date})) " +
            "OR  (`repeat` = 4 AND task_date <= #{date} AND DAYOFWEEK(#{date}) BETWEEN 2 AND 6) " +
            "OR  (`repeat` = 5 AND task_date = #{date}) " +
            "OR  (`repeat` = 6 AND task_date <= #{date})" +
            "     AND ((DATE_ADD(task_date, INTERVAL 0 DAY) = #{date})" +
            "     OR (DATE_ADD(task_date, INTERVAL 1 DAY) = #{date})" +
            "     OR (DATE_ADD(task_date, INTERVAL 2 DAY) = #{date})" +
            "     OR (DATE_ADD(task_date, INTERVAL 4 DAY) = #{date})" +
            "     OR (DATE_ADD(task_date, INTERVAL 7 DAY) = #{date})" +
            "     OR (DATE_ADD(task_date, INTERVAL 15 DAY) = #{date})" +
            "     OR (DATE_ADD(task_date, INTERVAL 30 DAY) = #{date}))" +
            ")")
    List<Task> selectUnTaskByOneDate(@Param("date") LocalDate date, @Param("userId") Long userId);

    @Select("SELECT ID, title, notes, task_date, task_time_begin, task_time_end, `repeat`, repeat_end, priority, is_complete, is_end, is_label, is_remind, is_have_child, is_timeout, father_task_id, related_task_id, user_id, created_time, update_time, flag_delete " +
            "FROM pl_task WHERE user_id = #{userId} " +
            "AND flag_delete = 0 " +
            "AND (is_complete = 1 AND task_date = #{date}) " +
            "OR (is_complete = 0 " +
            "AND (" +
            "    (`repeat` = 0 AND task_date = #{date}) " +
            "OR  (`repeat` = 1 AND task_date <= #{date}) " +
            "OR  (`repeat` = 2 AND task_date <= #{date} AND DAYOFWEEK(task_date) = DAYOFWEEK(#{date})) " +
            "OR  (`repeat` = 3 AND task_date <= #{date} AND DAYOFMONTH(task_date) = DAYOFMONTH(#{date})) " +
            "OR  (`repeat` = 4 AND task_date <= #{date} AND DAYOFWEEK(#{date}) BETWEEN 2 AND 6) " +
            "OR  (`repeat` = 5 AND task_date = #{date}) " +
            "OR  (`repeat` = 6 AND task_date <= #{date})" +
            "     AND ((DATE_ADD(task_date, INTERVAL 0 DAY) = #{date})" +
            "     OR (DATE_ADD(task_date, INTERVAL 1 DAY) = #{date})" +
            "     OR (DATE_ADD(task_date, INTERVAL 2 DAY) = #{date})" +
            "     OR (DATE_ADD(task_date, INTERVAL 4 DAY) = #{date})" +
            "     OR (DATE_ADD(task_date, INTERVAL 7 DAY) = #{date})" +
            "     OR (DATE_ADD(task_date, INTERVAL 15 DAY) = #{date})" +
            "     OR (DATE_ADD(task_date, INTERVAL 30 DAY) = #{date}))" +
            "))")
    List<Task> selectAllTaskByOneDate(@Param("date") LocalDate date, @Param("userId") Long userId);

    @Select("SELECT ID, title, notes, task_date, task_time_begin, task_time_end, `repeat`, repeat_end, priority, is_complete, is_end, is_label, is_remind, is_have_child, is_timeout, father_task_id, related_task_id, user_id, created_time, update_time, flag_delete " +
            "FROM pl_task WHERE user_id = #{userId} " +
            "AND flag_delete = 0 " +
            "AND (is_complete = 1 AND task_date = #{date}) " +
            "OR (is_complete = 0 " +
            "AND (" +
            "    (`repeat` = 0 AND task_date = #{date}) " +
            "OR  (`repeat` = 1 AND task_date <= #{date}) " +
            "OR  (`repeat` = 2 AND task_date <= #{date} AND DAYOFWEEK(task_date) = DAYOFWEEK(#{date})) " +
            "OR  (`repeat` = 3 AND task_date <= #{date} AND DAYOFMONTH(task_date) = DAYOFMONTH(#{date})) " +
            "OR  (`repeat` = 4 AND task_date <= #{date} AND DAYOFWEEK(#{date}) BETWEEN 2 AND 6) " +
            "OR  (`repeat` = 5 AND task_date = #{date}) " +
            "OR  (`repeat` = 6 AND task_date <= #{date})" +
            "     AND ((DATE_ADD(task_date, INTERVAL 0 DAY) = #{date})" +
            "     OR (DATE_ADD(task_date, INTERVAL 1 DAY) = #{date})" +
            "     OR (DATE_ADD(task_date, INTERVAL 2 DAY) = #{date})" +
            "     OR (DATE_ADD(task_date, INTERVAL 4 DAY) = #{date})" +
            "     OR (DATE_ADD(task_date, INTERVAL 7 DAY) = #{date})" +
            "     OR (DATE_ADD(task_date, INTERVAL 15 DAY) = #{date})" +
            "     OR (DATE_ADD(task_date, INTERVAL 30 DAY) = #{date}))" +
            ")) AND priority = #{priority}")
    List<Task> selectAllTaskByOneDateOrderPriority(@Param("date") LocalDate date, @Param("userId") Long userId, @Param("priority") int priority);
}





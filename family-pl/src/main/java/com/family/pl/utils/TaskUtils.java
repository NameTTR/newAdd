package com.family.pl.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.family.pl.domain.TaskLabel;
import com.family.pl.domain.TaskRemind;
import com.family.pl.domain.Task;
import com.family.pl.domain.VO.TaskRemindVO;
import com.family.pl.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 功能：
 * 作者：Name
 * 日期：2024/7/3 14:56
 */
public class TaskUtils {


    /**
     * 判断两个字符串是否相等
     *
     * @param str1
     * @param str2
     * @return
     */
    public static boolean isEquals(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equals(str2);
    }

    /**
     * 判断数据库中提醒和前端传入的提醒是否相等
     *
     * @param dbReminds
     * @param frontendReminds
     * @return
     */
    public static boolean compareReminds(List<TaskRemind> dbReminds, List<TaskRemind> frontendReminds) {
        if (dbReminds == null && frontendReminds == null) return true;
        if (dbReminds == null || frontendReminds == null) return false;
        if (dbReminds.size() != frontendReminds.size()) return false;
        for (TaskRemind frontendRemind : frontendReminds) {
            if (frontendRemind.getId() == null) return false;
        }
        dbReminds.sort(Comparator.comparing(TaskRemind::getId));
        frontendReminds.sort(Comparator.comparing(TaskRemind::getId));
        for (int i = 0; i < dbReminds.size(); i++) {
            TaskRemind dbRemind = dbReminds.get(i);
            TaskRemind frontendRemind = frontendReminds.get(i);
            if (!Objects.equals(dbRemind.getType(), frontendRemind.getType())) return false;
            if (!Objects.equals(dbRemind.getRemindByTime(), frontendRemind.getRemindByTime())) return false;
            if (!Objects.equals(dbRemind.getRemindByDate(), frontendRemind.getRemindByDate())) return false;
            if (!Objects.equals(dbRemind.getCorn(), frontendRemind.getCorn())) return false;
        }
        return true;
    }

    /**
     * 判断数据库中标签和前端传入的提醒是否相等
     *
     * @param dbLabels
     * @param frontendLabels
     * @return
     */
    public static boolean compareLabels(List<TaskLabel> dbLabels, List<TaskLabel> frontendLabels) {
        if (dbLabels == null && frontendLabels == null) return true;
        if (dbLabels == null || frontendLabels == null) return false;
        if (dbLabels.size() != frontendLabels.size()) return false;
        for (TaskLabel frontendLabel : frontendLabels) {
            if (frontendLabel.getId() == null) return false;
        }
        dbLabels.sort(Comparator.comparing(TaskLabel::getId));
        frontendLabels.sort(Comparator.comparing(TaskLabel::getId));
        for (int i = 0; i < dbLabels.size(); i++) {
            TaskLabel dbLabel = dbLabels.get(i);
            TaskLabel frontendLabel = frontendLabels.get(i);
            if (!Objects.equals(dbLabel.getLabelName(), frontendLabel.getLabelName())) return false;
        }
        return true;
    }

    /**
     * 判断数据库中子任务和前端传入的子任务是否相等
     *
     * @param dbChildTask
     * @param frontendChildTask
     * @return
     */
    public static boolean compareChildTasks(Task dbChildTask, Task frontendChildTask) {
        if (dbChildTask == null && frontendChildTask == null) return true;
        if (dbChildTask == null || frontendChildTask == null) return false;
        if (frontendChildTask.getId() == null) return false;
        if (!Objects.equals(dbChildTask.getTitle(), frontendChildTask.getTitle())) return false;
        if (!Objects.equals(dbChildTask.getNotes(), frontendChildTask.getNotes())) return false;
        if (!Objects.equals(dbChildTask.getTaskDate(), frontendChildTask.getTaskDate())) return false;
        if (!Objects.equals(dbChildTask.getTaskTimeBegin(), frontendChildTask.getTaskTimeBegin())) return false;
        if (!Objects.equals(dbChildTask.getTaskTimeEnd(), frontendChildTask.getTaskTimeEnd())) return false;
        if (!Objects.equals(dbChildTask.getRepeat(), frontendChildTask.getRepeat())) return false;
        if (!Objects.equals(dbChildTask.getRepeatEnd(), frontendChildTask.getRepeatEnd())) return false;
        if (!Objects.equals(dbChildTask.getPriority(), frontendChildTask.getPriority())) return false;
        if (!Objects.equals(dbChildTask.getIsComplete(), frontendChildTask.getIsComplete())) return false;
        if (!Objects.equals(dbChildTask.getIsEnd(), frontendChildTask.getIsEnd())) return false;
        if (!Objects.equals(dbChildTask.getIsLabel(), frontendChildTask.getIsLabel())) return false;
        if (!Objects.equals(dbChildTask.getIsRemind(), frontendChildTask.getIsRemind())) return false;
        if (!Objects.equals(dbChildTask.getIsHaveChild(), frontendChildTask.getIsHaveChild())) return false;
        if (!Objects.equals(dbChildTask.getIsTimeout(), frontendChildTask.getIsTimeout())) return false;
        if (!Objects.equals(dbChildTask.getFatherTaskId(), frontendChildTask.getFatherTaskId())) return false;
        if (!Objects.equals(dbChildTask.getRelatedTaskId(), frontendChildTask.getRelatedTaskId())) return false;

        return true;
    }
}
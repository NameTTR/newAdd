package com.family.pl.service;

import com.family.pl.domain.TaskLabel;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 名字
* @description 针对表【pl_task_label(任务标签表)】的数据库操作Service
* @createDate 2024-07-05 17:05:16
*/
public interface TaskLabelService extends IService<TaskLabel> {

    List<TaskLabel> selectLabelsByTaskId(Long taskId);
}

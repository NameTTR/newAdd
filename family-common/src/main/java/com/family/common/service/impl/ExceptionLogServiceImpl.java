package com.family.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.common.domain.ExceptionLog;
import com.family.common.service.ExceptionLogService;
import com.family.common.mapper.ExceptionLogMapper;
import org.springframework.stereotype.Service;

/**
* @author 名字
* @description 针对表【exception_log(异常日志表)】的数据库操作Service实现
* @createDate 2024-07-11 17:06:30
*/
@Service
public class ExceptionLogServiceImpl extends ServiceImpl<ExceptionLogMapper, ExceptionLog>
    implements ExceptionLogService{

}





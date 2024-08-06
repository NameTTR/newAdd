package com.family.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.common.domain.ExceptionLog;
import com.family.common.service.ExceptionLogService;
import com.family.common.mapper.ExceptionLogMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 针对表【exception_log(异常日志表)】的数据库操作Service实现
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-24
 */
@Service
public class ExceptionLogServiceImpl extends ServiceImpl<ExceptionLogMapper, ExceptionLog>
        implements ExceptionLogService {

}





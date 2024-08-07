package com.family.pi.service.impl;

import com.family.pi.domain.po.PiTest;
import com.family.pi.mapper.PiTestMapper;
import com.family.pi.service.IPiTestService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 拼音测试表 服务实现类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-26
 */
@Service
public class PiTestServiceImpl extends ServiceImpl<PiTestMapper, PiTest> implements IPiTestService {

}

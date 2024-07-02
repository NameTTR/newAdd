package com.family.pb.service.impl;

import com.family.pb.domain.po.PbBooks;
import com.family.pb.mapper.PbBooksMapper;
import com.family.pb.service.IPbBooksService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 绘本表 服务实现类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-07-02
 */
@Service
public class PbBooksServiceImpl extends ServiceImpl<PbBooksMapper, PbBooks> implements IPbBooksService {

}

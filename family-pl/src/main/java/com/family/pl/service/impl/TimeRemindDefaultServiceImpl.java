package com.family.pl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.pl.domain.TimeRemindDefault;
import com.family.pl.domain.DTO.request.UpdateRemindDefaultDTO;
import com.family.pl.service.TimeRemindDefaultService;
import com.family.pl.mapper.TimeRemindDefaultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 针对表【pl_time_remind_default(提醒默认表)】的数据库操作Service实现
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
@Service
public class TimeRemindDefaultServiceImpl extends ServiceImpl<TimeRemindDefaultMapper, TimeRemindDefault>
        implements TimeRemindDefaultService {

    @Autowired
    private TimeRemindDefaultService timeRemindDefaultService;


    /**
     * 更新默认提醒设置。
     *
     * @param defaultVO 更新默认提醒设置的数据传输对象，包含需要更新的提醒默认设置列表。
     * @return 更新操作的标志，1 表示更新成功。
     */
    @Override
    public int updateRemindDefault(UpdateRemindDefaultDTO defaultVO) {
        // 设置模拟用户ID，此处应根据实际业务情况进行替换或动态获取
        Long userId = 1L;
        // 初始化标志变量，用于后续表示是否成功执行了删除操作
        int flag = 1;
        // 查询当前用户是否存在已设置的默认提醒类型
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<TimeRemindDefault>()
                .eq(TimeRemindDefault::getUserId, userId)
                .eq(TimeRemindDefault::getType, defaultVO.getRemindDefaultList().get(0).getType()));
        // 如果存在已设置的默认提醒类型，则先删除原有的设置
        if (count > 0) {
            flag = baseMapper.delete(new LambdaQueryWrapper<TimeRemindDefault>()
                    .eq(TimeRemindDefault::getUserId, userId)
                    .eq(TimeRemindDefault::getType, defaultVO.getRemindDefaultList().get(0).getType()));
        }
        // 对默认提醒设置列表进行排序，确保更新顺序一致
        List<TimeRemindDefault> remindDefaults = defaultVO.getRemindDefaultList().stream()
                .sorted(Comparator.comparing(TimeRemindDefault::getType))
                .collect(Collectors.toList());
        // 统一设置用户ID，为后续批量保存做准备
        remindDefaults.forEach(timeRemindDefault -> timeRemindDefault.setUserId(userId));
        // 批量保存更新后的默认提醒设置
        timeRemindDefaultService.saveBatch(remindDefaults);
        // 返回操作标志
        return flag;
    }

}





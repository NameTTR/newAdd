package com.family.pl.service;

import com.family.pl.domain.TimeRemindDefault;
import com.baomidou.mybatisplus.extension.service.IService;
import com.family.pl.domain.DTO.request.UpdateRemindDefaultDTO;

/**
 * <p>
 * 针对表【pl_time_remind_default(提醒默认表)】的数据库操作Service
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
public interface TimeRemindDefaultService extends IService<TimeRemindDefault> {

    /**
     * 更新默认提醒设置。
     *
     * @param defaultVO 更新默认提醒设置的数据传输对象，包含需要更新的提醒默认设置列表。
     * @return 更新操作的标志，1 表示更新成功。
     */
    int updateRemindDefault(UpdateRemindDefaultDTO defaultVO);
}

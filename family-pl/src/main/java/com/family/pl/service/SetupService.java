package com.family.pl.service;

import com.family.pl.domain.Setup;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 针对表【pl_setup(设置表)】的数据库操作Service
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
public interface SetupService extends IService<Setup> {

    /**
     * 根据用户ID查询设置的重复次数。
     *
     * @param userId 用户的唯一标识ID。
     * @return 用户设置的重复次数，如果找不到对应用户，则返回null。
     */
    Integer selectRepeat(Long userId);

    /**
     * 此方法首先尝试根据用户ID查询已存在的设置。如果存在，则更新重复次数并保存更新。
     * 如果不存在，则创建一个新的设置对象，设置用户ID和重复次数，并插入到数据库中。
     * 方法的目的是确保用户的设置信息是最新的，无论之前是否存在该设置。
     *
     * @param repeat 用户设置的重复次数。
     * @param userId 用户的ID。
     * @return 更新操作的标志，始终返回1，表示操作已完成。
     */
    int updateRepeat(Integer repeat, Long userId);

    /**
     * 根据用户ID查询用户的隐藏完成设置。
     * 如果该用户尚未设置隐藏完成状态，则先进行设置并返回默认值0。
     *
     * @param userId 用户ID，用于查询用户的隐藏完成设置。
     * @return 用户的隐藏完成设置值。
     */
    Integer selectHideComplete(Long userId);

    /**
     * 更新用户的隐藏完成状态。
     *
     * @param hide   新的隐藏完成状态值。该参数指示用户是否希望隐藏已完成的任务。
     * @param userId 用户的ID，用于指定哪个用户的隐藏完成状态需要更新。
     * @return 返回操作标志，1表示操作成功，其他值表示操作失败。
     */
    int updateHideComplete(Integer hide, Long userId);

    /**
     * 根据用户ID查询用户的设置，用于确定订单的显示顺序。
     * 如果用户尚未设置，則初始化设置并返回默认值。
     *
     * @param userId 用户ID
     * @return 用户的订单显示顺序设置
     */
    Integer selectOrder(Long userId);

    /**
     * 更新用户的选定排序设置。
     *
     * @param order 用户新选定的排序值。
     * @param userId 用户的ID，用于指定更新哪个用户的设置。
     * @return 返回操作标志，1表示操作成功，其他值表示操作失败。
     */
    int updateOrder(Integer order, Long userId);

}

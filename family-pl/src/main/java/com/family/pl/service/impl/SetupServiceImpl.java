package com.family.pl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.pl.domain.Setup;
import com.family.pl.service.SetupService;
import com.family.pl.mapper.SetupMapper;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 针对表【pl_setup(设置表)】的数据库操作Service实现
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
@Service
public class SetupServiceImpl extends ServiceImpl<SetupMapper, Setup>
        implements SetupService {

    @Autowired
    private SetupMapper setupMapper;

    @Autowired
    private SetupService setupService;

    public SetupServiceImpl(SetupMapper setupMapper) {
        this.setupMapper = setupMapper;
    }

    /**
     * 根据用户ID查询设置的重复次数。
     * 如果该用户尚未设置重复次数，则默认为1，并进行初始化设置。
     *
     * @param userId 用户的唯一标识ID。
     * @return 用户设置的重复次数，如果用户不存在则返回null。
     */
    @Override
    public Integer selectRepeat(Long userId) {
        // 使用LambdaQueryWrapper查询符合条件的第一个Setup对象，并获取其repeat属性值
        Setup setup = baseMapper.selectOne(new LambdaQueryWrapper<Setup>()
                .eq(Setup::getUserId, userId));

        // 如果查询结果为空，说明该用户尚未设置重复次数，进行初始化设置
        if (StringUtils.isNull(setup)) {
            initSetup(userId);
            return 1;
        }

        // 返回已查询到的重复次数
        Integer repeat = setup.getRepeat();
        return repeat;
    }


    /**
     * 保存设置信息。
     * 该方法用于创建一个新的设置对象并将其保存到数据库中。它接收一个用户ID作为参数，
     * 用于关联设置对象和特定用户。通过这种方式，可以为每个用户保存个性化的设置数据。
     *
     * @param userId 用户ID，用于标识哪个用户的设置信息将被保存。
     */
    private void initSetup(Long userId) {
        // 创建一个新的设置对象
        Setup setup = new Setup();
        // 设置用户的ID，用于关联此设置对象和特定用户
        setup.setUserId(userId);
        // 调用setupMapper的insert方法，将新的设置对象插入到数据库中
        setupMapper.insert(setup);
    }

    /**
     * 根据用户ID更新设置的重复次数。
     * 如果该用户的设置已存在，则直接更新重复次数；
     * 如果不存在，则视为新设置，进行插入操作。
     * 此方法通过查询数据库来判断设置是否存在，然后执行相应的更新或插入操作。
     *
     * @param repeat 设置的重复次数，本次更新的目标值。
     * @param userId 用户的唯一标识ID，用于定位设置。
     * @return 返回操作的标志，通常为1，表示操作成功。
     */
    @Override
    public int updateRepeat(Integer repeat, Long userId) {
        // 初始化操作标志为1，表示操作成功。
        int flag = 1;

        // 构建更新条件，指定更新用户的重复次数。
        UpdateWrapper<Setup> queryWrapper = new UpdateWrapper<>();
        queryWrapper.lambda().eq(Setup::getUserId, userId).set(Setup::getRepeat, repeat);

        // 调用Mapper接口的update方法，尝试更新设置的重复次数。
        flag = setupMapper.update(null, queryWrapper);

        // 返回操作标志
        return flag;
    }

    /**
     * 根据用户ID查询用户的隐藏完成设置。
     * 如果该用户尚未设置隐藏完成状态，则先进行设置并返回默认值0。
     *
     * @param userId 用户ID，用于查询用户的隐藏完成设置。
     * @return 用户的隐藏完成设置值。
     */
    @Override
    public Integer selectHideComplete(Long userId) {
        // 使用LambdaQueryWrapper查询唯一的Setup对象，根据userId
        Setup setup = setupMapper.selectOne(new LambdaQueryWrapper<Setup>()
                .eq(Setup::getUserId, userId));

        // 检查查询结果是否为空，如果为空则表示该用户尚未设置隐藏完成状态
        if (StringUtils.isNull(setup)) {
            // 为用户初始化设置
            initSetup(userId);
            // 返回默认值0，表示尚未设置隐藏完成
            return 0;
        }

        // 返回查询到的用户的隐藏完成设置值
        Integer hideComplete = setup.getHideComplete();
        return hideComplete;
    }

    /**
     * 更新用户的隐藏完成状态。
     *
     * @param hide   新的隐藏完成状态值。该参数指示用户是否希望隐藏已完成的任务。
     * @param userId 用户的ID，用于指定哪个用户的隐藏完成状态需要更新。
     * @return 返回操作标志，1表示操作成功，其他值表示操作失败。
     */
    @Override
    public int updateHideComplete(Integer hide, Long userId) {
        // 初始化操作标志为1，表示操作成功。
        int flag = 1;

        // 构建更新条件，指定更新用户的隐藏完成设置。
        UpdateWrapper<Setup> queryWrapper = new UpdateWrapper<>();
        queryWrapper.lambda().eq(Setup::getUserId, userId).set(Setup::getHideComplete, hide);

        // 调用Mapper接口的update方法，尝试更新设置的隐藏完成设置。
        flag = baseMapper.update(null, queryWrapper);

        // 返回操作标志
        return flag;
    }

    /**
     * 根据用户ID查询用户的设置，用于确定订单的显示顺序。
     * 如果用户尚未设置，則初始化设置并返回默认值。
     *
     * @param userId 用户ID
     * @return 用户的订单显示顺序设置
     */
    @Override
    public Integer selectOrder(Long userId) {
        // 使用LambdaQueryWrapper查询唯一的Setup对象，根据userId
        Setup setup = setupMapper.selectOne(new LambdaQueryWrapper<Setup>()
                .eq(Setup::getUserId, userId));

        // 检查查询结果是否为空，如果为空则表示该用户尚未设置隐藏完成状态
        if (StringUtils.isNull(setup)) {
            // 为用户初始化设置
            initSetup(userId);
            // 返回默认值0，表示尚未设置隐藏完成
            return 1;
        }

        // 返回查询到的用户的隐藏完成设置值
        Integer order = setup.getSelectedOrder();
        return order;
    }

    /**
     * 更新用户的选定排序设置。
     *
     * @param order 用户新选定的排序值。
     * @param userId 用户的ID，用于指定更新哪个用户的设置。
     * @return 返回操作标志，1表示操作成功，其他值表示操作失败。
     */
    @Override
    public int updateOrder(Integer order, Long userId) {
        // 初始化操作标志为1，表示操作成功。
        int flag = 1;

        // 构建更新条件，指定更新用户的隐藏完成设置。
        UpdateWrapper<Setup> queryWrapper = new UpdateWrapper<>();
        queryWrapper.lambda().eq(Setup::getUserId, userId).set(Setup::getSelectedOrder, order);

        // 调用Mapper接口的update方法，尝试更新设置的隐藏完成设置。
        flag = baseMapper.update(null, queryWrapper);

        // 返回操作标志
        return flag;
    }


}





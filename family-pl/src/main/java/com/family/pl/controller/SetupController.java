package com.family.pl.controller;

import com.family.pl.service.SetupService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 设置控制器，用于处理计划相关的设置操作。
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-11
 */
@RestController
@RequestMapping("/family/pl/setup")
public class SetupController extends BaseController {

    @Autowired
    private SetupService setupService;

    /**
     * 查询用户的重复设置信息。
     * 通过GET请求访问该方法，返回用户当前的重复设置状态。
     *
     * @return AjaxResult 包含操作结果和用户重复设置信息的映射。
     */
    @GetMapping("/repeat")
    public AjaxResult selectRepeat() {
        Long userId = 1L;
        Integer repeat = setupService.selectRepeat(userId);
        Map<String, Integer> map = new HashMap<>();
        map.put("repeat", repeat);
        return AjaxResult.success(map);
    }

    /**
     * 更新用户的重复设置信息。
     * 通过PUT请求访问该方法，更新用户的重复设置状态。
     *
     * @param repeat 用户新的重复设置状态。
     * @return AjaxResult 包含操作结果的响应对象。
     */
    @PutMapping("/repeat/{repeat}")
    public AjaxResult updateRepeat(@PathVariable Integer repeat) {
        Long userId = 1L;

        return toAjax(setupService.updateRepeat(repeat, userId));
    }

    /**
     * 通过GET请求获取隐藏完成状态
     *
     * @return AjaxResult 包含隐藏完成状态的结果对象
     */
    @GetMapping("/hide_complete")
    public AjaxResult selectHideComplete() {
        // 设置默认用户ID
        Long userId = 1L;

        // 查询指定用户ID的隐藏完成状态
        Integer hide = setupService.selectHideComplete(userId);

        // 构建包含隐藏状态的响应数据
        Map<String, Integer> map = new HashMap<>();
        map.put("hide", hide);

        // 返回查询结果
        return AjaxResult.success(map);
    }

    /**
     * 更新用户的隐藏完成状态
     *
     * @param hide 用户新的隐藏完成状态
     * @return AjaxResult 包含操作结果的响应对象
     */
    @PutMapping("/hide_complete/{hide}")
    public AjaxResult updateHideComplete(@PathVariable Integer hide) {
        // 设置默认用户ID
        Long userId = 1L;

        // 更新指定用户ID的隐藏完成状态
        return toAjax(setupService.updateHideComplete(hide, userId));
    }

    /**
     * 通过GET请求获取用户的排序信息。
     *
     * @return AjaxResult 包含排序信息的结果对象。
     */
    @GetMapping("/order")
    public AjaxResult selectSort() {
        // 默认使用的用户ID，此处假设为系统管理员ID。
        Long userId = 1L;

        // 调用设置服务，查询该用户的排序信息。
        Integer sort = setupService.selectOrder(userId);

        // 构建结果映射，将排序信息封装进去。
        Map<String, Integer> map = new HashMap<>();
        map.put("sort", sort);

        // 返回查询成功的结果，包含排序信息。
        return AjaxResult.success(map);
    }


    /**
     * 通过PUT请求更新的排序。
     *
     * @param order 需要更新排序的订单号
     * @return 更新操作的结果，包含成功与否及可能的错误信息
     */
    @PutMapping("/order/{order}")
    public AjaxResult updateSort(@PathVariable Integer order) {
        // 假设当前操作用户的ID为1
        Long userId = 1L;
        // 调用setupService中的updateOrder方法更新订单排序，并将结果转换为AjaxResult返回
        return toAjax(setupService.updateOrder(order, userId));
    }

    /**
     * 查询用户组别
     *
     * 通过GET请求访问"/group"端点，用于查询当前用户的组别信息
     *
     * @return 返回一个AjaxResult对象，包含用户组别信息
     */
    @GetMapping("/group")
    public AjaxResult selectGroupCompletion() {
        // 模拟获取用户ID，实际应用中应通过安全框架或上下文获取当前用户ID
        Long userId = 1L;

        // 调用service层方法，根据用户ID查询用户组别
        Integer group = setupService.selectGroup(userId);

        // 创建映射对象，用于存放查询到的组别信息
        Map<String, Integer> map = new HashMap<>();
        // 将组别信息放入映射对象中
        map.put("group", group);

        // 返回一个成功的AjaxResult对象，将用户组别信息作为数据携带
        return AjaxResult.success(map);
    }

    /**
     * 更新用户组信息
     * 通过接收前端发送的PUT请求，更新指定的用户组信息
     * 本方法主要负责将请求转发给服务层进行处理，并将处理结果封装为AjaxResult对象返回给前端
     *
     * @param group 用户组ID，从路径变量中获取，用于标识需要更新的用户组
     * @return 返回更新操作的结果，包含状态码和消息，用于告知前端操作是否成功
     */
    @PutMapping("/group/{group}")
    public AjaxResult updateGroup(@PathVariable Integer group) {
        // 模拟当前操作的用户ID，用于识别操作是由哪个用户执行的
        Long userId = 1L;
        // 调用服务层方法更新用户组信息，并将结果封装为AjaxResult对象返回
        return toAjax(setupService.updateGroup(group, userId));
    }

}
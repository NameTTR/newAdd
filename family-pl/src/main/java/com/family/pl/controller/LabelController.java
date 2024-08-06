package com.family.pl.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.family.pl.constant.LabelConstants;
import com.family.pl.domain.Label;
import com.family.pl.domain.DTO.request.AddLabelDTO;
import com.family.pl.service.LabelService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标签控制器，负责处理与标签相关的请求。
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
@RestController
@RequestMapping("/family/pl/label")
public class LabelController extends BaseController {

    @Autowired
    private LabelService labelService;

    /**
     * 根据用户ID查询标签库
     * 本方法提供了一个接口，用于查询与特定用户ID关联的标签列表。这在诸如显示用户标签、管理用户标签等场景中非常有用。
     * 通过利用LambdaQueryWrapper和标签服务（labelService），本方法能够高效地从数据库中检索所需数据。
     *
     * @return AjaxResult 包含查询结果的Ajax响应对象。成功时，结果将是标签列表；失败时，将包含错误信息。
     */
    @GetMapping()
    public AjaxResult selectLabel() {
        // 默认使用用户ID 1进行查询，可以根据实际需求进行修改或动态获取
        Long userId = 1L;
        List<Label> labelList = labelService.list(new LambdaQueryWrapper<Label>().eq(Label::getUserId, userId));
        if (labelList.isEmpty()) {
            return AjaxResult.error("标签库为空");
        }
        Map<String, List<Label>> data = new HashMap<>();
        data.put("labelList", labelList);
        // 返回包含查询结果的AjaxResult对象
        return AjaxResult.success(data);
    }

    /**
     * 控制器方法，用于添加标签。
     * 通过@RequestBody注解，将前端发送的AddLabelDTO对象绑定到方法参数addLabelDTO上。
     *
     * @param addLabelDTO 包含标签信息的数据传输对象，其中包含待添加的标签名。
     * @return 返回一个AjaxResult对象，根据添加标签的操作结果进行相应设置。
     *         如果标签名为空，返回错误信息"标签名不能为空"。
     *         如果标签已存在，返回错误信息"标签已存在"。
     *         否则，返回添加标签的操作结果。
     */
    @PostMapping()
    public AjaxResult addLabel(@RequestBody AddLabelDTO addLabelDTO) {
        // 校验标签名是否为空
        if(!StringUtils.hasText(addLabelDTO.getLabelName())) {
            return AjaxResult.error("标签名不能为空");
        }
        // 尝试添加标签，返回值flag表示添加结果
        int flag = labelService.addLabel(addLabelDTO);
        // 如果标签名已存在，返回错误信息
        if(flag == LabelConstants.LABEL_NAME_EXIST) {
            return AjaxResult.error("标签已存在");
        }
        // 返回添加标签的操作结果
        return toAjax(flag);
    }

    /**
     * 通过DELETE请求方式删除指定标签。
     * 此方法使用@DeleteMapping注解指定请求方法为DELETE，并通过@PathVariable注解将URL路径中的labelId参数绑定到方法参数labelId上。
     * 方法调用labelService的removeById方法，传入labelId以删除对应的标签，并将操作结果转换为AjaxResult返回。
     * AjaxResult是前端通用的返回格式，可以包含操作成功与否的信息以及可能的错误码。
     *
     * @param labelId 需要删除的标签的ID，作为路径变量传递。
     * @return 返回AjaxResult对象，其中包含删除操作的结果。
     */
    @DeleteMapping("{labelId}")
    public AjaxResult delLabel(@PathVariable Long labelId) {
        return toAjax(labelService.removeById(labelId));
    }




}
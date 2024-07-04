package com.family.cc.service;

import com.family.cc.domain.po.CcCharacter;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.File;

/**
 * <p>
 * 汉字章节表 服务类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-04-28
 */
public interface ICcCharacterService extends IService<CcCharacter> {

    /**
     * 导入数据
     * @param tempFile 导入的文件
     */
    void importData(File tempFile);
}

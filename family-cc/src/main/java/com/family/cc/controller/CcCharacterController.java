package com.family.cc.controller;


import com.family.cc.service.ICcCharacterService;
import com.ruoyi.common.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * <p>
 * 汉字表 前端控制器
 * </p>
 *
 * @author 陈文杰
 * @since 2024-04-28
 */
@RestController
@RequestMapping("/family/cc/character")
public class CcCharacterController extends BaseController {
    @Autowired
    private ICcCharacterService ccCharacterService;

    /**
     * 导入Excel数据
     * @param file 上传的文件
     * @return
     */
    @PostMapping("/excel")
    public ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("文件为空");
        }
        try {
            File tempFile = File.createTempFile("temp", null);
            file.transferTo(tempFile);
            ccCharacterService.importData(tempFile);
            tempFile.delete();
            return ResponseEntity.ok("导入成功");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("导入失败");
        }
    }
}

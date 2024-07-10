package com.family.cc.controller;

import com.family.common.domain.result.ChatTTSResult;
import com.family.common.sevice.OkHttpService;
import com.ruoyi.common.core.controller.BaseController;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 汉字章节表 前端控制器
 * </p>
 *
 * @author 陈文杰
 * @since 2024-04-28
 */
@RestController
@RequestMapping("/family/cc/chapter")
@AllArgsConstructor
public class CcChapterController extends BaseController {

    private final OkHttpService okHttpService;
    @GetMapping("/test")
    public ChatTTSResult test(@RequestBody String text) {
        ChatTTSResult chatTTSResult = okHttpService.getAudioOfChatTTS(text);
        return chatTTSResult;
    }
}

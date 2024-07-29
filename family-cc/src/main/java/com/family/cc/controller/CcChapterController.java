package com.family.cc.controller;

import com.family.common.domain.result.ChatTTSResult;
import com.family.common.domain.result.WhisperResult;
import com.family.common.service.OkHttpService;
import com.ruoyi.common.core.controller.BaseController;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

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
    @PostMapping("/chat")
    public List<ChatTTSResult> test(@RequestBody List<String> texts) {
        List<ChatTTSResult> chatTTSResults = new ArrayList<>();
        for (String text : texts) {
            chatTTSResults.add(okHttpService.getAudioOfChatTTS(text));
        }
        return chatTTSResults;
    }

    @PostMapping("/whisper")
    public WhisperResult whisper(@RequestBody MultipartFile[] files) {
        return okHttpService.getTextOfWhisper(files);
    }
}

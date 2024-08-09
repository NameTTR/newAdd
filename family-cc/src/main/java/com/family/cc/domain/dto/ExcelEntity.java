package com.family.cc.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 读取Excel文件实体类
 *
 * @author 陈文杰
 * @since 2024-05-04
 */
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ExcelEntity {
    /**
     * 章节ID
     */
    private Long chapterId;

    /**
     * 汉字
     */
    private String character;

    /**
     * 类型
     */
    private String characterType;

    /**
     * 拼音
     */
    private String pinyin;

    /**
     * 词性
     */
    private String translation;

    /**
     * 组词
     */
    private List<String> compounds;

    /**
     * 近义词
     */
    private List<String> synonyms;

    /**
     * 反义词
     */
    private List<String> antonyms;

    /**
     * 出现次数
     */
    private Long frequency;
}

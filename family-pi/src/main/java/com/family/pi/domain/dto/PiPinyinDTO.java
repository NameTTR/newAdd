package com.family.pi.domain.dto;

import com.family.pi.domain.po.PiPinyin;
import com.family.pi.domain.po.PiPinyinGroup;
import com.family.pi.enums.PiPinyinState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * 单个拼音DTO
 *
 * @author 陈文杰
 * @since 2024-05-04
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class PiPinyinDTO extends PiPinyin {
    /**
     *  状态  0：未学；1：已学完；2：未掌握
     */
    private PiPinyinState state;

}

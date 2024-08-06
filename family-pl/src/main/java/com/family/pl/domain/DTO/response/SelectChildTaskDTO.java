package com.family.pl.domain.VO.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 名称：子任务VO类
 * 功能：表示任务的子任务，仅包含子任务的ID和子任务列表
 * 作者：Name
 * 日期：2024/7/5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectChildTaskVO {
    /**
     * 子任务ID
     */
    private Long childTaskId;

    /**
     * 子任务标题
     */
    private String title;

    /**
     * 子任务列表
     */
    private List<SelectChildTaskVO> childTaskVOS;

    public <E> SelectChildTaskVO(Long id, ArrayList<E> es) {

    }
}
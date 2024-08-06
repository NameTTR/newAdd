package com.family.pl.domain.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 用于表示选择子任务的数据传输对象。
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectChildTaskDTO {
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
    private List<SelectChildTaskDTO> childTaskDTOList;

    /**
     * 通用构造函数，用于创建一个SelectChildTaskDTO实例。
     * 此构造函数的意图是提供一种方式来初始化对象，尤其是当需要从复杂数据结构中构建对象时。
     *
     * @param id 子任务的标识符。
     * @param es 一个ArrayList，用于存储与子任务相关的数据。此参数的类型参数化，提供了灵活性。
     */
    public <E> SelectChildTaskDTO(Long id, ArrayList<E> es) {

    }
}
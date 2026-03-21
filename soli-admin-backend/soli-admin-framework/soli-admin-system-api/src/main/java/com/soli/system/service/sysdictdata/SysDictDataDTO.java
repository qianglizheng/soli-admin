package com.soli.system.service.sysdictdata;

import com.soli.common.api.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 字典数据 DTO
 *
 * @author lizhengqiang
 * @since 2026-03-21 23:57
 */
@Getter
@Setter
public class SysDictDataDTO extends BaseDTO {

    /** 字典类型 ID */
    private Long dictTypeId;

    /** 字典标签 */
    private String label;

    /** 字典键值 */
    private String value;

    /** 显示排序 */
    private String sort;

    /** 样式属性 */
    private String cssClass;

    /** 表格回显样式 */
    private String listClass;

    /** 是否默认 */
    private String isDefault;

    /** 状态 */
    private String status;

}

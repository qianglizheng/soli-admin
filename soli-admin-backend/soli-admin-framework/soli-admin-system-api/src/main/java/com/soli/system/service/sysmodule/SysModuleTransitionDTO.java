package com.soli.system.service.sysmodule;

import com.soli.common.api.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 模块状态流转对象
 *
 * @author lizhengqiang
 * @since 2026-03-25 10:45
 */
@Getter
@Setter
public class SysModuleTransitionDTO extends BaseDTO {

    /** 模块 ID */
    private Long moduleId;

    /** 触发按钮编码 */
    private String actionButtonCode;

    /** 触发按钮名称 */
    private String actionButtonName;

    /** 来源状态编码 */
    private String fromStateCode;

    /** 目标状态编码 */
    private String toStateCode;

    /** 排序 */
    private Integer sort;

    /** 状态 */
    private String status;

}

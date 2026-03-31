package com.soli.system.service.sysmodule;

import com.soli.common.api.dto.BaseDTO;
import com.soli.common.api.enums.NormalDisableStatusEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 模块状态对象
 *
 * @author lizhengqiang
 * @since 2026-03-25 10:45
 */
@Getter
@Setter
public class SysModuleStateDTO extends BaseDTO {

    /** 模块 ID */
    private Long moduleId;

    /** 状态编码 */
    private String stateCode;

    /** 状态名称 */
    private String stateName;

    /** 排序 */
    private Integer sort;

    /** 是否初始状态 */
    private String isInitial;

    /** 是否最终状态 */
    private String isFinal;

    /** 状态 */
    private NormalDisableStatusEnum status;

}

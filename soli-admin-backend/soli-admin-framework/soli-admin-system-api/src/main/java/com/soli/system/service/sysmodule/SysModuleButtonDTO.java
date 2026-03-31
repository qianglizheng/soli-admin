package com.soli.system.service.sysmodule;

import com.soli.common.api.dto.BaseDTO;
import com.soli.common.api.enums.NormalDisableStatusEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 模块按钮对象
 *
 * @author lizhengqiang
 * @since 2026-03-25 00:05
 */
@Getter
@Setter
public class SysModuleButtonDTO extends BaseDTO {

    /** 模块 ID */
    private Long moduleId;

    /** 按钮编码 */
    private String buttonCode;

    /** 默认标题 */
    private String defaultTitle;

    /** 排序 */
    private Integer sort;

    /** 状态 */
    private NormalDisableStatusEnum status;

}

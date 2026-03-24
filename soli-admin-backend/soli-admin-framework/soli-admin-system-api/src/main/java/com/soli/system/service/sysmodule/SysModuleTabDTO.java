package com.soli.system.service.sysmodule;

import com.soli.common.api.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 模块 Tab 对象
 *
 * @author lizhengqiang
 * @since 2026-03-25 00:05
 */
@Getter
@Setter
public class SysModuleTabDTO extends BaseDTO {

    /** 模块 ID */
    private Long moduleId;

    /** Tab 作用域 */
    private String tabScope;

    /** Tab 编码 */
    private String tabCode;

    /** Tab 名称 */
    private String tabName;

    /** 排序 */
    private Integer sort;

    /** 状态 */
    private String status;

}

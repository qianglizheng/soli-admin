package com.soli.system.core.service.impl.sysmodule;

import com.soli.common.api.enums.NormalDisableStatusEnum;
import com.soli.common.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Module component entity.
 *
 * @author lizhengqiang
 * @since 2026-03-28 14:52
 */
@Getter
@Setter
public class SysModuleComponentEntity extends BaseEntity {

    private Long moduleId;

    private String componentCode;

    private String componentName;

    private Integer sort;

    private NormalDisableStatusEnum status;

}

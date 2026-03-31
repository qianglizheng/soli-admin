package com.soli.system.core.service.impl.sysmodule;

import com.soli.common.api.enums.NormalDisableStatusEnum;
import com.soli.common.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 模块状态实体
 *
 * @author lizhengqiang
 * @since 2026-03-25 10:45
 */
@Getter
@Setter
public class SysModuleStateEntity extends BaseEntity {

    private Long moduleId;

    private String stateCode;

    private String stateName;

    private Integer sort;

    private String isInitial;

    private String isFinal;

    private NormalDisableStatusEnum status;

}

package com.soli.system.core.service.impl.sysmodule;

import com.soli.common.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 模块状态流转实体
 *
 * @author lizhengqiang
 * @since 2026-03-25 10:45
 */
@Getter
@Setter
public class SysModuleTransitionEntity extends BaseEntity {

    private Long moduleId;

    private String actionButtonCode;

    private String actionButtonName;

    private String fromStateCode;

    private String toStateCode;

    private Integer sort;

    private String status;

}

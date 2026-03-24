package com.soli.system.core.service.impl.sysmodule;

import com.soli.common.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 模块实体
 *
 * @author lizhengqiang
 * @since 2026-03-25 00:15
 */
@Getter
@Setter
public class SysModuleEntity extends BaseEntity {

    private Long parentId;

    private String ancestors;

    private String moduleCode;

    private String moduleName;

    private String moduleType;

    private String routePath;

    private String componentPath;

    private String icon;

    private Integer sort;

    private String navVisible;

    private String statefulFlag;

    private String stateFieldCode;

    private Integer contextVersion;

    private String status;

}

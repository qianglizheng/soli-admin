package com.soli.system.core.service.impl.sysmodule;

import com.soli.common.api.enums.BinaryFlagEnum;
import com.soli.common.api.enums.NormalDisableStatusEnum;
import com.soli.system.service.enums.ModuleTypeEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 模块树节点查询模型
 *
 * @author lizhengqiang
 * @since 2026-03-25 00:15
 */
@Getter
@Setter
public class SysModuleTreeNodeModel {

    private Long id;

    private Long parentId;

    private String moduleCode;

    private String moduleName;

    private ModuleTypeEnum moduleType;

    private String routePath;

    private String componentPath;

    private String icon;

    private Integer sort;

    private BinaryFlagEnum navVisible;

    private BinaryFlagEnum statefulFlag;

    private NormalDisableStatusEnum status;

}

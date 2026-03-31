package com.soli.system.core.service.impl.sysmodulepermission;

import com.soli.common.api.enums.BinaryFlagEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户模块导航权限模型
 *
 * @author lizhengqiang
 * @since 2026-03-28 16:50
 */
@Getter
@Setter
public class SysUserModuleNavPermissionModel {

    private Long moduleId;

    private BinaryFlagEnum navVisible;

}

package com.soli.system.core.service.impl.sysmodulepermission;

import com.soli.system.service.enums.PermissionLevelEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户模块字段权限模型
 *
 * @author lizhengqiang
 * @since 2026-03-27 15:35
 */
@Getter
@Setter
public class SysUserModuleFieldPermissionModel {

    /** 字段 ID */
    private Long fieldId;

    /** 权限级别 */
    private PermissionLevelEnum permissionLevel;

}

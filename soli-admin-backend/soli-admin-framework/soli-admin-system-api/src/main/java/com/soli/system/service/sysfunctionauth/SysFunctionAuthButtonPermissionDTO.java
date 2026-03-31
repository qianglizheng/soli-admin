package com.soli.system.service.sysfunctionauth;

import com.soli.system.service.enums.PermissionLevelEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 岗位按钮权限对象
 *
 * @author lizhengqiang
 * @since 2026-03-25 11:05
 */
@Getter
@Setter
public class SysFunctionAuthButtonPermissionDTO {

    /** 按钮 ID */
    private Long buttonId;

    /** 权限级别 */
    private PermissionLevelEnum permissionLevel;

}

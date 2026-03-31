package com.soli.system.service.sysstateauth;

import com.soli.system.service.enums.PermissionLevelEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 状态字段限制对象
 *
 * @author lizhengqiang
 * @since 2026-03-25 11:05
 */
@Getter
@Setter
public class SysStateFieldPermissionDTO {

    /** 字段 ID */
    private Long fieldId;

    /** 限制级别 */
    private PermissionLevelEnum permissionLevel;

}

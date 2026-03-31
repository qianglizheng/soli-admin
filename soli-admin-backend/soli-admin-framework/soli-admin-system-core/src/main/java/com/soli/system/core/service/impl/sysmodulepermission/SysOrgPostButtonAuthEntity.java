package com.soli.system.core.service.impl.sysmodulepermission;

import com.soli.common.core.entity.BaseEntity;
import com.soli.system.service.enums.PermissionLevelEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 岗位按钮权限实体
 *
 * @author lizhengqiang
 * @since 2026-03-25 11:10
 */
@Getter
@Setter
public class SysOrgPostButtonAuthEntity extends BaseEntity {

    private Long orgPostId;

    private Long moduleId;

    private Long buttonId;

    private PermissionLevelEnum permissionLevel;

}

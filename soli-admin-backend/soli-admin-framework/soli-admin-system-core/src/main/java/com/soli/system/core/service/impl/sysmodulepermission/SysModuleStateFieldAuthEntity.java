package com.soli.system.core.service.impl.sysmodulepermission;

import com.soli.common.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 状态字段权限实体
 *
 * @author lizhengqiang
 * @since 2026-03-25 11:10
 */
@Getter
@Setter
public class SysModuleStateFieldAuthEntity extends BaseEntity {

    private Long moduleId;

    private String stateCode;

    private Long fieldId;

    private Integer permissionLevel;

}

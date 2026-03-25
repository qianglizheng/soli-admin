package com.soli.system.core.service.impl.sysmodulepermission;

import com.soli.common.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 岗位字段权限实体
 *
 * @author lizhengqiang
 * @since 2026-03-25 11:10
 */
@Getter
@Setter
public class SysOrgPostFieldAuthEntity extends BaseEntity {

    private Long orgPostId;

    private Long moduleId;

    private Long fieldId;

    private Integer permissionLevel;

}

package com.soli.system.core.service.impl.sysrole;

import com.soli.common.core.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lizhengqiang
 * @since 2026-03-14 16:00
*/
@Getter
@Setter
public class SysRoleEntity extends BaseEntity {

    /** 角色名称 */
    private String name;

    /** 权限编码 */
    private String code;

    /** 显示顺序 */
    private String sort;

    /** 数据限制范围 */
    private String dataScope;

    /** 角色状态 */
    private String status;

}
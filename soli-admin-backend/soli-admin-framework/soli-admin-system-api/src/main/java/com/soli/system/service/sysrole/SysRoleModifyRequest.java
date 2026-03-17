package com.soli.system.service.sysrole;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lizhengqiang
 * @since 2026-03-17 21:33
*/
@Getter
@Setter
public class SysRoleModifyRequest {

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
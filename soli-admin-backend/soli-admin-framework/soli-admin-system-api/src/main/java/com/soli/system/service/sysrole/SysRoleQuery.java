package com.soli.system.service.sysrole;

import com.soli.common.api.vo.PageQuery;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lizhengqiang
 * @since 2026-03-17 20:37
*/
@Getter
@Setter
public class SysRoleQuery extends PageQuery {

    /** 角色名称 */
    private String roleName;

    /** 权限字符 */
    private String roleKey;

    /** 角色状态 */
    private String status;

}

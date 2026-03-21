package com.soli.system.service.sysrole;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lizhengqiang
 * @since 2026-03-17 21:33
*/
@Getter
@Setter
public class SysRoleCreateRequest {

    /** 角色名称 */
    @NotBlank(message = "角色名称不能为空")
    private String name;

    /** 权限编码 */
    @NotBlank(message = "权限字符不能为空")
    private String code;

    /** 显示顺序 */
    private String sort;

    /** 数据限制范围 */
    private String dataScope;

    /** 角色状态 */
    private String status;

}

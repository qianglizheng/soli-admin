package com.soli.system.service.sysfunctionauth;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 岗位功能授权配置对象
 *
 * @author lizhengqiang
 * @since 2026-03-25 11:05
 */
@Getter
@Setter
public class SysFunctionAuthConfigDTO {

    /** 岗位 ID */
    private Long orgPostId;

    /** 模块 ID */
    private Long moduleId;

    /** 模块可见 */
    private Boolean moduleVisible;

    /** 导航可见 */
    private Boolean navVisible;

    /** 字段权限 */
    private List<SysFunctionAuthFieldPermissionDTO> fieldPermissions;

    /** 按钮权限 */
    private List<SysFunctionAuthButtonPermissionDTO> buttonPermissions;

}

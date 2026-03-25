package com.soli.system.service.sysfunctionauth;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 岗位功能授权保存请求
 *
 * @author lizhengqiang
 * @since 2026-03-25 11:05
 */
@Getter
@Setter
public class SysFunctionAuthSaveRequest {

    @NotNull(message = "岗位 ID 不能为空")
    private Long orgPostId;

    @NotNull(message = "模块 ID 不能为空")
    private Long moduleId;

    @NotNull(message = "模块可见标识不能为空")
    private Boolean moduleVisible;

    @NotNull(message = "导航可见标识不能为空")
    private Boolean navVisible;

    private List<SysFunctionAuthFieldPermissionDTO> fieldPermissions;

    private List<SysFunctionAuthButtonPermissionDTO> buttonPermissions;

}

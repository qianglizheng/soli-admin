package com.soli.system.service.sysstateauth;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 状态权限保存请求
 *
 * @author lizhengqiang
 * @since 2026-03-25 11:05
 */
@Getter
@Setter
public class SysStateAuthSaveRequest {

    /** 模块 ID */
    @NotNull(message = "模块 ID 不能为空")
    private Long moduleId;

    /** 按状态分组的权限配置 */
    private List<SysStatePermissionByStateDTO> permissionsByState;

}

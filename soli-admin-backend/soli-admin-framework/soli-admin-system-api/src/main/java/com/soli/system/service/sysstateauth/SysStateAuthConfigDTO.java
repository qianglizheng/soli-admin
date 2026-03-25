package com.soli.system.service.sysstateauth;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 状态权限配置对象
 *
 * @author lizhengqiang
 * @since 2026-03-25 11:05
 */
@Getter
@Setter
public class SysStateAuthConfigDTO {

    /** 模块 ID */
    private Long moduleId;

    /** 按状态分组的权限 */
    private List<SysStatePermissionByStateDTO> permissionsByState;

}

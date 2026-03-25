package com.soli.system.service.sysstateauth;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 按状态分组的限制配置对象
 *
 * @author lizhengqiang
 * @since 2026-03-25 11:05
 */
@Getter
@Setter
public class SysStatePermissionByStateDTO {

    /** 状态编码 */
    private String stateCode;

    /** 字段限制 */
    private List<SysStateFieldPermissionDTO> fieldPermissions;

    /** 按钮限制 */
    private List<SysStateButtonPermissionDTO> buttonPermissions;

}

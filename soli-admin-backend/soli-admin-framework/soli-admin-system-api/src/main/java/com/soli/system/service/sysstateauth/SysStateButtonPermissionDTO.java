package com.soli.system.service.sysstateauth;

import lombok.Getter;
import lombok.Setter;

/**
 * 状态按钮限制对象
 *
 * @author lizhengqiang
 * @since 2026-03-25 11:05
 */
@Getter
@Setter
public class SysStateButtonPermissionDTO {

    /** 按钮 ID */
    private Long buttonId;

    /** 限制级别 */
    private Integer permissionLevel;

}

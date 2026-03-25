package com.soli.system.service.sysstateauth;

import com.soli.system.service.sysmodule.SysModuleDetailDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 状态权限页面详情
 *
 * @author lizhengqiang
 * @since 2026-03-25 11:05
 */
@Getter
@Setter
public class SysStateAuthPageDetailDTO {

    /** 模块详情 */
    private SysModuleDetailDTO moduleDetail;

    /** 状态限制配置 */
    private SysStateAuthConfigDTO config;

}

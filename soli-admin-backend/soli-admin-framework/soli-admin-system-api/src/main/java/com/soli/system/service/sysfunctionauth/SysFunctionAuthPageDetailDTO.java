package com.soli.system.service.sysfunctionauth;

import com.soli.system.service.sysmodule.SysModuleDetailDTO;
import com.soli.system.service.sysorgpost.SysOrgPostDetailDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 岗位功能授权页面详情
 *
 * @author lizhengqiang
 * @since 2026-03-25 11:05
 */
@Getter
@Setter
public class SysFunctionAuthPageDetailDTO {

    /** 可见模块数 */
    private Integer visibleModuleCount;

    /** 岗位详情 */
    private SysOrgPostDetailDTO postDetail;

    /** 模块详情 */
    private SysModuleDetailDTO moduleDetail;

    /** 岗位基线配置 */
    private SysFunctionAuthConfigDTO config;

}

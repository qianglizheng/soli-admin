package com.soli.system.service.sysfunctionauth;

import com.soli.system.service.sysorgpost.SysOrgPostTreeNodeDTO;
import com.soli.system.service.sysmodule.SysModuleTreeNodeDTO;

import java.util.List;

/**
 * 功能授权服务
 *
 * @author lizhengqiang
 * @since 2026-03-25 11:05
 */
public interface SysFunctionAuthService {

    /**
     * 查询岗位树
     *
     * @return 岗位树
     */
    List<SysOrgPostTreeNodeDTO> queryOrgPostTree();

    /**
     * 查询模块树
     *
     * @return 模块树
     */
    List<SysModuleTreeNodeDTO> queryModuleTree();

    /**
     * 查询页面详情
     *
     * @param orgPostId 岗位 ID
     * @param moduleId 模块 ID
     * @return 页面详情
     */
    SysFunctionAuthPageDetailDTO queryPageDetail(Long orgPostId, Long moduleId);

    /**
     * 保存岗位基线权限
     *
     * @param request 保存请求
     */
    void save(SysFunctionAuthSaveRequest request);

}

package com.soli.system.service.sysstateauth;

import com.soli.system.service.sysmodule.SysModuleTreeNodeDTO;

import java.util.List;

/**
 * 状态权限服务
 *
 * @author lizhengqiang
 * @since 2026-03-25 11:05
 */
public interface SysStateAuthService {

    /**
     * 查询状态型模块树
     *
     * @return 状态型模块树
     */
    List<SysModuleTreeNodeDTO> queryModuleTree();

    /**
     * 查询页面详情
     *
     * @param moduleId 模块 ID
     * @return 页面详情
     */
    SysStateAuthPageDetailDTO queryPageDetail(Long moduleId);

    /**
     * 保存状态限制
     *
     * @param request 保存请求
     */
    void save(SysStateAuthSaveRequest request);

}

package com.soli.system.service.sysmoduletitle;

import com.soli.system.service.sysmodule.SysModuleDetailDTO;
import com.soli.system.service.sysmodule.SysModuleTreeNodeDTO;

import java.util.List;

/**
 * 字段标题中心服务
 *
 * @author lizhengqiang
 * @since 2026-03-25 11:05
 */
public interface SysModuleTitleService {

    /**
     * 查询模块树
     *
     * @return 模块树
     */
    List<SysModuleTreeNodeDTO> queryModuleTree();

    /**
     * 查询模块详情
     *
     * @param moduleId 模块 ID
     * @return 模块详情
     */
    SysModuleDetailDTO queryDetail(Long moduleId);

    /**
     * 保存字段标题配置
     *
     * @param request 保存请求
     */
    void save(SysModuleTitleSaveRequest request);

}

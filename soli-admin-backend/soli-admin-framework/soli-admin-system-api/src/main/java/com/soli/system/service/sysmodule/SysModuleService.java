package com.soli.system.service.sysmodule;

import com.soli.system.service.BaseCrudService;

import java.util.List;

/**
 * 模块管理服务
 *
 * @author lizhengqiang
 * @since 2026-03-25 00:05
 */
public interface SysModuleService extends BaseCrudService<SysModuleDTO, SysModuleQuery> {

    /**
     * 查询完整模块树
     *
     * @return 模块树
     */
    List<SysModuleTreeNodeDTO> queryAllTreeList();

    /**
     * 查询用户侧可见模块树
     *
     * @return 模块树
     */
    List<SysModuleTreeNodeDTO> queryTreeList();

    /**
     * 查询当前用户可见的系统导航模块树
     *
     * @param userId 用户 ID
     * @param companyId 公司 ID
     * @return 模块树
     */
    List<SysModuleTreeNodeDTO> queryNavTree(Long userId, Long companyId);

    /**
     * 根据模块 ID 查询模块设计详情
     *
     * @param id 模块 ID
     * @return 模块设计详情
     */
    SysModuleDetailDTO queryManageDetailById(Long id);

    /**
     * 根据模块 ID 查询模块运行时详情
     *
     * @param id 模块 ID
     * @return 模块详情
     */
    SysModuleDetailDTO queryDetailById(Long id);

    /**
     * 新增 Tab
     *
     * @param dto Tab 对象
     */
    void createTab(SysModuleTabDTO dto);

    /**
     * 修改 Tab
     *
     * @param dto Tab 对象
     */
    void modifyTab(SysModuleTabDTO dto);

    /**
     * 删除 Tab
     *
     * @param id Tab ID
     */
    void removeTab(Long id);

    /**
     * 新增字段
     *
     * @param dto 字段对象
     */
    void createField(SysModuleFieldDTO dto);

    /**
     * 修改字段
     *
     * @param dto 字段对象
     */
    void modifyField(SysModuleFieldDTO dto);

    /**
     * 删除字段
     *
     * @param id 字段 ID
     */
    void removeField(Long id);

    /**
     * 新增按钮
     *
     * @param dto 按钮对象
     */
    void createButton(SysModuleButtonDTO dto);

    /**
     * 修改按钮
     *
     * @param dto 按钮对象
     */
    void modifyButton(SysModuleButtonDTO dto);

    /**
     * 删除按钮
     *
     * @param id 按钮 ID
     */
    void removeButton(Long id);

}

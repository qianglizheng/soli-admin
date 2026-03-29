package com.soli.system.service.sysmodule;

import com.soli.system.service.BaseCrudService;

import java.util.List;

/**
 * 模块服务
 *
 * @author lizhengqiang
 * @since 2026-03-28 14:55
 */
public interface SysModuleService extends BaseCrudService<SysModuleDTO, SysModuleQuery> {

    /**
     * 查询全部模块树
     *
     * @return 模块树
     */
    List<SysModuleTreeNodeDTO> queryAllTreeList();

    /**
     * 查询用户可见模块树
     *
     * @return 模块树
     */
    List<SysModuleTreeNodeDTO> queryTreeList();

    /**
     * 查询当前用户导航模块树
     *
     * @param userId 用户 ID
     * @param companyId 公司 ID
     * @return 导航模块树
     */
    List<SysModuleTreeNodeDTO> queryNavTree(Long userId, Long companyId);

    /**
     * 查询模块管理详情
     *
     * @param id 模块 ID
     * @return 模块详情
     */
    SysModuleDetailDTO queryManageDetailById(Long id);

    /**
     * 查询模块运行详情
     *
     * @param id 模块 ID
     * @return 模块详情
     */
    SysModuleDetailDTO queryDetailById(Long id);

    /**
     * 新增模块组件
     *
     * @param dto 模块组件对象
     */
    void createComponent(SysModuleComponentDTO dto);

    /**
     * 修改模块组件
     *
     * @param dto 模块组件对象
     */
    void modifyComponent(SysModuleComponentDTO dto);

    /**
     * 删除模块组件
     *
     * @param id 组件 ID
     */
    void removeComponent(Long id);

    /**
     * 新增模块字段
     *
     * @param dto 模块字段对象
     */
    void createField(SysModuleFieldDTO dto);

    /**
     * 修改模块字段
     *
     * @param dto 模块字段对象
     */
    void modifyField(SysModuleFieldDTO dto);

    /**
     * 删除模块字段
     *
     * @param id 字段 ID
     */
    void removeField(Long id);

    /**
     * 新增模块按钮
     *
     * @param dto 模块按钮对象
     */
    void createButton(SysModuleButtonDTO dto);

    /**
     * 修改模块按钮
     *
     * @param dto 模块按钮对象
     */
    void modifyButton(SysModuleButtonDTO dto);

    /**
     * 删除模块按钮
     *
     * @param id 按钮 ID
     */
    void removeButton(Long id);

}

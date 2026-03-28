package com.soli.system.service.sysmodule;

import com.soli.system.service.BaseCrudService;

import java.util.List;

/**
 * Module service.
 *
 * @author lizhengqiang
 * @since 2026-03-28 14:55
 */
public interface SysModuleService extends BaseCrudService<SysModuleDTO, SysModuleQuery> {

    List<SysModuleTreeNodeDTO> queryAllTreeList();

    List<SysModuleTreeNodeDTO> queryTreeList();

    List<SysModuleTreeNodeDTO> queryNavTree(Long userId, Long companyId);

    SysModuleDetailDTO queryManageDetailById(Long id);

    SysModuleDetailDTO queryDetailById(Long id);

    void createComponent(SysModuleComponentDTO dto);

    void modifyComponent(SysModuleComponentDTO dto);

    void removeComponent(Long id);

    void createField(SysModuleFieldDTO dto);

    void modifyField(SysModuleFieldDTO dto);

    void removeField(Long id);

    void createButton(SysModuleButtonDTO dto);

    void modifyButton(SysModuleButtonDTO dto);

    void removeButton(Long id);

}

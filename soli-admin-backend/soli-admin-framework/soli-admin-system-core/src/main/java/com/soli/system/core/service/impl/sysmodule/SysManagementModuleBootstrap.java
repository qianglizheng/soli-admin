package com.soli.system.core.service.impl.sysmodule;

import com.github.yitter.idgen.YitIdHelper;
import com.soli.system.core.mapper.SysModuleMapper;
import com.soli.system.core.mapper.SysModulePermissionMapper;
import com.soli.system.core.mapper.SysModuleTitleMapper;
import com.soli.system.core.service.impl.sysmodulepermission.SysOrgPostButtonAuthEntity;
import com.soli.system.core.service.impl.sysmodulepermission.SysOrgPostFieldAuthEntity;
import com.soli.system.core.service.impl.sysmodulepermission.SysOrgPostModuleAuthEntity;
import com.soli.system.service.sysmodule.SysModuleButtonDTO;
import com.soli.system.service.sysmodule.SysModuleDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 系统模块初始化引导
 *
 * @author lizhengqiang
 * @since 2026-03-25 17:10
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SysManagementModuleBootstrap implements ApplicationRunner {

    private static final String SYSTEM_USER = "system";

    private static final String ENABLED = "0";

    private final SysModuleServiceImpl sysModuleService;

    private final SysModuleMapper sysModuleMapper;

    private final SysModulePermissionMapper sysModulePermissionMapper;

    private final SysModuleTitleMapper sysModuleTitleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(ApplicationArguments args) {
        try {
            syncSystemModules();
            syncAdminPermissions();
        } catch (Exception ex) {
            log.error("系统模块初始化失败", ex);
        }
    }

    private void syncSystemModules() {
        Long purchaseRootId = ensureModule(null, ModuleSeed.catalog(
                "purchase", "采购管理", "/purchase", null, "ShoppingCart", 1, "采购业务模块目录"
        ));
        ensureModule(purchaseRootId, ModuleSeed.bill(
                "purchase_order", "采购订单", "bill-template", "purchase/bill-template/BillTemplateIndex", "Document", 1, "采购订单模板页面"
        ));

        Long salesRootId = ensureModule(null, ModuleSeed.hiddenCatalog(
                "sales", "销售管理", "/sales", null, "Goods", 2, "销售业务模块目录"
        ));
        ensureModule(salesRootId, ModuleSeed.hiddenBill(
                "sales_order", "销售订单", "order", "sales/order/index", "Tickets", 1, "销售订单预留模块"
        ));

        Long systemRootId = ensureModule(null, ModuleSeed.catalog(
                "sys_system", "系统管理", "/system", null, "Tools", 99, "系统后台导航根目录"
        ));
        retireModule("sys_role");
        ensureModule(systemRootId, ModuleSeed.page(
                "sys_user", "用户管理", "user", "system/user/index", "User", 1, "系统用户维护页面"
        ), List.of(
                ButtonSeed.of("create", "新增", "LIST_TOOLBAR", 1),
                ButtonSeed.of("modify", "修改", "LIST_ROW_BUTTON", 2),
                ButtonSeed.of("remove", "删除", "LIST_ROW_BUTTON", 3)
        ));
        ensureModule(systemRootId, ModuleSeed.page(
                "sys_module", "模块管理", "module-center", "system/module-center/index", "Grid", 3, "模块管理页面"
        ), List.of(
                ButtonSeed.of("create", "新增", "LIST_TOOLBAR", 1),
                ButtonSeed.of("modify", "修改", "LIST_ROW_BUTTON", 2),
                ButtonSeed.of("remove", "删除", "LIST_ROW_BUTTON", 3),
                ButtonSeed.of("tabCreate", "Tab新增", "HEADER_TOOLBAR", 4),
                ButtonSeed.of("tabModify", "Tab修改", "HEADER_TOOLBAR", 5),
                ButtonSeed.of("tabRemove", "Tab删除", "HEADER_TOOLBAR", 6),
                ButtonSeed.of("fieldCreate", "字段新增", "HEADER_TOOLBAR", 7),
                ButtonSeed.of("fieldModify", "字段修改", "HEADER_TOOLBAR", 8),
                ButtonSeed.of("fieldRemove", "字段删除", "HEADER_TOOLBAR", 9),
                ButtonSeed.of("buttonCreate", "按钮新增", "HEADER_TOOLBAR", 10),
                ButtonSeed.of("buttonModify", "按钮修改", "HEADER_TOOLBAR", 11),
                ButtonSeed.of("buttonRemove", "按钮删除", "HEADER_TOOLBAR", 12)
        ));
        ensureModule(systemRootId, ModuleSeed.page(
                "sys_org_post", "岗位管理", "post-manage", "system/post-manage/index", "OfficeBuilding", 4, "组织岗位维护页面"
        ), List.of(
                ButtonSeed.of("create", "新增", "LIST_TOOLBAR", 1),
                ButtonSeed.of("modify", "修改", "LIST_ROW_BUTTON", 2),
                ButtonSeed.of("remove", "删除", "LIST_ROW_BUTTON", 3),
                ButtonSeed.of("userBind", "绑定员工", "HEADER_TOOLBAR", 4),
                ButtonSeed.of("userUnbind", "解绑员工", "HEADER_TOOLBAR", 5)
        ));
        ensureModule(systemRootId, ModuleSeed.page(
                "sys_function_auth", "功能授权", "function-auth", "system/function-auth/index", "Lock", 5, "岗位功能授权页面"
        ), List.of(
                ButtonSeed.of("save", "保存", "HEADER_TOOLBAR", 1)
        ));
        ensureModule(systemRootId, ModuleSeed.page(
                "sys_module_title", "字段标题", "module-title", "system/module-title/index", "EditPen", 6, "字段标题维护页面"
        ), List.of(
                ButtonSeed.of("save", "保存", "HEADER_TOOLBAR", 1)
        ));
        ensureModule(systemRootId, ModuleSeed.page(
                "sys_state_auth", "状态权限", "state-auth", "system/state-auth/index", "SetUp", 7, "状态权限维护页面"
        ), List.of(
                ButtonSeed.of("save", "保存", "HEADER_TOOLBAR", 1)
        ));
        ensureModule(systemRootId, ModuleSeed.page(
                "sys_dict", "字典管理", "dict", "system/dict/index", "Reading", 8, "字典类型与字典数据维护页面"
        ), List.of(
                ButtonSeed.of("create", "新增", "LIST_TOOLBAR", 1),
                ButtonSeed.of("modify", "修改", "LIST_ROW_BUTTON", 2),
                ButtonSeed.of("remove", "删除", "LIST_ROW_BUTTON", 3)
        ));
        ensureModule(systemRootId, ModuleSeed.page(
                "sys_config", "参数设置", "config", "system/config/index", "Tools", 9, "系统参数维护页面"
        ), List.of(
                ButtonSeed.of("create", "新增", "LIST_TOOLBAR", 1),
                ButtonSeed.of("modify", "修改", "LIST_ROW_BUTTON", 2),
                ButtonSeed.of("remove", "删除", "LIST_ROW_BUTTON", 3),
                ButtonSeed.of("refreshCache", "刷新缓存", "HEADER_TOOLBAR", 4)
        ));
        Long logRootId = ensureModule(systemRootId, ModuleSeed.catalog(
                "sys_log", "日志管理", "log", null, "Monitor", 10, "日志管理目录"
        ));
        ensureModule(logRootId, ModuleSeed.page(
                "sys_operlog", "操作日志", "operlog", "system/monitor/operlog/index", "Document", 1, "操作日志页面"
        ));
        ensureModule(logRootId, ModuleSeed.page(
                "sys_logininfor", "登录日志", "logininfor", "system/monitor/logininfor/index", "Key", 2, "登录日志页面"
        ));
    }

    private Long ensureModule(Long parentId, ModuleSeed seed) {
        return ensureModule(parentId, seed, List.of());
    }

    private Long ensureModule(Long parentId, ModuleSeed seed, List<ButtonSeed> buttonSeeds) {
        Long normalizedParentId = parentId == null ? 0L : parentId;
        SysModuleEntity existing = sysModuleMapper.selectByModuleCode(seed.getModuleCode());
        Long moduleId;
        if (existing == null) {
            SysModuleDTO dto = buildModuleDto(null, normalizedParentId, seed);
            sysModuleService.create(dto);
            moduleId = dto.getId();
        } else {
            syncModule(existing, normalizedParentId, seed);
            moduleId = existing.getId();
        }
        ensureButtons(moduleId, buttonSeeds);
        return moduleId;
    }

    private void retireModule(String moduleCode) {
        SysModuleEntity existing = sysModuleMapper.selectByModuleCode(moduleCode);
        if (existing == null) {
            return;
        }
        removeModuleRelations(existing.getId());
        sysModuleMapper.deleteById(existing.getId());
    }

    private void removeModuleRelations(Long moduleId) {
        List<SysModuleFieldEntity> fieldList = sysModuleMapper.selectFieldsByModuleId(moduleId);
        for (SysModuleFieldEntity fieldEntity : fieldList) {
            sysModuleTitleMapper.deleteByFieldId(fieldEntity.getId());
            sysModulePermissionMapper.deleteOrgPostFieldAuthByFieldId(fieldEntity.getId());
            sysModulePermissionMapper.deleteStateFieldAuthByFieldId(fieldEntity.getId());
            sysModuleMapper.deleteFieldById(fieldEntity.getId());
        }
        List<SysModuleButtonEntity> buttonList = sysModuleMapper.selectButtonsByModuleId(moduleId);
        for (SysModuleButtonEntity buttonEntity : buttonList) {
            sysModulePermissionMapper.deleteOrgPostButtonAuthByButtonId(buttonEntity.getId());
            sysModulePermissionMapper.deleteStateButtonAuthByButtonId(buttonEntity.getId());
            sysModuleMapper.deleteButtonById(buttonEntity.getId());
        }
        List<SysModuleTabEntity> tabList = sysModuleMapper.selectTabsByModuleId(moduleId);
        for (SysModuleTabEntity tabEntity : tabList) {
            sysModuleMapper.deleteTabById(tabEntity.getId());
        }
        sysModulePermissionMapper.deleteOrgPostModuleAuthByModuleId(moduleId);
        sysModulePermissionMapper.deleteStateFieldAuthList(moduleId);
        sysModulePermissionMapper.deleteStateButtonAuthList(moduleId);
        sysModuleMapper.deleteTransitionsByModuleId(moduleId);
        sysModuleMapper.deleteStatesByModuleId(moduleId);
    }

    private void syncModule(SysModuleEntity existing, Long parentId, ModuleSeed seed) {
        if (!isModuleChanged(existing, parentId, seed)) {
            return;
        }
        SysModuleDTO dto = buildModuleDto(existing.getId(), parentId, seed);
        sysModuleService.modify(dto);
    }

    private boolean isModuleChanged(SysModuleEntity existing, Long parentId, ModuleSeed seed) {
        return !Objects.equals(existing.getParentId(), parentId)
                || !Objects.equals(existing.getModuleName(), seed.getModuleName())
                || !Objects.equals(existing.getModuleType(), seed.getModuleType())
                || !Objects.equals(normalizeText(existing.getRoutePath()), normalizeText(seed.getRoutePath()))
                || !Objects.equals(normalizeText(existing.getComponentPath()), normalizeText(seed.getComponentPath()))
                || !Objects.equals(normalizeText(existing.getIcon()), normalizeText(seed.getIcon()))
                || !Objects.equals(existing.getSort(), seed.getSort())
                || !Objects.equals(existing.getNavVisible(), seed.getNavVisible())
                || !Objects.equals(existing.getStatefulFlag(), seed.getStatefulFlag())
                || !Objects.equals(normalizeText(existing.getStateFieldCode()), normalizeText(seed.getStateFieldCode()))
                || !Objects.equals(existing.getStatus(), ENABLED)
                || !Objects.equals(normalizeText(existing.getNote()), normalizeText(seed.getNote()));
    }

    private SysModuleDTO buildModuleDto(Long id, Long parentId, ModuleSeed seed) {
        SysModuleDTO dto = new SysModuleDTO();
        dto.setId(id);
        dto.setParentId(parentId);
        dto.setModuleCode(seed.getModuleCode());
        dto.setModuleName(seed.getModuleName());
        dto.setModuleType(seed.getModuleType());
        dto.setRoutePath(seed.getRoutePath());
        dto.setComponentPath(seed.getComponentPath());
        dto.setIcon(seed.getIcon());
        dto.setSort(seed.getSort());
        dto.setNavVisible(seed.getNavVisible());
        dto.setStatefulFlag(seed.getStatefulFlag());
        dto.setStateFieldCode(seed.getStateFieldCode());
        dto.setStatus(ENABLED);
        dto.setNote(seed.getNote());
        return dto;
    }

    private String normalizeText(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private void ensureButtons(Long moduleId, List<ButtonSeed> buttonSeeds) {
        for (ButtonSeed buttonSeed : buttonSeeds) {
            if (sysModuleMapper.selectButtonByModuleIdAndCode(moduleId, buttonSeed.getButtonCode()) != null) {
                continue;
            }
            SysModuleButtonDTO dto = new SysModuleButtonDTO();
            dto.setModuleId(moduleId);
            dto.setButtonCode(buttonSeed.getButtonCode());
            dto.setDefaultTitle(buttonSeed.getDefaultTitle());
            dto.setArea(buttonSeed.getArea());
            dto.setSort(buttonSeed.getSort());
            dto.setStatus(ENABLED);
            dto.setNote("系统模块引导自动补齐");
            sysModuleService.createButton(dto);
        }
    }

    private void syncAdminPermissions() {
        List<Long> adminOrgPostIdList = sysModulePermissionMapper.selectAdminOrgPostIdList();
        if (adminOrgPostIdList == null || adminOrgPostIdList.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        List<SysModuleEntity> moduleList = sysModuleMapper.selectAllModuleList();
        for (Long orgPostId : adminOrgPostIdList) {
            for (SysModuleEntity moduleEntity : moduleList) {
                syncAdminModulePermission(orgPostId, moduleEntity, now);
                syncAdminFieldPermissions(orgPostId, moduleEntity.getId(), now);
                syncAdminButtonPermissions(orgPostId, moduleEntity.getId(), now);
            }
        }
    }

    private void syncAdminModulePermission(Long orgPostId, SysModuleEntity moduleEntity, LocalDateTime now) {
        sysModulePermissionMapper.deleteOrgPostModuleAuth(orgPostId, moduleEntity.getId());
        SysOrgPostModuleAuthEntity entity = new SysOrgPostModuleAuthEntity();
        entity.setId(YitIdHelper.nextId());
        entity.setOrgPostId(orgPostId);
        entity.setModuleId(moduleEntity.getId());
        entity.setModuleVisible("1");
        entity.setNavVisible("1".equals(moduleEntity.getNavVisible()) ? "1" : "0");
        entity.setCreateBy(SYSTEM_USER);
        entity.setCreateTime(now);
        entity.setNote("admin 用户默认拥有全部模块权限");
        sysModulePermissionMapper.insertOrgPostModuleAuth(entity);
    }

    private void syncAdminFieldPermissions(Long orgPostId, Long moduleId, LocalDateTime now) {
        sysModulePermissionMapper.deleteOrgPostFieldAuthList(orgPostId, moduleId);
        List<SysModuleFieldEntity> fieldList = sysModuleMapper.selectFieldsByModuleId(moduleId);
        if (fieldList == null || fieldList.isEmpty()) {
            return;
        }
        List<SysOrgPostFieldAuthEntity> entityList = new ArrayList<>();
        for (SysModuleFieldEntity fieldEntity : fieldList) {
            SysOrgPostFieldAuthEntity entity = new SysOrgPostFieldAuthEntity();
            entity.setId(YitIdHelper.nextId());
            entity.setOrgPostId(orgPostId);
            entity.setModuleId(moduleId);
            entity.setFieldId(fieldEntity.getId());
            entity.setPermissionLevel(2);
            entity.setCreateBy(SYSTEM_USER);
            entity.setCreateTime(now);
            entity.setNote("admin 用户默认拥有全部字段可写权限");
            entityList.add(entity);
        }
        sysModulePermissionMapper.insertOrgPostFieldAuthBatch(entityList);
    }

    private void syncAdminButtonPermissions(Long orgPostId, Long moduleId, LocalDateTime now) {
        sysModulePermissionMapper.deleteOrgPostButtonAuthList(orgPostId, moduleId);
        List<SysModuleButtonEntity> buttonList = sysModuleMapper.selectButtonsByModuleId(moduleId);
        if (buttonList == null || buttonList.isEmpty()) {
            return;
        }
        List<SysOrgPostButtonAuthEntity> entityList = new ArrayList<>();
        for (SysModuleButtonEntity buttonEntity : buttonList) {
            SysOrgPostButtonAuthEntity entity = new SysOrgPostButtonAuthEntity();
            entity.setId(YitIdHelper.nextId());
            entity.setOrgPostId(orgPostId);
            entity.setModuleId(moduleId);
            entity.setButtonId(buttonEntity.getId());
            entity.setPermissionLevel(2);
            entity.setCreateBy(SYSTEM_USER);
            entity.setCreateTime(now);
            entity.setNote("admin 用户默认拥有全部按钮可用权限");
            entityList.add(entity);
        }
        sysModulePermissionMapper.insertOrgPostButtonAuthBatch(entityList);
    }

    @Getter
    @RequiredArgsConstructor
    private static class ModuleSeed {

        private final String moduleCode;

        private final String moduleName;

        private final String moduleType;

        private final String routePath;

        private final String componentPath;

        private final String icon;

        private final Integer sort;

        private final String navVisible;

        private final String statefulFlag;

        private final String stateFieldCode;

        private final String note;

        private static ModuleSeed catalog(String moduleCode, String moduleName, String routePath, String componentPath, String icon, Integer sort, String note) {
            return new ModuleSeed(moduleCode, moduleName, "CATALOG", routePath, componentPath, icon, sort, "1", "0", "", note);
        }

        private static ModuleSeed hiddenCatalog(String moduleCode, String moduleName, String routePath, String componentPath, String icon, Integer sort, String note) {
            return new ModuleSeed(moduleCode, moduleName, "CATALOG", routePath, componentPath, icon, sort, "0", "0", "", note);
        }

        private static ModuleSeed page(String moduleCode, String moduleName, String routePath, String componentPath, String icon, Integer sort, String note) {
            return new ModuleSeed(moduleCode, moduleName, "PAGE", routePath, componentPath, icon, sort, "1", "0", "", note);
        }

        private static ModuleSeed bill(String moduleCode, String moduleName, String routePath, String componentPath, String icon, Integer sort, String note) {
            return new ModuleSeed(moduleCode, moduleName, "BILL", routePath, componentPath, icon, sort, "1", "1", "status", note);
        }

        private static ModuleSeed hiddenBill(String moduleCode, String moduleName, String routePath, String componentPath, String icon, Integer sort, String note) {
            return new ModuleSeed(moduleCode, moduleName, "BILL", routePath, componentPath, icon, sort, "0", "1", "status", note);
        }

    }

    @Getter
    @RequiredArgsConstructor
    private static class ButtonSeed {

        private final String buttonCode;

        private final String defaultTitle;

        private final String area;

        private final Integer sort;

        private static ButtonSeed of(String buttonCode, String defaultTitle, String area, Integer sort) {
            return new ButtonSeed(buttonCode, defaultTitle, area, sort);
        }

    }

}

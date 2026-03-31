package com.soli.system.core.service.impl.sysmodule;

import com.github.yitter.idgen.YitIdHelper;
import com.soli.common.api.enums.BinaryFlagEnum;
import com.soli.common.api.exception.BusinessException;
import com.soli.system.core.mapper.SysModuleMapper;
import com.soli.system.core.mapper.SysModulePermissionMapper;
import com.soli.system.core.mapper.SysModuleTitleMapper;
import com.soli.system.core.service.impl.BaseCrudServiceImpl;
import com.soli.system.core.service.impl.sysmodulepermission.SysOrgPostButtonAuthEntity;
import com.soli.system.core.service.impl.sysmodulepermission.SysOrgPostFieldAuthEntity;
import com.soli.system.core.service.impl.sysmodulepermission.SysOrgPostModuleAuthEntity;
import com.soli.system.core.service.impl.sysmodulepermission.SysUserModuleNavPermissionModel;
import com.soli.system.core.service.impl.sysmoduletitle.SysModuleFieldTitleEntity;
import com.soli.system.service.sysmodule.SysModuleButtonDTO;
import com.soli.system.service.sysmodule.SysModuleComponentDTO;
import com.soli.system.service.sysmodule.SysModuleComponentDetailDTO;
import com.soli.system.service.sysmodule.SysModuleDTO;
import com.soli.system.service.sysmodule.SysModuleDetailDTO;
import com.soli.system.service.sysmodule.SysModuleFieldDTO;
import com.soli.system.service.sysmodule.SysModuleQuery;
import com.soli.system.service.sysmodule.SysModuleService;
import com.soli.system.service.sysmodule.SysModuleStateDTO;
import com.soli.system.service.sysmodule.SysModuleTransitionDTO;
import com.soli.system.service.sysmodule.SysModuleTreeNodeDTO;
import com.soli.system.service.enums.ModuleTypeEnum;
import com.soli.system.service.enums.PermissionLevelEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 模块服务实现
 *
 * @author lizhengqiang
 * @since 2026-03-29 18:10
 */
@Service
public class SysModuleServiceImpl extends BaseCrudServiceImpl<SysModuleDTO, SysModuleEntity, SysModuleQuery> implements SysModuleService {

    private static final String DEFAULT_LOCALE = "zh_CN";
    private static final String SYSTEM_USER = "system";
    private static final String MODULE_NOT_FOUND_MESSAGE = "模块不存在";
    private static final String PARENT_MODULE_NOT_FOUND_MESSAGE = "上级模块不存在";
    private static final String MODULE_CODE_EXISTS_MESSAGE = "模块编码已存在";
    private static final String COMPONENT_CODE_EXISTS_MESSAGE = "组件编码已存在";
    private static final String FIELD_CODE_EXISTS_MESSAGE = "字段编码已存在";
    private static final String BUTTON_CODE_EXISTS_MESSAGE = "按钮编码已存在";
    private static final String COMPONENT_NOT_FOUND_MESSAGE = "组件不存在";
    private static final String FIELD_NOT_FOUND_MESSAGE = "字段不存在";
    private static final String BUTTON_NOT_FOUND_MESSAGE = "按钮不存在";
    private static final String CHILD_MODULE_REQUIRES_CATALOG_MESSAGE = "存在子模块的节点必须保持为目录";
    private static final String MODULE_DESIGN_DATA_EXISTS_MESSAGE = "当前模块仍存在设计数据，不能删除";
    private static final String FIELD_COMPONENT_MISMATCH_MESSAGE = "字段所属组件不属于当前模块";

    private final SysModuleMapper sysModuleMapper;
    private final SysModuleTitleMapper sysModuleTitleMapper;
    private final SysModulePermissionMapper sysModulePermissionMapper;
    private final SysModuleConverter sysModuleConverter;

    public SysModuleServiceImpl(final SysModuleMapper mapper,
                                final SysModuleConverter converter,
                                final SysModuleTitleMapper titleMapper,
                                final SysModulePermissionMapper permissionMapper) {
        super(mapper, converter);
        this.sysModuleMapper = mapper;
        this.sysModuleTitleMapper = titleMapper;
        this.sysModulePermissionMapper = permissionMapper;
        this.sysModuleConverter = converter;
    }

    @Override
    public List<SysModuleTreeNodeDTO> queryAllTreeList() {
        return buildTreeList();
    }

    @Override
    public List<SysModuleTreeNodeDTO> queryTreeList() {
        return filterUserVisibleTree(buildTreeList());
    }

    @Override
    public List<SysModuleTreeNodeDTO> queryNavTree(Long userId, Long companyId) {
        if (userId == null || companyId == null) {
            return new ArrayList<>();
        }
        List<SysUserModuleNavPermissionModel> navPermissionList = sysModulePermissionMapper.selectUserNavPermissionList(userId, companyId);
        if (navPermissionList == null || navPermissionList.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, BinaryFlagEnum> navVisibleMap = buildNavVisibleMap(navPermissionList);
        return applyNavVisibility(filterNavTree(buildTreeList(), navVisibleMap.keySet()), navVisibleMap);
    }

    @Override
    public SysModuleDetailDTO queryManageDetailById(Long id) {
        return buildDetailById(id, false);
    }

    @Override
    public SysModuleDetailDTO queryDetailById(Long id) {
        return buildDetailById(id, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createComponent(SysModuleComponentDTO dto) {
        normalizeComponent(dto);
        validateModuleExists(dto.getModuleId());
        validateComponentCodeUnique(dto.getModuleId(), dto.getComponentCode(), null);
        dto.setCreateTime(LocalDateTime.now());
        SysModuleComponentEntity entity = sysModuleConverter.toComponentEntity(dto);
        entity.setId(YitIdHelper.nextId());
        if (sysModuleMapper.insertComponent(entity) == 0) {
            throw new BusinessException("新增组件失败");
        }
        dto.setId(entity.getId());
        touchModuleVersion(dto.getModuleId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyComponent(SysModuleComponentDTO dto) {
        normalizeComponent(dto);
        SysModuleComponentEntity currentEntity = requireComponent(dto.getId());
        if (!Objects.equals(currentEntity.getModuleId(), dto.getModuleId())) {
            throw new BusinessException("不支持跨模块移动组件");
        }
        validateModuleExists(dto.getModuleId());
        validateComponentCodeUnique(dto.getModuleId(), dto.getComponentCode(), dto.getId());
        dto.setUpdateTime(LocalDateTime.now());
        if (sysModuleMapper.updateComponent(sysModuleConverter.toComponentEntity(dto)) == 0) {
            throw new BusinessException("修改组件失败");
        }
        sysModuleTitleMapper.updateComponentInfo(dto.getId(), dto.getComponentCode(), SYSTEM_USER, dto.getUpdateTime());
        touchModuleVersion(dto.getModuleId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeComponent(Long id) {
        SysModuleComponentEntity currentEntity = requireComponent(id);
        if (sysModuleMapper.countFieldByComponentId(id) > 0) {
            throw new BusinessException("当前组件仍包含字段，不能删除");
        }
        if (sysModuleMapper.deleteComponentById(id) == 0) {
            throw new BusinessException("删除组件失败");
        }
        touchModuleVersion(currentEntity.getModuleId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createField(SysModuleFieldDTO dto) {
        normalizeField(dto);
        validateModuleExists(dto.getModuleId());
        SysModuleComponentEntity componentEntity = requireComponent(dto.getComponentId());
        validateFieldBelongsToModule(dto.getModuleId(), componentEntity);
        dto.setComponentCode(componentEntity.getComponentCode());
        validateFieldCodeUnique(dto.getModuleId(), dto.getComponentId(), dto.getFieldCode(), null);
        dto.setCreateTime(LocalDateTime.now());
        SysModuleFieldEntity entity = sysModuleConverter.toFieldEntity(dto);
        entity.setId(YitIdHelper.nextId());
        if (sysModuleMapper.insertField(entity) == 0) {
            throw new BusinessException("新增字段失败");
        }
        dto.setId(entity.getId());
        createFieldTitleRelation(entity);
        grantAdminFieldPermissions(entity);
        touchModuleVersion(dto.getModuleId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyField(SysModuleFieldDTO dto) {
        normalizeField(dto);
        SysModuleFieldEntity currentEntity = requireField(dto.getId());
        if (!Objects.equals(currentEntity.getModuleId(), dto.getModuleId())) {
            throw new BusinessException("不支持跨模块移动字段");
        }
        validateModuleExists(dto.getModuleId());
        SysModuleComponentEntity componentEntity = requireComponent(dto.getComponentId());
        validateFieldBelongsToModule(dto.getModuleId(), componentEntity);
        dto.setComponentCode(componentEntity.getComponentCode());
        validateFieldCodeUnique(dto.getModuleId(), dto.getComponentId(), dto.getFieldCode(), dto.getId());
        dto.setUpdateTime(LocalDateTime.now());
        SysModuleFieldEntity entity = sysModuleConverter.toFieldEntity(dto);
        if (sysModuleMapper.updateField(entity) == 0) {
            throw new BusinessException("修改字段失败");
        }
        updateFieldTitleRelation(entity);
        touchModuleVersion(dto.getModuleId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeField(Long id) {
        SysModuleFieldEntity currentEntity = requireField(id);
        if (sysModuleMapper.deleteFieldById(id) == 0) {
            throw new BusinessException("删除字段失败");
        }
        removeFieldRelations(id);
        touchModuleVersion(currentEntity.getModuleId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createButton(SysModuleButtonDTO dto) {
        normalizeButton(dto);
        validateModuleExists(dto.getModuleId());
        validateButtonCodeUnique(dto.getModuleId(), dto.getButtonCode(), null);
        dto.setCreateTime(LocalDateTime.now());
        SysModuleButtonEntity entity = sysModuleConverter.toButtonEntity(dto);
        entity.setId(YitIdHelper.nextId());
        if (sysModuleMapper.insertButton(entity) == 0) {
            throw new BusinessException("新增按钮失败");
        }
        dto.setId(entity.getId());
        grantAdminButtonPermissions(entity);
        touchModuleVersion(dto.getModuleId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyButton(SysModuleButtonDTO dto) {
        normalizeButton(dto);
        SysModuleButtonEntity currentEntity = requireButton(dto.getId());
        if (!Objects.equals(currentEntity.getModuleId(), dto.getModuleId())) {
            throw new BusinessException("不支持跨模块移动按钮");
        }
        validateModuleExists(dto.getModuleId());
        validateButtonCodeUnique(dto.getModuleId(), dto.getButtonCode(), dto.getId());
        dto.setUpdateTime(LocalDateTime.now());
        if (sysModuleMapper.updateButton(sysModuleConverter.toButtonEntity(dto)) == 0) {
            throw new BusinessException("修改按钮失败");
        }
        touchModuleVersion(dto.getModuleId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeButton(Long id) {
        SysModuleButtonEntity currentEntity = requireButton(id);
        if (sysModuleMapper.deleteButtonById(id) == 0) {
            throw new BusinessException("删除按钮失败");
        }
        removeButtonRelations(id);
        touchModuleVersion(currentEntity.getModuleId());
    }

    @Override
    protected String moduleName() {
        return "模块管理";
    }

    @Override
    protected void beforeCreate(SysModuleDTO dto) {
        normalizeModule(dto);
        validateModuleParent(dto.getParentId());
        validateModuleCodeUnique(dto.getModuleCode(), null);
        SysModuleEntity parentEntity = resolveParentModule(dto.getParentId());
        dto.setAncestors(resolveAncestors(parentEntity));
        dto.setContextVersion(1);
        dto.setCreateTime(LocalDateTime.now());
    }

    @Override
    protected void afterCreate(SysModuleDTO dto, SysModuleEntity entity) {
        dto.setId(entity.getId());
        grantAdminModulePermissions(entity);
    }

    @Override
    protected void afterRemove(Long id) {
        sysModulePermissionMapper.deleteOrgPostModuleAuthByModuleId(id);
        sysModulePermissionMapper.deleteStateFieldAuthList(id);
        sysModulePermissionMapper.deleteStateButtonAuthList(id);
        sysModuleMapper.deleteTransitionsByModuleId(id);
        sysModuleMapper.deleteStatesByModuleId(id);
    }

    @Override
    protected void beforeModify(SysModuleDTO dto) {
        normalizeModule(dto);
        SysModuleEntity currentEntity = sysModuleMapper.selectById(dto.getId());
        if (currentEntity == null) {
            throw new BusinessException(MODULE_NOT_FOUND_MESSAGE);
        }
        validateModuleParent(dto.getParentId());
        SysModuleEntity parentEntity = resolveParentModule(dto.getParentId());
        if (parentEntity != null && (Objects.equals(parentEntity.getId(), dto.getId()) || containsId(parentEntity.getAncestors(), dto.getId()))) {
            throw new BusinessException("上级模块不能选择当前模块或其下级模块");
        }
        if (ModuleTypeEnum.CATALOG != dto.getModuleType() && sysModuleMapper.countChildModuleByParentId(dto.getId()) > 0) {
            throw new BusinessException(CHILD_MODULE_REQUIRES_CATALOG_MESSAGE);
        }
        validateModuleCodeUnique(dto.getModuleCode(), dto.getId());
        String newAncestors = resolveAncestors(parentEntity);
        dto.setAncestors(newAncestors);
        dto.setContextVersion((currentEntity.getContextVersion() == null ? 0 : currentEntity.getContextVersion()) + 1);
        dto.setUpdateTime(LocalDateTime.now());
        String oldPath = buildSelfPath(currentEntity.getAncestors(), currentEntity.getId());
        String newPath = buildSelfPath(newAncestors, currentEntity.getId());
        if (!Objects.equals(oldPath, newPath)) {
            sysModuleMapper.updateDescendantAncestors(oldPath, newPath, LocalDateTime.now());
        }
    }

    @Override
    protected void beforeRemove(Long id) {
        if (sysModuleMapper.selectById(id) == null) {
            throw new BusinessException(MODULE_NOT_FOUND_MESSAGE);
        }
        if (sysModuleMapper.countChildModuleByParentId(id) > 0) {
            throw new BusinessException("当前模块仍存在子模块，不能删除");
        }
        if (sysModuleMapper.countComponentByModuleId(id) > 0 || sysModuleMapper.countFieldByModuleId(id) > 0 || sysModuleMapper.countButtonByModuleId(id) > 0) {
            throw new BusinessException(MODULE_DESIGN_DATA_EXISTS_MESSAGE);
        }
    }

    private SysModuleDetailDTO buildDetailById(Long id, boolean includeFieldTitles) {
        SysModuleEntity moduleEntity = sysModuleMapper.selectById(id);
        if (moduleEntity == null) {
            throw new BusinessException(MODULE_NOT_FOUND_MESSAGE);
        }
        SysModuleDetailDTO detail = sysModuleConverter.toDetailDTO(moduleEntity);
        List<SysModuleComponentDTO> componentList = sysModuleConverter.toComponentDTOList(sysModuleMapper.selectComponentsByModuleId(id));
        List<SysModuleFieldDTO> fieldList = sysModuleConverter.toFieldDTOList(includeFieldTitles ? sysModuleMapper.selectFieldsByModuleId(id) : sysModuleMapper.selectFieldDefinitionsByModuleId(id));
        List<SysModuleButtonDTO> buttonList = sysModuleConverter.toButtonDTOList(sysModuleMapper.selectButtonsByModuleId(id));
        detail.setComponents(buildComponentDetails(componentList, fieldList));
        detail.setButtons(buttonList);
        detail.getButtons().sort(Comparator.comparing(SysModuleButtonDTO::getSort, Comparator.nullsLast(Integer::compareTo)));
        detail.setStates(sysModuleConverter.toStateDTOList(sysModuleMapper.selectStatesByModuleId(id)));
        detail.setTransitions(sysModuleConverter.toTransitionDTOList(sysModuleMapper.selectTransitionsByModuleId(id)));
        detail.getStates().sort(Comparator.comparing(SysModuleStateDTO::getSort, Comparator.nullsLast(Integer::compareTo)));
        detail.getTransitions().sort(Comparator.comparing(SysModuleTransitionDTO::getSort, Comparator.nullsLast(Integer::compareTo)));
        return detail;
    }

    private List<SysModuleComponentDetailDTO> buildComponentDetails(List<SysModuleComponentDTO> componentList, List<SysModuleFieldDTO> fieldList) {
        Map<Long, List<SysModuleFieldDTO>> fieldMap = new LinkedHashMap<>();
        fieldList.stream().sorted(Comparator.comparing(SysModuleFieldDTO::getSort, Comparator.nullsLast(Integer::compareTo))).forEach(field -> fieldMap.computeIfAbsent(field.getComponentId(), key -> new ArrayList<>()).add(field));
        return componentList.stream().sorted(Comparator.comparing(SysModuleComponentDTO::getSort, Comparator.nullsLast(Integer::compareTo))).map(component -> {
            SysModuleComponentDetailDTO detail = new SysModuleComponentDetailDTO();
            detail.setComponentInfo(component);
            detail.setFields(fieldMap.getOrDefault(component.getId(), new ArrayList<>()));
            return detail;
        }).toList();
    }

    private void normalizeModule(SysModuleDTO dto) {
        if (dto.getSort() != null && dto.getSort() <= 0) {
            dto.setSort(1);
        }
        if (BinaryFlagEnum.YES != dto.getStatefulFlag()) {
            dto.setStateFieldCode("");
        }
        if (ModuleTypeEnum.BILL == dto.getModuleType()) {
            dto.setModuleType(ModuleTypeEnum.PAGE);
        }
    }

    private void normalizeComponent(SysModuleComponentDTO dto) {
        if (dto.getSort() != null && dto.getSort() <= 0) {
            dto.setSort(1);
        }
    }

    private void normalizeField(SysModuleFieldDTO dto) {
        if (dto.getSort() != null && dto.getSort() <= 0) {
            dto.setSort(1);
        }
    }

    private void normalizeButton(SysModuleButtonDTO dto) {
        if (dto.getSort() != null && dto.getSort() <= 0) {
            dto.setSort(1);
        }
    }

    private void validateModuleParent(Long parentId) {
        if (parentId == null || parentId <= 0) {
            return;
        }
        SysModuleEntity parentEntity = sysModuleMapper.selectById(parentId);
        if (parentEntity == null) {
            throw new BusinessException(PARENT_MODULE_NOT_FOUND_MESSAGE);
        }
        if (ModuleTypeEnum.CATALOG != parentEntity.getModuleType()) {
            throw new BusinessException("只有目录节点允许包含子模块");
        }
    }

    private void validateModuleExists(Long moduleId) {
        if (moduleId == null || sysModuleMapper.selectById(moduleId) == null) {
            throw new BusinessException(MODULE_NOT_FOUND_MESSAGE);
        }
    }

    private void validateModuleCodeUnique(String moduleCode, Long currentId) {
        SysModuleEntity entity = sysModuleMapper.selectByModuleCode(moduleCode);
        if (entity == null) {
            return;
        }
        if (currentId != null && Objects.equals(entity.getId(), currentId)) {
            return;
        }
        throw new BusinessException(MODULE_CODE_EXISTS_MESSAGE);
    }

    private void validateComponentCodeUnique(Long moduleId, String componentCode, Long currentId) {
        SysModuleComponentEntity entity = sysModuleMapper.selectComponentByModuleIdAndCode(moduleId, componentCode);
        if (entity == null) {
            return;
        }
        if (currentId != null && Objects.equals(entity.getId(), currentId)) {
            return;
        }
        throw new BusinessException(COMPONENT_CODE_EXISTS_MESSAGE);
    }

    private void validateFieldCodeUnique(Long moduleId, Long componentId, String fieldCode, Long currentId) {
        SysModuleFieldEntity entity = sysModuleMapper.selectFieldByModuleIdAndComponentIdAndCode(moduleId, componentId, fieldCode);
        if (entity == null) {
            return;
        }
        if (currentId != null && Objects.equals(entity.getId(), currentId)) {
            return;
        }
        throw new BusinessException(FIELD_CODE_EXISTS_MESSAGE);
    }

    private void validateButtonCodeUnique(Long moduleId, String buttonCode, Long currentId) {
        SysModuleButtonEntity entity = sysModuleMapper.selectButtonByModuleIdAndCode(moduleId, buttonCode);
        if (entity == null) {
            return;
        }
        if (currentId != null && Objects.equals(entity.getId(), currentId)) {
            return;
        }
        throw new BusinessException(BUTTON_CODE_EXISTS_MESSAGE);
    }

    private void validateFieldBelongsToModule(Long moduleId, SysModuleComponentEntity componentEntity) {
        if (!Objects.equals(componentEntity.getModuleId(), moduleId)) {
            throw new BusinessException(FIELD_COMPONENT_MISMATCH_MESSAGE);
        }
    }

    private SysModuleEntity resolveParentModule(Long parentId) {
        if (parentId == null || parentId <= 0) {
            return null;
        }
        SysModuleEntity parentEntity = sysModuleMapper.selectById(parentId);
        if (parentEntity == null) {
            throw new BusinessException(PARENT_MODULE_NOT_FOUND_MESSAGE);
        }
        return parentEntity;
    }

    private SysModuleComponentEntity requireComponent(Long id) {
        SysModuleComponentEntity entity = sysModuleMapper.selectComponentById(id);
        if (entity == null) {
            throw new BusinessException(COMPONENT_NOT_FOUND_MESSAGE);
        }
        return entity;
    }

    private SysModuleFieldEntity requireField(Long id) {
        SysModuleFieldEntity entity = sysModuleMapper.selectFieldById(id);
        if (entity == null) {
            throw new BusinessException(FIELD_NOT_FOUND_MESSAGE);
        }
        return entity;
    }

    private SysModuleButtonEntity requireButton(Long id) {
        SysModuleButtonEntity entity = sysModuleMapper.selectButtonById(id);
        if (entity == null) {
            throw new BusinessException(BUTTON_NOT_FOUND_MESSAGE);
        }
        return entity;
    }

    private String resolveAncestors(SysModuleEntity parentEntity) {
        if (parentEntity == null) {
            return "0";
        }
        return buildSelfPath(parentEntity.getAncestors(), parentEntity.getId());
    }

    private String buildSelfPath(String ancestors, Long id) {
        if (!StringUtils.hasText(ancestors)) {
            return String.valueOf(id);
        }
        return ancestors + "," + id;
    }

    private boolean containsId(String ancestors, Long id) {
        if (!StringUtils.hasText(ancestors) || id == null) {
            return false;
        }
        return ("," + ancestors + ",").contains("," + id + ",");
    }

    private void createFieldTitleRelation(SysModuleFieldEntity fieldEntity) {
        LocalDateTime now = LocalDateTime.now();
        SysModuleFieldTitleEntity titleEntity = buildFieldTitleEntity(fieldEntity);
        titleEntity.setId(YitIdHelper.nextId());
        titleEntity.setCreateBy(SYSTEM_USER);
        titleEntity.setCreateTime(now);
        titleEntity.setUpdateBy(SYSTEM_USER);
        titleEntity.setUpdateTime(now);
        sysModuleTitleMapper.insert(titleEntity);
    }

    private void updateFieldTitleRelation(SysModuleFieldEntity fieldEntity) {
        if (sysModuleTitleMapper.selectByFieldIdAndLocale(fieldEntity.getId(), DEFAULT_LOCALE) == null) {
            createFieldTitleRelation(fieldEntity);
            return;
        }
        SysModuleFieldTitleEntity titleEntity = buildFieldTitleEntity(fieldEntity);
        titleEntity.setUpdateBy(SYSTEM_USER);
        titleEntity.setUpdateTime(LocalDateTime.now());
        sysModuleTitleMapper.updateDefinition(titleEntity);
    }

    private SysModuleFieldTitleEntity buildFieldTitleEntity(SysModuleFieldEntity fieldEntity) {
        SysModuleFieldTitleEntity titleEntity = new SysModuleFieldTitleEntity();
        titleEntity.setFieldId(fieldEntity.getId());
        titleEntity.setModuleId(fieldEntity.getModuleId());
        titleEntity.setComponentId(fieldEntity.getComponentId());
        titleEntity.setComponentCode(fieldEntity.getComponentCode());
        titleEntity.setFieldCode(fieldEntity.getFieldCode());
        titleEntity.setDefaultTitle(fieldEntity.getDefaultTitle());
        titleEntity.setLocale(DEFAULT_LOCALE);
        titleEntity.setComponentType(fieldEntity.getComponentType());
        titleEntity.setDataPath(fieldEntity.getDataPath());
        titleEntity.setValueType(fieldEntity.getValueType());
        titleEntity.setRequiredFlag(fieldEntity.getRequiredFlag());
        titleEntity.setSort(fieldEntity.getSort());
        titleEntity.setStatus(fieldEntity.getStatus());
        titleEntity.setNote(fieldEntity.getNote());
        return titleEntity;
    }

    private void grantAdminModulePermissions(SysModuleEntity moduleEntity) {
        LocalDateTime now = LocalDateTime.now();
        resolveAdminOrgPostIds().forEach(orgPostId -> {
            if (sysModulePermissionMapper.selectOrgPostModuleAuth(orgPostId, moduleEntity.getId()) != null) {
                return;
            }
            SysOrgPostModuleAuthEntity entity = new SysOrgPostModuleAuthEntity();
            entity.setId(YitIdHelper.nextId());
            entity.setOrgPostId(orgPostId);
            entity.setModuleId(moduleEntity.getId());
            entity.setModuleVisible(BinaryFlagEnum.YES);
            entity.setNavVisible(BinaryFlagEnum.YES == moduleEntity.getNavVisible() ? BinaryFlagEnum.YES : BinaryFlagEnum.NO);
            entity.setCreateBy(SYSTEM_USER);
            entity.setCreateTime(now);
            entity.setNote("admin 账号默认拥有模块可见权限");
            sysModulePermissionMapper.insertOrgPostModuleAuth(entity);
        });
    }

    private void grantAdminFieldPermissions(SysModuleFieldEntity fieldEntity) {
        List<Long> adminOrgPostIdList = resolveAdminOrgPostIds();
        if (adminOrgPostIdList.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        List<SysOrgPostFieldAuthEntity> entityList = adminOrgPostIdList.stream().map(orgPostId -> {
            SysOrgPostFieldAuthEntity entity = new SysOrgPostFieldAuthEntity();
            entity.setId(YitIdHelper.nextId());
            entity.setOrgPostId(orgPostId);
            entity.setModuleId(fieldEntity.getModuleId());
            entity.setFieldId(fieldEntity.getId());
            entity.setPermissionLevel(PermissionLevelEnum.LEVEL_2);
            entity.setCreateBy(SYSTEM_USER);
            entity.setCreateTime(now);
            entity.setNote("admin 账号默认拥有字段可写权限");
            return entity;
        }).toList();
        sysModulePermissionMapper.insertOrgPostFieldAuthBatch(entityList);
    }

    private void grantAdminButtonPermissions(SysModuleButtonEntity buttonEntity) {
        List<Long> adminOrgPostIdList = resolveAdminOrgPostIds();
        if (adminOrgPostIdList.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        List<SysOrgPostButtonAuthEntity> entityList = adminOrgPostIdList.stream().map(orgPostId -> {
            SysOrgPostButtonAuthEntity entity = new SysOrgPostButtonAuthEntity();
            entity.setId(YitIdHelper.nextId());
            entity.setOrgPostId(orgPostId);
            entity.setModuleId(buttonEntity.getModuleId());
            entity.setButtonId(buttonEntity.getId());
            entity.setPermissionLevel(PermissionLevelEnum.LEVEL_2);
            entity.setCreateBy(SYSTEM_USER);
            entity.setCreateTime(now);
            entity.setNote("admin 账号默认拥有按钮权限");
            return entity;
        }).toList();
        sysModulePermissionMapper.insertOrgPostButtonAuthBatch(entityList);
    }

    private void removeFieldRelations(Long fieldId) {
        sysModuleTitleMapper.deleteByFieldId(fieldId);
        sysModulePermissionMapper.deleteOrgPostFieldAuthByFieldId(fieldId);
        sysModulePermissionMapper.deleteStateFieldAuthByFieldId(fieldId);
    }

    private void removeButtonRelations(Long buttonId) {
        sysModulePermissionMapper.deleteOrgPostButtonAuthByButtonId(buttonId);
        sysModulePermissionMapper.deleteStateButtonAuthByButtonId(buttonId);
    }

    private List<Long> resolveAdminOrgPostIds() {
        List<Long> adminOrgPostIdList = sysModulePermissionMapper.selectAdminOrgPostIdList();
        return adminOrgPostIdList == null ? new ArrayList<>() : adminOrgPostIdList;
    }

    private void touchModuleVersion(Long moduleId) {
        sysModuleMapper.incrementContextVersion(moduleId, LocalDateTime.now());
    }

    private List<SysModuleTreeNodeDTO> buildTreeList() {
        List<SysModuleTreeNodeDTO> dtoList = sysModuleConverter.toTreeNodeDTOList(sysModuleMapper.selectTreeNodes());
        Map<Long, SysModuleTreeNodeDTO> nodeMap = new LinkedHashMap<>();
        List<SysModuleTreeNodeDTO> rootList = new ArrayList<>();
        dtoList.forEach(node -> {
            node.setChildren(new ArrayList<>());
            nodeMap.put(node.getId(), node);
        });
        dtoList.forEach(node -> appendNode(rootList, nodeMap, node));
        sortTree(rootList);
        return rootList;
    }

    private void appendNode(List<SysModuleTreeNodeDTO> rootList, Map<Long, SysModuleTreeNodeDTO> nodeMap, SysModuleTreeNodeDTO currentNode) {
        if (currentNode.getParentId() == null || currentNode.getParentId() <= 0) {
            rootList.add(currentNode);
            return;
        }
        SysModuleTreeNodeDTO parentNode = nodeMap.get(currentNode.getParentId());
        if (parentNode == null) {
            rootList.add(currentNode);
            return;
        }
        parentNode.getChildren().add(currentNode);
    }

    private void sortTree(List<SysModuleTreeNodeDTO> nodes) {
        nodes.sort(Comparator.comparing(SysModuleTreeNodeDTO::getSort, Comparator.nullsLast(Integer::compareTo)).thenComparing(SysModuleTreeNodeDTO::getId));
        nodes.forEach(node -> {
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                sortTree(node.getChildren());
            }
        });
    }

    private List<SysModuleTreeNodeDTO> filterNavTree(List<SysModuleTreeNodeDTO> nodeList, Set<Long> visibleModuleIdSet) {
        List<SysModuleTreeNodeDTO> result = new ArrayList<>();
        for (SysModuleTreeNodeDTO node : nodeList) {
            List<SysModuleTreeNodeDTO> children = node.getChildren() == null ? new ArrayList<>() : filterNavTree(node.getChildren(), visibleModuleIdSet);
            if (visibleModuleIdSet.contains(node.getId()) || !children.isEmpty()) {
                SysModuleTreeNodeDTO copied = copyTreeNode(node);
                copied.setChildren(children);
                result.add(copied);
            }
        }
        return result;
    }

    private List<SysModuleTreeNodeDTO> filterUserVisibleTree(List<SysModuleTreeNodeDTO> nodeList) {
        List<SysModuleTreeNodeDTO> result = new ArrayList<>();
        for (SysModuleTreeNodeDTO node : nodeList) {
            List<SysModuleTreeNodeDTO> children = node.getChildren() == null ? new ArrayList<>() : filterUserVisibleTree(node.getChildren());
            if (isUserVisibleNode(node) || !children.isEmpty()) {
                SysModuleTreeNodeDTO copied = copyTreeNode(node);
                copied.setChildren(children);
                result.add(copied);
            }
        }
        return result;
    }

    private boolean isUserVisibleNode(SysModuleTreeNodeDTO node) {
        return BinaryFlagEnum.YES == node.getNavVisible();
    }

    private Map<Long, BinaryFlagEnum> buildNavVisibleMap(List<SysUserModuleNavPermissionModel> navPermissionList) {
        Map<Long, BinaryFlagEnum> navVisibleMap = new LinkedHashMap<>();
        navPermissionList.forEach(item -> navVisibleMap.put(item.getModuleId(), item.getNavVisible()));
        return navVisibleMap;
    }

    private List<SysModuleTreeNodeDTO> applyNavVisibility(List<SysModuleTreeNodeDTO> nodeList, Map<Long, BinaryFlagEnum> navVisibleMap) {
        nodeList.forEach(node -> {
            BinaryFlagEnum navVisible = navVisibleMap.get(node.getId());
            if (navVisible != null) {
                node.setNavVisible(navVisible);
            }
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                applyNavVisibility(node.getChildren(), navVisibleMap);
            }
        });
        return nodeList;
    }

    private SysModuleTreeNodeDTO copyTreeNode(SysModuleTreeNodeDTO node) {
        SysModuleTreeNodeDTO copied = new SysModuleTreeNodeDTO();
        copied.setId(node.getId());
        copied.setParentId(node.getParentId());
        copied.setModuleCode(node.getModuleCode());
        copied.setModuleName(node.getModuleName());
        copied.setModuleType(node.getModuleType());
        copied.setRoutePath(node.getRoutePath());
        copied.setComponentPath(node.getComponentPath());
        copied.setIcon(node.getIcon());
        copied.setSort(node.getSort());
        copied.setNavVisible(node.getNavVisible());
        copied.setStatefulFlag(node.getStatefulFlag());
        copied.setStatus(node.getStatus());
        return copied;
    }

}

package com.soli.system.core.service.impl.sysmodule;

import com.github.yitter.idgen.YitIdHelper;
import com.soli.common.api.exception.BusinessException;
import com.soli.system.core.mapper.SysModuleMapper;
import com.soli.system.core.service.impl.BaseCrudServiceImpl;
import com.soli.system.service.sysmodule.SysModuleButtonDTO;
import com.soli.system.service.sysmodule.SysModuleContextPreviewDTO;
import com.soli.system.service.sysmodule.SysModuleDTO;
import com.soli.system.service.sysmodule.SysModuleDetailDTO;
import com.soli.system.service.sysmodule.SysModuleFieldDTO;
import com.soli.system.service.sysmodule.SysModuleQuery;
import com.soli.system.service.sysmodule.SysModuleService;
import com.soli.system.service.sysmodule.SysModuleTabDTO;
import com.soli.system.service.sysmodule.SysModuleTabDetailDTO;
import com.soli.system.service.sysmodule.SysModuleTreeNodeDTO;
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

/**
 * 模块中心服务实现
 *
 * @author lizhengqiang
 * @since 2026-03-25 00:15
 */
@Service
public class SysModuleServiceImpl extends BaseCrudServiceImpl<SysModuleDTO, SysModuleEntity, SysModuleQuery>
        implements SysModuleService {

    private final SysModuleMapper sysModuleMapper;

    private final SysModuleConverter sysModuleConverter;

    public SysModuleServiceImpl(final SysModuleMapper mapper, final SysModuleConverter converter) {
        super(mapper, converter);
        this.sysModuleMapper = mapper;
        this.sysModuleConverter = converter;
    }

    @Override
    public List<SysModuleTreeNodeDTO> queryTreeList() {
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

    @Override
    public SysModuleDetailDTO queryDetailById(Long id) {
        SysModuleEntity moduleEntity = sysModuleMapper.selectById(id);
        if (moduleEntity == null) {
            throw new BusinessException("模块不存在");
        }
        SysModuleDetailDTO detail = sysModuleConverter.toDetailDTO(moduleEntity);
        List<SysModuleTabDTO> tabList = sysModuleConverter.toTabDTOList(sysModuleMapper.selectTabsByModuleId(id));
        List<SysModuleFieldDTO> fieldList = sysModuleConverter.toFieldDTOList(sysModuleMapper.selectFieldsByModuleId(id));
        detail.setHeaderTabs(buildTabDetails(tabList, fieldList, "HEADER"));
        detail.setDetailTabs(buildTabDetails(tabList, fieldList, "DETAIL"));
        detail.setButtons(sysModuleConverter.toButtonDTOList(sysModuleMapper.selectButtonsByModuleId(id)));
        detail.getButtons().sort(Comparator.comparing(SysModuleButtonDTO::getSort, Comparator.nullsLast(Integer::compareTo)));
        return detail;
    }

    @Override
    public SysModuleContextPreviewDTO queryContextPreview(Long id) {
        SysModuleDetailDTO detail = queryDetailById(id);
        SysModuleContextPreviewDTO preview = new SysModuleContextPreviewDTO();
        preview.setModuleCode(detail.getModuleCode());
        preview.setModuleName(detail.getModuleName());
        preview.setContextVersion(detail.getContextVersion());
        // 模块中心只预览模块结构元数据，不拼装岗位或状态的假上下文。

        preview.setHeaderTabs(buildPreviewTabs(detail.getHeaderTabs()));
        preview.setDetailTabs(buildPreviewTabs(detail.getDetailTabs()));
        preview.setButtons(buildButtonPreview(detail.getButtons()));
        return preview;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createTab(SysModuleTabDTO dto) {
        normalizeTab(dto);
        validateModuleExists(dto.getModuleId());
        validateTabCodeUnique(dto.getModuleId(), dto.getTabScope(), dto.getTabCode(), null);
        dto.setCreateTime(LocalDateTime.now());
        SysModuleTabEntity entity = sysModuleConverter.toTabEntity(dto);
        entity.setId(YitIdHelper.nextId());
        if (sysModuleMapper.insertTab(entity) == 0) {
            throw new BusinessException("模块 Tab 创建失败");
        }
        dto.setId(entity.getId());
        touchModuleVersion(dto.getModuleId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyTab(SysModuleTabDTO dto) {
        normalizeTab(dto);
        SysModuleTabEntity currentEntity = requireTab(dto.getId());
        if (!Objects.equals(currentEntity.getModuleId(), dto.getModuleId())) {
            throw new BusinessException("不支持跨模块移动 Tab");
        }
        validateModuleExists(dto.getModuleId());
        validateTabCodeUnique(dto.getModuleId(), dto.getTabScope(), dto.getTabCode(), dto.getId());
        dto.setUpdateTime(LocalDateTime.now());
        if (sysModuleMapper.updateTab(sysModuleConverter.toTabEntity(dto)) == 0) {
            throw new BusinessException("模块 Tab 更新失败");
        }
        touchModuleVersion(dto.getModuleId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeTab(Long id) {
        SysModuleTabEntity currentEntity = requireTab(id);
        if (sysModuleMapper.countFieldByTabId(id) > 0) {
            throw new BusinessException("当前 Tab 下存在字段，不能删除");
        }
        if (sysModuleMapper.deleteTabById(id) == 0) {
            throw new BusinessException("模块 Tab 删除失败");
        }
        touchModuleVersion(currentEntity.getModuleId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createField(SysModuleFieldDTO dto) {
        normalizeField(dto);
        validateModuleExists(dto.getModuleId());
        SysModuleTabEntity tabEntity = requireTab(dto.getTabId());
        validateFieldBelongsToModule(dto.getModuleId(), tabEntity);
        dto.setFieldScope(tabEntity.getTabScope());
        validateFieldCodeUnique(dto.getModuleId(), dto.getFieldCode(), null);
        dto.setCreateTime(LocalDateTime.now());
        SysModuleFieldEntity entity = sysModuleConverter.toFieldEntity(dto);
        entity.setId(YitIdHelper.nextId());
        if (sysModuleMapper.insertField(entity) == 0) {
            throw new BusinessException("模块字段创建失败");
        }
        dto.setId(entity.getId());
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
        SysModuleTabEntity tabEntity = requireTab(dto.getTabId());
        validateFieldBelongsToModule(dto.getModuleId(), tabEntity);
        dto.setFieldScope(tabEntity.getTabScope());
        validateFieldCodeUnique(dto.getModuleId(), dto.getFieldCode(), dto.getId());
        dto.setUpdateTime(LocalDateTime.now());
        if (sysModuleMapper.updateField(sysModuleConverter.toFieldEntity(dto)) == 0) {
            throw new BusinessException("模块字段更新失败");
        }
        touchModuleVersion(dto.getModuleId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeField(Long id) {
        SysModuleFieldEntity currentEntity = requireField(id);
        if (sysModuleMapper.deleteFieldById(id) == 0) {
            throw new BusinessException("模块字段删除失败");
        }
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
            throw new BusinessException("模块按钮创建失败");
        }
        dto.setId(entity.getId());
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
            throw new BusinessException("模块按钮更新失败");
        }
        touchModuleVersion(dto.getModuleId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeButton(Long id) {
        SysModuleButtonEntity currentEntity = requireButton(id);
        if (sysModuleMapper.deleteButtonById(id) == 0) {
            throw new BusinessException("模块按钮删除失败");
        }
        touchModuleVersion(currentEntity.getModuleId());
    }

    @Override
    protected String moduleName() {
        return "模块中心";
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
    }

    @Override
    protected void beforeModify(SysModuleDTO dto) {
        normalizeModule(dto);
        SysModuleEntity currentEntity = sysModuleMapper.selectById(dto.getId());
        if (currentEntity == null) {
            throw new BusinessException("模块不存在");
        }
        validateModuleParent(dto.getParentId());
        SysModuleEntity parentEntity = resolveParentModule(dto.getParentId());
        if (parentEntity != null
                && (Objects.equals(parentEntity.getId(), dto.getId()) || containsId(parentEntity.getAncestors(), dto.getId()))) {
            throw new BusinessException("上级模块不能选择当前模块或其下级模块");
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
            throw new BusinessException("模块不存在");
        }
        if (sysModuleMapper.countChildModuleByParentId(id) > 0) {
            throw new BusinessException("当前模块存在子模块，不能删除");
        }
        if (sysModuleMapper.countTabByModuleId(id) > 0
                || sysModuleMapper.countFieldByModuleId(id) > 0
                || sysModuleMapper.countButtonByModuleId(id) > 0) {
            throw new BusinessException("当前模块存在设计数据，不能删除");
        }
    }

    private List<SysModuleTabDetailDTO> buildTabDetails(List<SysModuleTabDTO> tabList, List<SysModuleFieldDTO> fieldList, String scope) {
        Map<Long, List<SysModuleFieldDTO>> fieldMap = new LinkedHashMap<>();
        fieldList.stream()
                .sorted(Comparator.comparing(SysModuleFieldDTO::getSort, Comparator.nullsLast(Integer::compareTo)))
                .forEach(field -> fieldMap.computeIfAbsent(field.getTabId(), key -> new ArrayList<>()).add(field));
        return tabList.stream()
                .filter(tab -> scope.equals(tab.getTabScope()))
                .sorted(Comparator.comparing(SysModuleTabDTO::getSort, Comparator.nullsLast(Integer::compareTo)))
                .map(tab -> {
                    SysModuleTabDetailDTO detail = new SysModuleTabDetailDTO();
                    detail.setTabInfo(tab);
                    detail.setFields(fieldMap.getOrDefault(tab.getId(), new ArrayList<>()));
                    return detail;
                })
                .toList();
    }

    private List<SysModuleContextPreviewDTO.TabPreview> buildPreviewTabs(List<SysModuleTabDetailDTO> tabList) {
        return tabList.stream().map(tab -> {
            SysModuleContextPreviewDTO.TabPreview preview = new SysModuleContextPreviewDTO.TabPreview();
            preview.setTabInfo(tab.getTabInfo());
            preview.setVisible(!tab.getFields().isEmpty());
            preview.setFields(tab.getFields().stream().map(field -> {
                SysModuleContextPreviewDTO.FieldPreview fieldPreview = new SysModuleContextPreviewDTO.FieldPreview();
                fieldPreview.setFieldCode(field.getFieldCode());
                fieldPreview.setLabel(field.getDefaultTitle());
                fieldPreview.setComponentType(field.getComponentType());
                fieldPreview.setVisible(Boolean.TRUE);
                fieldPreview.setEditable(Boolean.TRUE);
                fieldPreview.setRequired("1".equals(field.getRequiredFlag()));
                return fieldPreview;
            }).toList());
            return preview;
        }).toList();
    }

    private SysModuleContextPreviewDTO.ButtonPreview buildButtonPreview(List<SysModuleButtonDTO> buttonList) {
        SysModuleContextPreviewDTO.ButtonPreview preview = new SysModuleContextPreviewDTO.ButtonPreview();
        preview.setListToolbar(groupButtons(buttonList, "LIST_TOOLBAR"));
        preview.setListRow(groupButtons(buttonList, "LIST_ROW_BUTTON"));
        preview.setHeaderToolbar(groupButtons(buttonList, "HEADER_TOOLBAR"));
        preview.setDetailRow(groupButtons(buttonList, "DETAIL_ROW_BUTTON"));
        return preview;
    }

    private Map<String, SysModuleContextPreviewDTO.ButtonItem> groupButtons(List<SysModuleButtonDTO> buttonList, String area) {
        Map<String, SysModuleContextPreviewDTO.ButtonItem> result = new LinkedHashMap<>();
        buttonList.stream()
                .filter(button -> area.equals(button.getArea()))
                .sorted(Comparator.comparing(SysModuleButtonDTO::getSort, Comparator.nullsLast(Integer::compareTo)))
                .forEach(button -> {
                    SysModuleContextPreviewDTO.ButtonItem item = new SysModuleContextPreviewDTO.ButtonItem();
                    item.setLabel(button.getDefaultTitle());
                    item.setVisible(Boolean.TRUE);
                    item.setDisabled(Boolean.FALSE);
                    result.put(button.getButtonCode(), item);
                });
        return result;
    }

    private void normalizeModule(SysModuleDTO dto) {
        if (dto.getParentId() == null) {
            dto.setParentId(0L);
        }
        if (dto.getSort() == null || dto.getSort() <= 0) {
            dto.setSort(1);
        }
        if (!StringUtils.hasText(dto.getNavVisible())) {
            dto.setNavVisible("1");
        }
        if (!StringUtils.hasText(dto.getStatefulFlag())) {
            dto.setStatefulFlag("0");
        }
        if (!"1".equals(dto.getStatefulFlag())) {
            dto.setStateFieldCode("");
        }
        if (!StringUtils.hasText(dto.getStatus())) {
            dto.setStatus("0");
        }
        if (StringUtils.hasText(dto.getModuleCode())) {
            dto.setModuleCode(dto.getModuleCode().trim());
        }
        if (StringUtils.hasText(dto.getModuleName())) {
            dto.setModuleName(dto.getModuleName().trim());
        }
    }

    private void normalizeTab(SysModuleTabDTO dto) {
        if (dto.getSort() == null || dto.getSort() <= 0) {
            dto.setSort(1);
        }
        if (!StringUtils.hasText(dto.getStatus())) {
            dto.setStatus("0");
        }
        if (StringUtils.hasText(dto.getTabScope())) {
            dto.setTabScope(dto.getTabScope().trim().toUpperCase());
        }
        if (StringUtils.hasText(dto.getTabCode())) {
            dto.setTabCode(dto.getTabCode().trim());
        }
        if (StringUtils.hasText(dto.getTabName())) {
            dto.setTabName(dto.getTabName().trim());
        }
    }

    private void normalizeField(SysModuleFieldDTO dto) {
        if (dto.getSort() == null || dto.getSort() <= 0) {
            dto.setSort(1);
        }
        if (!StringUtils.hasText(dto.getRequiredFlag())) {
            dto.setRequiredFlag("0");
        }
        if (!StringUtils.hasText(dto.getStatus())) {
            dto.setStatus("0");
        }
        if (StringUtils.hasText(dto.getFieldCode())) {
            dto.setFieldCode(dto.getFieldCode().trim());
        }
        if (StringUtils.hasText(dto.getDefaultTitle())) {
            dto.setDefaultTitle(dto.getDefaultTitle().trim());
        }
        if (StringUtils.hasText(dto.getDataPath())) {
            dto.setDataPath(dto.getDataPath().trim());
        }
    }

    private void normalizeButton(SysModuleButtonDTO dto) {
        if (dto.getSort() == null || dto.getSort() <= 0) {
            dto.setSort(1);
        }
        if (!StringUtils.hasText(dto.getStatus())) {
            dto.setStatus("0");
        }
        if (StringUtils.hasText(dto.getArea())) {
            dto.setArea(dto.getArea().trim().toUpperCase());
        }
        if (StringUtils.hasText(dto.getButtonCode())) {
            dto.setButtonCode(dto.getButtonCode().trim());
        }
        if (StringUtils.hasText(dto.getDefaultTitle())) {
            dto.setDefaultTitle(dto.getDefaultTitle().trim());
        }
    }

    private void validateModuleParent(Long parentId) {
        if (parentId == null || parentId <= 0) {
            return;
        }
        if (sysModuleMapper.selectById(parentId) == null) {
            throw new BusinessException("上级模块不存在");
        }
    }

    private void validateModuleExists(Long moduleId) {
        if (moduleId == null || sysModuleMapper.selectById(moduleId) == null) {
            throw new BusinessException("模块不存在");
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
        throw new BusinessException("模块编码已存在");
    }

    private void validateTabCodeUnique(Long moduleId, String tabScope, String tabCode, Long currentId) {
        SysModuleTabEntity entity = sysModuleMapper.selectTabByModuleIdAndScopeAndCode(moduleId, tabScope, tabCode);
        if (entity == null) {
            return;
        }
        if (currentId != null && Objects.equals(entity.getId(), currentId)) {
            return;
        }
        throw new BusinessException("Tab 编码已存在");
    }

    private void validateFieldCodeUnique(Long moduleId, String fieldCode, Long currentId) {
        SysModuleFieldEntity entity = sysModuleMapper.selectFieldByModuleIdAndCode(moduleId, fieldCode);
        if (entity == null) {
            return;
        }
        if (currentId != null && Objects.equals(entity.getId(), currentId)) {
            return;
        }
        throw new BusinessException("字段编码已存在");
    }

    private void validateButtonCodeUnique(Long moduleId, String buttonCode, Long currentId) {
        SysModuleButtonEntity entity = sysModuleMapper.selectButtonByModuleIdAndCode(moduleId, buttonCode);
        if (entity == null) {
            return;
        }
        if (currentId != null && Objects.equals(entity.getId(), currentId)) {
            return;
        }
        throw new BusinessException("按钮编码已存在");
    }

    private void validateFieldBelongsToModule(Long moduleId, SysModuleTabEntity tabEntity) {
        if (!Objects.equals(tabEntity.getModuleId(), moduleId)) {
            throw new BusinessException("字段所属模块与 Tab 不一致");
        }
    }

    private SysModuleEntity resolveParentModule(Long parentId) {
        if (parentId == null || parentId <= 0) {
            return null;
        }
        SysModuleEntity parentEntity = sysModuleMapper.selectById(parentId);
        if (parentEntity == null) {
            throw new BusinessException("上级模块不存在");
        }
        return parentEntity;
    }

    private SysModuleTabEntity requireTab(Long id) {
        SysModuleTabEntity entity = sysModuleMapper.selectTabById(id);
        if (entity == null) {
            throw new BusinessException("Tab 不存在");
        }
        return entity;
    }

    private SysModuleFieldEntity requireField(Long id) {
        SysModuleFieldEntity entity = sysModuleMapper.selectFieldById(id);
        if (entity == null) {
            throw new BusinessException("字段不存在");
        }
        return entity;
    }

    private SysModuleButtonEntity requireButton(Long id) {
        SysModuleButtonEntity entity = sysModuleMapper.selectButtonById(id);
        if (entity == null) {
            throw new BusinessException("按钮不存在");
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

    private void touchModuleVersion(Long moduleId) {
        sysModuleMapper.incrementContextVersion(moduleId, LocalDateTime.now());
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
        nodes.sort(Comparator
                .comparing(SysModuleTreeNodeDTO::getSort, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(SysModuleTreeNodeDTO::getId));
        nodes.forEach(node -> {
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                sortTree(node.getChildren());
            }
        });
    }

}

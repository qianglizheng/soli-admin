package com.soli.system.core.service.impl.sysmodule;

import com.soli.common.api.exception.BusinessException;
import com.soli.system.core.mapper.SysModuleMapper;
import com.soli.system.core.mapper.SysModulePermissionMapper;
import com.soli.system.core.service.impl.sysmodulepermission.SysModuleStateButtonAuthEntity;
import com.soli.system.core.service.impl.sysmodulepermission.SysModuleStateFieldAuthEntity;
import com.soli.system.core.service.impl.sysmodulepermission.SysUserModuleButtonPermissionModel;
import com.soli.system.core.service.impl.sysmodulepermission.SysUserModuleFieldPermissionModel;
import com.soli.system.service.sysmodule.SysModuleButtonDTO;
import com.soli.system.service.sysmodule.SysModuleContextDTO;
import com.soli.system.service.sysmodule.SysModuleContextService;
import com.soli.system.service.sysmodule.SysModuleDetailDTO;
import com.soli.system.service.sysmodule.SysModuleFieldDTO;
import com.soli.system.service.sysmodule.SysModuleService;
import com.soli.system.service.sysmodule.SysModuleStateDTO;
import com.soli.system.service.sysmodule.SysModuleTabDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Runtime module context service implementation.
 *
 * @author lizhengqiang
 * @since 2026-03-27 16:40
 */
@Service
@RequiredArgsConstructor
public class SysModuleContextServiceImpl implements SysModuleContextService {

    private static final int HIDDEN_PERMISSION_LEVEL = 0;

    private static final int FULL_PERMISSION_LEVEL = 2;

    private final SysModuleMapper sysModuleMapper;

    private final SysModulePermissionMapper sysModulePermissionMapper;

    private final SysModuleService sysModuleService;

    @Override
    public SysModuleContextDTO buildContext(String moduleCode, Long userId, Long companyId, String stateCode) {
        if (!StringUtils.hasText(moduleCode)) {
            throw new BusinessException("Module code must not be blank");
        }
        SysModuleEntity moduleEntity = sysModuleMapper.selectByModuleCode(moduleCode.trim());
        if (moduleEntity == null) {
            throw new BusinessException("Module does not exist");
        }

        SysModuleDetailDTO moduleDetail = sysModuleService.queryDetailById(moduleEntity.getId());
        String currentStateCode = StringUtils.hasText(stateCode) ? stateCode.trim() : null;
        SysModuleContextDTO.CurrentState currentState = buildCurrentState(moduleDetail, currentStateCode);
        Map<Long, Integer> fieldPermissionMap = buildFieldPermissionMap(moduleDetail, userId, companyId, currentStateCode);
        Map<Long, Integer> buttonPermissionMap = buildButtonPermissionMap(moduleDetail, userId, companyId, currentStateCode);

        SysModuleContextDTO context = new SysModuleContextDTO();
        context.setModuleId(moduleDetail.getId());
        context.setModuleCode(moduleDetail.getModuleCode());
        context.setModuleName(moduleDetail.getModuleName());
        context.setContextVersion(moduleDetail.getContextVersion());
        context.setStatefulFlag(moduleDetail.getStatefulFlag());
        context.setStateFieldCode(moduleDetail.getStateFieldCode());
        context.setState(currentState);
        context.setHeaderTabs(buildTabContextList(moduleDetail.getHeaderTabs(), fieldPermissionMap));
        context.setDetailTabs(buildTabContextList(moduleDetail.getDetailTabs(), fieldPermissionMap));
        context.setButtons(buildButtonContext(moduleDetail.getButtons(), buttonPermissionMap));
        return context;
    }

    private SysModuleContextDTO.CurrentState buildCurrentState(SysModuleDetailDTO moduleDetail, String stateCode) {
        if (!StringUtils.hasText(stateCode)) {
            return null;
        }
        if (!"1".equals(moduleDetail.getStatefulFlag())) {
            throw new BusinessException("Module is not stateful");
        }
        SysModuleStateDTO matchedState = resolveState(moduleDetail.getStates(), stateCode);
        if (matchedState == null) {
            throw new BusinessException("State code does not belong to the current module");
        }

        SysModuleContextDTO.CurrentState currentState = new SysModuleContextDTO.CurrentState();
        currentState.setCurrentValue(matchedState.getStateCode());
        currentState.setCurrentLabel(matchedState.getStateName());
        currentState.setStateField(moduleDetail.getStateFieldCode());
        return currentState;
    }

    private SysModuleStateDTO resolveState(List<SysModuleStateDTO> stateList, String stateCode) {
        if (stateList == null) {
            return null;
        }
        return stateList.stream()
                .filter(state -> stateCode.equals(state.getStateCode()))
                .findFirst()
                .orElse(null);
    }

    private Map<Long, Integer> buildFieldPermissionMap(SysModuleDetailDTO moduleDetail,
                                                       Long userId,
                                                       Long companyId,
                                                       String stateCode) {
        Map<Long, Integer> baselinePermissionMap = new LinkedHashMap<>();
        if (userId != null && companyId != null) {
            List<SysUserModuleFieldPermissionModel> permissionList = sysModulePermissionMapper
                    .selectUserFieldPermissionList(userId, companyId, moduleDetail.getId());
            if (permissionList != null) {
                permissionList.forEach(item ->
                        baselinePermissionMap.put(item.getFieldId(), normalizePermissionLevel(item.getPermissionLevel()))
                );
            }
        }
        if (!StringUtils.hasText(stateCode)) {
            return baselinePermissionMap;
        }

        Map<Long, Integer> statePermissionLimitMap = buildStateFieldPermissionLimitMap(moduleDetail.getId(), stateCode);
        Map<Long, Integer> result = new LinkedHashMap<>();
        flattenFields(moduleDetail).forEach(field -> {
            Integer baselinePermission = baselinePermissionMap.getOrDefault(field.getId(), HIDDEN_PERMISSION_LEVEL);
            Integer statePermission = statePermissionLimitMap.getOrDefault(field.getId(), FULL_PERMISSION_LEVEL);
            result.put(field.getId(), Math.min(baselinePermission, statePermission));
        });
        return result;
    }

    private Map<Long, Integer> buildButtonPermissionMap(SysModuleDetailDTO moduleDetail,
                                                        Long userId,
                                                        Long companyId,
                                                        String stateCode) {
        Map<Long, Integer> baselinePermissionMap = new LinkedHashMap<>();
        if (userId != null && companyId != null) {
            List<SysUserModuleButtonPermissionModel> permissionList = sysModulePermissionMapper
                    .selectUserButtonPermissionList(userId, companyId, moduleDetail.getId());
            if (permissionList != null) {
                permissionList.forEach(item ->
                        baselinePermissionMap.put(item.getButtonId(), normalizePermissionLevel(item.getPermissionLevel()))
                );
            }
        }
        if (!StringUtils.hasText(stateCode)) {
            return baselinePermissionMap;
        }

        Map<Long, Integer> statePermissionLimitMap = buildStateButtonPermissionLimitMap(moduleDetail.getId(), stateCode);
        Map<Long, Integer> result = new LinkedHashMap<>();
        List<SysModuleButtonDTO> buttonList = moduleDetail.getButtons() == null ? new ArrayList<>() : moduleDetail.getButtons();
        buttonList.forEach(button -> {
            Integer baselinePermission = baselinePermissionMap.getOrDefault(button.getId(), HIDDEN_PERMISSION_LEVEL);
            Integer statePermission = statePermissionLimitMap.getOrDefault(button.getId(), FULL_PERMISSION_LEVEL);
            result.put(button.getId(), Math.min(baselinePermission, statePermission));
        });
        return result;
    }

    private Map<Long, Integer> buildStateFieldPermissionLimitMap(Long moduleId, String stateCode) {
        Map<Long, Integer> permissionMap = new LinkedHashMap<>();
        List<SysModuleStateFieldAuthEntity> authList = sysModulePermissionMapper.selectStateFieldAuthList(moduleId);
        if (authList == null) {
            return permissionMap;
        }
        authList.stream()
                .filter(item -> stateCode.equals(item.getStateCode()))
                .forEach(item -> permissionMap.put(item.getFieldId(), normalizeStatePermissionLevel(item.getPermissionLevel())));
        return permissionMap;
    }

    private Map<Long, Integer> buildStateButtonPermissionLimitMap(Long moduleId, String stateCode) {
        Map<Long, Integer> permissionMap = new LinkedHashMap<>();
        List<SysModuleStateButtonAuthEntity> authList = sysModulePermissionMapper.selectStateButtonAuthList(moduleId);
        if (authList == null) {
            return permissionMap;
        }
        authList.stream()
                .filter(item -> stateCode.equals(item.getStateCode()))
                .forEach(item -> permissionMap.put(item.getButtonId(), normalizeStatePermissionLevel(item.getPermissionLevel())));
        return permissionMap;
    }

    private List<SysModuleContextDTO.TabContext> buildTabContextList(List<SysModuleTabDetailDTO> tabList,
                                                                     Map<Long, Integer> permissionMap) {
        List<SysModuleContextDTO.TabContext> result = new ArrayList<>();
        if (tabList == null) {
            return result;
        }
        tabList.forEach(tab -> {
            SysModuleContextDTO.TabContext tabContext = new SysModuleContextDTO.TabContext();
            tabContext.setTabInfo(tab.getTabInfo());
            List<SysModuleContextDTO.FieldContext> fieldContextList = buildFieldContextList(tab.getFields(), permissionMap);
            tabContext.setFields(fieldContextList);
            tabContext.setVisible(fieldContextList.stream().anyMatch(item -> Boolean.TRUE.equals(item.getVisible())));
            result.add(tabContext);
        });
        return result;
    }

    private List<SysModuleContextDTO.FieldContext> buildFieldContextList(List<SysModuleFieldDTO> fieldList,
                                                                         Map<Long, Integer> permissionMap) {
        List<SysModuleContextDTO.FieldContext> result = new ArrayList<>();
        if (fieldList == null) {
            return result;
        }
        fieldList.forEach(field -> {
            int permissionLevel = permissionMap.getOrDefault(field.getId(), HIDDEN_PERMISSION_LEVEL);
            String label = resolveFieldLabel(field);
            SysModuleContextDTO.FieldContext fieldContext = new SysModuleContextDTO.FieldContext();
            fieldContext.setFieldId(field.getId());
            fieldContext.setFieldCode(field.getFieldCode());
            fieldContext.setDefaultTitle(field.getDefaultTitle());
            fieldContext.setDisplayTitle(label);
            fieldContext.setLabel(label);
            fieldContext.setPlaceholder(resolvePlaceholder(field));
            fieldContext.setHelpText(resolveHelpText(field));
            fieldContext.setComponentType(field.getComponentType());
            fieldContext.setDataPath(field.getDataPath());
            fieldContext.setValueType(field.getValueType());
            fieldContext.setPermissionLevel(permissionLevel);
            fieldContext.setVisible(permissionLevel > HIDDEN_PERMISSION_LEVEL);
            fieldContext.setEditable(permissionLevel >= FULL_PERMISSION_LEVEL);
            fieldContext.setRequired("1".equals(field.getRequiredFlag()));
            result.add(fieldContext);
        });
        return result;
    }

    private SysModuleContextDTO.ButtonContext buildButtonContext(List<SysModuleButtonDTO> buttonList,
                                                                 Map<Long, Integer> permissionMap) {
        SysModuleContextDTO.ButtonContext context = new SysModuleContextDTO.ButtonContext();
        context.setListToolbar(new LinkedHashMap<>());
        context.setListRow(new LinkedHashMap<>());
        context.setHeaderToolbar(new LinkedHashMap<>());
        context.setDetailRow(new LinkedHashMap<>());
        if (buttonList == null) {
            return context;
        }
        buttonList.stream()
                .sorted(Comparator.comparing(SysModuleButtonDTO::getSort, Comparator.nullsLast(Integer::compareTo)))
                .forEach(button -> appendButtonContext(context, button, permissionMap.getOrDefault(button.getId(), HIDDEN_PERMISSION_LEVEL)));
        return context;
    }

    private void appendButtonContext(SysModuleContextDTO.ButtonContext context,
                                     SysModuleButtonDTO button,
                                     Integer permissionLevel) {
        SysModuleContextDTO.ButtonItem item = new SysModuleContextDTO.ButtonItem();
        item.setButtonId(button.getId());
        item.setButtonCode(button.getButtonCode());
        item.setDefaultTitle(button.getDefaultTitle());
        item.setLabel(button.getDefaultTitle());
        item.setArea(button.getArea());
        item.setPermissionLevel(permissionLevel);
        item.setVisible(permissionLevel > HIDDEN_PERMISSION_LEVEL);
        item.setDisabled(permissionLevel < FULL_PERMISSION_LEVEL);

        if ("LIST_TOOLBAR".equals(button.getArea())) {
            context.getListToolbar().put(button.getButtonCode(), item);
            return;
        }
        if ("LIST_ROW_BUTTON".equals(button.getArea())) {
            context.getListRow().put(button.getButtonCode(), item);
            return;
        }
        if ("HEADER_TOOLBAR".equals(button.getArea())) {
            context.getHeaderToolbar().put(button.getButtonCode(), item);
            return;
        }
        if ("DETAIL_ROW_BUTTON".equals(button.getArea())) {
            context.getDetailRow().put(button.getButtonCode(), item);
        }
    }

    private List<SysModuleFieldDTO> flattenFields(SysModuleDetailDTO moduleDetail) {
        List<SysModuleFieldDTO> fieldList = new ArrayList<>();
        appendTabFields(fieldList, moduleDetail.getHeaderTabs());
        appendTabFields(fieldList, moduleDetail.getDetailTabs());
        return fieldList;
    }

    private void appendTabFields(List<SysModuleFieldDTO> fieldList, List<SysModuleTabDetailDTO> tabList) {
        if (tabList == null) {
            return;
        }
        tabList.forEach(tab -> {
            if (tab.getFields() != null) {
                fieldList.addAll(tab.getFields());
            }
        });
    }

    private String resolveFieldLabel(SysModuleFieldDTO field) {
        if (StringUtils.hasText(field.getDisplayTitle())) {
            return field.getDisplayTitle();
        }
        return field.getDefaultTitle();
    }

    private String resolvePlaceholder(SysModuleFieldDTO field) {
        if (StringUtils.hasText(field.getPlaceholder())) {
            return field.getPlaceholder();
        }
        if ("tag".equals(field.getComponentType()) || "text".equals(field.getComponentType())) {
            return "";
        }
        if ("date".equals(field.getComponentType())
                || "datetime".equals(field.getComponentType())
                || "search-select".equals(field.getComponentType())) {
            return "\u8bf7\u9009\u62e9" + field.getDefaultTitle();
        }
        return "\u8bf7\u8f93\u5165" + field.getDefaultTitle();
    }

    private String resolveHelpText(SysModuleFieldDTO field) {
        if (StringUtils.hasText(field.getHelpText())) {
            return field.getHelpText();
        }
        if (StringUtils.hasText(field.getNote())) {
            return field.getNote();
        }
        if ("1".equals(field.getRequiredFlag())) {
            return field.getDefaultTitle() + "\u4e3a\u5fc5\u586b\u9879\uff0c\u8bf7\u6309\u4e1a\u52a1\u89c4\u8303\u7ef4\u62a4\u3002";
        }
        return field.getDefaultTitle() + "\u7528\u4e8e\u8865\u5145\u5f53\u524d\u4e1a\u52a1\u5355\u636e\u4fe1\u606f\u3002";
    }

    private Integer normalizePermissionLevel(Integer permissionLevel) {
        if (permissionLevel == null || permissionLevel < HIDDEN_PERMISSION_LEVEL) {
            return HIDDEN_PERMISSION_LEVEL;
        }
        return Math.min(permissionLevel, FULL_PERMISSION_LEVEL);
    }

    private Integer normalizeStatePermissionLevel(Integer permissionLevel) {
        if (permissionLevel == null || permissionLevel < HIDDEN_PERMISSION_LEVEL) {
            return FULL_PERMISSION_LEVEL;
        }
        return Math.min(permissionLevel, FULL_PERMISSION_LEVEL);
    }

}

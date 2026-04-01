package com.soli.system.core.service.impl.sysmodule;

import com.soli.common.api.enums.BinaryFlagEnum;
import com.soli.common.api.exception.BusinessException;
import com.soli.system.core.mapper.SysModuleMapper;
import com.soli.system.core.mapper.SysModulePermissionMapper;
import com.soli.system.core.service.impl.sysmodulepermission.SysModuleStateButtonAuthEntity;
import com.soli.system.core.service.impl.sysmodulepermission.SysModuleStateFieldAuthEntity;
import com.soli.system.core.service.impl.sysmodulepermission.SysUserModuleButtonPermissionModel;
import com.soli.system.core.service.impl.sysmodulepermission.SysUserModuleFieldPermissionModel;
import com.soli.system.service.sysmodule.SysModuleButtonDTO;
import com.soli.system.service.sysmodule.SysModuleComponentDetailDTO;
import com.soli.system.service.sysmodule.SysModuleContextDTO;
import com.soli.system.service.sysmodule.SysModuleContextService;
import com.soli.system.service.sysmodule.SysModuleDetailDTO;
import com.soli.system.service.sysmodule.SysModuleFieldDTO;
import com.soli.system.service.sysmodule.SysModuleService;
import com.soli.system.service.sysmodule.SysModuleStateDTO;
import com.soli.system.service.enums.ModuleComponentTypeEnum;
import com.soli.system.service.enums.PermissionLevelEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 模块上下文服务实现
 *
 * @author lizhengqiang
 * @since 2026-03-27 16:40
 */
@Service
@RequiredArgsConstructor
public class SysModuleContextServiceImpl implements SysModuleContextService {

    private static final int HIDDEN_PERMISSION_LEVEL = 0;

    private static final int FULL_PERMISSION_LEVEL = 2;

    private static final String FIELD_CONFIG_TYPE = "field";

    private static final String BUTTON_CONFIG_TYPE = "button";

    private static final String BUTTON_COMPONENT = "button";

    private final SysModuleMapper sysModuleMapper;

    private final SysModulePermissionMapper sysModulePermissionMapper;

    private final SysModuleService sysModuleService;

    @Override
    public SysModuleContextDTO buildContext(String moduleCode, Long userId, Long companyId, String stateCode) {
        if (!StringUtils.hasText(moduleCode)) {
            throw new BusinessException("模块编码不能为空");
        }
        SysModuleEntity moduleEntity = sysModuleMapper.selectByModuleCode(moduleCode.trim());
        if (moduleEntity == null) {
            throw new BusinessException("模块不存在");
        }

        SysModuleDetailDTO moduleDetail = sysModuleService.queryDetailById(moduleEntity.getId());
        String currentStateCode = StringUtils.hasText(stateCode) ? stateCode.trim() : null;
        SysModuleContextDTO.CurrentState currentState = buildCurrentState(moduleDetail, currentStateCode);
        Map<Long, Integer> fieldPermissionMap = buildFieldPermissionMap(moduleDetail, userId, companyId, currentStateCode);
        Map<Long, Integer> buttonPermissionMap = buildButtonPermissionMap(moduleDetail, userId, companyId, currentStateCode);

        Map<String, SysModuleContextDTO.FieldConfig> fieldConfigs = buildFieldConfigs(moduleDetail, fieldPermissionMap, buttonPermissionMap);

        SysModuleContextDTO context = new SysModuleContextDTO();
        context.setModuleId(moduleDetail.getId());
        context.setModuleCode(moduleDetail.getModuleCode());
        context.setModuleName(moduleDetail.getModuleName());
        context.setContextVersion(moduleDetail.getContextVersion());
        context.setStatefulFlag(moduleDetail.getStatefulFlag());
        context.setStateFieldCode(moduleDetail.getStateFieldCode());
        context.setState(currentState);
        context.setFieldConfigs(fieldConfigs);
        return context;
    }

    private SysModuleContextDTO.CurrentState buildCurrentState(SysModuleDetailDTO moduleDetail, String stateCode) {
        if (!StringUtils.hasText(stateCode)) {
            return null;
        }
        if (BinaryFlagEnum.YES != moduleDetail.getStatefulFlag()) {
            throw new BusinessException("当前模块不是状态型模块");
        }
        SysModuleStateDTO matchedState = resolveState(moduleDetail.getStates(), stateCode);
        if (matchedState == null) {
            throw new BusinessException("状态编码不属于当前模块");
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

    private Map<String, SysModuleContextDTO.FieldConfig> buildFieldConfigs(SysModuleDetailDTO moduleDetail,
                                                                           Map<Long, Integer> fieldPermissionMap,
                                                                           Map<Long, Integer> buttonPermissionMap) {
        Map<String, SysModuleContextDTO.FieldConfig> fieldConfigs = new LinkedHashMap<>();
        appendFieldConfigs(fieldConfigs, moduleDetail.getComponents(), fieldPermissionMap);
        appendButtonConfigs(fieldConfigs, moduleDetail.getButtons(), buttonPermissionMap);
        return fieldConfigs;
    }

    private void appendFieldConfigs(Map<String, SysModuleContextDTO.FieldConfig> fieldConfigs,
                                    List<SysModuleComponentDetailDTO> componentList,
                                    Map<Long, Integer> permissionMap) {
        if (componentList == null) {
            return;
        }
        componentList.forEach(component -> {
            if (component.getFields() == null) {
                return;
            }
            component.getFields().forEach(field -> appendFieldConfig(fieldConfigs, field, permissionMap.getOrDefault(field.getId(), HIDDEN_PERMISSION_LEVEL)));
        });
    }

    private void appendFieldConfig(Map<String, SysModuleContextDTO.FieldConfig> fieldConfigs,
                                   SysModuleFieldDTO field,
                                   Integer permissionLevel) {
        String label = resolveFieldLabel(field);
        String configKey = resolveFieldConfigKey(field);
        SysModuleContextDTO.FieldConfig fieldConfig = new SysModuleContextDTO.FieldConfig();
        fieldConfig.setConfigKey(configKey);
        fieldConfig.setConfigType(FIELD_CONFIG_TYPE);
        fieldConfig.setComponent(resolveFieldComponent(field));
        fieldConfig.setCode(field.getFieldCode());
        fieldConfig.setFieldId(field.getId());
        fieldConfig.setDefaultTitle(field.getDefaultTitle());
        fieldConfig.setDisplayTitle(label);
        fieldConfig.setLabel(label);
        fieldConfig.setPlaceholder(resolvePlaceholder(field, label));
        fieldConfig.setHelpText(resolveHelpText(field, label));
        fieldConfig.setComponentType(field.getComponentType());
        fieldConfig.setDataPath(field.getDataPath());
        fieldConfig.setValueType(field.getValueType());
        fieldConfig.setPermissionLevel(toPermissionLevel(permissionLevel));
        fieldConfig.setVisible(permissionLevel > HIDDEN_PERMISSION_LEVEL);
        fieldConfig.setEditable(permissionLevel >= FULL_PERMISSION_LEVEL);
        fieldConfig.setRequired(BinaryFlagEnum.YES == field.getRequiredFlag());
        fieldConfig.setDisabled(false);
        fieldConfigs.put(configKey, fieldConfig);
    }

    private void appendButtonConfigs(Map<String, SysModuleContextDTO.FieldConfig> fieldConfigs,
                                     List<SysModuleButtonDTO> buttonList,
                                     Map<Long, Integer> permissionMap) {
        if (buttonList == null) {
            return;
        }
        buttonList.stream()
                .sorted(Comparator.comparing(SysModuleButtonDTO::getSort, Comparator.nullsLast(Integer::compareTo)))
                .forEach(button -> appendButtonConfig(fieldConfigs, button, permissionMap.getOrDefault(button.getId(), HIDDEN_PERMISSION_LEVEL)));
    }

    private void appendButtonConfig(Map<String, SysModuleContextDTO.FieldConfig> fieldConfigs,
                                    SysModuleButtonDTO button,
                                    Integer permissionLevel) {
        String configKey = resolveButtonConfigKey(button.getButtonCode());
        SysModuleContextDTO.FieldConfig buttonConfig = new SysModuleContextDTO.FieldConfig();
        buttonConfig.setConfigKey(configKey);
        buttonConfig.setConfigType(BUTTON_CONFIG_TYPE);
        buttonConfig.setComponent(BUTTON_COMPONENT);
        buttonConfig.setCode(button.getButtonCode());
        buttonConfig.setButtonId(button.getId());
        buttonConfig.setDefaultTitle(button.getDefaultTitle());
        buttonConfig.setDisplayTitle(button.getDefaultTitle());
        buttonConfig.setLabel(button.getDefaultTitle());
        buttonConfig.setComponentType(ModuleComponentTypeEnum.BUTTON);
        buttonConfig.setDataPath(BUTTON_COMPONENT + "." + button.getButtonCode());
        buttonConfig.setPermissionLevel(toPermissionLevel(permissionLevel));
        buttonConfig.setVisible(permissionLevel > HIDDEN_PERMISSION_LEVEL);
        buttonConfig.setEditable(permissionLevel >= FULL_PERMISSION_LEVEL);
        buttonConfig.setRequired(false);
        buttonConfig.setDisabled(permissionLevel < FULL_PERMISSION_LEVEL);
        fieldConfigs.put(configKey, buttonConfig);
    }

    private String resolveFieldConfigKey(SysModuleFieldDTO field) {
        return resolveFieldComponent(field) + ":" + field.getFieldCode();
    }

    private String resolveButtonConfigKey(String buttonCode) {
        return BUTTON_COMPONENT + ":" + buttonCode;
    }

    private String resolveFieldComponent(SysModuleFieldDTO field) {
        if (field != null && StringUtils.hasText(field.getComponentCode())) {
            return field.getComponentCode().trim();
        }
        if (field != null && StringUtils.hasText(field.getDataPath())) {
            int separatorIndex = field.getDataPath().indexOf('.');
            if (separatorIndex > 0) {
                return field.getDataPath().substring(0, separatorIndex);
            }
            return field.getDataPath().trim();
        }
        return "field";
    }

    private List<SysModuleFieldDTO> flattenFields(SysModuleDetailDTO moduleDetail) {
        List<SysModuleFieldDTO> fieldList = new ArrayList<>();
        appendComponentFields(fieldList, moduleDetail.getComponents());
        return fieldList;
    }

    private void appendComponentFields(List<SysModuleFieldDTO> fieldList, List<SysModuleComponentDetailDTO> componentList) {
        if (componentList == null) {
            return;
        }
        componentList.forEach(component -> {
            if (component.getFields() != null) {
                fieldList.addAll(component.getFields());
            }
        });
    }

    private String resolveFieldLabel(SysModuleFieldDTO field) {
        if (StringUtils.hasText(field.getDisplayTitle())) {
            return field.getDisplayTitle();
        }
        return field.getDefaultTitle();
    }

    private String resolvePlaceholder(SysModuleFieldDTO field, String label) {
        if (StringUtils.hasText(field.getPlaceholder())) {
            return field.getPlaceholder();
        }
        if (field.getComponentType() == ModuleComponentTypeEnum.TAG
                || field.getComponentType() == ModuleComponentTypeEnum.TEXT
                || field.getComponentType() == ModuleComponentTypeEnum.SWITCH) {
            return "";
        }
        if (field.getComponentType() == ModuleComponentTypeEnum.DATE
                || field.getComponentType() == ModuleComponentTypeEnum.DATETIME
                || field.getComponentType() == ModuleComponentTypeEnum.SEARCH_SELECT
                || field.getComponentType() == ModuleComponentTypeEnum.SELECT
                || field.getComponentType() == ModuleComponentTypeEnum.RADIO) {
            return "请选择" + label;
        }
        return "请输入" + label;
    }

    private String resolveHelpText(SysModuleFieldDTO field, String label) {
        if (StringUtils.hasText(field.getHelpText())) {
            return field.getHelpText();
        }
        if (StringUtils.hasText(field.getNote())) {
            return field.getNote();
        }
        if (BinaryFlagEnum.YES == field.getRequiredFlag()) {
            return label + "为必填项，请按业务规范维护。";
        }
        return label + "用于补充当前业务单据信息。";
    }

    private PermissionLevelEnum toPermissionLevel(Integer permissionLevel) {
        return switch (normalizePermissionLevel(permissionLevel)) {
            case 1 -> PermissionLevelEnum.LEVEL_1;
            case 2 -> PermissionLevelEnum.LEVEL_2;
            default -> PermissionLevelEnum.LEVEL_0;
        };
    }

    private Integer normalizePermissionLevel(PermissionLevelEnum permissionLevel) {
        return normalizePermissionLevel(permissionLevel == null ? null : permissionLevel.getValue());
    }

    private Integer normalizePermissionLevel(Integer permissionLevel) {
        if (permissionLevel == null || permissionLevel < HIDDEN_PERMISSION_LEVEL) {
            return HIDDEN_PERMISSION_LEVEL;
        }
        return Math.min(permissionLevel, FULL_PERMISSION_LEVEL);
    }

    private Integer normalizeStatePermissionLevel(PermissionLevelEnum permissionLevel) {
        return normalizeStatePermissionLevel(permissionLevel == null ? null : permissionLevel.getValue());
    }

    private Integer normalizeStatePermissionLevel(Integer permissionLevel) {
        if (permissionLevel == null || permissionLevel < HIDDEN_PERMISSION_LEVEL) {
            return FULL_PERMISSION_LEVEL;
        }
        return Math.min(permissionLevel, FULL_PERMISSION_LEVEL);
    }

}

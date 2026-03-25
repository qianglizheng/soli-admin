package com.soli.system.core.service.impl.sysstateauth;

import com.github.yitter.idgen.YitIdHelper;
import com.soli.common.api.exception.BusinessException;
import com.soli.system.core.mapper.SysModuleMapper;
import com.soli.system.core.mapper.SysModulePermissionMapper;
import com.soli.system.core.service.impl.sysmodulepermission.SysModuleStateButtonAuthEntity;
import com.soli.system.core.service.impl.sysmodulepermission.SysModuleStateFieldAuthEntity;
import com.soli.system.service.sysmodule.SysModuleButtonDTO;
import com.soli.system.service.sysmodule.SysModuleDetailDTO;
import com.soli.system.service.sysmodule.SysModuleFieldDTO;
import com.soli.system.service.sysmodule.SysModuleService;
import com.soli.system.service.sysmodule.SysModuleStateDTO;
import com.soli.system.service.sysmodule.SysModuleTabDetailDTO;
import com.soli.system.service.sysmodule.SysModuleTreeNodeDTO;
import com.soli.system.service.sysstateauth.SysStateAuthConfigDTO;
import com.soli.system.service.sysstateauth.SysStateAuthPageDetailDTO;
import com.soli.system.service.sysstateauth.SysStateAuthSaveRequest;
import com.soli.system.service.sysstateauth.SysStateAuthService;
import com.soli.system.service.sysstateauth.SysStateButtonPermissionDTO;
import com.soli.system.service.sysstateauth.SysStateFieldPermissionDTO;
import com.soli.system.service.sysstateauth.SysStatePermissionByStateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 状态权限服务实现
 *
 * @author lizhengqiang
 * @since 2026-03-25 11:15
 */
@Service
@RequiredArgsConstructor
public class SysStateAuthServiceImpl implements SysStateAuthService {

    private final SysModuleService sysModuleService;

    private final SysModuleMapper sysModuleMapper;

    private final SysModulePermissionMapper sysModulePermissionMapper;

    @Override
    public List<SysModuleTreeNodeDTO> queryModuleTree() {
        return filterStatefulNodes(sysModuleService.queryTreeList());
    }

    @Override
    public SysStateAuthPageDetailDTO queryPageDetail(Long moduleId) {
        SysModuleDetailDTO moduleDetail = sysModuleService.queryDetailById(moduleId);
        validateStatefulModule(moduleDetail);
        SysStateAuthPageDetailDTO detail = new SysStateAuthPageDetailDTO();
        detail.setModuleDetail(moduleDetail);
        detail.setConfig(buildConfig(moduleDetail));
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SysStateAuthSaveRequest request) {
        if (request == null) {
            throw new BusinessException("保存请求不能为空");
        }
        SysModuleDetailDTO moduleDetail = sysModuleService.queryDetailById(request.getModuleId());
        validateStatefulModule(moduleDetail);
        List<SysModuleFieldDTO> fieldList = flattenFields(moduleDetail);
        List<SysModuleButtonDTO> buttonList = moduleDetail.getButtons() == null ? new ArrayList<>() : moduleDetail.getButtons();
        Set<Long> validFieldIdSet = fieldList.stream().map(SysModuleFieldDTO::getId).collect(Collectors.toSet());
        Set<Long> validButtonIdSet = buttonList.stream().map(SysModuleButtonDTO::getId).collect(Collectors.toSet());
        Set<String> validStateCodeSet = (moduleDetail.getStates() == null ? new ArrayList<SysModuleStateDTO>() : moduleDetail.getStates())
                .stream()
                .map(SysModuleStateDTO::getStateCode)
                .collect(Collectors.toSet());

        LocalDateTime now = LocalDateTime.now();
        sysModulePermissionMapper.deleteStateFieldAuthList(request.getModuleId());
        sysModulePermissionMapper.deleteStateButtonAuthList(request.getModuleId());

        List<SysModuleStateFieldAuthEntity> fieldAuthEntityList = new ArrayList<>();
        List<SysModuleStateButtonAuthEntity> buttonAuthEntityList = new ArrayList<>();
        if (request.getPermissionsByState() != null) {
            request.getPermissionsByState().forEach(item -> {
                if (item == null || item.getStateCode() == null) {
                    return;
                }
                if (!validStateCodeSet.contains(item.getStateCode())) {
                    throw new BusinessException("状态编码不属于当前模块");
                }
                appendStateFieldAuth(fieldAuthEntityList, request.getModuleId(), item, validFieldIdSet, now);
                appendStateButtonAuth(buttonAuthEntityList, request.getModuleId(), item, validButtonIdSet, now);
            });
        }

        if (!fieldAuthEntityList.isEmpty()) {
            sysModulePermissionMapper.insertStateFieldAuthBatch(fieldAuthEntityList);
        }
        if (!buttonAuthEntityList.isEmpty()) {
            sysModulePermissionMapper.insertStateButtonAuthBatch(buttonAuthEntityList);
        }
        sysModuleMapper.incrementContextVersion(request.getModuleId(), now);
    }

    private SysStateAuthConfigDTO buildConfig(SysModuleDetailDTO moduleDetail) {
        Map<String, Map<Long, Integer>> fieldPermissionMap = new LinkedHashMap<>();
        sysModulePermissionMapper.selectStateFieldAuthList(moduleDetail.getId()).forEach(item ->
                fieldPermissionMap.computeIfAbsent(item.getStateCode(), key -> new LinkedHashMap<>()).put(item.getFieldId(), item.getPermissionLevel())
        );
        Map<String, Map<Long, Integer>> buttonPermissionMap = new LinkedHashMap<>();
        sysModulePermissionMapper.selectStateButtonAuthList(moduleDetail.getId()).forEach(item ->
                buttonPermissionMap.computeIfAbsent(item.getStateCode(), key -> new LinkedHashMap<>()).put(item.getButtonId(), item.getPermissionLevel())
        );

        List<SysModuleFieldDTO> fieldList = flattenFields(moduleDetail);
        List<SysModuleButtonDTO> buttonList = moduleDetail.getButtons() == null ? new ArrayList<>() : moduleDetail.getButtons();
        List<SysStatePermissionByStateDTO> permissionByStateList = new ArrayList<>();
        if (moduleDetail.getStates() != null) {
            moduleDetail.getStates().forEach(state -> {
                SysStatePermissionByStateDTO item = new SysStatePermissionByStateDTO();
                item.setStateCode(state.getStateCode());
                Map<Long, Integer> currentFieldPermissionMap = fieldPermissionMap.getOrDefault(state.getStateCode(), new LinkedHashMap<>());
                item.setFieldPermissions(fieldList.stream().map(field -> {
                    SysStateFieldPermissionDTO fieldPermissionDTO = new SysStateFieldPermissionDTO();
                    fieldPermissionDTO.setFieldId(field.getId());
                    fieldPermissionDTO.setPermissionLevel(normalizeStatePermissionLevel(currentFieldPermissionMap.get(field.getId())));
                    return fieldPermissionDTO;
                }).toList());
                Map<Long, Integer> currentButtonPermissionMap = buttonPermissionMap.getOrDefault(state.getStateCode(), new LinkedHashMap<>());
                item.setButtonPermissions(buttonList.stream().map(button -> {
                    SysStateButtonPermissionDTO buttonPermissionDTO = new SysStateButtonPermissionDTO();
                    buttonPermissionDTO.setButtonId(button.getId());
                    buttonPermissionDTO.setPermissionLevel(normalizeStatePermissionLevel(currentButtonPermissionMap.get(button.getId())));
                    return buttonPermissionDTO;
                }).toList());
                permissionByStateList.add(item);
            });
        }

        SysStateAuthConfigDTO config = new SysStateAuthConfigDTO();
        config.setModuleId(moduleDetail.getId());
        config.setPermissionsByState(permissionByStateList);
        return config;
    }

    private void appendStateFieldAuth(List<SysModuleStateFieldAuthEntity> entityList,
                                      Long moduleId,
                                      SysStatePermissionByStateDTO item,
                                      Set<Long> validFieldIdSet,
                                      LocalDateTime now) {
        if (item.getFieldPermissions() == null) {
            return;
        }
        item.getFieldPermissions().forEach(fieldPermission -> {
            if (fieldPermission.getFieldId() == null || !validFieldIdSet.contains(fieldPermission.getFieldId())) {
                return;
            }
            Integer permissionLevel = normalizeStatePermissionLevel(fieldPermission.getPermissionLevel());
            if (permissionLevel >= 2) {
                return;
            }
            SysModuleStateFieldAuthEntity entity = new SysModuleStateFieldAuthEntity();
            entity.setId(YitIdHelper.nextId());
            entity.setModuleId(moduleId);
            entity.setStateCode(item.getStateCode());
            entity.setFieldId(fieldPermission.getFieldId());
            entity.setPermissionLevel(permissionLevel);
            entity.setCreateBy("system");
            entity.setCreateTime(now);
            entityList.add(entity);
        });
    }

    private void appendStateButtonAuth(List<SysModuleStateButtonAuthEntity> entityList,
                                       Long moduleId,
                                       SysStatePermissionByStateDTO item,
                                       Set<Long> validButtonIdSet,
                                       LocalDateTime now) {
        if (item.getButtonPermissions() == null) {
            return;
        }
        item.getButtonPermissions().forEach(buttonPermission -> {
            if (buttonPermission.getButtonId() == null || !validButtonIdSet.contains(buttonPermission.getButtonId())) {
                return;
            }
            Integer permissionLevel = normalizeStatePermissionLevel(buttonPermission.getPermissionLevel());
            if (permissionLevel >= 2) {
                return;
            }
            SysModuleStateButtonAuthEntity entity = new SysModuleStateButtonAuthEntity();
            entity.setId(YitIdHelper.nextId());
            entity.setModuleId(moduleId);
            entity.setStateCode(item.getStateCode());
            entity.setButtonId(buttonPermission.getButtonId());
            entity.setPermissionLevel(permissionLevel);
            entity.setCreateBy("system");
            entity.setCreateTime(now);
            entityList.add(entity);
        });
    }

    private List<SysModuleTreeNodeDTO> filterStatefulNodes(List<SysModuleTreeNodeDTO> nodeList) {
        List<SysModuleTreeNodeDTO> result = new ArrayList<>();
        nodeList.forEach(node -> {
            List<SysModuleTreeNodeDTO> children = node.getChildren() == null ? new ArrayList<>() : filterStatefulNodes(node.getChildren());
            if ("CATALOG".equals(node.getModuleType())) {
                if (!children.isEmpty()) {
                    SysModuleTreeNodeDTO copied = copyTreeNode(node);
                    copied.setChildren(children);
                    result.add(copied);
                }
                return;
            }
            if ("1".equals(node.getStatefulFlag())) {
                SysModuleTreeNodeDTO copied = copyTreeNode(node);
                copied.setChildren(new ArrayList<>());
                result.add(copied);
            }
        });
        return result;
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

    private void validateStatefulModule(SysModuleDetailDTO moduleDetail) {
        if (!"1".equals(moduleDetail.getStatefulFlag())) {
            throw new BusinessException("当前模块不是状态型模块");
        }
    }

    private Integer normalizeStatePermissionLevel(Integer permissionLevel) {
        if (permissionLevel == null || permissionLevel < 0) {
            return 2;
        }
        return Math.min(permissionLevel, 2);
    }

}

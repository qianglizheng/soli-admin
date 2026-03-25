package com.soli.system.core.service.impl.sysfunctionauth;

import com.github.yitter.idgen.YitIdHelper;
import com.soli.common.api.exception.BusinessException;
import com.soli.system.core.mapper.SysModuleMapper;
import com.soli.system.core.mapper.SysModulePermissionMapper;
import com.soli.system.core.service.impl.sysmodulepermission.SysOrgPostButtonAuthEntity;
import com.soli.system.core.service.impl.sysmodulepermission.SysOrgPostFieldAuthEntity;
import com.soli.system.core.service.impl.sysmodulepermission.SysOrgPostModuleAuthEntity;
import com.soli.system.service.sysfunctionauth.SysFunctionAuthButtonPermissionDTO;
import com.soli.system.service.sysfunctionauth.SysFunctionAuthConfigDTO;
import com.soli.system.service.sysfunctionauth.SysFunctionAuthFieldPermissionDTO;
import com.soli.system.service.sysfunctionauth.SysFunctionAuthPageDetailDTO;
import com.soli.system.service.sysfunctionauth.SysFunctionAuthSaveRequest;
import com.soli.system.service.sysfunctionauth.SysFunctionAuthService;
import com.soli.system.service.sysmodule.SysModuleButtonDTO;
import com.soli.system.service.sysmodule.SysModuleDetailDTO;
import com.soli.system.service.sysmodule.SysModuleFieldDTO;
import com.soli.system.service.sysmodule.SysModuleService;
import com.soli.system.service.sysmodule.SysModuleTabDetailDTO;
import com.soli.system.service.sysmodule.SysModuleTreeNodeDTO;
import com.soli.system.service.sysorgpost.SysOrgPostService;
import com.soli.system.service.sysorgpost.SysOrgPostTreeNodeDTO;
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
 * 功能授权服务实现
 *
 * @author lizhengqiang
 * @since 2026-03-25 11:15
 */
@Service
@RequiredArgsConstructor
public class SysFunctionAuthServiceImpl implements SysFunctionAuthService {

    private final SysOrgPostService sysOrgPostService;

    private final SysModuleService sysModuleService;

    private final SysModuleMapper sysModuleMapper;

    private final SysModulePermissionMapper sysModulePermissionMapper;

    @Override
    public List<SysOrgPostTreeNodeDTO> queryOrgPostTree() {
        return sysOrgPostService.queryTreeList();
    }

    @Override
    public List<SysModuleTreeNodeDTO> queryModuleTree() {
        return sysModuleService.queryTreeList();
    }

    @Override
    public SysFunctionAuthPageDetailDTO queryPageDetail(Long orgPostId, Long moduleId) {
        SysFunctionAuthPageDetailDTO detail = new SysFunctionAuthPageDetailDTO();
        detail.setVisibleModuleCount(sysModulePermissionMapper.countVisibleModuleByOrgPostId(orgPostId));
        detail.setPostDetail(sysOrgPostService.queryDetailById(orgPostId));
        SysModuleDetailDTO moduleDetail = sysModuleService.queryDetailById(moduleId);
        detail.setModuleDetail(moduleDetail);
        detail.setConfig(buildConfig(orgPostId, moduleDetail));
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SysFunctionAuthSaveRequest request) {
        if (request == null) {
            throw new BusinessException("保存请求不能为空");
        }
        sysOrgPostService.queryDetailById(request.getOrgPostId());
        SysModuleDetailDTO moduleDetail = sysModuleService.queryDetailById(request.getModuleId());
        List<SysModuleFieldDTO> fieldList = flattenFields(moduleDetail);
        List<SysModuleButtonDTO> buttonList = moduleDetail.getButtons() == null ? new ArrayList<>() : moduleDetail.getButtons();
        Set<Long> validFieldIdSet = fieldList.stream().map(SysModuleFieldDTO::getId).collect(Collectors.toSet());
        Set<Long> validButtonIdSet = buttonList.stream().map(SysModuleButtonDTO::getId).collect(Collectors.toSet());

        Map<Long, Integer> requestFieldPermissionMap = new LinkedHashMap<>();
        if (request.getFieldPermissions() != null) {
            request.getFieldPermissions().forEach(item -> {
                if (item.getFieldId() != null && validFieldIdSet.contains(item.getFieldId())) {
                    requestFieldPermissionMap.put(item.getFieldId(), normalizePermissionLevel(item.getPermissionLevel()));
                }
            });
        }

        Map<Long, Integer> requestButtonPermissionMap = new LinkedHashMap<>();
        if (request.getButtonPermissions() != null) {
            request.getButtonPermissions().forEach(item -> {
                if (item.getButtonId() != null && validButtonIdSet.contains(item.getButtonId())) {
                    requestButtonPermissionMap.put(item.getButtonId(), normalizePermissionLevel(item.getPermissionLevel()));
                }
            });
        }

        LocalDateTime now = LocalDateTime.now();
        sysModulePermissionMapper.deleteOrgPostModuleAuth(request.getOrgPostId(), request.getModuleId());
        sysModulePermissionMapper.deleteOrgPostFieldAuthList(request.getOrgPostId(), request.getModuleId());
        sysModulePermissionMapper.deleteOrgPostButtonAuthList(request.getOrgPostId(), request.getModuleId());

        SysOrgPostModuleAuthEntity moduleAuthEntity = new SysOrgPostModuleAuthEntity();
        moduleAuthEntity.setId(YitIdHelper.nextId());
        moduleAuthEntity.setOrgPostId(request.getOrgPostId());
        moduleAuthEntity.setModuleId(request.getModuleId());
        moduleAuthEntity.setModuleVisible(Boolean.TRUE.equals(request.getModuleVisible()) ? "1" : "0");
        moduleAuthEntity.setNavVisible(Boolean.TRUE.equals(request.getModuleVisible()) && Boolean.TRUE.equals(request.getNavVisible()) ? "1" : "0");
        moduleAuthEntity.setCreateBy("system");
        moduleAuthEntity.setCreateTime(now);
        sysModulePermissionMapper.insertOrgPostModuleAuth(moduleAuthEntity);

        List<SysOrgPostFieldAuthEntity> fieldAuthEntityList = fieldList.stream().map(field -> {
            SysOrgPostFieldAuthEntity entity = new SysOrgPostFieldAuthEntity();
            entity.setId(YitIdHelper.nextId());
            entity.setOrgPostId(request.getOrgPostId());
            entity.setModuleId(request.getModuleId());
            entity.setFieldId(field.getId());
            entity.setPermissionLevel(requestFieldPermissionMap.getOrDefault(field.getId(), 0));
            entity.setCreateBy("system");
            entity.setCreateTime(now);
            return entity;
        }).toList();
        if (!fieldAuthEntityList.isEmpty()) {
            sysModulePermissionMapper.insertOrgPostFieldAuthBatch(fieldAuthEntityList);
        }

        List<SysOrgPostButtonAuthEntity> buttonAuthEntityList = buttonList.stream().map(button -> {
            SysOrgPostButtonAuthEntity entity = new SysOrgPostButtonAuthEntity();
            entity.setId(YitIdHelper.nextId());
            entity.setOrgPostId(request.getOrgPostId());
            entity.setModuleId(request.getModuleId());
            entity.setButtonId(button.getId());
            entity.setPermissionLevel(requestButtonPermissionMap.getOrDefault(button.getId(), 0));
            entity.setCreateBy("system");
            entity.setCreateTime(now);
            return entity;
        }).toList();
        if (!buttonAuthEntityList.isEmpty()) {
            sysModulePermissionMapper.insertOrgPostButtonAuthBatch(buttonAuthEntityList);
        }

        sysModuleMapper.incrementContextVersion(request.getModuleId(), now);
    }

    private SysFunctionAuthConfigDTO buildConfig(Long orgPostId, SysModuleDetailDTO moduleDetail) {
        SysOrgPostModuleAuthEntity moduleAuthEntity = sysModulePermissionMapper.selectOrgPostModuleAuth(orgPostId, moduleDetail.getId());
        Map<Long, Integer> fieldPermissionMap = sysModulePermissionMapper.selectOrgPostFieldAuthList(orgPostId, moduleDetail.getId())
                .stream()
                .collect(Collectors.toMap(SysOrgPostFieldAuthEntity::getFieldId, SysOrgPostFieldAuthEntity::getPermissionLevel, (left, right) -> right, LinkedHashMap::new));
        Map<Long, Integer> buttonPermissionMap = sysModulePermissionMapper.selectOrgPostButtonAuthList(orgPostId, moduleDetail.getId())
                .stream()
                .collect(Collectors.toMap(SysOrgPostButtonAuthEntity::getButtonId, SysOrgPostButtonAuthEntity::getPermissionLevel, (left, right) -> right, LinkedHashMap::new));

        SysFunctionAuthConfigDTO config = new SysFunctionAuthConfigDTO();
        config.setOrgPostId(orgPostId);
        config.setModuleId(moduleDetail.getId());
        config.setModuleVisible(moduleAuthEntity != null && "1".equals(moduleAuthEntity.getModuleVisible()));
        config.setNavVisible(moduleAuthEntity != null && "1".equals(moduleAuthEntity.getNavVisible()));
        config.setFieldPermissions(flattenFields(moduleDetail).stream().map(field -> {
            SysFunctionAuthFieldPermissionDTO item = new SysFunctionAuthFieldPermissionDTO();
            item.setFieldId(field.getId());
            item.setPermissionLevel(fieldPermissionMap.getOrDefault(field.getId(), 0));
            return item;
        }).toList());
        config.setButtonPermissions((moduleDetail.getButtons() == null ? new ArrayList<SysModuleButtonDTO>() : moduleDetail.getButtons()).stream().map(button -> {
            SysFunctionAuthButtonPermissionDTO item = new SysFunctionAuthButtonPermissionDTO();
            item.setButtonId(button.getId());
            item.setPermissionLevel(buttonPermissionMap.getOrDefault(button.getId(), 0));
            return item;
        }).toList());
        return config;
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

    private Integer normalizePermissionLevel(Integer permissionLevel) {
        if (permissionLevel == null || permissionLevel < 0) {
            return 0;
        }
        return Math.min(permissionLevel, 2);
    }

}

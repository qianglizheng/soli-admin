package com.soli.system.core.service.impl.sysmoduletitle;

import com.github.yitter.idgen.YitIdHelper;
import com.soli.common.api.exception.BusinessException;
import com.soli.system.core.mapper.SysModuleMapper;
import com.soli.system.core.mapper.SysModuleTitleMapper;
import com.soli.system.service.sysmodule.SysModuleComponentDetailDTO;
import com.soli.system.service.sysmodule.SysModuleDetailDTO;
import com.soli.system.service.sysmodule.SysModuleFieldDTO;
import com.soli.system.service.sysmodule.SysModuleService;
import com.soli.system.service.sysmodule.SysModuleTreeNodeDTO;
import com.soli.system.service.sysmoduletitle.SysModuleFieldTitleDTO;
import com.soli.system.service.sysmoduletitle.SysModuleTitleSaveRequest;
import com.soli.system.service.sysmoduletitle.SysModuleTitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 字段标题服务实现
 *
 * @author lizhengqiang
 * @since 2026-03-25 11:15
 */
@Service
@RequiredArgsConstructor
public class SysModuleTitleServiceImpl implements SysModuleTitleService {

    private static final String DEFAULT_LOCALE = "zh_CN";

    private static final String SYSTEM_USER = "system";

    private final SysModuleService sysModuleService;

    private final SysModuleMapper sysModuleMapper;

    private final SysModuleTitleMapper sysModuleTitleMapper;

    @Override
    public List<SysModuleTreeNodeDTO> queryModuleTree() {
        return sysModuleService.queryTreeList();
    }

    @Override
    public SysModuleDetailDTO queryDetail(Long moduleId) {
        return sysModuleService.queryDetailById(moduleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SysModuleTitleSaveRequest request) {
        if (request == null) {
            throw new BusinessException("保存请求不能为空");
        }
        SysModuleDetailDTO moduleDetail = sysModuleService.queryDetailById(request.getModuleId());
        Map<Long, SysModuleFieldDTO> fieldMap = flattenFields(moduleDetail).stream()
                .collect(LinkedHashMap::new, (result, field) -> result.put(field.getId(), field), Map::putAll);
        if (request.getFields() == null || request.getFields().isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        request.getFields().forEach(item -> updateFieldTitle(fieldMap, item, now));
        sysModuleMapper.incrementContextVersion(request.getModuleId(), now);
    }

    private void updateFieldTitle(Map<Long, SysModuleFieldDTO> fieldMap, SysModuleFieldTitleDTO item, LocalDateTime now) {
        if (item == null || item.getFieldId() == null) {
            return;
        }
        SysModuleFieldDTO fieldDTO = fieldMap.get(item.getFieldId());
        if (fieldDTO == null) {
            throw new BusinessException("字段不存在或不属于当前模块");
        }

        SysModuleFieldTitleEntity entity = sysModuleTitleMapper.selectByFieldIdAndLocale(item.getFieldId(), DEFAULT_LOCALE);
        if (entity == null) {
            entity = buildFieldTitleEntity(fieldDTO, now);
            entity.setDisplayTitle(normalizeDisplayTitle(item.getDisplayTitle(), fieldDTO.getDefaultTitle()));
            entity.setPlaceholder(normalizeText(item.getPlaceholder()));
            entity.setHelpText(normalizeText(item.getHelpText()));
            sysModuleTitleMapper.insert(entity);
            return;
        }

        entity.setDisplayTitle(normalizeDisplayTitle(item.getDisplayTitle(), fieldDTO.getDefaultTitle()));
        entity.setPlaceholder(normalizeText(item.getPlaceholder()));
        entity.setHelpText(normalizeText(item.getHelpText()));
        entity.setUpdateBy(SYSTEM_USER);
        entity.setUpdateTime(now);
        sysModuleTitleMapper.update(entity);
    }

    private SysModuleFieldTitleEntity buildFieldTitleEntity(SysModuleFieldDTO fieldDTO, LocalDateTime now) {
        SysModuleFieldTitleEntity entity = new SysModuleFieldTitleEntity();
        entity.setId(YitIdHelper.nextId());
        entity.setFieldId(fieldDTO.getId());
        entity.setModuleId(fieldDTO.getModuleId());
        entity.setComponentId(fieldDTO.getComponentId());
        entity.setComponentCode(fieldDTO.getComponentCode());
        entity.setFieldCode(fieldDTO.getFieldCode());
        entity.setDefaultTitle(fieldDTO.getDefaultTitle());
        entity.setLocale(DEFAULT_LOCALE);
        entity.setComponentType(fieldDTO.getComponentType());
        entity.setDataPath(fieldDTO.getDataPath());
        entity.setValueType(fieldDTO.getValueType());
        entity.setRequiredFlag(fieldDTO.getRequiredFlag());
        entity.setSort(fieldDTO.getSort());
        entity.setStatus(fieldDTO.getStatus());
        entity.setCreateBy(SYSTEM_USER);
        entity.setCreateTime(now);
        entity.setUpdateBy(SYSTEM_USER);
        entity.setUpdateTime(now);
        entity.setNote(fieldDTO.getNote());
        return entity;
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

    private String normalizeDisplayTitle(String text, String defaultTitle) {
        String normalized = normalizeText(text);
        if (normalized == null) {
            return null;
        }
        if (normalized.equals(defaultTitle)) {
            return null;
        }
        return normalized;
    }

    private String normalizeText(String text) {
        if (!StringUtils.hasText(text)) {
            return null;
        }
        return text.trim();
    }

}

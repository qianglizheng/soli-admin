package com.soli.system.core.service.impl.sysconfig;

import com.soli.common.api.exception.BusinessException;
import com.soli.system.core.mapper.SysConfigMapper;
import com.soli.system.core.service.impl.BaseCrudServiceImpl;
import com.soli.system.service.sysconfig.SysConfigDTO;
import com.soli.system.service.sysconfig.SysConfigQuery;
import com.soli.system.service.sysconfig.SysConfigService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * 参数配置服务实现
 *
 * @author lizhengqiang
 * @since 2026-03-22 01:43
 */
@Service
public class SysConfigServiceImpl extends BaseCrudServiceImpl<SysConfigDTO, SysConfigEntity, SysConfigQuery>
        implements SysConfigService {

    private static final String OPTION_CACHE_KEY_PATTERN = "option*";

    private final SysConfigMapper sysConfigMapper;

    private final StringRedisTemplate stringRedisTemplate;

    public SysConfigServiceImpl(final SysConfigMapper mapper,
                                final SysConfigConverter converter,
                                final StringRedisTemplate stringRedisTemplate) {
        super(mapper, converter);
        this.sysConfigMapper = mapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    protected void beforeCreate(SysConfigDTO dto) {
        normalize(dto);
        if (sysConfigMapper.selectByConfigKey(dto.getConfigKey()) != null) {
            throw new BusinessException("参数键名已存在");
        }
        dto.setCreateTime(LocalDateTime.now());
    }

    @Override
    protected void beforeModify(SysConfigDTO dto) {
        normalize(dto);
        SysConfigEntity entity = sysConfigMapper.selectByConfigKey(dto.getConfigKey());
        if (entity != null && !Objects.equals(entity.getId(), dto.getId())) {
            throw new BusinessException("参数键名已存在");
        }
        dto.setUpdateTime(LocalDateTime.now());
    }

    @Override
    protected void beforeRemove(Long id) {
        SysConfigEntity entity = sysConfigMapper.selectById(id);
        if (entity != null && "Y".equalsIgnoreCase(entity.getConfigType())) {
            throw new BusinessException("系统内置参数不能删除");
        }
    }

    @Override
    public void refreshCache() {
        Set<String> keys = stringRedisTemplate.keys(OPTION_CACHE_KEY_PATTERN);
        if (keys == null || keys.isEmpty()) {
            return;
        }
        stringRedisTemplate.delete(keys);
    }

    @Override
    protected String moduleName() {
        return "参数设置";
    }

    private void normalize(SysConfigDTO dto) {
        if (dto.getConfigType() != null) {
            dto.setConfigType(dto.getConfigType().toUpperCase());
        }
    }

}

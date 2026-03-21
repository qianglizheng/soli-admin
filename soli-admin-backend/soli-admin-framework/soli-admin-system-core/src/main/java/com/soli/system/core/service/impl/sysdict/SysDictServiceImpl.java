package com.soli.system.core.service.impl.sysdict;

import com.soli.common.api.exception.BusinessException;
import com.soli.system.core.mapper.SysDictDataMapper;
import com.soli.system.core.mapper.SysDictMapper;
import com.soli.system.core.service.impl.BaseCrudServiceImpl;
import com.soli.system.service.sysdict.SysDictDTO;
import com.soli.system.service.sysdict.SysDictQuery;
import com.soli.system.service.sysdict.SysDictService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 字典类型服务实现
 *
 * @author lizhengqiang
 * @since 2026-03-21 23:55
 */
@Service
public class SysDictServiceImpl extends BaseCrudServiceImpl<SysDictDTO, SysDictEntity, SysDictQuery>
        implements SysDictService {

    private final SysDictMapper sysDictMapper;

    private final SysDictDataMapper sysDictDataMapper;

    public SysDictServiceImpl(final SysDictMapper mapper,
                              final SysDictConverter converter,
                              final SysDictDataMapper sysDictDataMapper) {
        super(mapper, converter);
        this.sysDictMapper = mapper;
        this.sysDictDataMapper = sysDictDataMapper;
    }

    @Override
    protected void beforeCreate(SysDictDTO dto) {
        normalize(dto);
        if (sysDictMapper.selectByType(dto.getType()) != null) {
            throw new BusinessException("字典类型已存在");
        }
        dto.setCreateTime(LocalDateTime.now());
    }

    @Override
    protected void beforeModify(SysDictDTO dto) {
        normalize(dto);
        SysDictEntity entity = sysDictMapper.selectByType(dto.getType());
        if (entity != null && !Objects.equals(entity.getId(), dto.getId())) {
            throw new BusinessException("字典类型已存在");
        }
        dto.setUpdateTime(LocalDateTime.now());
    }

    @Override
    protected void beforeRemove(Long id) {
        sysDictDataMapper.deleteByDictTypeId(id);
    }

    @Override
    protected String moduleName() {
        return "字典管理";
    }

    private void normalize(SysDictDTO dto) {
        if (dto.getStatus() == null || dto.getStatus().isBlank()) {
            dto.setStatus("0");
        }
    }

}
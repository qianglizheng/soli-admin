package com.soli.system.core.service.impl.sysdictdata;

import com.soli.common.api.enums.YesNoEnum;
import com.soli.common.api.exception.BusinessException;
import com.soli.system.core.mapper.SysDictDataMapper;
import com.soli.system.core.mapper.SysDictMapper;
import com.soli.system.core.service.impl.BaseCrudServiceImpl;
import com.soli.system.core.service.impl.sysdict.SysDictEntity;
import com.soli.system.service.sysdictdata.SysDictDataDTO;
import com.soli.system.service.sysdictdata.SysDictDataQuery;
import com.soli.system.service.sysdictdata.SysDictDataService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 字典数据服务实现
 *
 * @author lizhengqiang
 * @since 2026-03-21 23:50
 */
@Service
public class SysDictDataServiceImpl extends BaseCrudServiceImpl<SysDictDataDTO, SysDictDataEntity, SysDictDataQuery>
        implements SysDictDataService {

    private final SysDictDataMapper sysDictDataMapper;

    private final SysDictMapper sysDictMapper;

    public SysDictDataServiceImpl(final SysDictDataMapper mapper,
                                  final SysDictDataConverter converter,
                                  final SysDictMapper sysDictMapper) {
        super(mapper, converter);
        this.sysDictDataMapper = mapper;
        this.sysDictMapper = sysDictMapper;
    }

    @Override
    protected void beforeCreate(SysDictDataDTO dto) {
        ensureDictExists(dto.getDictTypeId());
        if (YesNoEnum.YES == dto.getDefaultFlag()) {
            sysDictDataMapper.clearDefaultByDictTypeId(dto.getDictTypeId(), null);
        }
        dto.setCreateTime(LocalDateTime.now());
    }

    @Override
    protected void beforeModify(SysDictDataDTO dto) {
        ensureDictExists(dto.getDictTypeId());
        if (YesNoEnum.YES == dto.getDefaultFlag()) {
            sysDictDataMapper.clearDefaultByDictTypeId(dto.getDictTypeId(), dto.getId());
        }
        dto.setUpdateTime(LocalDateTime.now());
    }

    @Override
    protected String moduleName() {
        return "字典数据";
    }

    private void ensureDictExists(Long dictTypeId) {
        SysDictEntity dictEntity = sysDictMapper.selectById(dictTypeId);
        if (dictEntity == null) {
            throw new BusinessException("字典类型不存在");
        }
    }

}

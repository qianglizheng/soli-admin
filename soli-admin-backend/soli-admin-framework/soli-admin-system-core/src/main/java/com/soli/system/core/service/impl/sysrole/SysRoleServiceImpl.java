package com.soli.system.core.service.impl.sysrole;

import java.util.List;

import com.github.yitter.idgen.YitIdHelper;
import com.soli.system.core.service.impl.BaseServiceImpl;
import com.soli.system.service.sysrole.SysRoleQuery;
import org.springframework.stereotype.Service;

import com.soli.system.core.mapper.SysRoleMapper;
import com.soli.system.service.sysrole.SysRoleDTO;
import com.soli.system.service.sysrole.SysRoleService;

/**
 * @author lizhengqiang
 * @since 2026-03-14 15:59
*/
@Service
public class SysRoleServiceImpl extends BaseServiceImpl<SysRoleDTO, SysRoleEntity, SysRoleQuery> implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;

    private final SysRoleConverter sysRoleConverter;

    public SysRoleServiceImpl(final SysRoleMapper sysRoleMapper, final SysRoleConverter sysRoleConverter) {
        super(sysRoleConverter);
        this.sysRoleMapper = sysRoleMapper;
        this.sysRoleConverter = sysRoleConverter;
    }

    @Override
    public List<SysRoleDTO> queryAll() {
        return List.of();
    }

    @Override
    public List<SysRoleDTO> getByUserId(Long userId) {
        List<SysRoleEntity> sysRoleEntities = sysRoleMapper.selectByUserId(userId);
        return sysRoleConverter.toDTOList(sysRoleEntities);
    }

    @Override
    protected SysRoleEntity selectById(Long id) {
        return sysRoleMapper.selectById(id);
    }

    @Override
    protected int insert(SysRoleEntity entity) {
        return sysRoleMapper.insert(entity);
    }

    @Override
    protected int update(SysRoleEntity entity) {
        return sysRoleMapper.update(entity);
    }

    @Override
    protected int deleteById(Long id) {
        return sysRoleMapper.deleteById(id);
    }

    @Override
    protected List<SysRoleEntity> select(SysRoleQuery query) {
        return sysRoleMapper.select(query);
    }

    @Override
    protected String moduleName() {
        return "系统用户";
    }

}
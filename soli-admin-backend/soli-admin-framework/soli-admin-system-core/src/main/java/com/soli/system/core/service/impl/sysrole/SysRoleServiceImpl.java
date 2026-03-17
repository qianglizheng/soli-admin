package com.soli.system.core.service.impl.sysrole;

import java.util.List;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.soli.common.api.vo.PageResult;
import com.soli.system.service.sysrole.SysRoleConverter;
import com.soli.system.service.sysrole.SysRoleQuery;
import org.springframework.stereotype.Service;

import com.soli.system.core.mapper.SysRoleMapper;
import com.soli.system.service.sysrole.SysRoleDTO;
import com.soli.system.service.sysrole.SysRoleService;

import lombok.RequiredArgsConstructor;

/**
 * @author lizhengqiang
 * @since 2026-03-14 15:59
*/
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;

    private final SysRoleConverter sysRoleConverter;

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
    public int create(SysRoleDTO dto) {
        return 0;
    }

    @Override
    public SysRoleDTO getById(Long id) {
        return null;
    }

    @Override
    public int modify(Long id, SysRoleDTO dto) {
        return 0;
    }

    @Override
    public int remove(Long id) {
        return 0;
    }

    @Override
    public PageResult<SysRoleDTO> page(SysRoleQuery query) {
        Page<SysRoleEntity> page = PageHelper.startPage(query.getPageNum(), query.getPageSize())
                .doSelectPage(() -> sysRoleMapper.select(query));
        List<SysRoleDTO> dtoList = sysRoleConverter.toDTOList(page.getResult());
        return PageResult.of(
                page.getPageNum(),
                page.getPageSize(),
                page.getTotal(),
                dtoList
        );
    }

}
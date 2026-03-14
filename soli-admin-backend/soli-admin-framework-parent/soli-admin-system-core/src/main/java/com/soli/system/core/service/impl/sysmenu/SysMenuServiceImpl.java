package com.soli.system.core.service.impl.sysmenu;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.soli.system.core.mapper.SysMenuMapper;
import com.soli.system.service.sysmenu.SysMenuService;

import lombok.RequiredArgsConstructor;

/**
 * @author lizhengqiang
 * @since 2026-03-14 20:52
*/
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl implements SysMenuService {

    private final SysMenuMapper sysMenuMapper;

    @Override
    public Set<String> getPermsByUserId(Long userId) {
        return sysMenuMapper.selectPermsByUserId(userId);
    }

}
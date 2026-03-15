package com.soli.system.core.service.impl.sysmenu;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.soli.system.core.mapper.SysMenuMapper;
import com.soli.system.service.sysmenu.SysMenuDTO;
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

    private final SysMenuConverter sysMenuConverter;

    @Override
    public Set<String> queryPermsByUserId(Long userId) {
        return sysMenuMapper.selectPermsByUserId(userId);
    }

    @Override
    public List<SysMenuDTO> queryTreeList(Long userId) {
        List<SysMenuEntity> sysMenuEntities = sysMenuMapper.selectMenuByUserId(userId);
        return buildTree(sysMenuConverter.toDTOList(sysMenuEntities));
    }

    private List<SysMenuDTO> buildTree(List<SysMenuDTO> menus) {
        List<SysMenuDTO> roots = menus.stream().filter(menu -> menu.getParentId().equals(0L)).toList();
        roots.forEach(root -> buildChildren(root, menus));
        return roots;
    }

    private void buildChildren(SysMenuDTO root, List<SysMenuDTO> menus) {
        List<SysMenuDTO> children = menus.stream().filter(menu -> menu.getParentId().equals(root.getId())).toList();
        root.setChildren(children);
        children.forEach(child -> buildChildren(child, menus));
    }

}
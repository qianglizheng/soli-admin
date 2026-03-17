package com.soli.system.core.service.impl.sysmenu;

import java.util.List;
import java.util.Set;

import com.soli.system.service.sysmenu.SysMenuConverter;
import org.springframework.stereotype.Service;

import com.github.yitter.idgen.YitIdHelper;
import com.soli.system.core.mapper.SysMenuMapper;
import com.soli.system.service.sysmenu.SysMenuDTO;
import com.soli.system.service.sysmenu.SysMenuService;

import lombok.AllArgsConstructor;

/**
 * 菜单服务
 *
 * @author lizhengqiang
 * @since 2026-03-14 20:52
*/
@Service
@AllArgsConstructor
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

    @Override
    public int create(SysMenuDTO sysMenuDTO) {
        SysMenuEntity entity = sysMenuConverter.toEntity(sysMenuDTO);
        entity.setId(YitIdHelper.nextId());
        return sysMenuMapper.insert(entity);
    }

    @Override
    public int modify(SysMenuDTO sysMenuDTO) {
        SysMenuEntity entity = sysMenuConverter.toEntity(sysMenuDTO);
        return sysMenuMapper.update(entity);
    }

    @Override
    public int remove(Long id) {
        return sysMenuMapper.delete(id);
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
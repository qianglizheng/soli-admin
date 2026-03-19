package com.soli.system.core.service.impl.sysmenu;

import java.util.List;
import java.util.Set;

import com.soli.system.core.service.impl.BaseCrudServiceImpl;
import com.soli.system.service.sysmenu.SysMenuQuery;
import org.springframework.stereotype.Service;

import com.soli.system.core.mapper.SysMenuMapper;
import com.soli.system.service.sysmenu.SysMenuDTO;
import com.soli.system.service.sysmenu.SysMenuService;

/**
 * 菜单服务
 *
 * @author lizhengqiang
 * @since 2026-03-14 20:52
*/
@Service
public class SysMenuServiceImpl extends BaseCrudServiceImpl<SysMenuDTO, SysMenuEntity, SysMenuQuery>
        implements SysMenuService {

    private final SysMenuMapper sysMenuMapper;

    public SysMenuServiceImpl(final SysMenuMapper mapper, final SysMenuConverter converter) {
        super(mapper, converter);
        this.sysMenuMapper = mapper;
    }

    @Override
    public Set<String> queryPermsByUserId(Long userId) {
        return sysMenuMapper.selectPermsByUserId(userId);
    }

    @Override
    public List<SysMenuDTO> queryTreeList(Long userId) {
        List<SysMenuEntity> sysMenuEntities = sysMenuMapper.selectMenuByUserId(userId);
        return buildTree(converter.toDTOList(sysMenuEntities));
    }

    @Override
    protected String moduleName() {
        return "系统菜单";
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
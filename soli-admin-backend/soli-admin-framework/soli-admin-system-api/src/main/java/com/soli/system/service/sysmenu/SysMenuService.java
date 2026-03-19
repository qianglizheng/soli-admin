package com.soli.system.service.sysmenu;

import com.soli.system.service.BaseCrudService;

import java.util.List;
import java.util.Set;

/**
 * 系统菜单服务
 *
 * @author lizhengqiang
 * @since 2026-03-14 20:53
 */
public interface SysMenuService extends BaseCrudService<SysMenuDTO, SysMenuQuery> {

    /**
     * 查询用户权限
     *
     * @param userId 用户 ID
     * @return 权限集合
     */
    Set<String> queryPermsByUserId(Long userId);

    /**
     * 查询菜单树
     *
     * @param userId 用户 ID
     * @return 树形菜单
     */
    List<SysMenuDTO> queryTreeList(Long userId);

}
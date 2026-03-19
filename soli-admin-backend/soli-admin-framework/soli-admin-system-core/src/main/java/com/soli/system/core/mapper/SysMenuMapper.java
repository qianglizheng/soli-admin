package com.soli.system.core.mapper;

import java.util.List;
import java.util.Set;

import com.soli.system.core.service.impl.sysmenu.SysMenuEntity;
import com.soli.system.service.sysmenu.SysMenuQuery;

/**
 * @author lizhengqiang
 * @since 2026-03-14 22:41
 */
public interface SysMenuMapper extends BaseCrudMapper<SysMenuEntity, SysMenuQuery> {

    /**
     * 根据用户 Id 查询权限
     * @param userId 系统用户 Id
     * @return 权限编码集合
     */
    Set<String> selectPermsByUserId(Long userId);

    /**
     * 根据用户 Id 查询用户菜单
     *
     * @param userId 用户 Id
     * @return 菜单列表
     */
    List<SysMenuEntity> selectMenuByUserId(Long userId);

}
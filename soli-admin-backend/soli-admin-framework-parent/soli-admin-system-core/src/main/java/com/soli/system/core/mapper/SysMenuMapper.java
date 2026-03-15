package com.soli.system.core.mapper;

import java.util.List;
import java.util.Set;

import com.soli.system.core.service.impl.sysmenu.SysMenuEntity;

/**
 * @author lizhengqiang
 * @since 2026-03-14 22:41
 */
public interface SysMenuMapper {

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

    /**
     * 插入菜单
     *
     * @param entity 菜单信息
     * @return 影响的条数
     */
    int insert(SysMenuEntity entity);
}
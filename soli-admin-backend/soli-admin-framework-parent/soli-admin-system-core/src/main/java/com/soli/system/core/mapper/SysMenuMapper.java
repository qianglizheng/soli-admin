package com.soli.system.core.mapper;

import java.util.Set;

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

}
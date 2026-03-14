package com.soli.system.service.sysmenu;

import java.util.Set;

/**
 * 系统菜单服务
 *
 * @author lizhengqiang
 * @since 2026-03-14 20:53
 */
public interface SysMenuService {

    Set<String> getPermsByUserId(Long userId);

}
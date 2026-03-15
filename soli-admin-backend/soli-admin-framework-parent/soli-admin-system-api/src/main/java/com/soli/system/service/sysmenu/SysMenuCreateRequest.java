package com.soli.system.service.sysmenu;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lizhengqiang
 * @since 2026-03-15 21:10
*/
@Getter
@Setter
public class SysMenuCreateRequest {

    private Long id;

    /** 菜单名称 */
    private String name;

    /** 父菜单 ID */
    private Long parentId;

    /** 显示顺序 */
    private String sort;

    /** 路由地址 */
    private String path;

    /** 组件地址 */
    private String component;

    /** 菜单类型 0 目录 1 菜单 2 按钮 */
    private String type;

    /** 菜单权限 */
    private String perms;

    /** 菜单图标 */
    private String icon;

    /** 菜单状态 */
    private String status;

}
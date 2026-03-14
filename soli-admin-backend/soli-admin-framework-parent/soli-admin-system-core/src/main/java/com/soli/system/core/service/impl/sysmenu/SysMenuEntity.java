package com.soli.system.core.service.impl.sysmenu;

import com.soli.common.core.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lizhengqiang
 * @since 2026-03-14 20:52
*/
@Getter
@Setter
public class SysMenuEntity extends BaseEntity {

    /** 菜单名称 */
    private String name;

    /** 父菜单 ID */
    private String parentId;

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
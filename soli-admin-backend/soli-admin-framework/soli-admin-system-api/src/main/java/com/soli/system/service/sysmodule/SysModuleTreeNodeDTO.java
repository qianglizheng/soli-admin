package com.soli.system.service.sysmodule;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 模块树节点对象
 *
 * @author lizhengqiang
 * @since 2026-03-25 00:05
 */
@Getter
@Setter
public class SysModuleTreeNodeDTO {

    /** 模块 ID */
    private Long id;

    /** 父模块 ID */
    private Long parentId;

    /** 模块编码 */
    private String moduleCode;

    /** 模块名称 */
    private String moduleName;

    /** 模块类型 */
    private String moduleType;

    /** 路由地址 */
    private String routePath;

    /** 组件路径 */
    private String componentPath;

    /** 图标 */
    private String icon;

    /** 排序 */
    private Integer sort;

    /** 导航可见标识 */
    private String navVisible;

    /** 状态型模块标识 */
    private String statefulFlag;

    /** 状态 */
    private String status;

    /** 子节点 */
    private List<SysModuleTreeNodeDTO> children;

}

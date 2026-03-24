package com.soli.system.service.sysmodule;

import com.soli.common.api.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 模块对象
 *
 * @author lizhengqiang
 * @since 2026-03-25 00:05
 */
@Getter
@Setter
public class SysModuleDTO extends BaseDTO {

    /** 父模块 ID */
    private Long parentId;

    /** 祖级路径 */
    private String ancestors;

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

    /** 状态字段编码 */
    private String stateFieldCode;

    /** 上下文版本 */
    private Integer contextVersion;

    /** 状态 */
    private String status;

}

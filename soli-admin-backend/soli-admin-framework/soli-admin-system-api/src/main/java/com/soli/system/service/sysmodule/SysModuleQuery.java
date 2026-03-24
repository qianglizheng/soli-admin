package com.soli.system.service.sysmodule;

import com.soli.common.api.vo.PageQuery;
import lombok.Getter;
import lombok.Setter;

/**
 * 模块查询对象
 *
 * @author lizhengqiang
 * @since 2026-03-25 00:05
 */
@Getter
@Setter
public class SysModuleQuery extends PageQuery {

    /** 父模块 ID */
    private Long parentId;

    /** 模块编码 */
    private String moduleCode;

    /** 模块名称 */
    private String moduleName;

    /** 模块类型 */
    private String moduleType;

    /** 状态型模块标识 */
    private String statefulFlag;

    /** 状态 */
    private String status;

}

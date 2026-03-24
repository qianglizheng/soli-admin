package com.soli.system.core.service.impl.sysorgpost;

import lombok.Getter;
import lombok.Setter;

/**
 * 组织岗位树节点查询模型
 *
 * @author lizhengqiang
 * @since 2026-03-24 23:20
 */
@Getter
@Setter
public class SysOrgPostTreeNodeModel {

    /** 树节点唯一键 */
    private String nodeKey;

    /** 业务 ID */
    private Long id;

    /** 父节点唯一键 */
    private String parentNodeKey;

    /** 节点类型 */
    private String nodeType;

    /** 节点编码 */
    private String nodeCode;

    /** 节点名称 */
    private String nodeName;

    /** 状态 */
    private String status;

    /** 所属组织 ID */
    private Long orgUnitId;

    /** 父岗位 ID */
    private Long parentPostId;

    /** 显示顺序 */
    private Integer sort;

    /** 员工数量 */
    private Long employeeCount;

    /** 下级岗位数量 */
    private Long childPostCount;

}

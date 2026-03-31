package com.soli.system.service.sysorgpost;

import com.soli.common.api.enums.NormalDisableStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 组织岗位树节点对象
 *
 * @author lizhengqiang
 * @since 2026-03-24 21:00
 */
@Getter
@Setter
public class SysOrgPostTreeNodeDTO {

    /** 树节点唯一键 */
    private String nodeKey;

    /** 节点业务 ID */
    private Long id;

    /** 父节点唯一键 */
    private String parentNodeKey;

    /** 节点类型 */
    private String nodeType;

    /** 节点编码 */
    private String nodeCode;

    /** 节点名称 */
    private String nodeName;

    /** 节点状态 */
    private NormalDisableStatusEnum status;

    /** 所属组织 ID */
    private Long orgUnitId;

    /** 父岗位 ID */
    private Long parentPostId;

    /** 显示顺序 */
    private Integer sort;

    /** 岗位员工数 */
    private Long employeeCount;

    /** 下级岗位数 */
    private Long childPostCount;

    /** 子节点 */
    private List<SysOrgPostTreeNodeDTO> children;

}

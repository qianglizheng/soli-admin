package com.soli.system.core.service.impl.sysorgpost;

import com.soli.common.api.enums.NormalDisableStatusEnum;
import com.soli.system.service.enums.OrgTypeEnum;
import com.soli.system.service.enums.PostTypeEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 岗位详情查询模型
 *
 * @author lizhengqiang
 * @since 2026-03-24 23:20
 */
@Getter
@Setter
public class SysOrgPostDetailModel {

    /** 岗位 ID */
    private Long id;

    /** 所属组织 ID */
    private Long orgUnitId;

    /** 父岗位 ID */
    private Long parentPostId;

    /** 岗位编码 */
    private String postCode;

    /** 岗位名称 */
    private String postName;

    /** 岗位类型 */
    private PostTypeEnum postType;

    /** 岗位负责人用户 ID */
    private Long managerUserId;

    /** 岗位负责人名称 */
    private String managerName;

    /** 所属组织名称 */
    private String orgName;

    /** 所属组织类型 */
    private OrgTypeEnum orgType;

    /** 上级节点名称 */
    private String parentNodeName;

    /** 显示顺序 */
    private Integer sort;

    /** 状态 */
    private NormalDisableStatusEnum status;

    /** 下级岗位数量 */
    private Long childPostCount;

    /** 岗位员工数量 */
    private Long employeeCount;

    /** 备注 */
    private String note;

}

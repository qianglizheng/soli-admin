package com.soli.system.core.service.impl.sysorgpost;

import com.soli.common.api.enums.NormalDisableStatusEnum;
import com.soli.common.core.entity.BaseEntity;
import com.soli.system.service.enums.PostTypeEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 组织岗位实体
 *
 * @author lizhengqiang
 * @since 2026-03-24 21:00
 */
@Getter
@Setter
public class SysOrgPostEntity extends BaseEntity {

    /** 所属组织 ID */
    private Long orgUnitId;

    /** 父岗位 ID */
    private Long parentPostId;

    /** 岗位祖级路径 */
    private String ancestors;

    /** 岗位编码 */
    private String postCode;

    /** 岗位名称 */
    private String postName;

    /** 岗位类型 */
    private PostTypeEnum postType;

    /** 岗位负责人用户 ID */
    private Long managerUserId;

    /** 显示顺序 */
    private Integer sort;

    /** 状态 */
    private NormalDisableStatusEnum status;

}

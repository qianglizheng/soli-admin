package com.soli.system.core.service.impl.sysorgpost;

import com.soli.common.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 组织单元实体
 *
 * @author lizhengqiang
 * @since 2026-03-24 21:00
 */
@Getter
@Setter
public class SysOrgUnitEntity extends BaseEntity {

    /** 父组织 ID */
    private Long parentId;

    /** 祖级路径 */
    private String ancestors;

    /** 组织编码 */
    private String orgCode;

    /** 组织名称 */
    private String orgName;

    /** 组织类型 */
    private String orgType;

    /** 显示顺序 */
    private Integer sort;

    /** 负责人用户 ID */
    private Long leaderUserId;

    /** 状态 */
    private String status;

}

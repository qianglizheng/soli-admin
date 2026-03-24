package com.soli.system.core.service.impl.sysorgpost;

import com.soli.common.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户组织岗位关联实体
 *
 * @author lizhengqiang
 * @since 2026-03-24 21:00
 */
@Getter
@Setter
public class SysUserOrgPostEntity extends BaseEntity {

    /** 用户 ID */
    private Long userId;

    /** 岗位 ID */
    private Long orgPostId;

    /** 主岗位标记 */
    private String primaryFlag;

    /** 状态 */
    private String status;

}

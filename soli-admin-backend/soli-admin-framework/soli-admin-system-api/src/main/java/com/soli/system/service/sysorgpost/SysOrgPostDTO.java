package com.soli.system.service.sysorgpost;

import com.soli.common.api.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 组织岗位传输对象
 *
 * @author lizhengqiang
 * @since 2026-03-24 21:00
 */
@Getter
@Setter
public class SysOrgPostDTO extends BaseDTO {

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
    private String postType;

    /** 岗位负责人用户 ID */
    private Long managerUserId;

    /** 显示顺序 */
    private Integer sort;

    /** 岗位状态 */
    private String status;

}

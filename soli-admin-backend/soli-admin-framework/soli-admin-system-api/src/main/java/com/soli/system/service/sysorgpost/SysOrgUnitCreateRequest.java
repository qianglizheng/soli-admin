package com.soli.system.service.sysorgpost;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 新增组织单元请求对象
 *
 * @author lizhengqiang
 * @since 2026-03-24 23:20
 */
@Getter
@Setter
public class SysOrgUnitCreateRequest {

    /** 父组织 ID */
    @NotNull(message = "上级组织不能为空")
    private Long parentId;

    /** 组织编码 */
    @NotBlank(message = "组织编码不能为空")
    private String orgCode;

    /** 组织名称 */
    @NotBlank(message = "组织名称不能为空")
    private String orgName;

    /** 负责人用户 ID */
    private Long leaderUserId;

    /** 显示顺序 */
    private Integer sort;

    /** 状态 */
    private String status;

    /** 备注 */
    private String note;

}

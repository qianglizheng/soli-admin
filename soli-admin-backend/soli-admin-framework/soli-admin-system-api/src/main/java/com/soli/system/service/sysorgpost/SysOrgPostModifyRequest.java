package com.soli.system.service.sysorgpost;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 修改岗位请求对象
 *
 * @author lizhengqiang
 * @since 2026-03-24 21:00
 */
@Getter
@Setter
public class SysOrgPostModifyRequest {

    /** 岗位 ID */
    @NotNull(message = "岗位ID不能为空")
    private Long id;

    /** 所属组织 ID */
    @NotNull(message = "所属组织不能为空")
    private Long orgUnitId;

    /** 父岗位 ID */
    private Long parentPostId;

    /** 岗位编码 */
    @NotBlank(message = "岗位编码不能为空")
    private String postCode;

    /** 岗位名称 */
    @NotBlank(message = "岗位名称不能为空")
    private String postName;

    /** 岗位类型 */
    private String postType;

    /** 岗位负责人用户 ID */
    private Long managerUserId;

    /** 显示顺序 */
    private Integer sort;

    /** 岗位状态 */
    private String status;

    /** 备注 */
    private String note;

}
